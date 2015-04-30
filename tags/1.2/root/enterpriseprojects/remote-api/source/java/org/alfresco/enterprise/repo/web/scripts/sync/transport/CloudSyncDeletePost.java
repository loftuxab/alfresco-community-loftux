/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import java.util.ConcurrentModificationException;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the Cloud Sync delete.post web script.
 * 
 * It handles working out what the node is to be un-synced, if it
 *  should also be deleted, and information to detect a conflict
 *  (eg delete requested but local edit has occurred). It then passes
 *  the delete details off to be processed.
 * 
 * Note - requires that multi-part form processing is disabled
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class CloudSyncDeletePost extends AbstractCloudSyncPostWebScript
{
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(CloudSyncDeletePost.class);
    
    @Override
    protected boolean isContentExpected()
    {
        // Delete is JSON only, no content should be sent 
        return false;
    }
    
    protected boolean isDeleteOnUnSync(WebScriptRequest req)
    {
        boolean deleteOnUnSync = false;
        if (req.getParameter("deleteOnUnSync") != null)
        {
            deleteOnUnSync = Boolean.parseBoolean(req.getParameter("deleteOnUnSync"));
        }
        return deleteOnUnSync;
    }

    @Override
    protected NodeRef performSyncAction(WebScriptRequest req, SyncNodeChangesInfo syncNode)
       throws ConcurrentModificationException
    {
        // Is it just an un-sync, or are they deleting too?
        boolean deleteOnUnSync = isDeleteOnUnSync(req);
        
        if (logger.isDebugEnabled())
        {
            if (deleteOnUnSync)
                logger.debug("Deleting previously synced node " + syncNode.getLocalNodeRef());
            else
                logger.debug("Un-syncing but retaining previously synced node " + syncNode.getLocalNodeRef());
        }
        
        // Ask for the un-sync / delete
        cloudSyncMemberNodeTransport.fetchLocalDetailsAndUnSync(syncNode, deleteOnUnSync);

        // All done
        return syncNode.getLocalNodeRef();
    }

    @Override
    protected Map<String, Object> populateModel(Map<String, Object> coreModel, WebScriptRequest req, SyncNodeChangesInfo changes)
    {
        if (isDeleteOnUnSync(req))
        {
            coreModel.put("result", "Node Deleted");
        }
        else {
            coreModel.put("result", "Node Un-Synced but retained");
        }
        return coreModel;
    }
    
}