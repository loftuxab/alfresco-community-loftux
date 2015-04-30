/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;

/**
 * {@link CloudSyncMonitor} implementation which collects metrics about sync requests and exposes them via JMX.
 *
 * @author Alex Miller
 */
public class CloudSyncMonitorImpl implements CloudSyncMonitor, CloudSyncMonitorMBean
{
    private static class CloudSyncMonitorCtxImpl implements CloudSyncMonitorCtx
    {
        private static final long serialVersionUID = -7378395636991488086L;

        private Date startTime = new Date();

        @Override
        public long getStartTime()
        {
            return startTime.getTime();
        }
    }

    private ReentrantReadWriteLock pullLock = new ReentrantReadWriteLock();

    private long currentPullRequestCount = 0;

    private long pullSuccessCount = 0;
    private long pullFailureCount = 0;
    private long pullExecutionTime = 0;


    private ReentrantReadWriteLock pushLock = new ReentrantReadWriteLock();

    private long currentPushRequestCount = 0;

    private long pushSuccessCount = 0;
    private long pushFailureCount = 0;
    private long pushExecutionTime = 0;

    @Override
    public CloudSyncMonitorCtx pullStarted(SyncNodeChangesInfo stubInfo)
    {
        CloudSyncMonitorCtxImpl context = new CloudSyncMonitorCtxImpl();
        pullLock.writeLock().lock();
        try
        {
            currentPullRequestCount++;
            return context;
        }
        finally
        {
            pullLock.writeLock().unlock();
        }
    }

    @Override
    public void pullFailed(CloudSyncMonitorCtx ctx, Exception ex)
    {
        long executionTime = System.currentTimeMillis() - ctx.getStartTime();
        pullLock.writeLock().lock();
        try
        {
            currentPullRequestCount--;
            pullExecutionTime += executionTime;
            pullFailureCount++;
        }
        finally
        {
            pullLock.writeLock().unlock();
        }
    }

    @Override
    public void pullComplete(CloudSyncMonitorCtx ctx)
    {
        long executionTime = System.currentTimeMillis() - ctx.getStartTime();
        pullLock.writeLock().lock();
        try
        {
            currentPullRequestCount--;
            pullExecutionTime += executionTime;
            pullSuccessCount++;
        }
        finally
        {
            pullLock.writeLock().unlock();
        }
    }

    @Override
    public CloudSyncMonitorCtx pushStarted(SyncNodeChangesInfo changes)
    {
        CloudSyncMonitorCtxImpl context = new CloudSyncMonitorCtxImpl();
        pushLock.writeLock().lock();
        try
        {
            currentPushRequestCount++;
            return context;
        }
        finally
        {
            pushLock.writeLock().unlock();
        }
    }

    @Override
    public void pushComplete(CloudSyncMonitorCtx ctx)
    {
        long executionTime = System.currentTimeMillis() - ctx.getStartTime();
        pushLock.writeLock().lock();
        try
        {
            currentPushRequestCount--;
            pushExecutionTime += executionTime;
            pushSuccessCount++;
        }
        finally
        {
            pushLock.writeLock().unlock();
        }
    }

    @Override
    public final void pushFailed(final CloudSyncMonitorCtx ctx, final Exception ex)
    {
        long executionTime = System.currentTimeMillis() - ctx.getStartTime();
        pushLock.writeLock().lock();
        try
        {
            currentPushRequestCount--;
            pushExecutionTime += executionTime;
            pushFailureCount++;
        }
        finally
        {
            pushLock.writeLock().unlock();
        }
    }

    @Override
    public final long getCurrentPullRequests()
    {
        pullLock.readLock().lock();
        try
        {
            return currentPullRequestCount;
        }
        finally
        {
            pullLock.readLock().unlock();
        }
    }

    @Override
    public long getTotalPullRequestCount()
    {
        pullLock.readLock().lock();
        try
        {
            return currentPullRequestCount + pullSuccessCount + pullFailureCount;
        }
        finally
        {
            pullLock.readLock().unlock();
        }
    }

    @Override
    public long getAveragePullRequestTime()
    {
        pullLock.readLock().lock();
        try
        {
        	long total = pullSuccessCount + pullFailureCount;
        	
        	if(total > 0)
        	{
        		return  pullExecutionTime / (total);
        	}
        	return 0;
        }
        finally
        {
            pullLock.readLock().unlock();
        }
    }

    @Override
    public long getPullRequestFailureCount()
    {
        pullLock.readLock().lock();
        try
        {
            return  pullFailureCount;
        }
        finally
        {
            pullLock.readLock().unlock();
        }
    }

    @Override
    public long getPullRequestSuccessCount()
    {
        pullLock.readLock().lock();
        try
        {
            return  pullSuccessCount;
        }
        finally
        {
            pullLock.readLock().unlock();
        }
    }

    @Override
    public long getCurrentPushRequests()
    {
        pushLock.readLock().lock();
        try
        {
            return currentPushRequestCount;
        }
        finally
        {
            pushLock.readLock().unlock();
        }
    }

    @Override
    public long getTotalPushRequestCount()
    {
        pushLock.readLock().lock();
        try
        {
            return currentPullRequestCount + pushSuccessCount + pushFailureCount;
        }
        finally
        {
            pushLock.readLock().unlock();
        }
    }

    @Override
    public long getAveragePushRequestTime()
    {
        pushLock.readLock().lock();
        try
        {
        	long total = pushSuccessCount + pushFailureCount;
        	
        	if(total > 0)
        	{
        		return pushExecutionTime / (total);
        	}
        	return 0;
        }
        finally
        {
            pushLock.readLock().unlock();
        }
    }

    @Override
    public long getPushRequestFailureCount()
    {
        pushLock.readLock().lock();
        try
        {
            return pushFailureCount;
        }
        finally
        {
            pushLock.readLock().unlock();
        }
    }

    @Override
    public long getPushRequestSuccessCount()
    {
        pushLock.readLock().lock();
        try
        {
            return pushSuccessCount;
        }
        finally
        {
            pushLock.readLock().unlock();
        }
    }

}
