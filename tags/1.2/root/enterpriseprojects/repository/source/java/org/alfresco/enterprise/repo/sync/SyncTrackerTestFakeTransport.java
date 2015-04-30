/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Test implementation of Cloud Sync Transport
 * <p>
 * Works to delegate requests to the SyncService on the same local repo.
 * <p>
 * Injects code to so a single server can be both a 
 * sync set source and a sync set target.    
 * <p>
 * The source and target sync sets are mapped.
 * 
 * @author mrogers
 * @since 4.1
 *
 */
public class SyncTrackerTestFakeTransport implements CloudSyncMemberNodeTransport, CloudSyncSetDefinitionTransport
{
    private String fakeRepoId;
    private SyncService syncService;
    private SyncAdminService syncAdminService;
    private SyncAuditService syncAuditService;
    private CloudSyncMemberNodeTransport realTransport; 
    
    public SyncTrackerTestFakeTransport(String fakeRepoId,
            SyncService syncService,  
            SyncAdminService syncAdminService, 
            SyncAuditService syncAuditService,
            CloudSyncMemberNodeTransport realTransport)
            
    {
        this.syncService = syncService;
        this.syncAdminService = syncAdminService;
        this.syncAuditService = syncAuditService;
        this.fakeRepoId = fakeRepoId;
        this.realTransport = realTransport;
    }
    
    /**
     * Source to Target Sync Set Mapping
     */
    public Map<String, SyncSetDefinition > syncSetMap = new HashMap<String,SyncSetDefinition>();
    
    public void init()
    {
    }
    
    
    @Override
    public NodeRef pushSyncInitial(SyncNodeChangesInfo syncNode,
            String cloudNetwork) throws SyncNodeException,
            NoSuchSyncSetDefinitionException, AuthenticationException,
            RemoteSystemUnavailableException
    {   
        SyncSetDefinition targetSyncSet = syncSetMap.get(syncNode.getSyncSetGUID());
        if(targetSyncSet == null)
        {
            throw new AlfrescoRuntimeException("Test error - target sync set not found");
        }
        
        SyncNodeChangesInfoImpl targetNode = new SyncNodeChangesInfoImpl(syncNode);
        
        targetNode.setSyncSetGUID(targetSyncSet.getId());
        
        if (syncNode.getRemoteParentNodeRef() != null)
        {
            targetNode.setLocalParentNodeRef(syncNode.getRemoteParentNodeRef());
        }
        else if (syncNode.getDirectSync())
        {
            targetNode.setLocalParentNodeRef(new NodeRef(targetSyncSet.getTargetFolderNodeRef()));
        }
        else
        {
            throw new IllegalArgumentException("Can't do an initial sync without a destination parent NodeRef");
        }
        
        NodeRef newNode = syncService.create(targetNode, true);
        return newNode;
    }

    @Override
    public void pushSyncChange(SyncNodeChangesInfo syncNode, String cloudNetwork)
            throws SyncNodeException, NoSuchSyncSetDefinitionException,
            ConcurrentModificationException, AuthenticationException,
            RemoteSystemUnavailableException
    {   
        SyncSetDefinition targetSyncSet = syncSetMap.get(syncNode.getSyncSetGUID());
        if(targetSyncSet == null)
        {
            throw new AlfrescoRuntimeException("Test error - target sync set not found");
        }
        
        SyncNodeChangesInfoImpl targetNode = new SyncNodeChangesInfoImpl(syncNode); 
        targetNode.setSyncSetGUID(targetSyncSet.getId());
        targetNode.setLocalParentNodeRef(new NodeRef(targetSyncSet.getTargetFolderNodeRef()));
        targetNode.setLocalNodeRef(syncNode.getRemoteNodeRef());
        
        syncService.update(targetNode);
    }

