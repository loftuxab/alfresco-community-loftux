/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import org.alfresco.enterprise.repo.sync.transport.CloudSyncOnCloudService;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;

/**
 * A dummy implementation of {@link CloudSyncOnCloudService} which
 *  always allows the sync to occur 
 *
 * TODO This should be THOR only
 *
 * @author Nick Burch
 * @since TODO
 */
public class AlwaysProceedCloudSyncOnCloudService implements CloudSyncOnCloudService
{
    public boolean canCloudSyncOccur()
    {
        return true;
    }
    
    public boolean canCloudSyncProceed(SyncNodeChangesInfo changes)
    {
        return true;
    }
    
    public int getMinimumNextSyncIntervalSeconds()
    {
        return 0;
    }
}