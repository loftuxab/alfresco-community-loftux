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
 *  is configurable as to if a sync can occur or not. It also tracks
 *  when the methods are called, which is helpful for unit testing
 *
 * @author Nick Burch
 * @since TODO
 */
public class TestControllableCloudSyncOnCloudService implements CloudSyncOnCloudService
{
    private boolean calledOccur;
    private boolean calledProceed;
    private boolean calledNextInterval;
    
    private boolean canOccur = true;
    private boolean canProceed = true;
    private int nextInterval = 0;
    
    public TestControllableCloudSyncOnCloudService()
    {
        reset();
    }
    public TestControllableCloudSyncOnCloudService(boolean canOccur, boolean canProceed, int nextInterval)
    {
        this();
        this.canOccur = canOccur;
        this.canProceed = canProceed;
        this.nextInterval = nextInterval;
    }
    
    /**
     * Resets the called flags
     */
    public void reset()
    {
        calledOccur = false;
        calledProceed = false;
        calledNextInterval = false;
    }
    
    public void setCanOccur(boolean canOccur)
    {
        this.canOccur = canOccur;
    }
    public void setCanProceed(boolean canProceed)
    {
        this.canProceed = canProceed;
    }
    public void setNextInterval(int nextInterval)
    {
        this.nextInterval = nextInterval;
    }
    
    public boolean wasCanOccurCalled()
    {
        return calledOccur;
    }
    public boolean wasCanProceedCalled()
    {
        return calledProceed;
    }
    public boolean wasNextIntervalCalled()
    {
        return calledNextInterval;
    }

    @Override
    public boolean canCloudSyncOccur()
    {
        calledOccur = true;
        return canOccur;
    }

    @Override
    public boolean canCloudSyncProceed(SyncNodeChangesInfo changes)
    {
        calledProceed = true;
        return canProceed;
    }

    @Override
    public int getMinimumNextSyncIntervalSeconds()
    {
        calledNextInterval = true;
        return nextInterval;
    }
}