    @Override
    public void pushSyncDelete(SyncNodeChangesInfo syncNode, String cloudNetwork)
            throws SyncNodeException, NoSuchSyncSetDefinitionException,
            ConcurrentModificationException, AuthenticationException,
            RemoteSystemUnavailableException
    {
        SyncSetDefinition targetSyncSet = syncSetMap.get(syncNode.getSyncSetGUID());
        if(targetSyncSet != null)
        {
            SyncNodeChangesInfoImpl targetNode = new SyncNodeChangesInfoImpl(syncNode); 
            targetNode.setSyncSetGUID(targetSyncSet.getId());
            targetNode.setLocalParentNodeRef(new NodeRef(targetSyncSet.getTargetFolderNodeRef()));
            targetNode.setLocalNodeRef(syncNode.getRemoteNodeRef());
            
            // note: force delete (even if unpulled changes) - see ALF-15380
            syncService.delete(targetNode, true);
        }
        
    }

    @Override
    public void pushUnSync(SyncNodeChangesInfo syncNode, String cloudNetwork)
            throws SyncNodeException, NoSuchSyncSetDefinitionException,
            ConcurrentModificationException, AuthenticationException,
            RemoteSystemUnavailableException
    {
        SyncSetDefinition targetSyncSet = syncSetMap.get(syncNode.getSyncSetGUID());
        if(targetSyncSet != null)
        {
            SyncNodeChangesInfoImpl targetNode = new SyncNodeChangesInfoImpl(syncNode); 
            targetNode.setSyncSetGUID(targetSyncSet.getId());
            targetNode.setLocalParentNodeRef(new NodeRef(targetSyncSet.getTargetFolderNodeRef()));
            targetNode.setLocalNodeRef(syncNode.getRemoteNodeRef());
            
            // note: force unsync (even if unpulled changes) - see ALF-15380
            syncService.removeFromSyncSet(targetNode, true);
        }
        
    }

    @Override
    public SyncNodeChangesInfo pullSyncChange(SyncNodeChangesInfo stubLocal,
            String cloudNetwork) throws SyncNodeException,
            NoSuchSyncSetDefinitionException, ConcurrentModificationException,
            AuthenticationException, RemoteSystemUnavailableException
    {
    
        SyncNodeChangesInfoImpl targetNode = new SyncNodeChangesInfoImpl(stubLocal); 
        targetNode.setLocalNodeRef(stubLocal.getRemoteNodeRef());
 
        SyncNodeChangesInfo stuff = syncService.fetchForPull(targetNode);
        
        SyncNodeChangesInfoImpl retNode = new SyncNodeChangesInfoImpl(stuff); 

        if(stubLocal.getLocalNodeRef() == null)
        {
            retNode.setRemoteNodeRef(stuff.getLocalNodeRef());
            retNode.setLocalNodeRef(null);
        }
        else
        {
            retNode.setLocalNodeRef(stubLocal.getLocalNodeRef());
            retNode.setRemoteNodeRef(stuff.getLocalNodeRef());
        }        
        
        if(stubLocal.getRemoteParentNodeRef() == null)
        {
            retNode.setRemoteParentNodeRef(stuff.getLocalParentNodeRef());
            retNode.setLocalParentNodeRef(stuff.getRemoteParentNodeRef());
        }
        else
        {
            retNode.setRemoteParentNodeRef(stubLocal.getRemoteParentNodeRef());   
            retNode.setLocalParentNodeRef(stubLocal.getLocalParentNodeRef());
        }

  
        if(retNode.getSyncSetGUID() != null)
        {
            String targetGUID = null;
            for(Map.Entry<String, SyncSetDefinition> entry : syncSetMap.entrySet())
            {
                if(entry.getValue().getId().equalsIgnoreCase(retNode.getSyncSetGUID()))
                {
                    targetGUID=entry.getKey();
                }
            }
            retNode.setSyncSetGUID(targetGUID);
        }
       
        return retNode;
    }

    @Override
    public void confirmPull(AuditToken[] things, String cloudNetwork)
            throws AuthenticationException, RemoteSystemUnavailableException
    {
        syncAuditService.deleteAuditEntries(things);
    }

