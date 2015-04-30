/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncNodeException;
import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncOnCloudService;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncDeclarativeWebScript;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentLimitViolationException;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.usage.ContentQuotaException;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.Pair;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * Parent class for the Cloud Sync webscripts that accept a multi-part
 *  upload of JSON describing the object, and optionally also the
 *  content.
 * 
 * Note - requires that multi-part form processing is disabled
 * 
 * @author Nick Burch
 * @since 4.1
 */
public abstract class AbstractCloudSyncPostWebScript extends AbstractCloudSyncDeclarativeWebScript
{
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(AbstractCloudSyncPostWebScript.class);
    
    protected CloudSyncMemberNodeTransport cloudSyncMemberNodeTransport;
    protected CloudSyncOnCloudService onCloudService;
    private boolean deleteTempFiles = true;
    
    public void setCloudSyncMemberNodeTransport(CloudSyncMemberNodeTransport cloudSyncMemberNodeTransport)
    {
        this.cloudSyncMemberNodeTransport = cloudSyncMemberNodeTransport;
    }
    public void setCloudSyncOnCloudService(CloudSyncOnCloudService onCloudService)
    {
        this.onCloudService = onCloudService;
    }
    /**
     * Normally, temp content files are deleted at the end of the request.
     * Disable this for unit tests which need the content to live for longer
     *  so it can be checked.
     */
    public void setDeleteTempFiles(boolean delete)
    {
        this.deleteTempFiles = delete;
    }

    /**
     * Could the post also include content, or is it JSON only?
     */
    protected abstract boolean isContentExpected();
    
    /**
     * Called to actually do the sync processing
     */
    protected abstract NodeRef performSyncAction(WebScriptRequest req, SyncNodeChangesInfo changes)
       throws ConcurrentModificationException;
    
    /**
     * Called to set additional information into the model
     */
    protected abstract Map<String, Object> populateModel(Map<String, Object> coreModel, WebScriptRequest req, SyncNodeChangesInfo changes);
    
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        SyncNodeChangesInfo changes = null;
        List<File> tmpFiles = new ArrayList<File>();
        Map<String, Object> model = new HashMap<String, Object>();
        
        // Decide based on the request details if they're allowed
        if (!onCloudService.canCloudSyncOccur())
        {
            if (logger.isInfoEnabled())
            {
                logger.info("Sync requested but declined");
            }
            recordStatus(Status.STATUS_NOT_ACCEPTABLE, "Sync not allowed at this time", model, status);
            return model;
        }
        
