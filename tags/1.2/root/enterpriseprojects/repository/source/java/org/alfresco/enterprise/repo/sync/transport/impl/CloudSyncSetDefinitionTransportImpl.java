/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.SyncServiceImpl;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorClientException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.Status;

/**
 * Implementation of the service {@link CloudSyncSetDefinitionTransport} which
 * performs Sync Set Definition operations on the cloud 
 * 
 * @author Nick Burch, Jan Vonka, Mark Rogers
 * @since 4.1
 */
public class CloudSyncSetDefinitionTransportImpl implements CloudSyncSetDefinitionTransport
{
    private static final Log log = LogFactory.getLog(CloudSyncSetDefinitionTransportImpl.class);

    private CloudConnectorService cloudConnectorService;
    
    public void setCloudConnectorService(CloudConnectorService cloudConnectorService)
    {
        this.cloudConnectorService = cloudConnectorService;
    }
    
    private int MAX_ITEMS = 1024;
    
    /**
     * This method makes a remote HTTP call to the target (Cloud) Alfresco instance requesting that the SSD be created there.
     * 
     * @param cloudNetwork        the network ID on the cloud, which contains the target folder.
     * @param targetFolderNodeRef the NodeRef of the targetFolder.
     * @param includeSubFolders   only applies to folder sync (ignored for direct file syncs).
     * @param ssdId               the {@link SyncModel#PROP_SYNC_GUID SSD ID}.
     * @param sourceRepoId        the repo id of the source repo.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void createSSD(String ssdId, NodeRef targetFolderNodeRef, boolean includeSubFolders, String sourceRepoId, boolean isDeleteOnCloud, boolean isDeleteOnPrem, String cloudNetwork)
            throws AuthenticationException, RemoteSystemUnavailableException
    {
        final String cloudUrl = "/enterprise/sync/remotesyncsetdefinitions";
        RemoteConnectorRequest rcr = cloudConnectorService.buildCloudRequest(cloudUrl, cloudNetwork, "POST");
        
        JSONObject obj = new JSONObject();
        obj.put(SyncServiceImpl.PARAM_SSD_ID, ssdId);
        obj.put(SyncServiceImpl.PARAM_SOURCE_REPO_ID, sourceRepoId);
        obj.put(SyncServiceImpl.PARAM_TARGET_FOLDER_NODEREF, targetFolderNodeRef.toString());
        obj.put(SyncServiceImpl.PARAM_INCLUDE_SUBFOLDERS, includeSubFolders);
        obj.put(SyncServiceImpl.PARAM_IS_DELETE_ON_CLOUD, isDeleteOnCloud);
        obj.put(SyncServiceImpl.PARAM_IS_DELETE_ON_PREM, isDeleteOnPrem);
        
        StringWriter stringWriter = new StringWriter();
        
        try
        {
            obj.writeJSONString(stringWriter);
        } catch (IOException iox)
        {
            throw new AlfrescoRuntimeException("Error creating JSON", iox);
        }
        
        String jsonString = stringWriter.toString();
        
        rcr.setContentType(MimetypeMap.MIMETYPE_JSON);
        rcr.setRequestBody(jsonString);
        
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Making call to Cloud for creation of remote SSD: "+ssdId);
            }
            
            RemoteConnectorResponse response = cloudConnectorService.executeCloudRequest(rcr);
            final int httpStatus = response.getStatus();
            
            if (log.isDebugEnabled())
            {
                log.debug("Call complete. Status = " + httpStatus);
            }
            // The remote call could throw authentication exceptions, which we'll just allow to bubble up the call stack.
        } 
        catch (IOException e)
        {
            throw new RemoteSystemUnavailableException("Unable to talk to Target Server", e);
        }
        catch(AlfrescoRuntimeException re)
        {
            if (re.getCause() instanceof IOException)
            {
                // IOExceptions can get wrapped, report
                throw new RemoteSystemUnavailableException("Unable to talk to Target Server", re.getCause());
            }
            else
            {
                // Some other runtime exception, bubble up
                throw re;
            }
        }
    }
    
    /**
     * This method makes a remote HTTP call to the target (Cloud) Alfresco instance requesting that the SSD be deleted there.
     * 
     * TODO Should we send our RepoId as a kind of authentication token?
     * 
     * @param cloudNetwork      the network ID on the cloud, which contains the SSD.
     * @param ssdId             the {@link SyncModel#PROP_SYNC_GUID SSD ID}.
     */
    @Override
    public void deleteSSD(String ssdId, String cloudNetwork) throws AuthenticationException,
            RemoteSystemUnavailableException, NoSuchSyncSetDefinitionException
    {
        StringBuffer url = new StringBuffer();
        url.append("/enterprise/sync/remotesyncsetdefinitions");
        url.append('?');
        url.append(SyncServiceImpl.PARAM_SSD_ID);
        url.append('=');
        url.append(ssdId);
        
        RemoteConnectorRequest rcr = cloudConnectorService.buildCloudRequest(url.toString(), cloudNetwork, "DELETE");
        
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Making call to Cloud for deletion of remote SSD: "+ssdId);
            }
            
