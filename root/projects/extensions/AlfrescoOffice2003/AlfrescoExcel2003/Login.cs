using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace AlfrescoExcel2003
{
   public partial class Login : Form
   {
      public Login()
      {
         InitializeComponent();
      }

      public string Username
      {
         get
         {
            return txtUsername.Text;
         }
         set
         {
            txtUsername.Text = value;
         }
      }

      public string Password
      {
         get
         {
            return txtPassword.Text;
         }
         set
         {
            txtPassword.Text = value;
         }
      }

      private void Login_Activated(object sender, EventArgs e)
      {
         if (txtUsername.Text.Length == 0)
         {
            this.ActiveControl = txtUsername;
         }
         else
         {
            this.ActiveControl = txtPassword;
         }
      }
   }
}