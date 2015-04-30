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

import org.alfresco.enterprise.repo.sync.SyncService;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the Cloud Sync conflict.post web script.
 * 
 * It handles marking locally that a conflict was detected on the other end
 * 
 * Note - requires that multi-part form processing is disabled
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class CloudSyncConflictPost extends AbstractCloudSyncPostWebScript
{
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(CloudSyncConflictPost.class);
    
    private SyncService syncService;
    private RetryingTransactionHelper transactionHelper; 
    
    public void setSyncService(SyncService syncService)
    {
        this.syncService = syncService;
    }
    public void setTransactionHelper(RetryingTransactionHelper transactionHelper)
    {
        this.transactionHelper = transactionHelper;
    }

    
    @Override
    protected boolean isContentExpected()
    {
        // Conflict is JSON only, no content should be sent 
        return false;
    }
    
    @Override
    protected NodeRef performSyncAction(WebScriptRequest req, final SyncNodeChangesInfo syncNode)
       throws ConcurrentModificationException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Marking a conflict for " + syncNode.getLocalNodeRef());
        }
        
        // Ask for the the conflict to be marked
        // TODO Should we do something with the ConflictResponse?
        transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
           {
              @Override
              public Void execute() throws Throwable
              {
                  syncService.dealWithConflictInAppropriateManner(syncNode);
                  return null;
              }
           }, false, true
        );

        // All done
        return syncNode.getLocalNodeRef();
    }

    @Override
    protected Map<String, Object> populateModel(Map<String, Object> coreModel, WebScriptRequest req, SyncNodeChangesInfo changes)
    {
        return coreModel;
    }
}