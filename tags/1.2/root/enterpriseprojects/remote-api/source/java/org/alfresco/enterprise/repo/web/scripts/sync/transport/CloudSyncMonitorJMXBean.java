/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

/**
 * CloudSyncMonitorMBean implementation
 * 
 * @author mrogers
 */
public class CloudSyncMonitorJMXBean implements CloudSyncMonitorMBean
{

	// Wrapped service
	private CloudSyncMonitorImpl monitor;
	
	@Override
	public long getCurrentPullRequests() 
	{
		return monitor.getCurrentPullRequests();
	}

	@Override
	public long getTotalPullRequestCount() 
	{
		return monitor.getTotalPullRequestCount();
	}

	@Override
	public long getAveragePullRequestTime() 
	{
		return monitor.getAveragePullRequestTime();
	}

	@Override
	public long getPullRequestFailureCount() 
	{
		return monitor.getPullRequestFailureCount();
	}

	@Override
	public long getPullRequestSuccessCount() 
	{
		return monitor.getPullRequestSuccessCount();
	}

	@Override
	public long getCurrentPushRequests() 
	{
		return monitor.getCurrentPushRequests();
	}

	@Override
	public long getTotalPushRequestCount() 
	{
		return monitor.getTotalPushRequestCount();
	}

	@Override
	public long getAveragePushRequestTime() 
	{
		return monitor.getAveragePullRequestTime();
	}

	@Override
	public long getPushRequestFailureCount() 
	{
		return monitor.getPushRequestFailureCount();
	}

	@Override
	public long getPushRequestSuccessCount() 
	{
		return monitor.getPushRequestSuccessCount();
	}

	public void setCloudSyncMonitor(CloudSyncMonitorImpl monitor) {
		this.monitor = monitor;
	}

	public CloudSyncMonitorImpl getCloudSyncMonitor() 
	{
		return monitor;
	}
}
