using System;
using System.Collections.Generic;
using System.Text;

namespace AlfrescoExcel2003
{
   public interface IServerHelper
   {
      string GetAlfrescoPath(string documentPath);
      string GetAuthenticationTicket();
      string GetAuthenticationTicket(string Username, string Password);
   }
}
