/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncNodeException;
import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.enterprise.repo.sync.SyncService;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncDeclinedException;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.events.types.Event;
import org.alfresco.events.types.SyncEvent;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.events.EventPreparator;
import org.alfresco.repo.events.EventPublisher;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.transfer.ContentDataPart;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorClientException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Implementation of the service {@link CloudSyncMemberNodeTransport} which
 * does push-sending and pull-receiving of {@link SyncNodeChangesInfo} instances
 * with the Cloud. 
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class CloudSyncMemberNodeTransportImpl implements CloudSyncMemberNodeTransport
{
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(CloudSyncMemberNodeTransportImpl.class);
    
    /** "multipart/mixed" content type */
    public static final String MULTIPART_MIXED_TYPE = "multipart/mixed";
    
    public static final String URL_PULL = "/enterprise/sync/pull";
    public static final String URL_PUSH = "/enterprise/sync/push";
    public static final String URL_DELETE = "/enterprise/sync/delete";
    public static final String URL_CONFIRM = "/enterprise/sync/confirm";
    public static final String URL_CONFLICT = "/enterprise/sync/conflict";
    public static final String URL_PARAM_DELETE_FORCE = "force";
    public static final String URL_PARAM_DELETE_DELETEONUNSYNC = "deleteOnUnSync";
    
    private RetryingTransactionHelper retryingTransactionHelper;
    private CloudConnectorService cloudConnectorService;
    private NamespaceService namespaceService;
    private ContentService contentService;
    private SyncService syncService;
    private NodeService nodeService;
    private EventPublisher eventPublisher;
    
    public void setEventPublisher(EventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
    
    public void setRetryingTransactionHelper(RetryingTransactionHelper retryingTransactionHelper)
    {
        this.retryingTransactionHelper = retryingTransactionHelper;
    }
    public void setCloudConnectorService(CloudConnectorService cloudConnectorService)
    {
        this.cloudConnectorService = cloudConnectorService;
    }
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    public void setSyncService(SyncService syncService)
    {
        this.syncService = syncService;
    }
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }


    /**
     * Pushes the initial version of the node to the cloud, as the current user,
     *  and return the NodeRef on the cloud that it was created at.
     */
    @Override
    public NodeRef pushSyncInitial(SyncNodeChangesInfo syncNode, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException
    {
        // Sanity check
        if (syncNode.getRemoteNodeRef() != null)
            throw new IllegalArgumentException("Can't do an initial sync of a Node that already has a remote NodeRef");
        if (syncNode.getRemoteParentNodeRef() == null)
            throw new IllegalArgumentException("Can't do an initial sync without a destination parent NodeRef");
        
        // Do the real push
        NodeRef remoteNodeRef = doPushSync(syncNode, cloudNetwork);
        return remoteNodeRef;
    }
    
    /**
     * Pushes the changes to the node to the cloud, as the current user.
     */
    @Override
    public void pushSyncChange(SyncNodeChangesInfo syncNode, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException
    {
        // Sanity check
        if (syncNode.getRemoteNodeRef() == null)
            throw new IllegalArgumentException("Can't do an update sync without the remote NodeRef");
           
        // Do the real push
        doPushSync(syncNode, cloudNetwork);
    }
    
    @Override
    public void pushSyncDelete(SyncNodeChangesInfo syncNode, String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException
    {
        boolean force = false;
        doPushDelete(syncNode, cloudNetwork, true, force);
    }
    
    @Override
    public void pushUnSync(SyncNodeChangesInfo syncNode, String cloudNetwork) throws 
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException,
       AuthenticationException, RemoteSystemUnavailableException
    {
        boolean force = false;
        doPushDelete(syncNode, cloudNetwork, false, force);
    }
    
    @Override
    public void pushConflictDetected(SyncNodeChangesInfo stubLocal, String cloudNetwork)
       throws SyncNodeException, NoSuchSyncSetDefinitionException, AuthenticationException, RemoteSystemUnavailableException
    {
        // Send the conflict
        @SuppressWarnings("unused")
        JSONObject response = doPostJSONAction(URL_CONFLICT, stubLocal, cloudNetwork);
        // TODO Do we care about the response message?
        
        // All done
    }

   // ------------------------------------------------
    
    private void doPushDelete(SyncNodeChangesInfo syncNode, String cloudNetwork, boolean deleteOnUnSync, boolean force) 
        throws SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
               AuthenticationException, RemoteSystemUnavailableException
    {
        // Sanity check
        if (syncNode.getRemoteNodeRef() == null)
            throw new IllegalArgumentException("Can't do a delete/un-sync without the remote NodeRef");
           
        // Do the real push
        // Build a helper string for errors
        String action = "Delete";
        if (!deleteOnUnSync)
        {
            action = "Un-Sync";
        }
        
        // Sanity check
        if (syncNode.getRemoteNodeRef() == null)
        {
            logger.error("Skipped remote "+action+" since no remote NodeRef for: "+syncNode.getLocalNodeRef());
            return;
        }
        
        // Build up our url, with the required parameters
        StringBuilder sb = new StringBuilder();
        sb.append(URL_DELETE);
        sb.append('?');
        sb.append(URL_PARAM_DELETE_DELETEONUNSYNC);
        sb.append('=');
        sb.append(Boolean.toString(deleteOnUnSync));
        sb.append('&');
        sb.append(URL_PARAM_DELETE_FORCE);
        sb.append('=');
        sb.append(Boolean.toString(force));
        
        // Ask the cloud to do the delete for us
        @SuppressWarnings("unused")
        JSONObject response = doPostJSONAction(sb.toString(), syncNode, cloudNetwork);
        // TODO Do we care about the response message?
   //     eventPublish.
     //   sync.delete
        // All done
    }
    
    private NodeRef doPushSync(final SyncNodeChangesInfo syncNode, final String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException
    {    
        // Perform the push
        JSONObject response = doPostJSONAction(URL_PUSH, syncNode, cloudNetwork);
        
        eventPublisher.publishEvent(new EventPreparator(){
            @Override
            public Event prepareEvent(String user, String networkId, String transactionId)
            {
                //First check for nulls
                String localRef = syncNode.getLocalNodeRef() != null?syncNode.getLocalNodeRef().getId():null;
                String remoteRef = syncNode.getRemoteNodeRef() != null?syncNode.getRemoteNodeRef().getId():null;
                String nodeType = syncNode.getType() != null?syncNode.getType().toString():null;
                
                //Raise event
                return new SyncEvent("sync.to.cloud", user, networkId,transactionId, localRef,
                                        null, nodeType, null, null, null, 0l, null,
                                        remoteRef, cloudNetwork, syncNode.getSyncSetGUID());
            }
        });
        
        // Process the response, to get the noderef, sync intervals etc
        NodeRef remoteNodeRef = null;
        if (response.containsKey("nodeRef"))
        {
            remoteNodeRef = new NodeRef((String)response.get("nodeRef"));
        }
        // TODO Get and return the Sync Intervals too
        
        return remoteNodeRef;
    }
    
    private JSONObject doPostJSONAction(String url, SyncNodeChangesInfo syncNode, String cloudNetwork)
            throws SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
                   AuthenticationException, RemoteSystemUnavailableException
    {
        // Perform the action
        if(logger.isTraceEnabled())
        {
        	logger.trace("doPostJSONAction url:" + url);
        }
        
        RemoteConnectorResponse resp = doPostAction(url, syncNode, cloudNetwork);
       
        // Check it worked, and we got the expected response
        if (! resp.getContentType().equals(MimetypeMap.MIMETYPE_JSON))
        {
            logger.trace("return type was not JSON = throw RemoteSystenUnavailable");
            throw new RemoteSystemUnavailableException("Invalid response received from sync");
        }
        if (resp.getStatus() != Status.STATUS_OK)
        {
            logger.trace("status was not success = throw RemoteSystenUnavailable");
            throw new RemoteSystemUnavailableException("Invalid response received from sync: " + resp.getStatus());
        }
        
        
        // Parse the response JSON
        JSONObject response;
        try
        {
            JSONParser parser = new JSONParser();
            String json = resp.getResponseBodyAsString();
            if(logger.isDebugEnabled())
            {
            	logger.debug("response body =" + json);
            }
            Object o = parser.parse(json);
            if (o instanceof JSONObject)
            {
                response = (JSONObject)o;
            }
            else
            {
                throw new RemoteSystemUnavailableException("Invalid JSON response received from sync: " + json);
            }
        }
        catch (ParseException pe)
        {
            throw new RemoteSystemUnavailableException("Invalid JSON response received from sync", pe);
        }
        catch (IOException ie)
        {
            throw new RemoteSystemUnavailableException("Error reading sync response", ie);
        }
        
        // Report
        if (logger.isDebugEnabled())
            logger.debug("Sync Action Successful, response was " + response.toString());
        
        // Return the JSON for processing
        return response;
    }
            
    private RemoteConnectorResponse doPostAction(String url, SyncNodeChangesInfo syncNode, String cloudNetwork)
        throws SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
               AuthenticationException, RemoteSystemUnavailableException
    {
        // Abort if we've no credentials for the current user
        if (cloudConnectorService.getCloudCredentials() == null)
        {
            throw new AuthenticationException("No Cloud Credentials exist for the current user");
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Pushing Sync Changes to " + cloudNetwork + " for " + syncNode);
        
        // Build the connection to the cloud
        RemoteConnectorRequest req = 
            cloudConnectorService.buildCloudRequest(url, cloudNetwork, "POST");
        req.setContentType(MULTIPART_MIXED_TYPE);
        
        MultipartRequestEntity body = encodeSyncChanges(syncNode);
        req.setRequestBody(body);
        
        // Attach the multipart boundary information to the content type
        req.setContentType(body.getContentType());
        
        
        // Send
        RemoteConnectorResponse resp = null;
        try
        {
            resp = cloudConnectorService.executeCloudRequest(req);
        }
        catch(RemoteConnectorClientException ce)
        {
            handleRemoteConnectorClientException(ce, syncNode);
        }
        catch(IOException e)
        {
            throw new RemoteSystemUnavailableException("Error syncing with cloud", e);
        }
        catch(AlfrescoRuntimeException re)
        {
            if (re.getCause() instanceof IOException)
            {
                // IOExceptions can get wrapped, report
                throw new RemoteSystemUnavailableException("Error syncing with cloud", re.getCause());
            }
            else
            {
                // Some other runtime exception, bubble up
                throw re;
            }
        }
        
        // If we get here, the response was apparently valid so return for processing
        return resp;
    }
    private void handleRemoteConnectorClientException(RemoteConnectorClientException ce, SyncNodeChangesInfo syncNode)
       throws ConcurrentModificationException, CloudSyncDeclinedException, NoSuchSyncSetDefinitionException, 
              SyncNodeException, RemoteSystemUnavailableException
    {
        // Is it one we need to handle specially?
        if (ce.getStatusCode() == Status.STATUS_CONFLICT)
        {
            throw new ConcurrentModificationException();
        }
        if (ce.getStatusCode() == Status.STATUS_NOT_ACCEPTABLE)
        {
            throw new CloudSyncDeclinedException(ce.getStatusText());
        }
        if (ce.getStatusCode() == Status.STATUS_GONE)
        {
            // By GONE we mean the SSD, rather than the Node itself
            // (If the node itself has gone, that's a SyncNodeException)
            throw new NoSuchSyncSetDefinitionException(ce.getStatusText(), syncNode.getSyncSetGUID());
        }
        if (ce.getStatusCode() == Status.STATUS_PRECONDITION_FAILED)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Precondition failed: status text:" + ce.getStatusText());
                logger.debug("Precondition failed: message id:" + ce.getMsgId());
                if(ce.getResponse() == null)
                {
                    logger.debug("response is null");
                }
            }
            
            // The SyncNodeExceptionType is set on the webscript side as the status text
            // However, a surf bug means it normally gets ignored...
            // So, use the JSON version if it's available
            try
            {
                if(ce.getResponse() != null)
                {
                    String json = ce.getResponse().getResponseBodyAsString();
                
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("exception json = " + json);
                    }
                    if (json != null && json.length() > 0)
                    {
                        JSONParser parser = new JSONParser();
                        JSONObject errJson = (JSONObject)parser.parse(json);
                        String messageId = (String)errJson.get("messageId");
                        if(logger.isDebugEnabled())
                        {
                        	logger.debug("read messageId=" + messageId);
                        }
                        
                        SyncNodeExceptionType type = SyncNodeExceptionType.fromMessageId(messageId);
                        if (type == SyncNodeExceptionType.UNKNOWN)
                        {
                            // Warn that there was something unhandled (eg a bug) on the server
                            logger.warn("Unhandled exception on the cloud when processing sync request:" + errJson.get("cause"));
                        }
                        throw new SyncNodeException(type);
                    }
                }
            }
            catch(IOException e) 
            {
                logger.error("unable to read response", e);
            }
            catch(ParseException pe) 
            {
                logger.error("unable to parse return", pe);
            }
            
            // Hope we can get what we need from the status text
            throw new SyncNodeException(SyncNodeExceptionType.fromMessageId(ce.getStatusText()));
        }
        
        // Fall back on a general error
        throw new RemoteSystemUnavailableException("Error syncing with cloud", ce);
    }
    
    /**
     * Pulls down changes to this node from the cloud, if any.
     */
    @Override
    public SyncNodeChangesInfo pullSyncChange(final SyncNodeChangesInfo stubLocal, final String cloudNetwork) throws
       SyncNodeException, NoSuchSyncSetDefinitionException, ConcurrentModificationException, 
       AuthenticationException, RemoteSystemUnavailableException
    {
        // Abort if we've no credentials for the current user
        if (cloudConnectorService.getCloudCredentials() == null)
        {
            throw new AuthenticationException("No Cloud Credentials exist for the current user");
        }
        
        // Ensure the stub has enough on it
        ParameterCheck.mandatory("syncSetGUID", stubLocal.getSyncSetGUID());
        ParameterCheck.mandatory("remoteNodeRef", stubLocal.getRemoteNodeRef());
        
        
        // Generate the URL, including the two noderefs
        StringBuffer url = new StringBuffer();
        url.append(URL_PULL);
        
        // Source is theirs, as we're pulling from them
        url.append('?');
        url.append(JSON_SOURCE_NODEREF);
        url.append('=');
        url.append(stubLocal.getRemoteNodeRef());
        if (stubLocal.getLocalNodeRef() != null)
        {
            // Target is ours, as we're pulling to here
            url.append('&');
            url.append(JSON_TARGET_NODEREF);
            url.append('=');
            url.append(stubLocal.getLocalNodeRef());
        }
        // What Sync Set this belongs to
        url.append('&');
        url.append(JSON_SYNCSET);
        url.append('=');
        url.append(stubLocal.getSyncSetGUID());
        // Tell them when ours changed, if we can
        if (stubLocal.getLocalModifiedAt() != null)
        {
            url.append('&');
            url.append(JSON_TARGET_MODIFIED_AT);
            url.append('=');
            url.append(ISO8601DateFormat.format(stubLocal.getLocalModifiedAt()));
        }
        
        // Build the connection to the cloud
        RemoteConnectorRequest req = 
            cloudConnectorService.buildCloudRequest(url.toString(), cloudNetwork, "GET");
        req.setContentType(MimetypeMap.MIMETYPE_JSON);
        
        // Send the request
        RemoteConnectorResponse resp = null;
        SyncNodeChangesInfo changes = null;
        List<File> tmpFiles = new ArrayList<File>();

        try
        {
            resp = cloudConnectorService.executeCloudRequest(req);
            
            // Check it worked
            if (resp.getStatus() != Status.STATUS_OK)
            {
                throw new RemoteSystemUnavailableException("Invalid response status received from sync: " + resp.getStatus());
            }
            if (! resp.getContentType().startsWith(MULTIPART_MIXED_TYPE))
            {
                throw new RemoteSystemUnavailableException("Invalid response type received from sync: " + resp.getContentType());
            }

            
            // Decode it
            HttpResponseFileUpload fileUp = new HttpResponseFileUpload();
            FileItemIterator items = fileUp.getItemIterator(resp);
            
            if (! items.hasNext())
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Corrupt sync details, no parts found");
            }
            
            // Grab the JSON part
            FileItemStream jsonStream = items.next();
            changes = decodeMainJSON(jsonStream);
            
            boolean isContent = false;
            
            // Followed by the content parts to temporary files
            Map<QName, CloudSyncContent> contents = changes.getContentUpdates();
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
                CloudSyncContent content = decodeContent(contentStream);
                contents.put(content.getPropName(), content);

                if (logger.isDebugEnabled())
                {
                    logger.debug("Found Content Property : " + content);
                }
            }
            ((SyncNodeChangesInfoImpl)changes).setContentUpdates(contents);
            
            if (changes.getType() == null)
            {   // pulling an unknown type for a file or folder.
                ((SyncNodeChangesInfoImpl)changes).setType(isContent ? ContentModel.TYPE_CONTENT : ContentModel.TYPE_FOLDER);
            }

            eventPublisher.publishEvent(new EventPreparator(){
                @Override
                public Event prepareEvent(String user, String networkId, String transactionId)
                {
                    //First check for nulls
                    String localRef = stubLocal.getLocalNodeRef() != null?stubLocal.getLocalNodeRef().getId():null;
                    String remoteRef = stubLocal.getRemoteNodeRef() != null?stubLocal.getRemoteNodeRef().getId():null;
                    String nodeType = stubLocal.getType() != null?stubLocal.getType().toString():null;
                    
                    //Raise event
                    return new SyncEvent("sync.from.cloud", user, networkId,transactionId, localRef,null,
                                         nodeType, null, null, null, 0l, null,
                                         remoteRef, cloudNetwork, stubLocal.getSyncSetGUID());
                }
            });
            
            // TODO How to tidy these temp files up?
        }
        catch(RemoteConnectorClientException rce)
        {
            handleRemoteConnectorClientException(rce, stubLocal);
        }
        catch(FileUploadException fe)
        {
            throw new RemoteSystemUnavailableException("Error syncing with cloud", fe);
        }
        catch(IOException e)
        {
            throw new RemoteSystemUnavailableException("Error syncing with cloud", e);
        }
        catch(AlfrescoRuntimeException re)
        {
            if (re.getCause() instanceof IOException)
            {
                // IOExceptions can get wrapped, report
                throw new RemoteSystemUnavailableException("Error syncing with cloud", re.getCause());
            }
            else
            {
                // Some other runtime exception, bubble up
                throw re;
            }
        }
        
        // All done
        return changes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void confirmPull(AuditToken[] things, String cloudNetwork)
       throws AuthenticationException, RemoteSystemUnavailableException
    {
        // Abort if we've no credentials for the current user
        if (cloudConnectorService.getCloudCredentials() == null)
        {
            throw new AuthenticationException("No Cloud Credentials exist for the current user");
        }
        
        // Build up the Confirmation JSON
        JSONArray tokens = new JSONArray();
        for (AuditToken thing : things)
        {
            tokens.add(thing.asJSON());
        }
        
        JSONObject json = new JSONObject();
        json.put(JSON_AUDIT_TOKENS, tokens);
        
        // Send it over
        RemoteConnectorRequest req = 
                cloudConnectorService.buildCloudRequest(URL_CONFIRM, cloudNetwork, "POST");
        req.setContentType(MimetypeMap.MIMETYPE_JSON);
        req.setRequestBody(json.toJSONString());
        
        RemoteConnectorResponse resp = null;
        try
        {
            resp = cloudConnectorService.executeCloudRequest(req);
            
            // Check it worked
            if (resp.getStatus() != Status.STATUS_OK && resp.getStatus() != Status.STATUS_NO_CONTENT)
            {
                throw new RemoteSystemUnavailableException("Invalid response status received from sync: " + resp.getStatus());
            }
        }
        catch(IOException e)
        {
            throw new RemoteSystemUnavailableException("Error syncing with cloud", e);
        }
        catch(AlfrescoRuntimeException re)
        {
            if (re.getCause() instanceof IOException)
            {
                // IOExceptions can get wrapped, report
                throw new RemoteSystemUnavailableException("Error syncing with cloud", re.getCause());
            }
            else
            {
                // Some other runtime exception, bubble up
                throw re;
            }
        }

        // All done
    }
    
    @Override
    public NodeRef fetchLocalDetailsAndApply(final SyncNodeChangesInfo syncNode, boolean isOnCloud) 
       throws ConcurrentModificationException
    {
        return doFetchAndAction(syncNode, false, false, isOnCloud);
    }
    @Override
    public void fetchLocalDetailsAndUnSync(SyncNodeChangesInfo syncNode, boolean deleteOnUnSync) 
       throws ConcurrentModificationException
    {
        doFetchAndAction(syncNode, true, deleteOnUnSync, false);
    }
    
    /**
     * Common helper which fetches all the additional local details, then delegates the
     *  create / update / un-sync / delete to {@link SyncService}
     */
    private NodeRef doFetchAndAction(final SyncNodeChangesInfo syncNode, final boolean isUnSync, final boolean deleteOnUnSync, final boolean isOnCloud)
    {
        final SyncNodeChangesInfoImpl syncNodeImpl = (SyncNodeChangesInfoImpl)syncNode;
        final NodeRef nodeRef = syncNode.getLocalNodeRef();
        
        try
        {
            // Start a new, retrying transaction. This ensures we always have the latest
            //  details available, especially important for if we retry due to other
            //  changes going on with the node
            return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
               {
                  @Override
                  public NodeRef execute() throws Throwable
                  {
                      // If this something other than the create case, then look up all the
                      //  additional information on the node that downstream services need
                      boolean nodeExists = false;
                      if (syncNode.getLocalNodeRef() != null)
                      {
                          nodeExists = nodeService.exists(syncNode.getLocalNodeRef());
                          if (nodeExists)
                          {
                              syncNodeImpl.setLocalModifiedAt((Date)nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED));
                              if (nodeService.hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE))
                              {
                                  syncNodeImpl.setLocalVersionLabel((String)nodeService.getProperty(nodeRef, ContentModel.PROP_VERSION_LABEL));
                              }
                          }
                      }
                      
                      // Do they want to do a create/update, or an un-sync/delete?
                      if (isUnSync)
                      {
                          // Is it an un-sync or a delete?
                          // note: force unsync or delete (even if unpulled changes) - see ALF-15380
                          if (deleteOnUnSync)
                          {
                              syncService.delete(syncNode, true);
                          }
                          else
                          {
                              syncService.removeFromSyncSet(syncNode, true);
                          }
                          return syncNode.getLocalNodeRef();
                      }
                      else
                      {
                          // Are they doing a create or an update?
                          if ((! nodeExists) && syncNode.getLocalParentNodeRef() != null)
                          {
                              // This is the first sync (or previously sync'ed node no longer exists)
                              return syncService.create(syncNode, isOnCloud);
                          }
                          else if (nodeExists)
                          {
                              // This is an update to an existing node (note: including move "within" a folder sync)
                              syncService.update(syncNode);
                              return syncNode.getLocalNodeRef();
                          }
                          else
                          {
                              throw new IllegalArgumentException("Invalid SyncNode, must have NodeRef or ParentNodeRef: "+syncNode);
                          }
                      }
                  }
               }, false, true
            );
        }
        catch (InvalidNodeRefException inre)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("doFetchAndAction: node does not exist (InvalidNodeRefException): "+inre.getNodeRef(), inre);
            }
            
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_NO_LONGER_EXISTS);
        }
    }
    
    // TODO Something common for encoding that can be used on-premise
    //  during the push-pull, and cloud side for accept-send
    
    public MultipartRequestEntity encodeSyncChanges(SyncNodeChangesInfo syncNode)
    {
        HttpMethodParams params = new HttpMethodParams();
        List<Part> parts = new ArrayList<Part>();
        
        // Turn the non content parts into JSON
        Part jsonPart = new JSONPart(encodeMainJSON(syncNode));
        parts.add(jsonPart);
        
        // Attach the content parts
        Map<QName,CloudSyncContent> contentUpdates = syncNode.getContentUpdates(); 
        if (contentUpdates != null)
        {
            for (QName cProp : contentUpdates.keySet())
            {
                CloudSyncContent c = contentUpdates.get(cProp);
                
                if (c != null)
                {
                	ContentReader r = c.openReader();
                    // Encode for lazy streaming write
                    // The setup is done as the current user, which checks permissions
                    // The actual streaming write is done as system, so it can make
                    //  use of low-level raw ContentService operations
                    RunAsSystemContentDataPart part = new RunAsSystemContentDataPart(
                            contentService, cProp.toPrefixString(namespaceService), r.getContentData());
                    parts.add(part);
                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Added content reader part for " + cProp);
                    }
                }
                else
                {
                    // A previously existing content property has been removed
                    // This information was already included in the main JSON
                    if (logger.isDebugEnabled())
                        logger.debug("Content Property was removed, details sent in JSON: " + cProp);
                }
            }
        }
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Request built, covers " + parts.size() + " parts");
        }
                
        // Finalise the multipart body
        ConfigurableMultipartRequestEntity body = new ConfigurableMultipartRequestEntity(
                parts.toArray(new Part[parts.size()]), params, MULTIPART_MIXED_TYPE);
        return body;
    }
    
    @SuppressWarnings("unchecked")
    protected String encodeMainJSON(SyncNodeChangesInfo syncChanges)
    {
        JSONObject json = new JSONObject();
        
        // Our local node becomes the source
        setJSON(json, JSON_SOURCE_NODEREF, syncChanges.getLocalNodeRef());
        setJSON(json, JSON_SOURCE_PATH, syncChanges.getLocalPath());
        setJSON(json, JSON_SOURCE_PARENT_NODEREF, syncChanges.getLocalParentNodeRef());
        setJSON(json, JSON_SOURCE_MODIFIED_AT, syncChanges.getLocalModifiedAt());
        json.put(JSON_SOURCE_VERSION_LABEL, syncChanges.getLocalVersionLabel());
        // The remote node becomes the target
        setJSON(json, JSON_TARGET_NODEREF, syncChanges.getRemoteNodeRef());
        setJSON(json, JSON_TARGET_PATH, syncChanges.getRemotePath());
        setJSON(json, JSON_TARGET_PARENT_NODEREF, syncChanges.getRemoteParentNodeRef());
        // We don't know the other end's modified at or version label
        
        // Simple details
        setJSON(json, JSON_TYPE, syncChanges.getType());
        json.put(JSON_SYNCSET, syncChanges.getSyncSetGUID());
        json.put(JSON_DIRECT_SYNC, syncChanges.getDirectSync());
        
        // Aspects added and removed
        setJSON(json, JSON_ASPECTS_ADDED, syncChanges.getAspectsAdded());
        setJSON(json, JSON_ASPECTS_REMOVED, syncChanges.getAspectsRemoved());
        
        // Audit Token
        json.put(JSON_AUDIT_TOKEN, syncChanges.getAuditToken().asJSON());
        
        // The properties
        JSONObject props = new JSONObject();
        setJSONProperties(props, syncChanges.getPropertyUpdates());
        json.put(JSON_PROPERTIES, props);
        
        // Content Properties which have been removed
        JSONArray cpropsRemoved = new JSONArray();
        Map<QName,CloudSyncContent> cprops = syncChanges.getContentUpdates();
        if (cprops != null && cprops.size() > 0)
        {
            for (QName prop : cprops.keySet())
            {
                if (cprops.get(prop) == null)
                {
                    // This content property has been removed
                    cpropsRemoved.add( prop.toPrefixString(namespaceService) );
                }
            }
        }
        if (cpropsRemoved.size() > 0)
        {
            json.put(JSON_CONTENT_PROPS_REMOVED, cpropsRemoved);
        }
            
        // Return as a JSON string, ready to be sent
        return json.toJSONString();
    }
    
    /**
     * Processes the JSON (main details) part of the multi part post,
     * returning a {@link SyncNodeChangesInfo} object lacking content.
     */
    public SyncNodeChangesInfo decodeMainJSON(FileItemStream jsonPart) throws IOException
    {
        // Check the type, get the encoding
        String encoding = getContentTypeEncoding(jsonPart.getContentType(), true);
        
        // Turn it into JSON
        InputStream stream = jsonPart.openStream();
        InputStreamReader reader = new InputStreamReader(stream, encoding);
        
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try
        {
            Object jsonO = parser.parse(reader);
            if (jsonO instanceof JSONObject)
            {
                json = (JSONObject)jsonO;
            }
            else
            {
                throw new IOException("JSON of the wrong type, found " + jsonO);
            }
        }
        catch(ParseException e)
        {
            throw new IOException("Invalid JSON received", e);
        }
        finally
        {
            reader.close();
            stream.close();
        }
        
        // Get the key details from the JSON
        // The Source becomes the Remote end for us
        NodeRef remoteNodeRef       = getJSONNodeRef(json, JSON_SOURCE_NODEREF);
        NodeRef remoteParentNodeRef = getJSONNodeRef(json, JSON_SOURCE_PARENT_NODEREF);
        String  remotePath          = (String)json.get(JSON_SOURCE_PATH);
        Date    remoteModifiedAt    = getJSONDate(json, JSON_SOURCE_MODIFIED_AT);
        String  remoteVersionLabel  = (String)json.get(JSON_SOURCE_VERSION_LABEL);
        
        // The Target becomes the Local end for us
        NodeRef localNodeRef       = getJSONNodeRef(json, JSON_TARGET_NODEREF);
        NodeRef localParentNodeRef = getJSONNodeRef(json, JSON_TARGET_PARENT_NODEREF);
        String  localPath          = (String)json.get(JSON_TARGET_PATH);
        Date    localModifiedAt    = getJSONDate(json, JSON_TARGET_MODIFIED_AT);
        String  localVersionLabel  = (String)json.get(JSON_TARGET_VERSION_LABEL);
        
        // Get the sync set 
        String syncSet = (String)json.get(JSON_SYNCSET);
        
        // Get type (null if unknown)
        QName type = getJSONQName(json, JSON_TYPE);
        
        // Get whether direct (or indirect)
        Boolean directSync = (Boolean)json.get(JSON_DIRECT_SYNC);
        if (directSync == null) { directSync = false; }
        
        // Build the initial object
        SyncNodeChangesInfoImpl syncChanges = new SyncNodeChangesInfoImpl(
                localNodeRef, remoteNodeRef, syncSet, type);
        syncChanges.setLocalPath(localPath);
        syncChanges.setRemotePath(remotePath);
        syncChanges.setLocalParentNodeRef(localParentNodeRef);
        syncChanges.setRemoteParentNodeRef(remoteParentNodeRef);
        syncChanges.setLocalModifiedAt(localModifiedAt);
        syncChanges.setRemoteModifiedAt(remoteModifiedAt);
        syncChanges.setLocalVersionLabel(localVersionLabel);
        syncChanges.setRemoteVersionLabel(remoteVersionLabel);
        syncChanges.setDirectSync(directSync);
        
        // Aspects added and removed
        syncChanges.setAspectsAdded(getJSONQNames(json, JSON_ASPECTS_ADDED));
        syncChanges.setAspectsRemoved(getJSONQNames(json, JSON_ASPECTS_REMOVED));
        
        // Get the Audit Token
        syncChanges.setAuditToken(new AuditTokenImpl(json.get(JSON_AUDIT_TOKEN)));
        
        // Get the properties
        syncChanges.setPropertyUpdates(getJSONProperties(json));
        
        // Get any content properties which were removed
        JSONArray cpropsRemoved = (JSONArray)json.get(JSON_CONTENT_PROPS_REMOVED);
        if (cpropsRemoved != null && cpropsRemoved.size() > 0)
        {
            Map<QName,CloudSyncContent> cprops = new HashMap<QName, CloudSyncContent>(cpropsRemoved.size());
            for (Object qnameO : cpropsRemoved)
            {
                try 
                {
                    QName qname = QName.createQName((String)qnameO, namespaceService);
                    cprops.put(qname, null);
                } 
                catch (NamespaceException ne)
                {
                    logger.warn("Cloud sync unable to remove node for unknown namespace:" + qnameO + ", " + ne);
                }
            }
            syncChanges.setContentUpdates(cprops);
        }
        
        // Note - content properties with data are handled elsewhere
        
        // All done
        return syncChanges;
    }
    
    @Override
    public SyncNodeChangesInfo decodePullParameters(WebScriptRequest request)
    {
        // Fetch the parameters
        String syncSet = request.getParameter(JSON_SYNCSET);
        ParameterCheck.mandatoryString(JSON_SYNCSET, syncSet);
        
        // We are the source, they are the target
        String localNodeRefS = request.getParameter(JSON_SOURCE_NODEREF);
        ParameterCheck.mandatoryString(JSON_SOURCE_NODEREF, localNodeRefS);
        if (!NodeRef.isNodeRef(localNodeRefS))
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Not a NodeRef '" + localNodeRefS + "'");
        }
        NodeRef localNodeRef = new NodeRef(localNodeRefS);
        
        NodeRef remoteNodeRef = null;
        String remoteNodeRefS = request.getParameter(JSON_TARGET_NODEREF);
        if (remoteNodeRefS != null)
        {
            if (!NodeRef.isNodeRef(remoteNodeRefS))
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Not a NodeRef '" + remoteNodeRefS + "'");
            }
            remoteNodeRef = new NodeRef(remoteNodeRefS);
        }
        
        // Wrap and return
        SyncNodeChangesInfoImpl stub = new SyncNodeChangesInfoImpl(
                localNodeRef, remoteNodeRef, syncSet, null
        );
        return stub;
    }
    
    /**
     * TODO I'm sure we already have this in a util somewhere...
     */
    protected static String getContentTypeEncoding(String contentType, boolean useDefault)
    {
        final String charsetParam = "charset=";
        if (contentType.contains(charsetParam))
        {
            return contentType.substring(contentType.indexOf(charsetParam)+charsetParam.length());
        }
        else
        {
            if (useDefault)
            {
                // If in doubt, it'll be utf-8
                return "utf-8";
            }
            else
            {
                // Explicitly indicate we don't know
                return null;
            }
        }
    }
    
    public CloudSyncContent decodeContent(FileItemStream contentPart) throws IOException
    {
        //   Sanity check
        if (contentPart.isFormField())
        {
            throw new IOException("Expecting Content, found Form Details " + contentPart.getFieldName());
        }
      
        // Create a temporary file to hold the content, so we don't loose
        //  everything if the transaction is retried
        File f = TempFileProvider.createTempFile("cloudSync", ".tmp");
    
        // Grab the property name, which is the part name
        QName prop = QName.createQName(contentPart.getName(), namespaceService);
    
        // Stream the contents into our temporary file
        InputStream inp = contentPart.openStream();
        FileOutputStream fout = new FileOutputStream(f);
        try
        {
            IOUtils.copy(inp, fout);
        } 
        finally
        {
            fout.close();
            inp.close();
        }
      
        String contentType = contentPart.getContentType();
        String encoding = getContentTypeEncoding(contentType, false);
        if (encoding == null)
        {
    	    return new CloudSyncContentFileImpl(prop, contentType, null, f);
        }
        else
        {
    	    return new CloudSyncContentFileImpl(prop, contentType.substring(0, contentType.indexOf(';')), encoding, f);
        }
    }
    
