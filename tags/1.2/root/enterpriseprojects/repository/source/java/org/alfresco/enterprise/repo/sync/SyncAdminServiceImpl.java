/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.alfresco.enterprise.license.InvalidLicenseEvent;
import org.alfresco.enterprise.license.ValidLicenseEvent;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.remotecredentials.BaseCredentialsInfo;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.util.GUID;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * This class only contains the business logic for the service. The lower level persistence code (calls to NodeService etc)
 * are in {@link SyncSetDefinitionStorage}.
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public class SyncAdminServiceImpl implements SyncAdminService, 
    ApplicationListener<ApplicationEvent>
{
    private static final Log logger = LogFactory.getLog(SyncAdminServiceImpl.class);
    
    private CloudConnectorService cloudConnectorService;
    private CloudSyncSetDefinitionTransport cloudSyncSetDefinitionTransport;
    private SyncSetDefinitionStorage syncSetDefinitionStorage;
    private SsdIdMappingStrategy ssdIdMappingStrategy;
    private SyncChangeMonitor syncChangeMonitor;
    private DescriptorService descriptorService;
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private PermissionService permissionService;
    private LockService lockService;
    private ServerModeProvider serverModeProvider;
    
    // Does the sync mode depend upon a valid license key.
    private boolean checkLicenseForSyncMode = true;
    // on-premise, sync is normally allowed for all
    private boolean isSyncEnabledForAllTenants = true;
    
    public void init()
    {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "permissionService", permissionService);
        PropertyCheck.mandatory(this, "fileFolderService", fileFolderService);
        PropertyCheck.mandatory(this, "descriptorService", descriptorService);
        PropertyCheck.mandatory(this, "syncChangeMonitor", syncChangeMonitor);
        PropertyCheck.mandatory(this, "ssdIdMappingStrategy", ssdIdMappingStrategy);
        PropertyCheck.mandatory(this, "syncSetDefinitionStorage", syncSetDefinitionStorage);
        PropertyCheck.mandatory(this, "cloudSyncSetDefinitionTransport", cloudSyncSetDefinitionTransport);
        PropertyCheck.mandatory(this, "cloudConnectorService", cloudConnectorService);
        PropertyCheck.mandatory(this, "lockService", getLockService());
    }
    
    public void setCloudConnectorService(CloudConnectorService cloudConnectorService)
    {
        this.cloudConnectorService = cloudConnectorService;
    }
    
    public void setCloudSyncSetDefinitionTransport(CloudSyncSetDefinitionTransport cloudSyncSetDefinitionTransport)
    {
        this.cloudSyncSetDefinitionTransport = cloudSyncSetDefinitionTransport;
    }

    public void setSyncSetDefinitionStorage(SyncSetDefinitionStorage syncSetDefinitionStorage)
    {
        this.syncSetDefinitionStorage = syncSetDefinitionStorage;
    }
    public void setSsdIdMappingStrategy(SsdIdMappingStrategy strategy)
    {
        this.ssdIdMappingStrategy = strategy;
    }
    
    public void setSyncChangeMonitor(SyncChangeMonitor monitor)
    {
        this.syncChangeMonitor = monitor;
    }
    
    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }
    
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    @Override 
    public SyncSetDefinition createSourceSyncSet(List<NodeRef> syncSetMembersIn,
            String remoteTenantId, 
            String targetFolderNodeRefStr,
            boolean lockSourceCopy,
            boolean isDeleteOnCloud,
            boolean isDeleteOnPrem)
    {
        return createSourceSyncSet(syncSetMembersIn, remoteTenantId, targetFolderNodeRefStr, lockSourceCopy, false, isDeleteOnCloud,
                isDeleteOnPrem);
    }
    
    @Override
    public boolean isTenantEnabledForSync(String tenantDomain)
    {
        return isSyncEnabledForAllTenants; // normally True on-premise
    }
    
    @Override 
    public SyncSetDefinition createSourceSyncSet(List<NodeRef> syncSetMembersIn,
                                                 String remoteTenantId, 
                                                 String targetFolderNodeRefStr,
                                                 boolean lockSourceCopy,
                                                 boolean includeSubFolders,
                                                 boolean isDeleteOnCloud,
                                                 boolean isDeleteOnPrem)
    {
        Long startTime = null;
        if (logger.isInfoEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        ParameterCheck.mandatoryCollection("syncSetMembers", syncSetMembersIn);
        
        ParameterCheck.mandatoryString("remoteTenantId", remoteTenantId);
        ParameterCheck.mandatoryString("targetFolderNodeRef", targetFolderNodeRefStr);
        NodeRef targetFolderNodeRef = new NodeRef(targetFolderNodeRefStr);
        
        
        if(serverModeProvider.getServerMode() != ServerMode.PRODUCTION)
        {
        	throw new AlfrescoRuntimeException("Unable to create sync set, not in PRODUCTION mode");
        }
        
        // Ensure they have credentials
        BaseCredentialsInfo remoteCredentials = cloudConnectorService.getCloudCredentials(); 
        if (remoteCredentials == null)
        {
            throw new AuthenticationException("No Cloud Credentials exist for the current user");
        }
        
        // We're using a LinkedList as we may need to delete entries during iteration.
        List<NodeRef> syncSetMembers = new LinkedList<NodeRef>();
        syncSetMembers.addAll(syncSetMembersIn);
        
        // Some pre-checks for sync eligibility:
        NodeRef rootFolderSyncNodeRef = null;
        
        // We may find some or all of the provided nodes are unsyncable. We'll keep those nodes in case we need to throw an exception.
        List<NodeRef> unsyncableNodeRefs = new ArrayList<NodeRef>();
        
        // We're syncing one file or one folder
        if (syncSetMembers.size() == 1)
        {
            NodeRef nodeRef = syncSetMembers.get(0);
            FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);
            
            if (fileInfo == null)
            {
                List<NodeRef> nodeRefs = new ArrayList<NodeRef>(1);
                nodeRefs.add(nodeRef);
                // We can immediately fail. If there's only one node and it's not acceptable for syncing, that's an error.
                throw new SyncSetCreationConflictException("Cannot create SyncSet Definition. At least one of the nodes is not a file or folder: ", nodeRefs);
            }
            
            if (fileInfo.isFolder())
            {
                rootFolderSyncNodeRef = nodeRef;
            }
            // It must implicitly be a file.
            else
            {
                // Intentionally empty. We only need to set the rootFolder if we have a folder sync.
            }
        }
        else
        {
            // we're syncing more than one files or folders
            for (NodeRef nodeRef : syncSetMembers)
            {
                FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);
                if (fileInfo == null)
                {
                    // If a node is not a file or folder, we will silently skip it - See User Stories US(4).
                    unsyncableNodeRefs.add(nodeRef);
                    syncSetMembers.remove(nodeRef);
                }
                
                else if (fileInfo.isFolder())
                {
                    if (syncSetMembers.size() > 1)
                    {
                        // We do not support syncing multiple folders in one set.
                        List<NodeRef> nodeRefs = new ArrayList<NodeRef>(1);
                        nodeRefs.add(nodeRef);
                        throw new SyncSetCreationConflictException("Cannot create SyncSet Definition. More than one node for folder sync: ", nodeRefs);
                    }
                }
            }
        }
        
        // 2. Are any of the local nodes already in a syncset?
        List<NodeRef> nodesThatAreAlreadySynced = syncSetDefinitionStorage.getAlreadySyncedNodes(syncSetMembers);
        if ( !nodesThatAreAlreadySynced.isEmpty())
        {
            // Skip any nodes that are already synced elsewhere. User Stories US(4)
            syncSetMembers.removeAll(nodesThatAreAlreadySynced);
            unsyncableNodeRefs.addAll(nodesThatAreAlreadySynced);
        }
        
        // 3. Are any nodes not writable by the current user?
        List<NodeRef> nodesThatAreNotWritable = syncSetDefinitionStorage.getUnwritableNodes(syncSetMembers);
        if ( !nodesThatAreNotWritable.isEmpty())
        {
            syncSetMembers.removeAll(nodesThatAreNotWritable);
            unsyncableNodeRefs.addAll(nodesThatAreNotWritable);
        }
        
        // Having checked all the provided NodeRefs, if we find that none of them are syncable, that's an error.
        if (syncSetMembers.isEmpty())
        {
            throw new SyncSetCreationConflictException("Cannot create SyncSet Definition. None of the provided nodes are suitable.", unsyncableNodeRefs);
        }
        
        // The SSDs (on source & target Alfrescos) will need a GUID.
        final String ssdId = GUID.generate();
        // and the remote SSD will need to know the repo ID of the On Premise repo.
        final String sourceRepoId = descriptorService.getCurrentRepositoryDescriptor().getId();
        
        // Synchronously make a call to the Target Alfresco (the Cloud) to have the equivalent SSD created there too.
        try
        {
            cloudSyncSetDefinitionTransport.createSSD(ssdId, targetFolderNodeRef, includeSubFolders, sourceRepoId, isDeleteOnCloud, isDeleteOnPrem, remoteTenantId);
        }
        catch (RemoteSystemUnavailableException e)
        {
            throw new SyncAdminServiceException("Unable to talk to Target Server", e);
        }
        
        
        // Create a local (On Premise) sync set definition with a generated id and the sourceRepoId (the Source copy).
        SyncSetDefinition ssd = new SyncSetDefinition(ssdId, sourceRepoId);
        ssd.setRemoteCredentials(remoteCredentials);
        ssd.setRemoteTenantId(remoteTenantId);
        ssd.setTargetFolderNodeRef(targetFolderNodeRefStr);
        ssd.setLockSourceCopy(lockSourceCopy);
        ssd.setIncludeSubFolders(includeSubFolders);
        ssd.setDeleteOnCloud(isDeleteOnCloud);
        ssd.setDeleteOnPrem(isDeleteOnPrem);
        
        
        // And create the NodeRef
        NodeRef ssdNodeRef = syncSetDefinitionStorage.createSyncSetDefinitionNode(ssd);
        ssd.setNodeRef(ssdNodeRef);
        
        // add the member nodes to the set
        if (rootFolderSyncNodeRef != null)
        {
            // folder sync - direct sync from point-of-view of this root/parent folder
            addSyncSetMembers(ssd, rootFolderSyncNodeRef, true, includeSubFolders);
        }
        else
        {
            // file sync
            addSyncSetMembers(ssd, syncSetMembers, true);
        }
        
        if (startTime != null)
        {
            StringBuilder sb = new StringBuilder("createSourceSyncSet: ");
            sb.append(ssd).append(" [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
            logger.info(sb.toString());
        }
        
        return ssd;
    }
    
    // recursive (if includeSubFolders) - note: we do not include the top-level parentNodeRef
    private void getChildren(NodeRef parentNodeRef, boolean includeSubFolders, List<NodeRef> childNodeRefs, boolean skipSyncedChildren)
    {
        //MNT-10664 The file with applied "sys:hidden" aspect in synced folder is not synced after removing the aspect
        List<FileInfo> children = new ArrayList<FileInfo>();
        if (includeSubFolders)
        {
            List<ChildAssociationRef> allChildren = nodeService.getChildAssocs(parentNodeRef);

            for (ChildAssociationRef assocRef : allChildren)
            {
                children.add(fileFolderService.getFileInfo(assocRef.getChildRef()));
            }
        }
        else
        {
            children = fileFolderService.listFiles(parentNodeRef);
        }
        
        for (FileInfo child : children)
        {
            NodeRef childNodeRef = child.getNodeRef();
            if (skipSyncedChildren && isSyncSetMemberNode(childNodeRef))
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("getChildren: skip "+(child.isFolder() ? "folder" : "file")+" - already sync'ed: "+childNodeRef);
                }
            }
            else if (nodeService.hasAspect(childNodeRef, ContentModel.ASPECT_WORKING_COPY))
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("getChildren: skip working copy: "+childNodeRef);
                }
            }
            else
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("getChildren: add "+(child.isFolder() ? "folder" : "file")+" to list: "+childNodeRef);
                }
                childNodeRefs.add(childNodeRef);
            }
            
            if (includeSubFolders && child.isFolder())
            {
                // recurse
                getChildren(childNodeRef, includeSubFolders, childNodeRefs, skipSyncedChildren);
            }
        }
    }
    
    @Override 
    public SyncSetDefinition createTargetSyncSet(String guid, 
    		String srcRepoId, 
    		NodeRef targetFolderNodeRef, 
    		boolean includeSubFolders,
    		boolean isDeleteOnCloud,
            boolean isDeleteOnPrem)
    {
        Long startTime = null;
        if (logger.isInfoEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        // TODO Parameter checking
        
        // Ensure that no SSD with this GUID exists already. This should never happen - it could only
        // happen if a Source and a Target SSD were created in the same repo, which would be a programmer error.
        SyncSetDefinition ssd = getSyncSetDefinition(guid);
        if (ssd != null)
        {
            final String msg = "Unexpectedly found an existing SyncSetDefinition with guid " + guid;
            if (logger.isWarnEnabled())
            {
                logger.error(msg);
            }
            throw new SyncAdminServiceException(msg);
        }
        
        // Convert the SSD ID to its Cloud equivalent.
        String cloudSsdId = ssdIdMappingStrategy.getCloudGUID(guid);
        
        SyncSetDefinition newSSD = new SyncSetDefinition(cloudSsdId, srcRepoId);
        newSSD.setTargetFolderNodeRef(targetFolderNodeRef.toString());
        newSSD.setIncludeSubFolders(includeSubFolders);
        newSSD.setDeleteOnCloud(isDeleteOnCloud);
        newSSD.setDeleteOnPrem(isDeleteOnPrem);

        
        // Before we create the 'Cloud' copy of the SyncSetDefinition node, we need to suppress the
        // behaviour that would cause a sync-audit entry to appear in the log (SSD_CREATED)
        NodeRef ssdNodeRef;
        try
        {
            syncChangeMonitor.disableSyncBehaviours();
            ssdNodeRef = syncSetDefinitionStorage.createSyncSetDefinitionNode(newSSD);
        }
        finally
        {
            syncChangeMonitor.enableSyncBehaviours();
        }
        newSSD.setNodeRef(ssdNodeRef);
        
        if (startTime != null)
        {
            StringBuilder sb = new StringBuilder("createTargetSyncSet: ");
            sb.append(newSSD).append(" [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
            logger.info(sb.toString());
        }
        
        return newSSD;
    }
    
    @Override public void deleteSourceSyncSet(String ssdId)
    {
        SyncSetDefinition ssd = getSyncSetDefinition(ssdId);
        
        if (ssd != null)
        {
            // delete on source
            deleteLocalSyncSet(ssdId);
            
            // delete on target
            cloudSyncSetDefinitionTransport.deleteSSD(ssdId, ssd.getRemoteTenantId());
        }
    }
    
    @Override public void deleteTargetSyncSet(String ssdId)
    {
        deleteLocalSyncSet(ssdId);
    }
    
    private void deleteLocalSyncSet(String ssdId)
    {
        syncSetDefinitionStorage.deleteSyncSetDefinition(ssdId);
    }
    
    @Override public SyncSetDefinition getSyncSetDefinition(final String ssdId)
    {
        return syncSetDefinitionStorage.getSyncSetDefinition(ssdId);
    }
    
    @Override public SyncSetDefinition getSyncSetDefinition(final NodeRef nodeRef)
    {
        return syncSetDefinitionStorage.getSyncSetDefinition(nodeRef);
    }
    
    @Override public void addSyncSetMember(SyncSetDefinition ssd, NodeRef newMemberNode)
    {
        addSyncSetMembers(ssd, newMemberNode, true, false);
    }
    
    @Override public void addSyncSetMembers(SyncSetDefinition ssd, List<NodeRef> syncSetMembers, boolean directSync)
    {
        for (NodeRef memberNodeRef : syncSetMembers)
        {
        	addSyncSetMembers(ssd, memberNodeRef, directSync, false);
        }
    }
    
    private void addNodeToSyncSet(NodeRef ssdNodeRef, NodeRef memberNodeRef, boolean directSync, boolean isDeleteOnPrem)
    {
    	boolean suspendLocks = false;
    	try
    	{
            if (lockService.getLockStatus(memberNodeRef) != LockStatus.NO_LOCK)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("addNodeToSyncSet: node to update is locked - suspend lock service: "+memberNodeRef);
                }
                lockService.suspendLocks();
                suspendLocks = true;
            }
            syncSetDefinitionStorage.addNodeToSyncSet(ssdNodeRef, memberNodeRef, directSync, isDeleteOnPrem);
    	}
    	finally
    	{
            if(suspendLocks)
            {
        	    lockService.enableLocks();
            }
    	}
    }
    
    @Override public void addSyncSetMembers(SyncSetDefinition ssd, NodeRef newMemberNode, boolean directSync, boolean includeSubFolders)
    {
        if (! nodeService.hasAspect(newMemberNode, ContentModel.ASPECT_WORKING_COPY))
        {
            NodeRef ssdNodeRef = ssd.getNodeRef();
            boolean isDeleteOnPrem = ssd.isDeleteOnPrem();
            FileInfo fileInfo = fileFolderService.getFileInfo(newMemberNode);
            if (fileInfo != null)
            {
                if (fileInfo.isFolder())
                {
                    addNodeToSyncSet(ssdNodeRef, newMemberNode, directSync, isDeleteOnPrem); // add parent folder
                    
                    List<NodeRef> children = new ArrayList<NodeRef>(10);
                    getChildren(newMemberNode, includeSubFolders, children, true);
                    
                    for (NodeRef childNodeRef : children)
                    {
                        addNodeToSyncSet(ssdNodeRef, childNodeRef, false, isDeleteOnPrem); // by definition children of folder sync will be indirect
                    }
                }
                else
                {
                    addNodeToSyncSet(ssdNodeRef, newMemberNode, directSync, isDeleteOnPrem);
                }
            }
        }
        else
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("addSyncSetMembers: skip working copy: "+newMemberNode);
            }
        }
    }
    
    @Override 
    public void removeSyncSetMember(SyncSetDefinition ssd, NodeRef existingMemberNode)
    {
        removeSyncSetMember(ssd, existingMemberNode, false);
    }
    
    @Override 
    public void removeSyncSetMember(SyncSetDefinition ssd, NodeRef existingMemberNode, boolean deleteRemote)
    {
        removeSyncSetMemberImpl(ssd.getNodeRef(), existingMemberNode, deleteRemote);
        
        // re-sync, if needed
        NodeRef parentRef = nodeService.getPrimaryParent(existingMemberNode).getParentRef();
        if (parentRef != null)
        {
            SyncSetDefinition ssdParent = getSyncSetDefinition(parentRef);
            if ((ssdParent != null) && (! ssdParent.equals(ssd)))
            {
                // indirect sync
                addSyncSetMembers(ssdParent, existingMemberNode, false, ssdParent.getIncludeSubFolders());
            }
        }
    }
    
    private void removeSyncSetMemberImpl(NodeRef ssdNodeRef, NodeRef memberToRemove, boolean deleteRemote)
    {
        SyncSetDefinition ssd = getSyncSetDefinition(ssdNodeRef);
        if ( ssd != null && !isSyncSetMemberNode(memberToRemove, ssd))
        {
            throw new NodeNotSyncRelatedException("Node was not a sync member: " + memberToRemove);
        }
        
        FileInfo fileInfo = fileFolderService.getFileInfo(memberToRemove);
        if (fileInfo != null)
        {
            if (fileInfo.isFolder())
            {
                //We want to remove all children of this folder from the sync set....
                AuthenticationUtil.pushAuthentication();
                try
                {
                    //We want to make sure that we find all the children which are members of this sync set below this folder
                    //so we run as the system user temporarily.
                    AuthenticationUtil.setRunAsUserSystem();
                    List<NodeRef> children = new ArrayList<NodeRef>(100);
                    getChildren(memberToRemove, ssd.getIncludeSubFolders(), children, false);
                    Set<NodeRef> allMemberNodes = new HashSet<NodeRef>(syncSetDefinitionStorage.getMemberNodes(ssdNodeRef));
                    
                    for (NodeRef childNodeRef : children)
                    {
                        if (allMemberNodes.contains(childNodeRef))
                        {
                            removeNodeFromSyncSetImpl(ssdNodeRef, childNodeRef, deleteRemote);
                        }
                    }
                }
                finally
                {
                    AuthenticationUtil.popAuthentication();
                }
            }
            removeNodeFromSyncSetImpl(ssdNodeRef, memberToRemove, deleteRemote);
        }
    }
    
    private void removeNodeFromSyncSetImpl(NodeRef ssdNodeRef, NodeRef memberToRemove, boolean deleteRemote)
    {
        if (deleteRemote)
        {
            // explicitly record a delete event (in order to delete remote)
            syncChangeMonitor.beforeDeleteSsmnNode(memberToRemove);
        }
        
        syncSetDefinitionStorage.removeNodeFromSyncSet(ssdNodeRef, memberToRemove);
    }
    
    
    @Override public NodeRef getSyncSetDefinitionsFolder()
    {
        return syncSetDefinitionStorage.getOrCreateSyncSetDefinitionContainer();
    }
    
    @Override public boolean hasSyncSetDefintions()
    {
        return syncSetDefinitionStorage.hasSyncSetDefintions();
    }

    @Override public List<SyncSetDefinition> getSyncSetDefinitions()
    {
        return syncSetDefinitionStorage.getSyncSetDefinitions();
    }
    
    @Override public List<NodeRef> getMemberNodes(SyncSetDefinition ssd)
    {
        return syncSetDefinitionStorage.getMemberNodes(ssd.getNodeRef());
    }
    
    @Override public boolean isSyncSetMemberNode(NodeRef localSyncMemberNode)
    {
        return syncSetDefinitionStorage.isSyncSetMemberNode(localSyncMemberNode);
    }
    
    @Override public boolean isDirectSyncSetMemberNode(NodeRef localSyncMemberNode)
    {
        return syncSetDefinitionStorage.isDirectSyncSetMemberNode(localSyncMemberNode);
    }
    
    @Override public boolean isSyncSetMemberNode(NodeRef localSyncMemberNode, SyncSetDefinition ssd)
    {
        return syncSetDefinitionStorage.isSyncSetMemberNode(localSyncMemberNode, ssd);
    }
    
    @Override
    public boolean isOnPremise()
    {
        return getMode() == SyncMode.ON_PREMISE;
    }

    @Override
    public boolean isEnabled()
    {
        return ! (getMode() == SyncMode.OFF);
    }

    private SyncMode mode = SyncMode.OFF;
    
    public void setModeString(String mode)
    {
        try
        {
            this.mode = SyncMode.valueOf(mode);
            
            if(logger.isDebugEnabled())
            {
                logger.debug("Sync Mode set to:" + mode);
            }
        }
        catch (IllegalArgumentException ie)
        {
            throw new AlfrescoRuntimeException("Unable to set the sync mode to: " + mode, ie);
        }
    }
    
    private enum CloudKeyStatus
    {
        UNKNOWN,
        AVAILABLE,
        NOT_AVAILABLE
    }
    
    private CloudKeyStatus cloudKeyStatus = CloudKeyStatus.UNKNOWN;
    
    public String getModeString()
    {
        return getMode().toString();
    }
    
    public void setMode(SyncMode mode)
    {
        this.mode = mode;
    }
    
    @Override
    public SyncMode getMode()
    {
        if (checkLicenseForSyncMode)
        {
            if (cloudKeyStatus == CloudKeyStatus.AVAILABLE)
            {
                return mode;
            }
            else
            {
                logger.debug("Sync mode is off,  cloudKey is not available");
                return SyncMode.OFF;
            }
        }
        else
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("License check turned off");
            }
            return mode;
        }
    }
    
    @Override public NodeRef getRootNodeRef(NodeRef localSyncMemberNode)
    {
        NodeRef result = null;
        
        if (isDirectSyncSetMemberNode(localSyncMemberNode))
        {
            result = localSyncMemberNode;
        }
        else if (isSyncSetMemberNode(localSyncMemberNode))
        {
            // for indirectly synced nodes we must navigate up the containment hierarchy.
            // TODO Is there a better way than this tree walking? We can't just take the root node off the SSD as there may be more than one.
            NodeRef nextAncestor = nodeService.getPrimaryParent(localSyncMemberNode).getParentRef();
            while ( !isDirectSyncSetMemberNode(nextAncestor))
            {
                nextAncestor = nodeService.getPrimaryParent(nextAncestor).getParentRef();
            }
            result = nextAncestor;
        }
        return result;
    }

    private void onLicenseChange(LicenseDescriptor licenseDescriptor)
    {
        if(licenseDescriptor.getCloudSyncKey() != null)
        {
            if(cloudKeyStatus != CloudKeyStatus.AVAILABLE)
            {
                logger.info(I18NUtil.getMessage("sync.cloud.key.available"));
                cloudKeyStatus = CloudKeyStatus.AVAILABLE;
            }
        }
        else
        {
            if(cloudKeyStatus != CloudKeyStatus.NOT_AVAILABLE)
            {
                logger.info(I18NUtil.getMessage("sync.cloud.key.not_available"));
                cloudKeyStatus = CloudKeyStatus.NOT_AVAILABLE;
            }
        }
    }

    private void onLicenseFail()
    {
        if(cloudKeyStatus != CloudKeyStatus.NOT_AVAILABLE)
        {
            logger.info(I18NUtil.getMessage("sync.cloud.key.not_available"));
            cloudKeyStatus = CloudKeyStatus.NOT_AVAILABLE;
        }
    }

    /**
     * Does the sync mode depend upon the license?
     * 
     * @param checkLicenseForSync
     */
    public void setCheckLicenseForSyncMode(boolean checkLicenseForSyncMode)
    {
        this.checkLicenseForSyncMode = checkLicenseForSyncMode;
    }

    public boolean isCheckLicenseForSyncMode()
    {
        return checkLicenseForSyncMode;
    }
    
    /**
     * Normally Sync should be enabled, on-premise, for all tenants.
     * (Cloud has its own rules). This allows it to be disabled for
     *  all tenants, normally for use during testing.
     */
    public void setSyncEnabledForAllTenants(boolean enabled)
    {
        this.isSyncEnabledForAllTenants = enabled;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        if(event instanceof InvalidLicenseEvent)
        {
            onLicenseFail();
        }
        else if(event instanceof ValidLicenseEvent)
        {
            ValidLicenseEvent vle = (ValidLicenseEvent)event;
            onLicenseChange(vle.getLicenseDescriptor());
        }        
    }

	public void setLockService(LockService lockService) 
	{
		this.lockService = lockService;
	}

	public LockService getLockService() 
	{
		return lockService;
	}

	public ServerModeProvider getServerModeProvider() {
		return serverModeProvider;
	}

	public void setServerModeProvider(ServerModeProvider serverModeProvider) {
		this.serverModeProvider = serverModeProvider;
	}
}
