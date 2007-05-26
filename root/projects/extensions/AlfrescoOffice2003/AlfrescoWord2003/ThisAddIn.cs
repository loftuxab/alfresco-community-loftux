using System;
using System.Windows.Forms;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

namespace AlfrescoWord2003
{
   public partial class ThisAddIn
   {
      private AlfrescoPane m_AlfrescoPane;
      private string m_DefaultTemplate = null;
//      private ServerDetails m_ServerDetails = null;

      private void ThisAddIn_Startup(object sender, System.EventArgs e)
      {
         m_DefaultTemplate = Properties.Settings.Default.DefaultTemplate;

         // Register event interest with the Word Application
         Application.WindowActivate += new Microsoft.Office.Interop.Word.ApplicationEvents4_WindowActivateEventHandler(Application_WindowActivate);
         Application.WindowDeactivate += new Microsoft.Office.Interop.Word.ApplicationEvents4_WindowDeactivateEventHandler(Application_WindowDeactivate);
         Application.DocumentOpen += new Word.ApplicationEvents4_DocumentOpenEventHandler(Application_DocumentOpen);
         Application.DocumentBeforeClose += new Word.ApplicationEvents4_DocumentBeforeCloseEventHandler(Application_DocumentBeforeClose);
         Application.DocumentChange += new Microsoft.Office.Interop.Word.ApplicationEvents4_DocumentChangeEventHandler(Application_DocumentChange);
         
         OpenAlfrescoPane();
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
            m_AlfrescoPane.OnWindowDeactivate();
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

      /// <summary>
      /// Fired upon opening a Word document
      /// </summary>
      /// <param name="wordDoc"></param>
      void Application_DocumentOpen(Word.Document wordDoc)
      {
         if (m_AlfrescoPane != null)
         {
            m_AlfrescoPane.OnDocumentOpen();
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
            m_AlfrescoPane.DefaultTemplate = m_DefaultTemplate;
         }

         if (Show)
         {
            m_AlfrescoPane.Show();
            m_AlfrescoPane.showHome();
         }
      }

      public void CloseAlfrescoPane()
      {
         m_AlfrescoPane.Hide();
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
   }
}