//    public Pair<QName, ContentReader> decodeContent(FileItemStream contentPart, List<File> tempFilesList) throws IOException
//    {
//        // Sanity check
//        if (contentPart.isFormField())
//        {
//            throw new IOException("Expecting Content, found Form Details " + contentPart.getFieldName());
//        }
//        
//        // Create a temporary file to hold the content, so we don't loose
//        //  everything if the transaction is retried
//        File f = TempFileProvider.createTempFile("cloudSync", ".tmp");
//        tempFilesList.add(f);
//        
//        // Grab the property name, which is the part name
//        QName prop = QName.createQName(contentPart.getName(), namespaceService);
//        
//        // Stream the contents into our temporary file
//        InputStream inp = contentPart.openStream();
//        FileOutputStream fout = new FileOutputStream(f);
//        try
//        {
//            IOUtils.copy(inp, fout);
//        }
//        finally
//        {
//            fout.close();
//            inp.close();
//        }
//        
//        // Turn this into a ContentReader
//        FileContentReader reader = new FileContentReader(f);
//        
//        // Extract out the mimetype and encoding (if set) from the content type
//        String contentType = contentPart.getContentType();
//        String encoding = getContentTypeEncoding(contentType, false);
//        if (encoding == null)
//        {
//            // No encoding set
//            reader.setMimetype(contentType);
//            reader.setEncoding(null);
//        }
//        else
//        {
//            // Encoding set
//            reader.setEncoding(encoding);
//            reader.setMimetype(contentType.substring(0, contentType.indexOf(';')));
//        }
//        
//        // Wrap and return
//        return new Pair<QName, ContentReader>(prop, reader);
//    }
    
    protected QName getJSONQName(JSONObject json, String key)
    {
        QName qname = null;
        String qn = (String)json.get(key);
        if (qn != null)
        {
            try
            {
                qname = QName.createQName(qn, namespaceService);
            }
            catch (NamespaceException ne)
            {
                logger.warn("Cloud sync unable to get qname for unknown namespace:" + qn + ", " + ne);
            }
        }
        return qname;
    }
    protected Set<QName> getJSONQNames(JSONObject json, String key)
    {
        Object qnO = json.get(key);
        if (qnO == null) return null;
        if (! (qnO instanceof JSONArray))
        {
            logger.warn("Invalid JSON, expecting array for " + key + " but got " + qnO);
        }
        
        JSONArray qns = (JSONArray)qnO;
        if (qns.size() == 0) return null;
        
        HashSet<QName> qnames = new HashSet<QName>(qns.size());
        for (Object qn : qns)
        {
            try
            {
                qnames.add(QName.createQName((String)qn, namespaceService));
            }
            catch (NamespaceException ne)
            {
                logger.warn("Cloud sync unable to get qname for unknown namespace:" + qn + ", " + ne);
            }
        }
        return qnames;
    }
    protected NodeRef getJSONNodeRef(JSONObject json, String key)
    {
        String nr = (String)json.get(key);
        if (nr == null) return null;
        
        return new NodeRef(nr);
    }
    protected Date getJSONDate(JSONObject json, String key)
    {
        String date = (String)json.get(key);
        if (date == null) return null;
        
        return ISO8601DateFormat.parse(date);
    }
    protected Map<QName,Serializable> getJSONProperties(JSONObject json)
    {
        JSONObject props = (JSONObject)json.get(JSON_PROPERTIES);
        if (props == null || props.isEmpty()) return null;
        
        Map<QName,Serializable> properties = new HashMap<QName, Serializable>();
        for (Object key : props.keySet())
        {
            try
            {
                QName qname = QName.createQName((String)key, namespaceService);
                JSONObject details = (JSONObject)props.get(key);
            
                String type = (String)details.get(JSON_PROP_TYPE);
                Object rawValue = details.get(JSON_PROP_VALUE);
                Serializable value = null;
            
                // Is it multi-valued?
                if (Boolean.TRUE.equals(details.get(JSON_PROP_MULTIVALUED)))
                {
                    List<Serializable> values = new ArrayList<Serializable>();
                    for (Object rv : (JSONArray)rawValue)
                    {
                        values.add(fromJSON(rv, type, true));
                    }
                    value = (Serializable)values;
                }
                else
                {
                    value = fromJSON(rawValue, type, false);
                }
            
                properties.put(qname, value);
            }
            catch (NamespaceException ne)
            {
                logger.warn("Cloud sync unable to process property for unknown namespace:" + key + ", " + ne);
            }
        }
        return properties;
    }
    private Serializable fromJSON(Object rawValue, String type, boolean isMV)
    {
        if (JSON_PROPTYPE_STRING.equals(type))
        {
            return (String)rawValue;
        }
        else if (JSON_PROPTYPE_DATE.equals(type))
        {
            return ISO8601DateFormat.parse((String)rawValue);
        }
        else if (JSON_PROPTYPE_BOOLEAN.equals(type))
        {
            return (Boolean)rawValue;
        }
        else if (JSON_PROPTYPE_INTEGER.equals(type))
        {
            // Integer may be decoded as either Int or Long 
            if (rawValue instanceof Long)
            {
                return ((Long)rawValue).intValue();
            }
            else
            {
                return (Integer)rawValue;
            }
        }
        else if (JSON_PROPTYPE_LONG.equals(type))
        {
            return (Long)rawValue;
        }
        else if (JSON_PROPTYPE_FLOAT.equals(type))
        {
            // Float may be decoded as either Float or Double
            if (rawValue instanceof Double)
            {
                return ((Double)rawValue).floatValue();
            }
            else
            {
                return (Float)rawValue;
            }
        }
        else if (JSON_PROPTYPE_DOUBLE.equals(type))
        {
            return (Double)rawValue;
        }
        else if (JSON_PROPTYPE_MLTEXT.equals(type))
        {
            JSONObject ml = (JSONObject)rawValue;
            MLText mlText = new MLText();
            
            for (Object k : ml.keySet())
            {
                Locale locale = I18NUtil.parseLocale((String)k);
                mlText.addValue(locale, (String)ml.get(k));
            }
            return mlText;
        }
        else if (JSON_PROPTYPE_NULL.equals(type))
        {
            return null;
        }
        else if (JSON_PROPTYPE_SERIALIZABLE.equals(type))
        {
            return (Serializable)Base64.decodeToObject((String)rawValue);
        }
        else
        {
            logger.warn("Unsupported object received: " + type + " of value: " + rawValue);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void setJSON(JSONObject json, String key, QName qname)
    {
        if (qname != null)
        {
            json.put(key, qname.toPrefixString(namespaceService));
        }
    }
    @SuppressWarnings("unchecked")
    protected void setJSON(JSONObject json, String key, Collection<QName> qnames)
    {
        if (qnames != null)
        {
            JSONArray qns = new JSONArray();
            for (QName qname : qnames)
            {
                qns.add(qname.toPrefixString(namespaceService));
            }
            json.put(key, qns);
        }
    }
    @SuppressWarnings("unchecked")
    protected void setJSON(JSONObject json, String key, NodeRef nodeRef)
    {
        if (nodeRef != null)
        {
            json.put(key, nodeRef.toString());
        }
    }
    @SuppressWarnings("unchecked")
    protected void setJSON(JSONObject json, String key, String value)
    {
        if (value != null)
        {
            json.put(key, value);
        }
    }
    @SuppressWarnings("unchecked")
    protected void setJSON(JSONObject json, String key, Date date)
    {
        if (date != null)
        {
            json.put(key, ISO8601DateFormat.format(date));
        }
    }
    @SuppressWarnings("unchecked")
    protected void setJSONProperties(JSONObject json, Map<QName,Serializable> props)
    {
        if (props == null || props.size() == 0) return;
        
        for (QName propQN : props.keySet())
        {
            JSONObject prop = new JSONObject();
            Serializable value = props.get(propQN);
            
            Object asJSON = null;
            if (value instanceof Collection)
            {
                // Multi-valued 
                JSONArray values = new JSONArray();
                for (Serializable v : ((Collection<Serializable>)value))
                {
                    values.add(toJSON(v, prop));
                }
                asJSON = values;
                prop.put(JSON_PROP_MULTIVALUED, true);
            }
            else
            {
                asJSON = toJSON(value, prop);
            }
            prop.put(JSON_PROP_VALUE, asJSON);
            
            String key = propQN.toPrefixString(namespaceService);
            json.put(key, prop);
        }
    }
    @SuppressWarnings("unchecked")
    private Object toJSON(Serializable value, JSONObject propDetials)
    {
        if (value instanceof String)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_STRING);
            return (String)value;
        }
        else if (value instanceof Date)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_DATE);
            return ISO8601DateFormat.format((Date)value);
        }
        else if (value instanceof Boolean)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_BOOLEAN);
            return (Boolean)value;
        }
        else if (value instanceof Integer)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_INTEGER);
            return (Integer)value;
        }
        else if (value instanceof Long)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_LONG);
            return (Long)value;
        }
        else if (value instanceof Float)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_FLOAT);
            return (Float)value;
        }
        else if (value instanceof Double)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_DOUBLE);
            return (Double)value;
        }
        else if (value instanceof MLText)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_MLTEXT);
            JSONObject ml = new JSONObject();
            MLText mlText = (MLText)value;
            for (Locale l : mlText.keySet())
            {
                ml.put(l.toString(), mlText.get(l));
            }
            return ml;
        }
        else if (value == null)
        {
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_NULL);
            return null;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No dedicated handler for property " + value + " of type " + value.getClass());
            }
            propDetials.put(JSON_PROP_TYPE, JSON_PROPTYPE_SERIALIZABLE);
            propDetials.put(JSON_PROP_CLASS, value.getClass().getName());
            return Base64.encodeObject(value);
        }
    }
    
    protected static final String JSON_SOURCE_NODEREF = "sourceNodeRef";
    protected static final String JSON_TARGET_NODEREF = "targetNodeRef";
    protected static final String JSON_SOURCE_PATH = "sourcePath";
    protected static final String JSON_TARGET_PATH = "targetPath";
    protected static final String JSON_SOURCE_PARENT_NODEREF = "sourceParentNodeRef";
    protected static final String JSON_TARGET_PARENT_NODEREF = "targetParentNodeRef";
    protected static final String JSON_SOURCE_MODIFIED_AT = "sourceModifiedAt";
    protected static final String JSON_TARGET_MODIFIED_AT = "targetModifiedAt";
    protected static final String JSON_SOURCE_VERSION_LABEL = "sourceVersionLabel";
    protected static final String JSON_TARGET_VERSION_LABEL = "targetVersionLabel";
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_SYNCSET = "syncSet";
    protected static final String JSON_DIRECT_SYNC = "directSync";
    protected static final String JSON_AUDIT_TOKEN = "auditToken";
    protected static final String JSON_AUDIT_TOKENS = "auditTokens";
    protected static final String JSON_ASPECTS_ADDED = "aspectsAdded";
    protected static final String JSON_ASPECTS_REMOVED = "aspectsRemoved";
    protected static final String JSON_CONTENT_PROPS_REMOVED = "contentPropsRemoved";
    
    protected static final String JSON_PROPERTIES = "properties";
    protected static final String JSON_PROP_TYPE  = "type";
    protected static final String JSON_PROP_VALUE = "value";
    protected static final String JSON_PROP_CLASS = "class";
    protected static final String JSON_PROP_MULTIVALUED = "multivalued";
    protected static final String JSON_PROPTYPE_STRING   = "string";
    protected static final String JSON_PROPTYPE_DATE     = "date";
    protected static final String JSON_PROPTYPE_BOOLEAN  = "boolean";
    protected static final String JSON_PROPTYPE_INTEGER  = "integer";
    protected static final String JSON_PROPTYPE_LONG     = "long";
    protected static final String JSON_PROPTYPE_FLOAT    = "float";
    protected static final String JSON_PROPTYPE_DOUBLE   = "double";
    protected static final String JSON_PROPTYPE_NULL     = "null";
    protected static final String JSON_PROPTYPE_MLTEXT   = "mltext";
    protected static final String JSON_PROPTYPE_SERIALIZABLE = "serializable";
    
    /**
     * A version of {@link MultipartRequestEntity} which supports all the different
     *  kinds of multipart content types (mixed etc), rather than only being
     *  hard coded to form
     */
    private static class ConfigurableMultipartRequestEntity extends MultipartRequestEntity
    {
        private String baseContentType;
        public ConfigurableMultipartRequestEntity(Part[] parts, HttpMethodParams params, String contentType)
        {
            super(parts, params);
            this.baseContentType = contentType;
        }
        
        public String getContentType() {
            StringBuffer buffer = new StringBuffer(baseContentType);
            buffer.append("; boundary=");
            buffer.append(EncodingUtil.getAsciiString(getMultipartBoundary()));
            return buffer.toString();
        }
    }
    
    /**
     * A wrapper around {@link ContentDataPart}, which uses raw readers
     *  and therefore must be run as System, to allow just the
     *  send call to be done as system
     */
    private static class RunAsSystemContentDataPart extends ContentDataPart
    {
        public RunAsSystemContentDataPart(ContentService contentService, String partName, ContentData data)
        {
            super(contentService, partName, data);
        }

        @Override
        protected long lengthOfData() throws IOException
        {
            return AuthenticationUtil.runAsSystem(new RunAsWork<Long>()
            {
                @Override
                public Long doWork() throws Exception
                {
                    return RunAsSystemContentDataPart.super.lengthOfData();
                }
            });
        }

        @Override
        protected void sendData(final OutputStream out) throws IOException
        {
            AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {
                @Override
                public Void doWork() throws Exception
                {
                    RunAsSystemContentDataPart.super.sendData(out);
                    return null;
                }
            });
        }
    }
}