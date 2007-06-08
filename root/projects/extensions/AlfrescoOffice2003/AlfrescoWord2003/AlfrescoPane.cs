using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Security.Permissions;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

namespace AlfrescoWord2003
{
   [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public partial class AlfrescoPane : Form
   {
      private Word.Application m_WordApplication;
      private ServerDetails m_ServerDetails;
      private string m_TemplateRoot = "";
      private bool m_ShowPaneOnActivate = false;


      public Word.Application WordApplication
      {
         set
         {
            m_WordApplication = value;
         }
      }

      public string DefaultTemplate
      {
         set
         {
            m_TemplateRoot = value;
         }
      }

      public ServerDetails CurrentServer
      {
         get
         {
            return m_ServerDetails;
         }
      }

      public AlfrescoPane()
      {
         InitializeComponent();

         m_ServerDetails = new ServerDetails();
         LoadSettings();
      }

      public void OnDocumentOpen()
      {
         m_ServerDetails.DocumentPath = m_WordApplication.ActiveDocument.FullName;
         this.showDocumentDetails(m_ServerDetails.DocumentPath);
      }

      public void OnDocumentChanged()
      {
         try
         {
            if ((m_WordApplication.ActiveDocument != null) && (m_ServerDetails.AuthenticationTicket != ""))
            {
               m_ServerDetails.DocumentPath = m_WordApplication.ActiveDocument.FullName;
               this.showDocumentDetails(m_ServerDetails.DocumentPath);
            }
            else
            {
               m_ServerDetails.DocumentPath = "";
               this.showHome();
            }
         }
         catch
         {
         }
      }

      public void OnWindowActivate()
      {
         if (m_ShowPaneOnActivate)
         {
            this.Show();
         }
      }

      public void OnWindowDeactivate()
      {
         m_ShowPaneOnActivate = true;
         this.Hide();
      }

      public void OnDocumentBeforeClose()
      {
         m_ServerDetails.DocumentPath = "";
         this.showHome();
      }

      public void showHome()
      {
         // Do we have a valid web server address?
         if (m_ServerDetails.WebClientURL == "")
         {
            // No - show the configuration UI
            PanelMode = PanelModes.PanelModeConfiguration;
         }
         else
         {
            // Yes - navigate to the home template
//            string theURI = string.Format(@"{0}{1}my_alfresco.ftl&contextPath=/Company%20Home", m_ServerDetails.WebClientURL, m_TemplateRoot);
            string theURI = string.Format(@"{0}{1}myAlfresco?p=/Company%20Home", m_ServerDetails.WebClientURL, m_TemplateRoot);
            string strAuthTicket = m_ServerDetails.AuthenticationTicket;
            if (strAuthTicket != "")
            {
               theURI += "&ticket=" + strAuthTicket;
            }
            webBrowser.ObjectForScripting = this;
            webBrowser.Navigate(new Uri(theURI));
            PanelMode = PanelModes.PanelModeWebBrowser;
         }
      }

      public void showDocumentDetails(string documentPath)
      {
         // Do we have a valid web server address?
         if (m_ServerDetails.WebClientURL == "")
         {
            // No - show the configuration UI
            PanelMode = PanelModes.PanelModeConfiguration;
         }
         else
         {
//            string theURI = string.Format(@"{0}{1}document_details.ftl&contextPath=/Company%20Home{2}", m_ServerDetails.WebClientURL, m_TemplateRoot, documentPath);
            string theURI = string.Format(@"{0}{1}documentDetails?p=/Company%20Home{2}", m_ServerDetails.WebClientURL, m_TemplateRoot, documentPath);
            string strAuthTicket = m_ServerDetails.AuthenticationTicket;
            if (strAuthTicket != "")
            {
               theURI += "&ticket=" + strAuthTicket;
            }
            webBrowser.ObjectForScripting = this;
            webBrowser.Navigate(new Uri(theURI));
            PanelMode = PanelModes.PanelModeWebBrowser;
         }
      }

      public void openDocument(string documentURL)
      {
         object missingValue = Type.Missing;
         // TODO: WebDAV or CIFS - need generic solution
         string strFullPath = string.Format(@"{0}webdav/{1}", m_ServerDetails.WebClientURL, documentURL);
         object file = strFullPath;
         try
         {
            Word.Document doc = m_WordApplication.Documents.Open(
               ref file, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue,
               ref missingValue, ref missingValue, ref missingValue, ref missingValue,
               ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue);
         }
         catch (Exception e)
         {
            MessageBox.Show("Unable to open the document from Alfresco: " + e.Message, "Alfresco Problem", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      public void compareDocument(string documentPath)
      {
         /* TODO: Is compareDocument() needed?
         object missingValue = Type.Missing;
         m_WordApplication.ActiveDocument.Compare(
            m_AlfrescoWebServer + documentPath, ref missingValue, ref missingValue, ref missingValue, ref missingValue,
            ref missingValue, ref missingValue, ref missingValue);
         */
      }

      public void saveToAlfresco(string documentPath)
      {
         object missingValue = Type.Missing;
         // TODO: WebDAV or CIFS path to save on?
         object file = m_ServerDetails.WebDAVURL + documentPath + "/" + m_WordApplication.ActiveDocument.Name;
         try
         {
            m_WordApplication.ActiveDocument.SaveAs(
            ref file, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue,
            ref missingValue, ref missingValue, ref missingValue, ref missingValue,
            ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue);
            showDocumentDetails(documentPath + "/" + m_WordApplication.ActiveDocument.Name);
         }
         catch (Exception e)
         {
            MessageBox.Show("Unable to save the document to Alfresco: " + e.Message, "Alfresco Problem", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      private enum PanelModes
      {
         PanelModeWebBrowser,
         PanelModeConfiguration
      }

      private PanelModes PanelMode
      {
         set
         {
            pnlWebBrowser.Visible = (value == PanelModes.PanelModeWebBrowser);
            pnlConfiguration.Visible = (value == PanelModes.PanelModeConfiguration);
         }
      }

      #region Settings Management
      /// <summary>
      /// Settings Management
      /// </summary>
      private bool m_SettingsChanged = false;

      private void LoadSettings()
      {
         m_ServerDetails.LoadFromRegistry();
         txtWebClientURL.Text = m_ServerDetails.WebClientURL;
         txtWebDAVURL.Text = m_ServerDetails.WebDAVURL;
         txtCIFSServer.Text = m_ServerDetails.CIFSServer;
         if (m_ServerDetails.Username != "")
         {
            txtUsername.Text = m_ServerDetails.Username;
            txtPassword.Text = m_ServerDetails.Password;
            chkRememberAuth.Checked = true;
         }
         else
         {
            txtUsername.Text = "";
            txtPassword.Text = "";
            chkRememberAuth.Checked = false;
         }
         m_SettingsChanged = false;
      }

      private void btnDetailsOK_Click(object sender, EventArgs e)
      {
         m_ServerDetails.WebClientURL = txtWebClientURL.Text;
         m_ServerDetails.WebDAVURL = txtWebDAVURL.Text;
         m_ServerDetails.CIFSServer = txtCIFSServer.Text;
         if (chkRememberAuth.Checked)
         {
            m_ServerDetails.Username = txtUsername.Text;
            m_ServerDetails.Password = txtPassword.Text;
         }
         else
         {
            m_ServerDetails.Username = "";
            m_ServerDetails.Password = "";
         }

         m_ServerDetails.SaveToRegistry();

         this.OnDocumentChanged();
      }

      private void btnDetailsCancel_Click(object sender, EventArgs e)
      {
         LoadSettings();
      }

      private void txtWebClientURL_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
         
         // Build autocomplete string for the WebDAV textbox
         try
         {
            string strWebDAV = txtWebClientURL.Text;
            if (!strWebDAV.EndsWith("/"))
            {
               strWebDAV += "/";
            }
            strWebDAV += "webdav/";
            txtWebDAVURL.AutoCompleteCustomSource.Clear();
            txtWebDAVURL.AutoCompleteCustomSource.Add(strWebDAV);
         }
         catch
         {
         }

         // Build autocomplete string for the CIFS textbox
         try
         {
            Uri clientUri = new Uri(txtWebClientURL.Text);
            string strCIFS = "\\\\" + clientUri.Host + "_a\\alfresco\\";
            txtCIFSServer.AutoCompleteCustomSource.Clear();
            txtCIFSServer.AutoCompleteCustomSource.Add(strCIFS);
         }
         catch
         {
         }
      }

      private void txtWebDAVURL_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void txtCIFSServer_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void txtUsername_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void txtPassword_TextChanged(object sender, EventArgs e)
      {
         m_SettingsChanged = true;
      }

      private void lnkBackToBrowser_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         PanelMode = PanelModes.PanelModeWebBrowser;
      }
      private void lnkShowConfiguration_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         PanelMode = PanelModes.PanelModeConfiguration;
      }
      #endregion
   }
}