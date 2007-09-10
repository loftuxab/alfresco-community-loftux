using System;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

namespace AlfrescoWord2003
{
   public partial class ThisAddIn
   {
      // Win32 SDK functions
      [DllImport("user32.dll")]
      public static extern int GetForegroundWindow();

      private AlfrescoPane m_AlfrescoPane;
      private Office.CommandBar m_CommandBar;
      private Office.CommandBarButton m_AlfrescoButton;

      private void ThisAddIn_Startup(object sender, System.EventArgs e)
      {
         // Register event interest with the Word Application
         Application.WindowActivate += new Microsoft.Office.Interop.Word.ApplicationEvents4_WindowActivateEventHandler(Application_WindowActivate);
         Application.WindowDeactivate += new Microsoft.Office.Interop.Word.ApplicationEvents4_WindowDeactivateEventHandler(Application_WindowDeactivate);
         Application.DocumentBeforeClose += new Word.ApplicationEvents4_DocumentBeforeCloseEventHandler(Application_DocumentBeforeClose);
         Application.DocumentChange += new Microsoft.Office.Interop.Word.ApplicationEvents4_DocumentChangeEventHandler(Application_DocumentChange);

         // Add the Alfresco button to the Office toolbar
         AddToolbar();
      }

      private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
      {
         CloseAlfrescoPane();
         m_AlfrescoPane = null;
      }

      #region VSTO generated code

      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InternalStartup()
      {
         this.Startup += new System.EventHandler(ThisAddIn_Startup);
         this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
      }

      #endregion

      /// <summary>
      /// Adds commandBars to Word Application
      /// </summary>
      private void AddToolbar()
      {
         // Try to get a handle to the Alfresco CommandBar
         try
         {
            m_CommandBar = Application.CommandBars["Alfresco"];
         }
         catch
         {
            object falseValue = false;
            // Toolbar named Alfresco does not exist so we should create it.
            m_CommandBar = Application.CommandBars.Add("Alfresco", Office.MsoBarPosition.msoBarTop, falseValue, falseValue);
         }

         // Try to get a handle to the Alfresco CommandButton
         try
         {
            m_AlfrescoButton = (Office.CommandBarButton)Application.CommandBars.FindControl(Office.MsoControlType.msoControlButton, missing, "AlfrescoButton", missing);
         }
         catch
         {
            m_AlfrescoButton = null;
         }

         if (m_AlfrescoButton == null)
         {
            // Add our button to the command bar and an event handler.
            m_AlfrescoButton = (Office.CommandBarButton)m_CommandBar.Controls.Add(Office.MsoControlType.msoControlButton, missing, missing, missing, false);
            if (m_AlfrescoButton != null)
            {
               m_AlfrescoButton.Style = Office.MsoButtonStyle.msoButtonCaption;
               m_AlfrescoButton.Caption = "Alfresco";
               m_AlfrescoButton.DescriptionText = "Show/hide the Alfresco Add-In window";
               Bitmap bmpButton = new Bitmap(GetType(), "toolbar.ico");
               m_AlfrescoButton.Picture = new ToolbarPicture(bmpButton);
               Bitmap bmpMask = new Bitmap(GetType(), "toolbar_mask.ico");
               m_AlfrescoButton.Mask = new ToolbarPicture(bmpMask);
               m_AlfrescoButton.Style = Office.MsoButtonStyle.msoButtonIconAndCaption;
               m_AlfrescoButton.Tag = "AlfrescoButton";
            }
         }

         // Finally add the event handler and make sure the button is visible
         if (m_AlfrescoButton != null)
         {
            m_AlfrescoButton.Click += new Microsoft.Office.Core._CommandBarButtonEvents_ClickEventHandler(m_AlfrescoButton_Click);
            m_CommandBar.Visible = true;
         }
      }

      /// <summary>
      /// Alfresco toolbar button event handler
      /// </summary>
      /// <param name="Ctrl"></param>
      /// <param name="CancelDefault"></param>
      void m_AlfrescoButton_Click(Office.CommandBarButton Ctrl, ref bool CancelDefault)
      {
         if (m_AlfrescoPane != null)
         {
            m_AlfrescoPane.OnToggleVisible();
         }
         else
         {
            OpenAlfrescoPane(true);
         }
      }

      /// <summary>
      /// Fired when active document changes
      /// </summary>
      void Application_DocumentChange()
      {
         if (m_AlfrescoPane != null)
         {
            m_AlfrescoPane.OnDocumentChanged();
         }
      }

      /// <summary>
      /// Fired when Word Application becomes the active window
      /// </summary>
      /// <param name="Doc"></param>
      /// <param name="Wn"></param>
      void Application_WindowActivate(Word.Document Doc, Word.Window Wn)
      {
         if (m_AlfrescoPane != null)
         {
            m_AlfrescoPane.OnWindowActivate();
         }
      }

      /// <summary>
      /// Fired when the Word Application loses focus
      /// </summary>
      /// <param name="Doc"></param>
      /// <param name="Wn"></param>
      void Application_WindowDeactivate(Word.Document Doc, Word.Window Wn)
      {
         if (m_AlfrescoPane != null)
         {
            if (m_AlfrescoPane.Handle.ToInt32() != GetForegroundWindow())
            {
               m_AlfrescoPane.OnWindowDeactivate();
            }
         }
      }

      /// <summary>
      /// Fired as a document is being closed
      /// </summary>
      /// <param name="Doc"></param>
      /// <param name="Cancel"></param>
      void Application_DocumentBeforeClose(Word.Document Doc, ref bool Cancel)
      {
         if (m_AlfrescoPane != null)
         {
            m_AlfrescoPane.OnDocumentBeforeClose();
         }
      }

      public void OpenAlfrescoPane()
      {
         OpenAlfrescoPane(true);
      }
      public void OpenAlfrescoPane(bool Show)
      {
         if (m_AlfrescoPane == null)
         {
            m_AlfrescoPane = new AlfrescoPane();
            m_AlfrescoPane.WordApplication = Application;
            m_AlfrescoPane.DefaultTemplate = "wcservice/office/";
         }

         if (Show)
         {
            m_AlfrescoPane.Show();
            if (Application.Documents.Count > 0)
            {
               m_AlfrescoPane.showDocumentDetails();
            }
            else
            {
               m_AlfrescoPane.showHome(false);
            }
            Application.Activate();
         }
      }

      public void CloseAlfrescoPane()
      {
         if (m_AlfrescoPane != null)
         {
            m_AlfrescoPane.Hide();
         }
      }
   }
}
