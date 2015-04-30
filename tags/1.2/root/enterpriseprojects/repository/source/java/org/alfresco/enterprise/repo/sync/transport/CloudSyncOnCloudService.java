/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport;

import org.alfresco.enterprise.repo.sync.SyncService;


/**
 * The Cloud-side service that handles cloud specific parts of the
 *  sync.
 * This includes checking to see if a sync is allowed, how often
 *  a sync should occur etc.
 * It doesn't do anything that is common (eg pending changes is
 *  done via the {@link SyncService})
 *
 * TODO This should be THOR only
 *
 * @author Nick Burch
 * @since TODO
 */
public interface CloudSyncOnCloudService
{
    /**
     * Checks if a cloud sync is allowed for the current tenant
     *  and the current user. Called when a request for a Cloud
     *  Sync is received, before processing.
     */
    boolean canCloudSyncOccur();
    
    /**
     * Checks to see if a cloud sync is allowed to continue,
     *  once the change metadata is received, but before the
     *  content has been accepted.
     * 
     * The {@link SyncNodeChangesInfo} will be lacking the following:
     *  - {@link SyncNodeChangesInfo#getLocalModifiedAt()} 
     *  - {@link SyncNodeChangesInfo#getLocalVersionLabel()} 
     *  - {@link SyncNodeChangesInfo#getContentUpdates()} 
     */
    boolean canCloudSyncProceed(SyncNodeChangesInfo changes);
    
    /**
     * Returns the minimum time until the next Cloud Sync may be
     *  performed, for the current tenant and user. 
     */
    int getMinimumNextSyncIntervalSeconds();
}