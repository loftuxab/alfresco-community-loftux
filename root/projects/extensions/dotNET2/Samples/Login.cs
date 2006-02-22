using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Alfresco;

namespace Samples
{
    public partial class Login : Form
    {
        public Login()
        {
            InitializeComponent();
        }

        private void buttonLogin_Click(object sender, EventArgs e)
        {
            AuthenticationUtils.startSession(textBoxUserName.Text, textBoxPassword.Text);
        }

        public static bool doLogin()
        {
            bool result = false;

            try
            {
                DialogResult dialogResult = new Login().ShowDialog();
                if (dialogResult == DialogResult.OK)
                {
                    result = true;
                }
            }
            catch (Exception)
            {
                // For now do nothing with this ...
                result = false;
            }

            return result;   
        }
    }
}