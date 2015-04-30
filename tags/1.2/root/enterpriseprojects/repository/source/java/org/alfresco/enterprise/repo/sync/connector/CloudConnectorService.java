/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.connector;

import java.io.IOException;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorService;
import org.alfresco.service.cmr.remotecredentials.BaseCredentialsInfo;
import org.alfresco.service.cmr.remotecredentials.RemoteCredentialsService;
import org.alfresco.service.cmr.remoteticket.RemoteAlfrescoTicketService;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;

/**
 * Service for talking to the Cloud system. 
 * 
 * Builds on top of {@link RemoteCredentialsService}, {@link RemoteAlfrescoTicketService}
 *  and {@link RemoteConnectorService} to provide an easy way to talk to the cloud.
 *  
 * @author Nick Burch
 * @since TODO
 */
public interface CloudConnectorService
{
    /**
     * Builds a new cloud Request object, to talk to the given relative 
     *  cloud url, with the supplied method
     */
    RemoteConnectorRequest buildCloudRequest(String relativeUrl, String network, String method);

    /**
     * Executes the specified request against cloud, supplying credentials
     *  (in the form of tickets) as available, and return the response
     */
    RemoteConnectorResponse executeCloudRequest(RemoteConnectorRequest request) throws IOException, AuthenticationException;
    
    
    /**
     * Validates and stores the cloud credentials for the current user
     * 
     * @throws AuthenticationException If the credentials are invalid
     * @throws RemoteSystemUnavailableException If the remote system is unavailable
     */
    BaseCredentialsInfo storeCloudCredentials(String username, String password)
       throws AuthenticationException, RemoteSystemUnavailableException;
    
    /**
     * Deletes the cloud credentials for the current user
     *  
     * @return Whether credentials were found to delete
     */
    boolean deleteCloudCredentials();

    /**
     * Retrieves the cloud credentials (if any) for the current user
     * 
     * @return The current user's remote credentials, or null if they don't have any
     */
    BaseCredentialsInfo getCloudCredentials();
}
