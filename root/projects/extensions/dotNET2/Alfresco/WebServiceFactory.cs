using System;
using System.Collections.Generic;
using System.Text;
using Alfresco.AuthenticationWebService;
using Alfresco.RepositoryWebService;
using Alfresco.ContentWebService;

namespace Alfresco
{
    public class WebServiceFactory
    {
        /** Default endpoint address **/
        private const string DEFAULT_ENDPOINT_ADDRESS = "http://demo.alfresco.com/alfresco";

        /** Service addresses */
        private const string AUTHENTICATION_SERVICE_ADDRESS = "/api/AuthenticationService";
        private const string REPOSITORY_SERVICE_ADDRESS = "/api/RepositoryService";
        private const string CONTENT_SERVICE_ADDRESS = "/api/ContentService";
        private const string AUTHORING_SERVICE_ADDRESS = "/api/AuthoringService";
        private const string CLASSIFICATION_SERVICE_ADDRESS = "/api/ClassificationService";
        private const string ACTION_SERVICE_ADDRESS = "/api/ActionService";
        private const string ACCESS_CONTROL_ADDRESS = "/api/AccessControlService";
        private const string ADMINISTRATION_ADDRESS = "/api/AdministrationService";

        /** Services */
        private static AuthenticationService authenticationService = null;
        private static RepositoryService repositoryService = null;
        private static ContentService contentService = null;

        public static AuthenticationService getAuthenticationService()
        {
            if (authenticationService == null)
            {
                authenticationService = new AuthenticationService();
                authenticationService.Url = getEndpointAddress() + AUTHENTICATION_SERVICE_ADDRESS;
            }

            return authenticationService;
        }

        public static RepositoryService getRepositoryService()
        {
            if (repositoryService == null)
            {
                repositoryService = new RepositoryService();
                repositoryService.Url = getEndpointAddress() + REPOSITORY_SERVICE_ADDRESS;
            }

            return repositoryService;
        }

        public static ContentService getContentService()
        {
            if (contentService == null)
            {
                contentService = new ContentService();
                contentService.Url = getEndpointAddress() + CONTENT_SERVICE_ADDRESS;
            }

            return contentService;
        }

        private static String getEndpointAddress()
        {
            // TODO get this value from a property fil

            return DEFAULT_ENDPOINT_ADDRESS;
        }
    }
}
