/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import java.util.ConcurrentModificationException;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.web.scripts.sync.transport.CloudSyncMonitor.CloudSyncMonitorCtx;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the Cloud Sync push.post web script.
 * 
 * It handles decoding the sync push, checking the sync is allowed
 *  at the moment (early on), and then passes the sync details off
 *  to be processed
 * 
 * Note - requires that multi-part form processing is disabled
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class CloudSyncPushPost extends AbstractCloudSyncPostWebScript
{
    private CloudSyncMonitor monitor;
    
    public void setCloudSyncMonitor(CloudSyncMonitor monitor)
    {
        this.monitor = monitor;
    }

    @Override
    protected boolean isContentExpected()
    {
        // Content can be supplied, but doesn't have to be
        // eg if no content properties are changed, it'll be skipped
        return true;
    }

    @Override
    protected NodeRef performSyncAction(WebScriptRequest req, SyncNodeChangesInfo changes)
       throws ConcurrentModificationException
    {
        CloudSyncMonitorCtx ctx = monitor.pushStarted(changes);
        // Have the sync changes performed, or an exception raised if the
        //  cloud has had changes locally too
        try
        {
            NodeRef result = cloudSyncMemberNodeTransport.fetchLocalDetailsAndApply(changes, true);
            monitor.pushComplete(ctx);
            return result;
        }
        catch (ConcurrentModificationException ex)
        {
            monitor.pushFailed(ctx, ex);
            throw ex;
        }
        catch (RuntimeException ex)
        {
            monitor.pushFailed(ctx, ex);
            throw ex;
        }
    }

    @Override
    protected Map<String, Object> populateModel(Map<String, Object> coreModel, WebScriptRequest req,
            SyncNodeChangesInfo changes)
    {
        // No changes needed
        return coreModel;
    }
}