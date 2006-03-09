using System;
using System.Collections.Generic;
using System.Text;
using Alfresco.AuthenticationWebService;

namespace Alfresco
{
    public class AuthenticationUtils
    {
        [ThreadStatic]
        private static string currentTicket;

        [ThreadStatic]
        private static string currentUserName;

        private static AuthenticationService authenticationService = WebServiceFactory.getAuthenticationService();

        public static string Ticket
        {
            get
            {
                return AuthenticationUtils.currentTicket;
            }
        }

        public static string UserName
        {
            get
            {
                return AuthenticationUtils.currentUserName;
            }
        }

        public static bool IsSessionValid
        {
            get
            {
                return (AuthenticationUtils.currentTicket != null);
            }
        }

        public static void startSession(string userName, string password)
        {
            // Try and authenticate the user and then store the results in the thread static members
            AuthenticationResult results = AuthenticationUtils.authenticationService.startSession(userName, password);
            AuthenticationUtils.currentTicket = results.ticket;
            AuthenticationUtils.currentUserName = results.username;
        }

        public static void endSession()
        {
            if (AuthenticationUtils.currentTicket != null)
            {
                AuthenticationUtils.authenticationService.endSession(AuthenticationUtils.currentTicket);
                AuthenticationUtils.currentTicket = null;
                AuthenticationUtils.currentUserName = null;
            }
        }
    }
}
