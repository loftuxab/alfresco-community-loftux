/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import java.io.IOException;
import java.io.OutputStream;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncNodeException;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncOnCloudService;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncAbstractWebScript;
import org.alfresco.enterprise.repo.web.scripts.sync.transport.CloudSyncMonitor.CloudSyncMonitorCtx;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This class is the controller for the Cloud Sync pull.get web script.
 * 
 * It handles fetching the sync changes (if any), reporting if there
 *  were none, or otherwise encoding the changes into a response.
 *
 * Note - handles the response directly in Java, as it needs to be
 *  done in a streaming efficient manner
 * 
 * @author Nick Burch
 * @since CloudSync
 */
public class CloudSyncPullGet extends AbstractCloudSyncAbstractWebScript
{
    private static Log logger = LogFactory.getLog(CloudSyncPullGet.class);
    
    protected CloudSyncMemberNodeTransport cloudSyncMemberNodeTransport;
    protected CloudSyncMonitor cloudSyncMonitor;
    protected CloudSyncOnCloudService onCloudService;
    private TransactionService transactionService;
    
    public void setCloudSyncMemberNodeTransport(CloudSyncMemberNodeTransport cloudSyncMemberNodeTransport)
    {
        this.cloudSyncMemberNodeTransport = cloudSyncMemberNodeTransport;
    }
    public void setCloudSyncOnCloudService(CloudSyncOnCloudService onCloudService)
    {
        this.onCloudService = onCloudService;
    }
    
    public void setCloudSyncMonitor(CloudSyncMonitor monitor)
    {
        this.cloudSyncMonitor = monitor;
    }
    
    @Override
    public void executeSyncImpl(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        // Grab the details of what we're to be working on
        // We only need a very small amount of information (for now...), so it's
        //  all done with a GET rather than a JSON POST
        SyncNodeChangesInfo stubInfo = cloudSyncMemberNodeTransport.decodePullParameters(req);
        if (logger.isDebugEnabled())
        {
            logger.debug("Fetching pull details for " + stubInfo.getLocalNodeRef() + " in " + stubInfo.getSyncSetGUID());
        }
        
        // Fetch the full SyncNodeChangesInfo for this 
        CloudSyncMonitorCtx monitor = cloudSyncMonitor.pullStarted(stubInfo);
        try
        {
            executeSyncImpl(res, stubInfo, monitor);
            cloudSyncMonitor.pullComplete(monitor);
        }
        catch (IOException ioEx)
        {
            cloudSyncMonitor.pullFailed(monitor, ioEx);
            throw ioEx;
        }
        catch (RuntimeException ex)
        {
            cloudSyncMonitor.pullFailed(monitor, ex);
            throw ex;
        }
    }
    
    private void executeSyncImpl(WebScriptResponse res, SyncNodeChangesInfo stubInfo, CloudSyncMonitorCtx monitor) throws IOException
    {
    	SyncNodeChangesInfo sync = null;
        try
        {
        	sync = syncService.fetchForPull(stubInfo);
        }
        catch (NoSuchSyncSetDefinitionException nsse)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("CloudSyncPullGet: "+stubInfo.getLocalNodeRef()+" - "+nsse.getMessage());
            }
            cloudSyncMonitor.pullFailed(monitor, nsse);
            reportException(nsse, res);
            return;
        }
        catch (SyncNodeException sne)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("CloudSyncPullGet: "+stubInfo.getLocalNodeRef()+" - "+sne.getMessage());
            }
            cloudSyncMonitor.pullFailed(monitor, sne);
            reportException(sne, res);
            return;
        }
        
        // Prepare to build up our respons
        
        RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
        
        final SyncNodeChangesInfo finalSync = sync;
        MultipartRequestEntity response = transactionHelper.doInTransaction(
            new RetryingTransactionCallback<MultipartRequestEntity>()
        	{
				@Override
				public MultipartRequestEntity execute() throws Throwable 
				{
				    // Ask for it to be lazy wrapped
				    MultipartRequestEntity response = cloudSyncMemberNodeTransport.encodeSyncChanges(finalSync);
					return response;
				}	
        	}
        , true);
       
        // Set the multi-part content type, including the boundary information
        res.setContentType(response.getContentType());
        if (logger.isTraceEnabled())
        {
            logger.trace("Response type set as " + response.getContentType());
        }
        
        // Serialize
        OutputStream out = res.getOutputStream();
        response.writeRequest(out);
        
        // All done
        out.close();
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Fetched pull details for " + stubInfo.getLocalNodeRef() + " in " + stubInfo.getSyncSetGUID());
        }
    }
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	public TransactionService getTransactionService() {
		return transactionService;
	}
}