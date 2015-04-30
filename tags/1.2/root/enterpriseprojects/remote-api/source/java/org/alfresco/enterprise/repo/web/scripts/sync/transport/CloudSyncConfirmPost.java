/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange.SsmnChangeType;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.impl.AuditTokenImpl;
import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncAbstractWebScript;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This class is the controller for the Cloud Sync confirm.post web script.
 * 
 * It handles fetching an {@link AuditToken}, and reporting to the 
 *  {@link SyncAuditService} that the remote end has confirmed that
 *  it has applied a given pull.
 *
 * @author Nick Burch, janv
 * @since 4.1
 */
public class CloudSyncConfirmPost extends AbstractCloudSyncAbstractWebScript
{
    private static Log logger = LogFactory.getLog(CloudSyncConfirmPost.class);
    
    private SyncAuditService syncAuditService;
    private RetryingTransactionHelper transactionHelper; 
    private LockService lockService;
    
    public void init()
    {
    	  PropertyCheck.mandatory(this, "nodeService", nodeService);
    	  PropertyCheck.mandatory(this, "lockService", getLockService());
    	  PropertyCheck.mandatory(this, "transactionHelper", transactionHelper);
    }
    
    public void setSyncAuditService(SyncAuditService syncAuditService)
    {
        this.syncAuditService = syncAuditService;
    }
    public void setTransactionHelper(RetryingTransactionHelper transactionHelper)
    {
        this.transactionHelper = transactionHelper;
    }
    
    @Override
    public void executeSyncImpl(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        // Grab the JSON
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try
        {
            Object jsonO = parser.parse(req.getContent().getReader());
            if (jsonO instanceof JSONObject)
            {
                json = (JSONObject)jsonO;
            }
            else
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "JSON of the wrong type, found " + jsonO);
            }
        }
        catch(ParseException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid JSON received", e);
        }
        
        // One or many?
        Object tokenO = json.get("auditToken");
        if (tokenO != null)
        {
            AuditToken token = new AuditTokenImpl(tokenO);
            doDeleteEntries(token);
            doSetOtherNodeRefForFolderCreates(token);
        }
        else
        {
            Object tokensO = json.get("auditTokens");
            if (tokensO != null)
            {
                JSONArray tokensA = (JSONArray)tokensO;
                AuditToken[] tokens = new AuditToken[tokensA.size()];
                for (int i=0; i<tokens.length; i++)
                {
                    tokens[i] = new AuditTokenImpl( tokensA.get(i) );
                }
                /*
                 * First delete the confirmed audit tokens 
                 */
                doDeleteEntries(tokens);
                
                /*
                 * Now update the other node ref  
                 */
                //TODO - re-architect to remove this from audit
                doSetOtherNodeRefForFolderCreatesAsSystem(tokens);
            }
            else
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No Audit Tokens supplied");
            }
        }
        
        // All done
        res.setStatus(Status.STATUS_NO_CONTENT);
    }

    private void doDeleteEntries(final AuditToken... tokens)
    {
        final List<AuditToken> addedTokens = new ArrayList<AuditToken>(tokens.length);
               
        transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                syncAuditService.deleteAuditEntries(tokens);
                return null;
            }
        }, false, true);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Confirm: Deleted tokens " + Arrays.asList(tokens));
        }
    }
    
    // TODO - redesign to remove this code from this class, in particular can't handle errors correctly here
    
    
    private void doSetOtherNodeRefForFolderCreatesAsSystem(final AuditToken... tokens)
    {
        AuthenticationUtil.RunAsWork<Void> work = new 
    	    AuthenticationUtil.RunAsWork<Void>()
    	    {
    			@Override
    			public Void doWork() throws Exception 
    			{
    				doSetOtherNodeRefForFolderCreates(tokens);
    				return null;
    			}
    		        	
    		};
    		        		
    	AuthenticationUtil.runAsSystem(work);
    	
    }
    
    /**
     * Method to set PROP_OTHER_NODEREF_STRING for pulled folder creates.
     * @param tokens
     */
    private void doSetOtherNodeRefForFolderCreates(final AuditToken... tokens)
    {
        final List<AuditToken> addedTokens = new ArrayList<AuditToken>(tokens.length);
        
        for (AuditToken token : tokens)
        {
            final AuditToken finalToken = token;
            if ((finalToken.getChangeType() != null) && (finalToken.getChangeType().equals(SsmnChangeType.CREATE)))
            {
            	try
            	{
                    transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
                    {
                        @Override
                        public Void execute() throws Throwable
                        {           
                           NodeRef nodeRef = finalToken.getNodeRef();
                           NodeRef otherNodeRef = finalToken.getOtherNodeRef();
                           if ((nodeRef != null) && (otherNodeRef != null))
                           {
                               if (nodeService.exists(nodeRef) && 
                        		   nodeService.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE) && 
                        		   nodeService.getProperty(nodeRef,SyncModel.PROP_OTHER_NODEREF_STRING) == null)
                               {
                            	   if(logger.isDebugEnabled())
                            	   {
                            		   logger.debug("set SyncModel.PROP_OTHER_NODEREF_STRING nodeRef: " + nodeRef +", otherNodeRef: " + otherNodeRef );
                            	   }
                            	   getLockService().suspendLocks();
                                   nodeService.setProperty(nodeRef, SyncModel.PROP_OTHER_NODEREF_STRING, otherNodeRef.toString());
                                   addedTokens.add(finalToken);
                               }
                           }
                           return null;
                        }
             
               
                    } , false, true);
            	}
            	catch (Throwable t)
            	{
            		logger.error("unable to set PROP_OTHER_NODEREF_STRING", t);
            	}
            	
            }
        } // end of for each token
        
        if (logger.isDebugEnabled() && (addedTokens.size() > 0))
        {
            logger.debug("Confirm: Updated 'otherNodeRef' for added nodes " + addedTokens);
        }
    }

	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}

	public LockService getLockService() {
		return lockService;
	}
}