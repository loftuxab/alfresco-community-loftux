/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport;

import java.io.IOException;
import java.util.ConcurrentModificationException;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncNodeException;
import org.alfresco.enterprise.repo.sync.SyncService;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Service for push-sending and pull-receiving {@link SyncNodeChangesInfo} instances
 *  with the Cloud. 
 * 
 * The push and pull methods on this service are only used on-premise, never 
 *  on the cloud. They are typically called from the actions that drive the
 *  sync.
 * In addition, a number of helper methods for encoding and decoding the
 *  transport data are provided, which are used both by on-premise and
 *  cloud code. For cloud, they are especially used by the webscripts that
 *  accept a push, and handle a pull.
 * 
 * Builds on top of {@link CloudConnectorService} and {@link SyncService}
 * 
 * @author Nick Burch
 * @since TODO
 */
public interface CloudSyncMemberNodeTransport
{
    /**
     * Pushes a node to the cloud for the first time, as the current user.
     * The node will be created on the cloud in the remote parent folder as
     *  specified on the {@link SyncNodeChangesInfo}, and associated with
     *  the SyncSet.
     *
     * @throws AuthenticationException
     * @throws RemoteSystemUnavailableException
     * @throws SyncNodeException, unable to sync the node.
     * 
     * @return the NodeRef of the new node on the cloud
     */
    NodeRef pushSyncInitial(SyncNodeChangesInfo syncNode, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, AuthenticationException, RemoteSystemUnavailableException;
    
    /**
     * Pushes the changes to the node to the cloud, as the current user.
     */
    void pushSyncChange(SyncNodeChangesInfo syncNode, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException;
   
    /**
     * Tells the cloud to delete the sync node, following either its local
     *  deletion, or its removal from the sync set with the remote delete flag. 
     */
    void pushSyncDelete(SyncNodeChangesInfo syncNode, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException;
    
    /**
     * Tells the cloud to remove the node from its sync set, but to retain the
     *  node.
     */
    void pushUnSync(SyncNodeChangesInfo syncNode, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException;
    
    /**
     * Pulls down changes to this node from the cloud, if any.
     * 
     * TODO Are these the right parameters?
     * @param stubLocal A stub {@link SyncNodeChangesInfo} holding the remote noderef, plus time and checksum
     * @return The {@link SyncNodeChangesInfo} representing changes made on the cloud, or null if the two are already in sync
     */
    SyncNodeChangesInfo pullSyncChange(SyncNodeChangesInfo stubLocal, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException;
    
    /**
     * Confirms that the pulled node/nodes have been applied to the local repo.
     * @param things The Audit Token(s) to confirm the pull of
     * @param cloudNetwork
     * @return
     * @throws AuthenticationException
     * @throws RemoteSystemUnavailableException
     */
    void confirmPull(AuditToken[] things, String cloudNetwork) throws
        AuthenticationException, RemoteSystemUnavailableException;
    
    
    /**
     * Pushes information about a conflict.
     * @param stubLocal containing details of the audit entries that have been processed.
     * @param cloudNetwork
     * @return
     * @throws AuthenticationException
     * @throws RemoteSystemUnavailableException
     * TODO  Probably wrong objects in the wrong place.
     * TODO * 17 This is the first stab for the short term sprint goal.
     */
    void pushConflictDetected(SyncNodeChangesInfo stubLocal, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, AuthenticationException, RemoteSystemUnavailableException;
    
    
    // ------------------------------------------------------------------
    //  These are for use by webscripts etc at the end that is pushed to 
    // ------------------------------------------------------------------
    
    /**
     * Decodes the JSON part of the multipart Sync Stream, returning a
     *  {@link SyncNodeChangesInfo} object with the details
     */
    SyncNodeChangesInfo decodeMainJSON(FileItemStream jsonPart) throws IOException;
    
//    /**
//     * Decodes a Content part of the multipart Sync Stream, returning
//     *  the Property and a temporary reader to it's Content
//     *  
//     * @param tempFilesList A list to which any temporary files are added to  
//     */
//    //Pair<QName, ContentReader> decodeContent(FileItemStream contentPart, List<File> tempFilesList) throws IOException;
    
    /**
     * Decodes a Content part of the multipart Sync Stream
     * @param contentPart
     */ 
    public CloudSyncContent decodeContent(FileItemStream contentPart) throws IOException;
    
    
    /**
     * Decodes the HTTP Paramters of a Pull Request, to yield a stub
     *  {@link SyncNodeChangesInfo} object with the details of the node to be pulled
     *  
     * @return A stub {@link SyncNodeChangesInfo}
     */
    SyncNodeChangesInfo decodePullParameters(WebScriptRequest request);
    
    /**
     * Encodes the given {@link SyncNodeChangesInfo} object into a MultiPart
     *  request, with JSON and Content parts as required, suitable for sending
     *  to the other system.
     */
    MultipartRequestEntity encodeSyncChanges(SyncNodeChangesInfo syncNode);
    
    /**
     * Begins a retrying read/write transaction, fetches the latest details of the remaining 
     *  local details (modified date, version label etc), then requests the sync changes be actioned.
     * The actual work will be done by appropriate methods on {@link SyncService}, once the
     *  local details have been populated
     */
    NodeRef fetchLocalDetailsAndApply(SyncNodeChangesInfo syncNode, boolean isOnCloud) throws ConcurrentModificationException;
    
    /**
     * Begins a retrying read/write transaction, fetches the latest details of the remaining 
     *  local details (modified date, version label etc), then requests the un-sync/delete be performed.
     * The actual work will be done by appropriate methods on {@link SyncService}, once the
     *  local details have been populated
     */
    void fetchLocalDetailsAndUnSync(SyncNodeChangesInfo syncNode, boolean deleteOnUnSync) throws ConcurrentModificationException;
    
    // TODO Something common for encoding that can be used on-premise
    //  during the push-pull, and cloud side for accept-send
}