            RemoteConnectorResponse response = cloudConnectorService.executeCloudRequest(rcr);
            final int httpStatus = response.getStatus();
            
            if (log.isDebugEnabled())
            {
                log.debug("Call complete. Status = " + httpStatus);
            }
        // The remote call could throw authentication exceptions, which we'll just allow to bubble up the call stack.
        }
        catch (RemoteConnectorClientException rce)
        {
            if (rce.getStatusCode() == Status.STATUS_NOT_FOUND)
            {
                // Sync Set not found with that ID
                throw new NoSuchSyncSetDefinitionException(rce.getStatusText(), ssdId);
            }
            else
            {
                // Some other problem we weren't excepting
                throw new RemoteSystemUnavailableException("Error communicating with Target Server", rce);
            }
        }
        catch (IOException e)
        {
            throw new RemoteSystemUnavailableException("Unable to talk to Target Server", e);
        }
        catch(AlfrescoRuntimeException re)
        {
            if (re.getCause() instanceof IOException)
            {
                // IOExceptions can get wrapped, report
                throw new RemoteSystemUnavailableException("Unable to talk to Target Server", re.getCause());
            }
            else
            {
                // Some other runtime exception, bubble up
                throw re;
            }
        }
    }

    /**
     * Fetches the list of SSD IDs of all the Sync Sets which have changes 
     *  on the cloud, for the specified repository, across all cloud networks 
     */
    @SuppressWarnings("unchecked")
    public List<String> pullChangedSSDs(String repoId) throws
        AuthenticationException, RemoteSystemUnavailableException
    {
        // Ask the cloud for changes
        try
        {
            RemoteConnectorRequest req = cloudConnectorService.buildCloudRequest("/enterprise/sync/syncsetmanifest?srcRepoId="+repoId+"&maxItems="+MAX_ITEMS, null, "GET");
            RemoteConnectorResponse resp = cloudConnectorService.executeCloudRequest(req);
            
            String contentAsString = resp.getResponseBodyAsString();
            if (contentAsString == null || contentAsString.length() == 0)
            {
                throw new RemoteSystemUnavailableException("JSON response required but none received");
            }
            
            JSONArray jsonRsp = (JSONArray) JSONValue.parse(contentAsString);
            return (List<String>)jsonRsp;
        }
        catch (IOException clientEx)
        {
            throw new RemoteSystemUnavailableException("Error from cloud", clientEx);
        }
        catch (RemoteConnectorClientException clientEx)
        {
            throw new RemoteSystemUnavailableException("Error from cloud", clientEx);
        }
        catch(AlfrescoRuntimeException re)
        {
            if (re.getCause() instanceof IOException)
            {
                // IOExceptions can get wrapped, report
                throw new RemoteSystemUnavailableException("Error from cloud", re.getCause());
            }
            else
            {
                // Some other runtime exception, bubble up
                throw re;
            }
        }
    }

    /**
     * Fetches the list of remote NodeRefs for a given SSD for which
     *  changes exist on the cloud.
     */
    public List<NodeRef> pullChangedNodesForSSD(String ssdId, String cloudNetwork) throws
        AuthenticationException, RemoteSystemUnavailableException, NoSuchSyncSetDefinitionException
    {
        // Ask the cloud for changes
        try
        {
            RemoteConnectorRequest req = cloudConnectorService.buildCloudRequest("/enterprise/sync/syncsetchanges?ssdId="+ssdId+"&maxItems="+MAX_ITEMS, cloudNetwork, "GET");
            RemoteConnectorResponse resp = cloudConnectorService.executeCloudRequest(req);
            
            String contentAsString = resp.getResponseBodyAsString();
            if (contentAsString == null || contentAsString.length() == 0)
            {
                throw new RemoteSystemUnavailableException("JSON response required but none received");
            }
            
            // Parse and process
            JSONArray jsonRsp = (JSONArray) JSONValue.parse(contentAsString);
            List<NodeRef> changedNodeRefs = new ArrayList<NodeRef>(jsonRsp.size());
            
            @SuppressWarnings("unchecked")
            Iterator<String> itr = jsonRsp.iterator();
            while (itr.hasNext())
            {
                String nodeRefString = itr.next();
                nodeRefString = nodeRefString.replace("\\", "");
                changedNodeRefs.add(new NodeRef(nodeRefString));
            }
            
            return changedNodeRefs;
        }
        catch (IOException clientEx)
        {
            throw new RemoteSystemUnavailableException("Error from cloud", clientEx);
        }
        catch (RemoteConnectorClientException clientEx)
        {
            if (clientEx.getStatusCode() == Status.STATUS_NOT_FOUND)
            {
                // The Sync Set Definition wasn't found
                throw new NoSuchSyncSetDefinitionException(clientEx.getMessage(), ssdId);
            }
            else
            {
                throw new RemoteSystemUnavailableException("Error from cloud", clientEx);
            }
        }
        catch(AlfrescoRuntimeException re)
        {
            if (re.getCause() instanceof IOException)
            {
                // IOExceptions can get wrapped, report
                throw new RemoteSystemUnavailableException("Error from cloud", re.getCause());
            }
            else
            {
                // Some other runtime exception, bubble up
                throw re;
            }
        }
    }
}
