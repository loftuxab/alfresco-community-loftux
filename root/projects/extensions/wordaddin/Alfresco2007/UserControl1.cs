using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Security.Permissions;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Word = Microsoft.Office.Interop.Word;
using Office = Microsoft.Office.Core;

namespace Alfresco2007
{
    [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
    [System.Runtime.InteropServices.ComVisibleAttribute(true)]
    public partial class UserControl1 : UserControl
    {
        private Word.Application UseWordApp;
        public String AlfServer = null;
        
        public UserControl1()
        {
            InitializeComponent();
        }

        public void showHome(Word.Application WordApp)
        {
            this.UseWordApp = WordApp;
            this.webBrowser1.ObjectForScripting = this;

            this.webBrowser1.Navigate(new Uri("http://localhost:8080/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/office/my_alfresco.ftl&contextPath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/office/my_alfresco.ftl"));
            //this.webBrowser1.Navigate(new Uri("http://www.alfresco.com"));

        }

        public void showDocumentDetails(String strAlfPath)
        {
            this.webBrowser1.Navigate(new Uri("http://localhost:8080/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/office/document_details.ftl&contextPath=/Company%20Home/" + strAlfPath));
                //this.webBrowser1.Navigate(new Uri("http://www.google.com"));
        }

        public void openDocument(String strAlfURL)
        {
            object missingValue = Type.Missing; 
            object file = AlfServer + strAlfURL;
            Word.Document doc = UseWordApp.Documents.Open(
                        ref file, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue,
                        ref missingValue, ref missingValue, ref missingValue, ref missingValue,
                        ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue, ref missingValue);
        }

        private void webBrowser1_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {

        }
    }
}
