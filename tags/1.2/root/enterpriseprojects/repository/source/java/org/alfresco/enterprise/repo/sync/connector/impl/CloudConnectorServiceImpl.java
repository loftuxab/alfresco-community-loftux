/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.connector.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.remoteticket.GuestRemoteAlfrescoTicketImpl;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorService;
import org.alfresco.service.cmr.remotecredentials.BaseCredentialsInfo;
import org.alfresco.service.cmr.remoteticket.NoCredentialsFoundException;
import org.alfresco.service.cmr.remoteticket.RemoteAlfrescoTicketInfo;
import org.alfresco.service.cmr.remoteticket.RemoteAlfrescoTicketService;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Implementation of a service for talking to the Cloud system. 
 * 
 * @author Nick Burch
 * @since CloudSync
 */
public class CloudConnectorServiceImpl extends AbstractLifecycleBean 
    implements CloudConnectorService, KeyProvider.KeyChangeHandler 
{
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(CloudConnectorServiceImpl.class);
    
    private static final String CLOUD_KEY_HEADER_NAME = "key";
    private static final String CLOUD_DEFAULT_NETWORK = "-default-";
    private static final String CLOUD_REMOTE_SYSTEM_ID = "Cloud";
    
    private RemoteConnectorService remoteConnectorService;
    private RemoteAlfrescoTicketService remoteAlfrescoTicketService;
    
    private String cloudBaseUrl;
    private KeyProvider keyProvider;
    
    private String cloudKey;
    
    /** Have we gone through the bootstrap, and registered ourselves with the remote ticket service? */
    private boolean initialisationPerformed = false;
    
    /**
     * @param remoteConnectorService The service to use to perform remote requests
     */
    public void setRemoteConnectorService(RemoteConnectorService remoteConnectorService)
    {
        this.remoteConnectorService = remoteConnectorService;
    }

    /**
     * @param remoteAlfrescoTicketService The service to use to manage tickets
     */
    public void setRemoteAlfrescoTicketService(RemoteAlfrescoTicketService remoteAlfrescoTicketService)
    {
        this.remoteAlfrescoTicketService = remoteAlfrescoTicketService;
    }

    /**
     * @param cloudBaseUrl The base URL of the cloud instance
     */
    public void setCloudBaseUrl(String cloudBaseUrl)
    {
        // Validate that the URL looks vaguely appropriate
        if ((cloudBaseUrl.startsWith("http://") || cloudBaseUrl.startsWith("https://")) 
             && cloudBaseUrl.endsWith("/"))
        {
            // The URL passes a basic sanity check, proceed with it
        }
        else
        {
            throw new AlfrescoRuntimeException("Cloud URL must start with http:// or https://, and must end with a trailing /");
        }

        if(logger.isDebugEnabled())
        {
        	logger.debug("cloudBaseUrl changed :" + cloudBaseUrl);
        }
        
        // Save
        this.cloudBaseUrl = cloudBaseUrl;
        
        // Trigger re-init if required
        if (initialisationPerformed)
        {
            logger.debug("Triggering Re-Initialization of CloudConnector following runtime Cloud URL change");
            onBootstrap(null);
        }
    }
    
    /**
     * @param cloudKey The key to specify in the HTTP headers for cloud
     */
    private void setCloudKey(String cloudKey)
    {
        // if the cloud key has changed
        
        if(this.cloudKey != null && cloudKey != null)
        {
            if(!this.cloudKey.equals(cloudKey))
            {
                logger.debug("Cloud Key has changed");
                this.cloudKey = cloudKey;
                
                if (initialisationPerformed)
                {
                    logger.debug("Triggering Re-Initialization of CloudConnector following runtime Cloud Key change");
                    onBootstrap(null);
                }                
            }
            else
            {
                logger.debug("Key has not changed - do nothing");
            }
        }
        else
        {
            if(this.cloudKey == null && cloudKey == null)
            {
                logger.debug("Key is still null - do nothing");
            }
            else
            {
                this.cloudKey = cloudKey;
                
                if (initialisationPerformed)
                {
                    logger.debug("Triggering Re-Initialization of CloudConnector following runtime Cloud Key change");
                    onBootstrap(null);
                }                
            }
        }
        
    }
    
    public void init()
    {
        // Check we got everything we need
        PropertyCheck.mandatory(this, "remoteAlfrescoTicketService", remoteAlfrescoTicketService);
        PropertyCheck.mandatory(this, "remoteConnectorService", remoteConnectorService);
        PropertyCheck.mandatory(this, "cloudBaseUrl", cloudBaseUrl);
        PropertyCheck.mandatory(this, "keyProvider", keyProvider);
    }
    
    @Override
    protected void onShutdown(ApplicationEvent event) {}

    /**
     * During boostrap, we register the Cloud with the Remote Alfresco Ticket service.
     * This shouldn't occur before the system is setup, otherwise the Audit ends up
     *  in a sulk - see ALF-14271 for details
     */
    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialising cloud connector service");
        }
        
        if(keyProvider != null && !initialisationPerformed)
        {
            // get the cloud key for the first time
            cloudKey = keyProvider.getKey();
        }
        
        // Build the header set from the cloud key
        Map<String,String> headers = null;
        if (cloudKey != null)
        {
            headers = new HashMap<String, String>();
            headers.put(CLOUD_KEY_HEADER_NAME, cloudKey);
        }
        final Map<String,String> headersF = headers;
        
        // When performing the login, we need to talk to the default tenant
        final String baseUrlDefaultNetwork = cloudBaseUrl.replace("{network}", CLOUD_DEFAULT_NETWORK);
        
        // Register the cloud with the ticket service
        if (logger.isDebugEnabled())
        {
            logger.debug("register with the ticket system");
        }
        AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception
            {
                remoteAlfrescoTicketService.registerRemoteSystem(
                        CLOUD_REMOTE_SYSTEM_ID, baseUrlDefaultNetwork, headersF); 
                return null;
            }
        });
        
        // Mark us as having gone through initialisation
        // Will mean that future changes to the key or URL need a re-init
        initialisationPerformed = true;
    }

    public RemoteConnectorRequest buildCloudRequest(String relativeUrl, String network, String method)
    {
        // Build the template URL
        String url = cloudBaseUrl + relativeUrl;
        if (relativeUrl.startsWith("/"))
        {
            url = cloudBaseUrl + relativeUrl.substring(1);
        }
        
        // Specify the network
        if (network == null)
        {
            network = CLOUD_DEFAULT_NETWORK;
        }
        url = url.replace("{network}", network);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Local URL " + relativeUrl + " will be mapped to Cloud URL " + url);
        }
        
        // Wrap and return
        return remoteConnectorService.buildRequest(url, method);
    }

    /**
     * Executes the specified request against cloud, supplying credentials
     *  (in the form of tickets) as available, and return the response
     */
    public RemoteConnectorResponse executeCloudRequest(RemoteConnectorRequest request)
       throws IOException, AuthenticationException
    {
        RemoteAlfrescoTicketInfo ticket = null;
        boolean tryGuest = false;
        
        if (! AuthenticationUtil.isRunAsUserTheSystemUser())
        {
            // See if we can get a ticket for them, based on their credentials (if stored)
            try
            {
                ticket = remoteAlfrescoTicketService.getAlfrescoTicket(CLOUD_REMOTE_SYSTEM_ID);
                
                if (logger.isDebugEnabled())
                    logger.debug("Using ticket " + ticket + " to authenticate to the cloud");
            }
            catch (NoCredentialsFoundException e) 
            {
                // Sent the request as Guest instead
                ticket = new GuestRemoteAlfrescoTicketImpl();
                tryGuest = true;
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("No cloud credentials found for the current user, " + 
                            AuthenticationUtil.getFullyAuthenticatedUser() + ", attempting request as Guest");
                }
            }
            
            // Pass the ticket details along with the request
            request.addRequestHeader(
                    new Header("Authorization", ticket.getAsHTTPAuthorization())
            );
        }
        
        // Add the cloud key if needed
        if (cloudKey != null)
        {
            request.addRequestHeader(
                    new Header(CLOUD_KEY_HEADER_NAME, cloudKey)
            );
        }
        
        try
        {
            // Perform the actual request
            return remoteConnectorService.executeRequest(request);
        }
        catch (AuthenticationException authEx)
        {
            if ((ticket != null) && (! tryGuest))
            {
                // Retry once for invalid auth (in case ticket is invalid)
                ticket = remoteAlfrescoTicketService.refetchAlfrescoTicket(CLOUD_REMOTE_SYSTEM_ID);
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Using re-fetched ticket " + ticket + " to authenticate to the cloud");
                }
                
                // Update the ticket details in the request
                request.addRequestHeader(new Header("Authorization", ticket.getAsHTTPAuthorization()));
                
                return remoteConnectorService.executeRequest(request);
            }
            throw authEx;
        }
    }
    
    
    /**
     * Validates and stores the cloud for the current user
     * 
     * @throws AuthenticationException If the credentials are invalid
     * @throws RemoteSystemUnavailableException If the remote system is unavailable
     */
    public BaseCredentialsInfo storeCloudCredentials(String username, String password)
       throws AuthenticationException, RemoteSystemUnavailableException
    {
        return remoteAlfrescoTicketService.storeRemoteCredentials(CLOUD_REMOTE_SYSTEM_ID, username, password);
    }

    /**
     * Retrieves the cloud credentials (if any) for the current user
     * 
     * @return The current user's remote credentials, or null if they don't have any
     */
    public BaseCredentialsInfo getCloudCredentials()
    {
        return remoteAlfrescoTicketService.getRemoteCredentials(CLOUD_REMOTE_SYSTEM_ID);
    }
    
    /**
     * Retrieves the cloud credentials (if any) for the current user
     * 
     * @return The current user's remote credentials, or null if they don't have any
     */
    public boolean deleteCloudCredentials()
    {
        return remoteAlfrescoTicketService.deleteRemoteCredentials(CLOUD_REMOTE_SYSTEM_ID);
    }

    public void setKeyProvider(KeyProvider keyProvider)
    {
        if(this.keyProvider != null)
        {
            this.keyProvider.removeListener(this);
        }
        this.keyProvider = keyProvider;
        this.keyProvider.addListener(this);
       
    }

    public KeyProvider getKeyProvider()
    {
        return keyProvider;
    }

    @Override
    public void onChangeKey(String newKey)
    {
        setCloudKey(newKey);
    }
}
