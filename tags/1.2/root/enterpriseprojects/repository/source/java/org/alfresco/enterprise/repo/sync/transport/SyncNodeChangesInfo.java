/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * This class holds all the details about the changes to a single Node,
 *  which are to be synced in one go.
 * 
 * Potentially multiple changes to the source node will go into one
 *  set of changes to be synced, depending on the interval.
 *  
 * TODO Checksums
 *  
 * @author Nick Burch
 * @since CloudSync
 */
public interface SyncNodeChangesInfo
{
    /**
     * The NodeRef of the Node to be synced, on this system.
     * During transport, this will be the source NodeRef, and will
     *  become the Remote NodeRef on the other end. 
     */
    NodeRef getLocalNodeRef();
    /**
     * The NodeRef of the Node to be synced, on the other system. 
     * During transport, this will be the target NodeRef, and will
     *  become the Local NodeRef on the other end. 
     */
    NodeRef getRemoteNodeRef();
    
    /**
     * The path of the Node to be synced, on this system. This
     *  will be / separated, and will include the site, folders
     *  and node name.
     */
    String getLocalPath();
    
    /**
     * The path of the Node to be synced, on the other system. This
     *  will be / separated, and will include the site, folders
     *  and node name.
     */
    String getRemotePath();
    
    /**
     * The primary parent of the node on this system. 
     */
    NodeRef getLocalParentNodeRef();
    /**
     * The primary parent of the node on the remote system,
     *  if known. (This will often be null until transported to the
     *  other system, but is always known for Create calls)
     */
    NodeRef getRemoteParentNodeRef();
    
    /**
     * The GUID of the Sync Set that this sync belongs to. This
     * GUID is the same on both ends.
     */
    String getSyncSetGUID();
    
    /**
     * The type of the node.
     * Both systems need the same definition of this type, so currently
     *  only built in types may be synced.
     */
    QName getType();
    
    /**
     * Is this node sync directly or not.
     * File sync nodes are direct. For folder sync, all nodes are indirect except the root source folder
     * which was selected to setup the sync.
     */
    Boolean getDirectSync();
    
    /**
     * The cm:modifiedAt datetime of the node on this system. 
     */
    Date getLocalModifiedAt();
    /**
     * The cm:modifiedAt datetime of the node on the remote system,
     *  if known. (This will often be null until transported to the
     *  other system)
     */
    Date getRemoteModifiedAt();
    
    /**
     * The version label of the current version of the node on
     *  this system, or null if it isn't versioned locally.
     */
    String getLocalVersionLabel();
    /**
     * The version label of the current version of the node on
     *  the remote system, if known. (This will often be null until 
     *  transported to the other system) 
     */
    String getRemoteVersionLabel();

    /**
     * The (syncable) aspects added to the node
     */
    Set<QName> getAspectsAdded();
    /**
     * The (syncable) aspects removed from the node
     */
    Set<QName> getAspectsRemoved();
    
    /**
     * The (syncable) non-content property changes to the node. Additions
     *  and Updates are both represented with the Property QName
     *  and the new value. Removals are represented as the Property QName
     *  with a null value, much as with the NodeService.
     */
    Map<QName,Serializable> getPropertyUpdates();

    /**
     * The (syncable) content property changes to the node. Additions
     *  and Updates are both represented with the Property QName
     *  and the new value. Removals are represented as the Property QName
     *  with a null value, much as with the NodeService.
     * Content Properties should only be expressed here, they should
     *  not be placed in {@link #getPropertyUpdates()}.
     */
    Map<QName, CloudSyncContent> getContentUpdates();
    
    /**
     * The {@link AuditService#clearAudit(List) audit IDs} represented by this change.
     * These IDs refer to the audit table in the Alfresco from which the changes originated.
     * TODO Switch to using the more opaque {@link #getAuditToken()}
     */
    @Deprecated
    List<Long> getLocalAuditIds();
    
    /**
     * The Audit Token
     * @return the auditToken
     */
    AuditToken getAuditToken();
}