        // Start on the form multi part processing
        NodeRef localNodeRef = null;
        try
        {
            try
            {
                ServletFileUpload fileUp = new ServletFileUpload();
                HttpServletRequest httpReq = ((WebScriptServletRequest)req).getHttpServletRequest();
                FileItemIterator items = fileUp.getItemIterator(httpReq);
                
                if (! items.hasNext())
                {
                    throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Corrupt sync details, no parts found");
                }
                
                
                // Grab the JSON part
                FileItemStream jsonStream = items.next();
                changes = cloudSyncMemberNodeTransport.decodeMainJSON(jsonStream);
                
                boolean isContent = false;
                
                // Are we expecting content?
                if (isContentExpected())
                {
                    // For content based uploads, decide now if that machine is still allowed to continue the sync
                    if (!onCloudService.canCloudSyncProceed(changes))
                    {
                        if (logger.isInfoEnabled())
                        {
                            logger.info("Sync declined based on the changes supplied");
                        }
                        
                        recordStatus(Status.STATUS_NOT_ACCEPTABLE, "Sync not permitted at this time, based on content", model, status);
                        return model;
                    }
                    
                    // If all looks fine, fetch the content parts to temporary files
                    Map<QName,CloudSyncContent> contents = changes.getContentUpdates();
                    while(items.hasNext())
                    {
                        isContent = true;
                        
                        // Have the content decoded into a temporary file. We need to use temporary
                        //  files here, so we don't loose everything if the transaction is retried
                        if (contents == null)
                        {
                            contents = new HashMap<QName, CloudSyncContent>();
                        }
                        FileItemStream contentStream = items.next();
                        CloudSyncContent content = cloudSyncMemberNodeTransport.decodeContent(contentStream);
                       
                        contents.put(content.getPropName(), content);
                        
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Found Content Property " + content);
                        }
                    }
                    ((SyncNodeChangesInfoImpl)changes).setContentUpdates(contents);
                }
                else
                {
                    // No content was expected
                    if (items.hasNext())
                    {
                        throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Only the JSON part is accepted for this method, but multiple parts received");
                    }
                }
                
                if (changes.getType() == null)
                {
                    // CLOUD-796 - final guess of unknown type
                    ((SyncNodeChangesInfoImpl)changes).setType(isContent ? ContentModel.TYPE_CONTENT : ContentModel.TYPE_FOLDER);
                }
            }
            catch (IOException io)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid Sync Request", io);
            }
            catch (FileUploadException fup)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid Sync Request", fup);
            }
            
            // Request Parsed
            
            try
            {
                // Have the sync operation performed, or an exception raised if the
                //  cloud has had changes locally too
                localNodeRef = performSyncAction(req, changes);
            }
            catch (ContentIOException cioe)
            {
                if (cioe instanceof ContentLimitViolationException)
                {
                    logger.error("Failed to post due to content limit violation exception: "+cioe);
                    throw new SyncNodeException(SyncNodeExceptionType.CONTENT_LIMIT_VIOLATION);
                }
                else
                {
                    logger.error("Failed to post due to content io exception", cioe);
                    Throwable t = cioe;
                    while (t != null)
                    {
                        if (t instanceof ContentLimitViolationException)
                        {
                        	logger.error("Failed to post due to content limit violation exception: "+cioe);
                            throw new SyncNodeException(SyncNodeExceptionType.CONTENT_LIMIT_VIOLATION);
                        }
                        t = t.getCause();
                    }
                }
                /*
                 * Early versions of sync treated this as STATUS_BAD_REQUEST
                 * 
                 *  However the request is fine.  The server has gone wrong - e.g.  the S3 store is a.w.o.l.
                 */
                logger.error("Failed to post due to content io exception", cioe);
                // TODO - How should we handle this  - so should it be a 500 or 503? or something else.
                //throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, cioe.getMessage(), cioe);
                throw SyncNodeException.wrapUnhandledException(cioe);
            }
            catch (ContentQuotaException cqe)
            {
            	if(logger.isDebugEnabled())
            	{
            		logger.debug("ContentQuotaException - returning QUOTA_LIMIT_VIOLATION", cqe);
            	}
                throw new SyncNodeException(SyncNodeExceptionType.QUOTA_LIMIT_VIOLATION);
            }
            catch (ConcurrentModificationException concurrent)
            {
            	if(logger.isDebugEnabled())
            	{
            		logger.debug("Concurrent Modification - returning CONFLICT", concurrent);
            	}
                recordStatus(Status.STATUS_CONFLICT, "Conflicting changes detected: " + concurrent.getMessage(), model, status);
                return model;
            }
            catch (NoSuchSyncSetDefinitionException nsse)
            {
            	if(logger.isDebugEnabled())
            	{
            		logger.debug("NoSuchSyncSetDefinition - returning GONE", nsse);
            	}
                model.put("exception", nsse);
                model.put("message", nsse.getMessage());
                model.put("ssdId", nsse.getUnrecognisedSsdId());
                recordStatus(Status.STATUS_GONE, nsse.getMessage(), model, status);
                return model;
            }
            catch (SyncNodeException sne)
            {
                throw sne;
            }
            /* TODO consider retryable exceptions ... where is the txn boundary
            catch (Throwable t)
            {
                // catch all other exceptions rather than returning a meaningless 500 (Internal Server Error) which causes sync tracker to loop
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                
                logger.error("Cloud Sync Post action failed: "+sw.toString());
                
                throw SyncNodeException.wrapUnhandledException(t);
            }
            */
        }
        catch (SyncNodeException sne)
        {
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("Sync Node Exception - return STATUS_PRECONDITION_FAILED", sne);
        	}
            model.put("exception", sne);
            model.put("message", sne.getMessage());
            model.put("messageId", sne.getMsgId());
            model.put("messageParams", sne.getMsgParams());

            // Only send the exception details for unknown (= unexpected failure/bug)
            // For all other cases, the message ID is all that's needed
            if (sne.getExceptionType() == SyncNodeExceptionType.UNKNOWN)
            {
                Map<String,Object> cause = new HashMap<String, Object>();
                if (sne.getCause() == null)
                {
                    cause.put("message", "(no cause available)");
                }
                else
                {
                    cause.put("class", sne.getCause().getClass().getName());
                    cause.put("message", sne.getCause().getMessage());
                    cause.put("stacktrace", sne.getCause().getStackTrace());
                }
                model.put("cause", cause);
            }
            
            recordStatus(Status.STATUS_PRECONDITION_FAILED, sne.getExceptionType().getMessageId(), model, status);
            return model;
        }
        finally
        {
            // Tidy up temp files, no matter if it worked or not, provided we're not unit testing
            if (deleteTempFiles)
            {
                for (File f : tmpFiles)
                {
                    f.delete();
                }
            }
        }
        
        // Fetch information on when they're next allowed to bug us
        long noSyncIntervalSecs = onCloudService.getMinimumNextSyncIntervalSeconds();
        Date noSyncBefore = new Date(System.currentTimeMillis() + noSyncIntervalSecs*1000);

        // Populate the core details of the model
        model.put("nodeRef", localNodeRef);
        model.put("noSyncBefore", noSyncBefore);
        model.put("noSyncBeforeISO8601", ISO8601DateFormat.format(noSyncBefore));
        model.put("noSyncInterval", noSyncIntervalSecs);
        
        // Allow the webscript to alter the model if needed
        model = populateModel(model, req, changes);
        
        // All done
        return model;
    }
}