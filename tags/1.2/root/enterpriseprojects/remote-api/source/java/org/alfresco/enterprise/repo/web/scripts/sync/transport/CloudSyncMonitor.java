/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import java.io.Serializable;

import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;

/**
 * Interface for monitoring the state of sync push and pull requests.
 *
 * @author Alex Miller
 */
public interface CloudSyncMonitor
{
    /**
     * Monitoring context.
     */
    public interface CloudSyncMonitorCtx extends Serializable
    {

        /**
         * Get the start time of the request.
         * 
         * @return the start time of the request
         */
        long getStartTime();

    }

    /**
     * Record the start of a pull request.
     *
     * @param stubInfo for pull
     * @return Context object for recording additional information about the request and linking
     *     subsequent completion events.
     */
    CloudSyncMonitorCtx pullStarted(SyncNodeChangesInfo stubInfo);

    /**
     * Record the failure of a pull request.
     *
     * @param ctx Context object returned by previous call to {@link #pullStarted(SyncNodeChangesInfo)}
     * @param ex Exception causing the failure.
     */
    void pullFailed(CloudSyncMonitorCtx ctx, Exception ex);

    /**
     * Record the completion of a pull request.
     *
     * @param ctx Context object returned by previous call to {@link #pullStarted(SyncNodeChangesInfo)}
     */
    void pullComplete(CloudSyncMonitorCtx ctx);

    /**
     * Record the start of push request.
     *
     * @param changes for push
     * @return Context object for linking subsequent completion events.
     */
    CloudSyncMonitorCtx pushStarted(SyncNodeChangesInfo changes);

    /**
     * Record the successful completion of a push request.
     *
     * @param ctx Context object, returned by previous call to {@link #pushStarted(SyncNodeChangesInfo)}
     */
    void pushComplete(CloudSyncMonitorCtx ctx);

    /**
     * Record the failure of a push request.
     * 
     * @param ctx Context object object, returned by previous call to {@link #pushStarted(SyncNodeChangesInfo)}
     * @param ex Exception causing failure
     */
    void pushFailed(CloudSyncMonitorCtx ctx, Exception ex);
}
