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

import javax.xml.rpc.ServiceException;

import org.alfresco.webservice.action.ActionServiceLocator;
import org.alfresco.webservice.action.ActionServiceSoapBindingStub;
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

/**
 * 
 * 
 * @author Roy Wetherall
 */
public final class WebServiceFactory
{
    /** Log */
    private static Log logger = LogFactory.getLog(WebServiceFactory.class);
    
    /** Default endpoint address **/
    private static final String DEFAULT_ENDPOINT_ADDRESS = "http://localhost:8080";
    
    /** Service addresses */
    private static final String AUTHENTICATION_SERVICE_ADDRESS  = "/alfresco/api/AuthenticationService";
    private static final String REPOSITORY_SERVICE_ADDRESS      = "/alfresco/api/RepositoryService";
    private static final String CONTENT_SERVICE_ADDRESS         = "/alfresco/api/ContentService";
    private static final String AUTHORING_SERVICE_ADDRESS       = "/alfresco/api/AuthoringService";
    private static final String CLASSIFICATION_SERVICE_ADDRESS  = "/alfresco/api/ClassificationService";
    private static final String ACTION_SERVICE_ADDRESS          = "/alfresco/api/ActionService";
    
    /** Services */
    private static AuthenticationServiceSoapBindingStub authenticationService   = null;
    private static RepositoryServiceSoapBindingStub     repositoryService       = null;
    private static ContentServiceSoapBindingStub        contentService          = null;
    private static AuthoringServiceSoapBindingStub      authoringService        = null;
    private static ClassificationServiceSoapBindingStub classificationService   = null;
    private static ActionServiceSoapBindingStub         actionService           = null;
    
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
     * @return
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
     * Gets the end point address from the properties file
     * 
     * @return
     */
    private static String getEndpointAddress()
    {
        // TODO need to get this from some config
        return DEFAULT_ENDPOINT_ADDRESS;
    }
}
