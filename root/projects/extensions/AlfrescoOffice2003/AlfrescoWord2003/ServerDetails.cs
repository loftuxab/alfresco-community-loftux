using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;

namespace AlfrescoWord2003
{
   public class ServerDetails
   {
      private const string HKCU_APP = @"Software\Alfresco\Word2003";
      private const string REG_WEBCLIENTURL = "WebClientURL";
      private const string REG_WEBDAVURL = "WebDAVURL";
      private const string REG_CIFSSERVER = "CIFSServer";
      private const string REG_USERNAME = "Username";
      private const string REG_PASSWORD = "Password";

      // Persisted settings
      private string m_ServerName = "";
      private string m_WebClientURL = "";
      public string WebDAVURL = "";
      public string CIFSServer = "";
      public string Username = "";
      public string Password = "";
      // Temporary/runtime-only settings
      private string m_AuthenticationTicket = "";
      private string m_DocumentPhysicalPath = "";
      private string m_DocumentAlfrescoPath = "";

      public string ServerName
      {
         get
         {
            return m_ServerName;
         }
      }

      public string WebClientURL
      {
         get
         {
            return m_WebClientURL;
         }
         set
         {
            try
            {
               m_WebClientURL = value;
               m_AuthenticationTicket = "";
               Uri webClient = new Uri(value);
               m_ServerName = webClient.Host;
            }
            catch
            {
               m_WebClientURL = "";
               m_ServerName = "";
            }
         }
      }

      private string EncryptedPassword
      {
         get
         {
            // TODO: Password will be encrypted as read from the registry
            return Password;
         }
         set
         {
            Password = value;
         }
      }

      public bool LoadFromRegistry()
      {
         bool bResult = true;
         RegistryKey rootKey = Registry.CurrentUser.OpenSubKey(HKCU_APP, true);

         if (rootKey == null)
         {
            // No entries in the registry yet
            rootKey = Registry.CurrentUser.CreateSubKey(HKCU_APP);
            bResult = false;
         }

         try
         {
            string serverName = rootKey.GetValue("").ToString();
            using (RegistryKey serverKey = rootKey.OpenSubKey(serverName))
            {
               this.WebClientURL = serverKey.GetValue(REG_WEBCLIENTURL).ToString();
               this.WebDAVURL = serverKey.GetValue(REG_WEBDAVURL).ToString();
               this.CIFSServer = serverKey.GetValue(REG_CIFSSERVER).ToString();
               this.Username = serverKey.GetValue(REG_USERNAME).ToString();
               this.EncryptedPassword = serverKey.GetValue(REG_PASSWORD).ToString();
            }

         }
         catch
         {
            bResult = false;
         }
         
         m_AuthenticationTicket = "";

         return bResult;
      }

      public void SaveToRegistry()
      {
         RegistryKey rootKey = Registry.CurrentUser.OpenSubKey(HKCU_APP, true);
         try
         {
            rootKey.DeleteSubKey(this.ServerName);
         }
         catch
         {
         }

         RegistryKey serverKey = rootKey.CreateSubKey(this.ServerName);
         serverKey.SetValue(REG_WEBCLIENTURL, this.WebClientURL);
         serverKey.SetValue(REG_WEBDAVURL, this.WebDAVURL);
         serverKey.SetValue(REG_CIFSSERVER, this.CIFSServer);
         serverKey.SetValue(REG_USERNAME, this.Username);
         serverKey.SetValue(REG_PASSWORD, this.EncryptedPassword);
         rootKey.SetValue("", this.ServerName);
      }

      public string DocumentPath
      {
         set
         {
            m_DocumentPhysicalPath = value;
            m_DocumentAlfrescoPath = "";
         }
         get
         {
            if (m_DocumentAlfrescoPath == "")
            {
               IServerHelper serverHelper;

               if (m_DocumentPhysicalPath.StartsWith("http"))
               {
                  // WebDAV path
                  serverHelper = new WebDAVHelper(this.WebDAVURL);
               }
               else
               {
                  // CIFS path
                  serverHelper = new CIFSHelper(this.CIFSServer);
               }

               m_DocumentAlfrescoPath = serverHelper.GetAlfrescoPath(m_DocumentPhysicalPath);
            }
            return m_DocumentAlfrescoPath;
         }
      }

      public string AuthenticationTicket
      {
         get
         {
            string strAuthTicket = "";
            if (m_AuthenticationTicket == "")
            {
               // Do we recognise the path as belonging to an Alfresco server?
               if ((m_DocumentPhysicalPath.StartsWith("http")) || (m_DocumentPhysicalPath == ""))
               {
                  // Try WebDAV
                  if ((this.MatchWebDAVURL(m_DocumentPhysicalPath)) || (m_DocumentPhysicalPath == ""))
                  {
                     IServerHelper myAuthTicket = new WebDAVHelper(this.WebDAVURL);
                     strAuthTicket = myAuthTicket.GetAuthenticationTicket();
                     if (strAuthTicket != "401")
                     {
                        m_AuthenticationTicket = strAuthTicket;
                     }
                     else
                     {
                        // Authentication failed - do we have a saved username/password?
                        if ((this.Username.Length > 0) && (this.Password.Length > 0))
                        {
                           strAuthTicket = myAuthTicket.GetAuthenticationTicket(this.Username, this.Password);
                        }
                        if (strAuthTicket != "401")
                        {
                           m_AuthenticationTicket = strAuthTicket;
                        }
                        else
                        {
                           // Last option - pop up the login form
                           using (Login myLogin = new Login())
                           {
                              bool bRetry = true;

                              // Pre-populate with values already configured
                              myLogin.Username = this.Username;
                              myLogin.Password = this.Password;

                              // Retry loop for typos
                              while (bRetry)
                              {
                                 if (myLogin.ShowDialog() == DialogResult.OK)
                                 {
                                    // Try to authenticate with entered credentials
                                    strAuthTicket = myAuthTicket.GetAuthenticationTicket(myLogin.Username, myLogin.Password);
                                    if ((strAuthTicket == "401") || (strAuthTicket == ""))
                                    {
                                       // Retry?
                                       bRetry = (MessageBox.Show("Couldn't authenticate with Alfresco server.", "Alfresco Authentication", MessageBoxButtons.RetryCancel) == DialogResult.Retry);
                                    }
                                    else
                                    {
                                       // Successful login
                                       m_AuthenticationTicket = strAuthTicket;
                                       bRetry = false;
                                    }
                                 }
                                 else
                                 {
                                    // Cancel or close chosen on login dialog
                                    bRetry = false;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
               else
               {
                  // Try CIFS
                  if (this.MatchCIFSServer(m_DocumentPhysicalPath))
                  {
                     IServerHelper myAuthTicket = new CIFSHelper(this.CIFSServer);
                     m_AuthenticationTicket = myAuthTicket.GetAuthenticationTicket();
                  }
               }
            }

            return m_AuthenticationTicket;
         }
      }

      public bool MatchWebDAVURL(string urlToMatch)
      {
         return (urlToMatch.IndexOf(this.WebDAVURL) == 0);
      }

      public bool MatchCIFSServer(string serverToMatch)
      {
         return (serverToMatch.IndexOf(this.CIFSServer) == 0);
      }
   }
}