    @Override
    public void pushConflictDetected(SyncNodeChangesInfo stubLocal,
            String cloudNetwork) throws SyncNodeException,
            NoSuchSyncSetDefinitionException, AuthenticationException,
            RemoteSystemUnavailableException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public SyncNodeChangesInfo decodeMainJSON(FileItemStream jsonPart)
            throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CloudSyncContent decodeContent(FileItemStream contentPart) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyncNodeChangesInfo decodePullParameters(WebScriptRequest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultipartRequestEntity encodeSyncChanges(SyncNodeChangesInfo syncNode)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NodeRef fetchLocalDetailsAndApply(SyncNodeChangesInfo syncNode, boolean isOnCloud)
            throws ConcurrentModificationException
    {
        return realTransport.fetchLocalDetailsAndApply(syncNode, true);
    }

    @Override
    public void fetchLocalDetailsAndUnSync(SyncNodeChangesInfo syncNode,
            boolean deleteOnUnSync) throws ConcurrentModificationException
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void createSSD(String ssdId, NodeRef targetFolderNodeRef,
            boolean includeSubFolders, String sourceRepoId, boolean isDeleteOnCloud, boolean isDeleteOnPrem, String cloudNetwork)
            throws AuthenticationException, RemoteSystemUnavailableException
    {
        // Monkey around with sync set id here
        syncAdminService.createTargetSyncSet(ssdId, sourceRepoId, targetFolderNodeRef, includeSubFolders, isDeleteOnPrem, isDeleteOnPrem);
    }


    @Override
    public void deleteSSD(String ssdId, String cloudNetwork)
            throws AuthenticationException, RemoteSystemUnavailableException,
            NoSuchSyncSetDefinitionException
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public List<String> pullChangedSSDs(String repoId)
            throws AuthenticationException, RemoteSystemUnavailableException
    {
        List<String> targetSyncSets = syncAuditService.querySsdManifest(fakeRepoId, 50);
        
        if(targetSyncSets == null)
        {
            return null;
        }
        
        // need to return source sync sets id rather than target sync set id.
        List<String>  sourceSyncSets = new ArrayList<String>(targetSyncSets.size());
        
        for(String targetSyncSet : targetSyncSets)
        {
            for(Map.Entry<String, SyncSetDefinition> entry : syncSetMap.entrySet())
            {
                if(entry.getValue().getId().equalsIgnoreCase(targetSyncSet))
                {
                    sourceSyncSets.add(entry.getKey());
                }
            }
        }
        
        return sourceSyncSets;
    }


    @Override
    public List<NodeRef> pullChangedNodesForSSD(String ssd, String cloudNetwork)
            throws AuthenticationException, RemoteSystemUnavailableException,
            NoSuchSyncSetDefinitionException
    {
        
        SyncSetDefinition targetSyncSet = syncSetMap.get(ssd);
        if(targetSyncSet != null)
        {
            List<SyncChangeEvent> syncChangeEvents = syncAuditService.queryBySsdId(targetSyncSet.getId(), 50);
        
            if(syncChangeEvents == null)
            {
               return new ArrayList<NodeRef>();
            }
        
            List<NodeRef> folders = new ArrayList<NodeRef>(syncChangeEvents.size());
            List<NodeRef> files   = new ArrayList<NodeRef>(syncChangeEvents.size());
        
            for (SyncChangeEvent event : syncChangeEvents)
            {
                NodeRef targetNodeRef = event.getNodeRef();
            
                if ((event.getNodeType() != null) && (event.getNodeType().equals(ContentModel.TYPE_FOLDER)))
                {
                    if (! folders.contains(targetNodeRef))
                    {
                        folders.add(targetNodeRef);
                    }
                }
                else
                {
                    if (! files.contains(targetNodeRef))
                    {
                        files.add(targetNodeRef);
                    }
                }
            }
     
            List<NodeRef> nodeRefs = new ArrayList<NodeRef>( folders.size() + files.size() );
            nodeRefs.addAll(folders); 
            nodeRefs.addAll(files);
        
            return nodeRefs;
        }
        
        return new ArrayList<NodeRef>(); 
        
        
    }





    

}
