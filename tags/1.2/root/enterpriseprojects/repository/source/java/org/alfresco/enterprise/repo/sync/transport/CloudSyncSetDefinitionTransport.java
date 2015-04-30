/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport;

import java.util.List;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncService;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Service for creating, modifying, deleting and querying {@link SyncSetDefinition}
 *  instances on the Cloud. 
 * 
 * Builds on top of {@link CloudConnectorService}, {@link SyncService} and
 *  {@link SyncAdminService}
 * 
 * @author Nick Burch, Jan Vonka, Neil McErlean
 * @since 4.1
 */
public interface CloudSyncSetDefinitionTransport
{
    /**
     * Creates a SSD with the specified SSD ID on the remote system, 
     *  associated with our local Repository ID
     *  
     * @param ssdId The ID of SSD to create, will match with the local system
     * @param targetFolderNodeRef The ???
     * @param sourceRepoId The ID of our Repository
     * @param isDeleteOnCloud
     * @param isDeleteOnPrem
     * @param cloudNetwork
     */
    void createSSD(String ssdId, NodeRef targetFolderNodeRef, boolean includeSubFolders, String sourceRepoId, boolean isDeleteOnCloud, boolean isDeleteOnPrem, String cloudNetwork) throws
       AuthenticationException, RemoteSystemUnavailableException;
    
    /**
     * Deletes a SSD on the remote system
     * 
     * @param ssdId The ID of the SSD to delete, will match with the local system
     */
    void deleteSSD(String ssdId, String cloudNetwork) throws 
       AuthenticationException, RemoteSystemUnavailableException, NoSuchSyncSetDefinitionException;

    /**
     * Fetches the list of SSD IDs of all the Sync Sets which have changes 
     *  on the cloud, for the specified repository, across all cloud networks 
     *  
     * @param repoId The repository ID to query for
     * @return A list of SSD IDs
     */
    List<String> pullChangedSSDs(String repoId) throws
       AuthenticationException, RemoteSystemUnavailableException;

    /**
     * Fetches the list of remote NodeRefs for a given SSD for which
     *  changes exist on the cloud. These can then be pulled over in 
     *  detail with {@link CloudSyncMemberNodeTransport#pullSyncChange(SyncNodeChangesInfo, String)}
     *
     * @param ssd The Sync Set ID to query for
     */
    List<NodeRef> pullChangedNodesForSSD(String ssd, String cloudNetwork) throws
       AuthenticationException, RemoteSystemUnavailableException, NoSuchSyncSetDefinitionException;
}