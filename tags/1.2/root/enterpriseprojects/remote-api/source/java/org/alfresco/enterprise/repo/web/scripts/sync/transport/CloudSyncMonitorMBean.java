/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

/**
 * JMX MBean interface for exposing metrics about hybrid sync
 *
 * @author Alex Miller
 */
public interface CloudSyncMonitorMBean
{
    /**
     * Get the number of pull requests currently running.
     *
     * @return the number of pull requests currently running
     */
    long getCurrentPullRequests();
    
    /**
     * Get the total number of pull requests made.
     * 
     * @return the total number of pull request made
     */
    long getTotalPullRequestCount();
    
    /**
     * Get the average execution time for pull requests.
     * 
     * @return the average execution time for pull requests
     */
    long getAveragePullRequestTime();
    
    /**
     * Get the number of pull requests that failed.
     *
     * @return the number of failed pull requests
     */
    long getPullRequestFailureCount();

    /**
     * Get the number of pull requests that succeeded.
     * 
     * @return the number of successful pull requests.
     */
    long getPullRequestSuccessCount();

    /**
     * Get the number of push requests currently running.
     *
     * @return the number of push requests currently running
     */
    long getCurrentPushRequests();
    
    /**
     * Get the total number of push requests made.
     * 
     * @return the total number of push request made
     */
    long getTotalPushRequestCount();
    
    /**
     * Get the average execution time for push requests.
     * 
     * @return the average execution time for pull requests
     */
    long getAveragePushRequestTime();
    
    /**
     * Get the number of push requests that failed.
     *
     * @return the number of failed push requests
     */
    long getPushRequestFailureCount();

    /**
     * Get the number of push requests that succeeded.
     * 
     * @return the number of successful push requests.
     */
    long getPushRequestSuccessCount();
}
