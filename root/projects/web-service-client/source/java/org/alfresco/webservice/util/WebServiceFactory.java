/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.webservice.util;

import java.io.InputStream;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.alfresco.webservice.accesscontrol.AccessControlServiceLocator;
import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.webservice.action.ActionServiceLocator;
import org.alfresco.webservice.action.ActionServiceSoapBindingStub;
import org.alfresco.webservice.administration.AdministrationServiceLocator;
import org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub;
import org.alfresco.webservice.authentication.AuthenticationServiceLocator;
import org.alfresco.webservice.authentication.AuthenticationServiceSoapBindingStub;
import org.alfresco.webservice.authoring.AuthoringServiceLocator;
import org.alfresco.webservice.authoring.AuthoringServiceSoapBindingStub;
import org.alfresco.webservice.classification.ClassificationServiceLocator;
import org.alfresco.webservice.classification.ClassificationServiceSoapBindingStub;
import org.alfresco.webservice.content.ContentServiceLocator;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.RepositoryServiceLocator;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public final class WebServiceFactory
{
    /** Log */
    private static Log logger = LogFactory.getLog(WebServiceFactory.class);
    
    /** Property file name */
    private static final String PROPERTY_FILE_NAME = "alfresco/webserviceclient.properties";
    private static final String REPO_LOCATION = "repository.location";
    
    /** Default endpoint address **/
    private static final String DEFAULT_ENDPOINT_ADDRESS = "http://localhost:8080";
    
    /** Service addresses */
    private static final String AUTHENTICATION_SERVICE_ADDRESS  = "/alfresco/api/AuthenticationService";
    private static final String REPOSITORY_SERVICE_ADDRESS      = "/alfresco/api/RepositoryService";
    private static final String CONTENT_SERVICE_ADDRESS         = "/alfresco/api/ContentService";
    private static final String AUTHORING_SERVICE_ADDRESS       = "/alfresco/api/AuthoringService";
    private static final String CLASSIFICATION_SERVICE_ADDRESS  = "/alfresco/api/ClassificationService";
    private static final String ACTION_SERVICE_ADDRESS          = "/alfresco/api/ActionService";
    private static final String ACCESS_CONTROL_ADDRESS          = "/alfresco/api/AccessControlService";
    private static final String ADMINISTRATION_ADDRESS          = "/alfresco/api/AdministrationService";
    
    /** Services */
    private static AuthenticationServiceSoapBindingStub authenticationService   = null;
    private static RepositoryServiceSoapBindingStub     repositoryService       = null;
    private static ContentServiceSoapBindingStub        contentService          = null;
    private static AuthoringServiceSoapBindingStub      authoringService        = null;
    private static ClassificationServiceSoapBindingStub classificationService   = null;
    private static ActionServiceSoapBindingStub         actionService           = null;
    private static AccessControlServiceSoapBindingStub  accessControlService    = null;
    private static AdministrationServiceSoapBindingStub administrationService   = null;
    
    /**
     * Get the authentication service
     * 
     * @return
     */
    public static AuthenticationServiceSoapBindingStub getAuthenticationService()
    {
        if (authenticationService == null)
        {            
            try 
            {
                // Get the authentication service
                AuthenticationServiceLocator locator = new AuthenticationServiceLocator();
                locator.setAuthenticationServiceEndpointAddress(getEndpointAddress() + AUTHENTICATION_SERVICE_ADDRESS);                
                authenticationService = (AuthenticationServiceSoapBindingStub)locator.getAuthenticationService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating authentication service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            authenticationService.setTimeout(60000);
        }        
        
        return authenticationService;
    }
    
    /**
     * Get the repository service
     * 
     * @return
     */
    public static RepositoryServiceSoapBindingStub getRepositoryService()
    {
        if (repositoryService == null)
        {            
            try 
            {
                // Get the repository service
                RepositoryServiceLocator locator = new RepositoryServiceLocator(AuthenticationUtils.getEngineConfiguration());
                locator.setRepositoryServiceEndpointAddress(getEndpointAddress() + REPOSITORY_SERVICE_ADDRESS);                
                repositoryService = (RepositoryServiceSoapBindingStub)locator.getRepositoryService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating repositoryService service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            repositoryService.setTimeout(60000);
        }        
        
        return repositoryService;
    }
    
    /**
     * Get the authoring service
     * 
     * @return
     */
    public static AuthoringServiceSoapBindingStub getAuthoringService()
    {
        if (authoringService == null)
        {            
            try 
            {
                // Get the authoring service
                AuthoringServiceLocator locator = new AuthoringServiceLocator(AuthenticationUtils.getEngineConfiguration());
                locator.setAuthoringServiceEndpointAddress(getEndpointAddress() + AUTHORING_SERVICE_ADDRESS);                
                authoringService = (AuthoringServiceSoapBindingStub)locator.getAuthoringService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating authoring service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            authoringService.setTimeout(60000);
        }        
        
        return authoringService;
    }
    
    /**
     * Get the classification service
     * 
     * @return
     */
    public static ClassificationServiceSoapBindingStub getClassificationService()
    {
        if (classificationService == null)
        {            
            try 
            {
                // Get the classification service
                ClassificationServiceLocator locator = new ClassificationServiceLocator(AuthenticationUtils.getEngineConfiguration());
                locator.setClassificationServiceEndpointAddress(getEndpointAddress() + CLASSIFICATION_SERVICE_ADDRESS);                
                classificationService = (ClassificationServiceSoapBindingStub)locator.getClassificationService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating classification service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            classificationService.setTimeout(60000);
        }        
        
        return classificationService;
    }
    
    /**
     * Get the action service
     * 
     * @return
     */
    public static ActionServiceSoapBindingStub getActionService()
    {
        if (actionService == null)
        {            
            try 
            {
                // Get the action service
                ActionServiceLocator locator = new ActionServiceLocator(AuthenticationUtils.getEngineConfiguration());
                locator.setActionServiceEndpointAddress(getEndpointAddress() + ACTION_SERVICE_ADDRESS);                
                actionService = (ActionServiceSoapBindingStub)locator.getActionService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating action service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            actionService.setTimeout(60000);
        }        
        
        return actionService;
    }
    
    /**
     * Get the content service
     * 
     * @return  the content service
     */
    public static ContentServiceSoapBindingStub getContentService()
    {
        if (contentService == null)
        {            
            try 
            {
                // Get the content service
                ContentServiceLocator locator = new ContentServiceLocator(AuthenticationUtils.getEngineConfiguration());
                locator.setContentServiceEndpointAddress(getEndpointAddress() + CONTENT_SERVICE_ADDRESS);                
                contentService = (ContentServiceSoapBindingStub)locator.getContentService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating content service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            contentService.setTimeout(60000);
        }        
        
        return contentService;
    }
    
    /**
     * Get the access control service
     * 
     * @return  the access control service
     */
    public static AccessControlServiceSoapBindingStub getAccessControlService()
    {
        if (accessControlService == null)
        {            
            try 
            {
                // Get the access control service
                AccessControlServiceLocator locator = new AccessControlServiceLocator(AuthenticationUtils.getEngineConfiguration());
                locator.setAccessControlServiceEndpointAddress(getEndpointAddress() + ACCESS_CONTROL_ADDRESS);                
                accessControlService = (AccessControlServiceSoapBindingStub)locator.getAccessControlService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating access control service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            accessControlService.setTimeout(60000);
        }        
        
        return accessControlService;
    }
    
    /**
     * Get the administation service
     * 
     * @return  the administration service
     */
    public static AdministrationServiceSoapBindingStub getAdministrationService()
    {
        if (administrationService == null)
        {            
            try 
            {
                // Get the adminstration service
                AdministrationServiceLocator locator = new AdministrationServiceLocator(AuthenticationUtils.getEngineConfiguration());
                locator.setAdministrationServiceEndpointAddress(getEndpointAddress() + ADMINISTRATION_ADDRESS);                
                administrationService = (AdministrationServiceSoapBindingStub)locator.getAdministrationService();
            }
            catch (ServiceException jre) 
            {
                if (logger.isDebugEnabled() == true)
                {
                    if (jre.getLinkedCause() != null)
                    {
                        jre.getLinkedCause().printStackTrace();
                    }
                }
   
                throw new WebServiceException("Error creating administration service: " + jre.getMessage(), jre);
            }        
            
            // Time out after a minute
            administrationService.setTimeout(60000);
        }        
        
        return administrationService;
    }
    
    /**
     * Gets the end point address from the properties file
     * 
     * @return
     */
    private static String getEndpointAddress()
    {
        String endPoint = DEFAULT_ENDPOINT_ADDRESS;

        InputStream is = ClassLoader.getSystemResourceAsStream(PROPERTY_FILE_NAME);
        if (is != null)
        {
            Properties props = new Properties();
            try
            {
                props.load(is);            
                endPoint = props.getProperty(REPO_LOCATION);
                
                if (logger.isDebugEnabled() == true)
                {
                    logger.debug("Using endpoint " + endPoint);
                }
            }
            catch (Exception e)
            {
                // Do nothing, just use the default endpoint
                if (logger.isDebugEnabled() == true)
                {
                    logger.debug("Unable to file web service client proerties file.  Using default.");
                }
            }
        }
        
        return endPoint;
    }
}
