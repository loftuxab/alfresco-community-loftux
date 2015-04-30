/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.util.ConcurrentModificationException;
import java.util.List;

import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * This service is responsible for managing the pushing and pulling of sync data between Alfresco instances.
 * 
 * @author mrogers
 */
public interface SyncService    
{
    
    /**
     * Create a new sync'd node on this system.
     * 
     * @param newNode containing details of the new node to create.
     * @param isOnCloud is this a create on cloud?
     * @return the node ref of the new node.
     * @throws SyncNodeException, a node with the same name already exists.
     */
    public NodeRef create(SyncNodeChangesInfo newNode, boolean isOnCloud) throws SyncNodeException;
    
    /**
     * Delete a sync'd node from this system.  
     * @throws ConcurrentModificationException the remove cannot be applied due to a conflicting update.
     * @param changes containing localNodeRef node to remove from sync set.
     * @param force ignore conflicts and go ahead and delete regardless of any conflicts
     */
    public void delete(SyncNodeChangesInfo changes, boolean force) throws ConcurrentModificationException;
    
    /**
     * Remove a sync'd node from a sync set but leave the node on this system.
     * <p>
     * After calling this method the node will still exist but no longer belong to the sync set. 
     * @param changes containing localNodeRef node to remove from sync set.
     * @param force - ignore conflicts and go ahead and remove from syncSet regardless of any conflicts
     * @throws ConcurrentModificationException the remove cannot be applied due to a conflicting update.
     */
    public void removeFromSyncSet(SyncNodeChangesInfo changes, boolean force) throws ConcurrentModificationException;
    
    /**
     * Update the local system with the specified change.  
     * 
     * @param change the change to apply.
     * 
     * @throws ConcurrentModificationException the update cannot be applied due to a conflicting update.
     * @throws sync set defintion not found
     * @throws SyncNodeException - unable to update the node with the specified changes
     */
    public void update(SyncNodeChangesInfo change) throws ConcurrentModificationException, SyncNodeException;
    
    /**
     * Update the local system with the specified change, regardless of any conflict  
     * 
     * @param change the change to apply.
     * @throws sync set defintion not found
     * @throws SyncNodeException - unable to update the node with the specified changes
     */
    public void forceUpdate(SyncNodeChangesInfo change) throws SyncNodeException;
    
    /**
     * Fetches the details of all the pending changes for a given Node
     *  (as identified by the stub {@link SyncNodeChangesInfo}), and returns
     *  a pully populated {@link SyncNodeChangesInfo} object suitable for
     *  returning from a Pull call.
     * The returned {@link SyncNodeChangesInfo} will include an {@link AuditToken},
     *  so that the change can be confirmed once applied on the other system.
     */
    public SyncNodeChangesInfo fetchForPull(SyncNodeChangesInfo stub) throws
       NoSuchSyncSetDefinitionException, SyncNodeException;
    
    /**
     * @deprecated
     * Deal with a conflicted node.   The conflict may be resolved automatically by the system.
     *
     * Details are persisted in the repo for manual conflict resolution.
     * @see org.alfresco.enterprise.repo.sync.ConflictResponse
     * @return ConflictResponse, what happened, Was the conflict dealt with automatically, and further details.
     *   * @throws SyncNodeException - unable to update the node with the specified changes
     */
    public ConflictResponse dealWithConflictInAppropriateManner(SyncNodeChangesInfo conflict) throws SyncNodeException;
    
    /**
     * Request sync.
     *
     * @param memberNodeRefs
     */
    public void requestSync(List<NodeRef> memberNodeRefs);
}
