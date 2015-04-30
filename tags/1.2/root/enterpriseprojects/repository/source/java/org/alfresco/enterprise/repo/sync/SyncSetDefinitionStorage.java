/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.node.SystemNodeUtils;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is responsible for the persistence of {@link SyncSetDefinition} objects using lower-level
 * repo services such as the {@link NodeService}. The higher-level business logic around these CRUD calls
 * is contained within the {@link SyncAdminServiceImpl}.
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
//TODO Rename - it does SSMNs too.
public class SyncSetDefinitionStorage
{
    private static final Log log = LogFactory.getLog(SyncSetDefinitionStorage.class);
    
    // service dependencies
    private ImporterBootstrap bootstrap;
    private Repository        repositoryHelper;
    private NodeService       nodeService;
    private NamespaceService  namespaceService;
    private PermissionService permissionService;
    private CheckOutCheckInService cociService;
    
    public void setImporterBootstrap(ImporterBootstrap bootstrap)
    {
        this.bootstrap = bootstrap;
    }
    
    public void setRepositoryHelper(Repository repositoryHelper)
    {
        this.repositoryHelper = repositoryHelper;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    public void setCheckOutCheckInService(CheckOutCheckInService service)
    {
        this.cociService = service;
    }
    
    public NodeRef createSyncSetDefinitionNode(SyncSetDefinition ssd)
    {
        final NodeRef ssdContainer = getOrCreateSyncSetDefinitionContainer();
        
        // Who is the current person? i.e. the user/person creating the sync set.
        String currentUser = AuthenticationUtil.getFullyAuthenticatedUser();
        // Note that this username will be different in the Cloud and On Premise.
        
        Map<QName, Serializable> ssdProperties = new HashMap<QName, Serializable>();
        ssdProperties.put(SyncModel.PROP_SYNC_GUID, ssd.getId());
        ssdProperties.put(SyncModel.PROP_SOURCE_REPO_ID, ssd.getSourceRepoId());
        ssdProperties.put(SyncModel.PROP_SYNC_CREATOR_USERNAME, currentUser);
        ssdProperties.put(SyncModel.PROP_SYNC_SET_IS_LOCKED_ON_PREMISE, ssd.getLockSourceCopy());
        ssdProperties.put(SyncModel.PROP_TARGET_NETWORK_ID, ssd.getRemoteTenantId());
        ssdProperties.put(SyncModel.PROP_TARGET_ROOT_FOLDER, ssd.getTargetFolderNodeRef());
        ssdProperties.put(SyncModel.PROP_SYNC_SET_INCLUDE_SUBFOLDERS, ssd.getIncludeSubFolders());
        ssdProperties.put(SyncModel.PROP_SYNC_SET_IS_DELETE_ON_CLOUD, ssd.isDeleteOnCloud());
        ssdProperties.put(SyncModel.PROP_SYNC_SET_IS_DELETE_ON_PREM, ssd.isDeleteOnPrem());
        
        ChildAssociationRef newChildAssoc = nodeService.createNode(ssdContainer,
                                                                   ContentModel.ASSOC_CHILDREN, ContentModel.ASSOC_CHILDREN,
                                                                   SyncModel.TYPE_SYNC_SET_DEFINITION,
                                                                   ssdProperties);
        final NodeRef ssdNodeRef = newChildAssoc.getChildRef();
        
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Created SyncSetDefinition. SSD-ID='").append(ssd.getId())
               .append("', SSD-SrcRepoId='").append(ssd.getSourceRepoId())
               .append("', SSD-NodeRef=").append(ssdNodeRef)
               .append(", owner:").append(currentUser);
            log.debug(msg.toString());
        }
        
        return ssdNodeRef;
    }
    
    public void addNodeToSyncSet(NodeRef ssdNodeRef, NodeRef newMember, boolean directSync, boolean isDeleteOnPrem)
    {
        if (isSyncSetMemberNode(newMember))
        {
            if (log.isInfoEnabled())
            {
                log.info("Node was already a SyncSet member: ssdNodeRef="+ssdNodeRef+", nodeRef="+newMember);
            }
            return;
        }
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(SyncModel.PROP_DIRECT_SYNC, directSync);
        
        nodeService.addAspect(newMember, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, props);
        nodeService.createAssociation(ssdNodeRef, newMember, SyncModel.ASSOC_SYNC_MEMBERS);
        
        if(isDeleteOnPrem)
        {
        	nodeService.addAspect(newMember, SyncModel.ASPECT_DELETE_ON_PREM, null);
        }
        
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Node added to SSD-NodeRef '")
               .append(ssdNodeRef).append("'. MemberNode=")
               .append(newMember);
            log.debug(msg.toString());
        }
    }
    
    // unsync
    public void removeNodeFromSyncSet(NodeRef ssdNodeRef, NodeRef memberToRemove)
    {
        nodeService.removeAssociation(ssdNodeRef, memberToRemove, SyncModel.ASSOC_SYNC_MEMBERS);
        
        nodeService.removeAspect(memberToRemove, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE);
        nodeService.removeAspect(memberToRemove, SyncModel.ASPECT_SYNC_FAILED);
        nodeService.removeAspect(memberToRemove, SyncModel.ASPECT_SYNCED);
        nodeService.removeAspect(memberToRemove, SyncModel.ASPECT_DELETE_ON_PREM);
        
        NodeRef wcNodeRef = cociService.getWorkingCopy(memberToRemove);
        if (wcNodeRef != null)
        {
            nodeService.removeAspect(wcNodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE);
            nodeService.removeAspect(wcNodeRef, SyncModel.ASPECT_SYNC_FAILED);
            nodeService.removeAspect(wcNodeRef, SyncModel.ASPECT_SYNCED);
        }
        
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Node removed from SSD=")
               .append(ssdNodeRef).append(", Node=")
               .append(memberToRemove);
            log.debug(msg.toString());
        }
    }
    
    /**
     * This method checks each of the specified NodeRefs to determine if any are already members of any syncset.
     * @param nodes a List of NodeRefs which are to be checked.
     * @return a List of NodeRefs which are already members of a syncset, an empty list if none are.
     */
    public List<NodeRef> getAlreadySyncedNodes(List<NodeRef> nodes)
    {
        List<NodeRef> nodesThatAreAlreadySynced = new ArrayList<NodeRef>();
        for (NodeRef proposedMember : nodes)
        {
            if ( nodeService.hasAspect(proposedMember, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE))
            {
                nodesThatAreAlreadySynced.add(proposedMember);
            }
        }
        return nodesThatAreAlreadySynced;
    }
    
    /**
     * This method checks each of the specified NodeRefs to determine if any are not writable by the current user.
     * @param nodes a List of NodeRefs which are to be checked.
     * @return a List of NodeRefs which are not writable by the current user, an empty list if none are.
     */
    public List<NodeRef> getUnwritableNodes(List<NodeRef> nodes)
    {
        List<NodeRef> unwritableNodes = new ArrayList<NodeRef>();
        for (NodeRef proposedMember : nodes)
        {
            AccessStatus writePermission = permissionService.hasPermission(proposedMember, PermissionService.WRITE);
            if (writePermission != AccessStatus.ALLOWED)
            {
                unwritableNodes.add(proposedMember);
            }
        }
        return unwritableNodes;
    }
    
    public SyncSetDefinition getSyncSetDefinition(final String ssdId)
    {
        NodeRef ssdNodeRef = null;
        
        List<ChildAssociationRef> existingSSDs = nodeService.getChildAssocsByPropertyValue(getOrCreateSyncSetDefinitionContainer(), SyncModel.PROP_SYNC_GUID, ssdId);
        
        if ( !existingSSDs.isEmpty())
        {
            ssdNodeRef = existingSSDs.get(0).getChildRef();
            
            if (existingSSDs.size() > 1)
            {
                if (log.isWarnEnabled())
                {
                    log.error("Unexpectedly found " + existingSSDs.size() + " syncSetDefinitions. Maximum allowed is 1.");
                }
            }
        }
        
        return ssdNodeRef == null ? null : getSyncSetDefinition(ssdNodeRef);
    }
    
    public List<SyncSetDefinition> getSyncSetDefinitions()
    {
        final NodeRef ssdContainer = getOrCreateSyncSetDefinitionContainer();
        
        List<ChildAssociationRef> childAssocs = 
                nodeService.getChildAssocs(ssdContainer, ContentModel.ASSOC_CHILDREN, ContentModel.ASSOC_CHILDREN);
        
        List<SyncSetDefinition> result = new ArrayList<SyncSetDefinition>(childAssocs.size());
        for (ChildAssociationRef childAssoc : childAssocs)
        {
            SyncSetDefinition nextSsd = getSyncSetDefinition(childAssoc.getChildRef());
            result.add(nextSsd);
        }
        
        return result;
    }
    
    public boolean hasSyncSetDefintions()
    {
        final NodeRef ssdContainer = getOrCreateSyncSetDefinitionContainer(); // TODO Should really be just a get
        if (ssdContainer == null)
        {
            return false;
        }
        
        List<ChildAssociationRef> childAssocs = 
                nodeService.getChildAssocs(ssdContainer, ContentModel.ASSOC_CHILDREN, ContentModel.ASSOC_CHILDREN);
        if (childAssocs.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public SyncSetDefinition getSyncSetDefinition(final NodeRef nodeRef)
    {
        if (nodeRef == null)
        {
            throw new NullPointerException("Illegal null nodeRef");
        }
        
        NodeRef ssdNodeRef = null;
        
        if (nodeService.exists(nodeRef))
        {
            // Is the nodeRef actually an SSD?
            if (nodeService.getType(nodeRef).equals(SyncModel.TYPE_SYNC_SET_DEFINITION))
            {
                ssdNodeRef = nodeRef;
            }
            // Or is it an SSMN node?
            else if (nodeService.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE))
            {
                final List<AssociationRef> targetAssocs = nodeService.getSourceAssocs(nodeRef, SyncModel.ASSOC_SYNC_MEMBERS);
                if (targetAssocs.size() == 1)
                {
                    ssdNodeRef = targetAssocs.get(0).getSourceRef();
                }
            }
        }
        
        SyncSetDefinition result = null;
        if (ssdNodeRef != null)
        {
            Map<QName, Serializable> nodeProps = nodeService.getProperties(ssdNodeRef);
            
            final String ssdId = (String) nodeProps.get(SyncModel.PROP_SYNC_GUID);
            final String srcRepoId = (String) nodeProps.get(SyncModel.PROP_SOURCE_REPO_ID);
            
            Boolean lockSourceCopy = (Boolean) nodeProps.get(SyncModel.PROP_SYNC_SET_IS_LOCKED_ON_PREMISE);
            if (lockSourceCopy == null) { lockSourceCopy = false; }
            
            Boolean includeSubFolders = (Boolean) nodeProps.get(SyncModel.PROP_SYNC_SET_INCLUDE_SUBFOLDERS);
             // TODO: minor - switch to false (to be consistent with default in model) - temporarily true here for backwards compat of sprint 4 dev/demo envs
            if (includeSubFolders == null) { includeSubFolders = true; }
            
            final String remoteTenantId = (String) nodeProps.get(SyncModel.PROP_TARGET_NETWORK_ID);
            final String targetFolderNodeRef = (String) nodeProps.get(SyncModel.PROP_TARGET_ROOT_FOLDER);
            String syncCreatorUsername = (String) nodeProps.get(SyncModel.PROP_SYNC_CREATOR_USERNAME);
            if (syncCreatorUsername == null)
            {
                // fallback / backwards compat
                syncCreatorUsername = (String) nodeProps.get(ContentModel.PROP_CREATOR);
            }
            
            Boolean isDeleteOnCloud = (Boolean) nodeProps.get(SyncModel.PROP_SYNC_SET_IS_DELETE_ON_CLOUD);
            
            if(isDeleteOnCloud == null)
            {
            	isDeleteOnCloud = true;
            }
            Boolean isDeleteOnPrem = (Boolean) nodeProps.get(SyncModel.PROP_SYNC_SET_IS_DELETE_ON_PREM);
            
            if(isDeleteOnPrem == null)
            {
            	isDeleteOnPrem = false;
            }
            
            result = new SyncSetDefinition(ssdId, srcRepoId, ssdNodeRef);
            result.setLockSourceCopy(lockSourceCopy);
            result.setRemoteTenantId(remoteTenantId);
            result.setTargetFolderNodeRef(targetFolderNodeRef);
            result.setSyncCreator(syncCreatorUsername);
            result.setIncludeSubFolders(includeSubFolders);
            result.setDeleteOnCloud(isDeleteOnCloud);
            result.setDeleteOnPrem(isDeleteOnPrem);
        }
        
        return result;
    }
    
    public void deleteSyncSetDefinition(final String ssdId)
    {
        Long startTime = null;
        if (log.isInfoEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        SyncSetDefinition ssd = getSyncSetDefinition(ssdId);
        
        if (ssd == null)
        {
            throw new NoSuchSyncSetDefinitionException("No such SSD", ssdId);
        }
        
        NodeRef ssdNodeRef = ssd.getNodeRef();
        if (ssdNodeRef == null && log.isWarnEnabled())
        {
            log.warn("Cannot delete SSD " + ssdId + " as it has no NodeRef");
        }
        
        nodeService.deleteNode(ssdNodeRef);
        // All the subsequent business logic will happen automatically.
        
        if (startTime != null)
        {
            StringBuilder sb = new StringBuilder("deleteSyncSet: ");
            sb.append(ssd).append(" [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
            log.info(sb.toString());
        }
    }
    
    public List<NodeRef> getMemberNodes(NodeRef ssdNodeRef)
    {
        if ( !nodeService.exists(ssdNodeRef) ||
             !nodeService.getType(ssdNodeRef).equals(SyncModel.TYPE_SYNC_SET_DEFINITION))
        {
            throw new AlfrescoRuntimeException("Illegal SSD NodeRef:" + ssdNodeRef);
        }
        
        List<NodeRef> results = new ArrayList<NodeRef>();
        
        List<AssociationRef> assocs = nodeService.getTargetAssocs(ssdNodeRef, SyncModel.ASSOC_SYNC_MEMBERS);
        for (AssociationRef assoc : assocs)
        {
            results.add(assoc.getTargetRef());
        }
        return results;
    }
    
    /**
     * This method finds the SyncSet Definition Container NodeRef, creating one if it does not exist.
     * 
     * @return the syncset definition container
     */
    public NodeRef getOrCreateSyncSetDefinitionContainer()
    {
        String name = bootstrap.getConfiguration().getProperty("system.syncset_definition_container.childname");
        QName container = QName.createQName(name, namespaceService);
        
        NodeRef systemSsdContainer = SystemNodeUtils.getSystemChildContainer(container, nodeService, repositoryHelper);
        
        if (systemSsdContainer == null)
        {
            if (log.isInfoEnabled())
                log.info("Lazy creating the SyncSetDefinition System Container " + name);

            systemSsdContainer = SystemNodeUtils.getOrCreateSystemChildContainer(container, nodeService, repositoryHelper).getFirst();
        }
        return systemSsdContainer;
    }
    
    /**
     * This method determines whether the specified node is a member of any SyncSet.
     */
    public boolean isSyncSetMemberNode(NodeRef nodeRef)
    {
        return nodeService.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE);
    }
    
    /**
     * This method determines whether the specified node is a <em>direct</em> member of any SyncSet.
     */
    public boolean isDirectSyncSetMemberNode(NodeRef localSyncMemberNode)
    {
        boolean result = false;
        if (isSyncSetMemberNode(localSyncMemberNode))
        {
            Serializable isDirect = nodeService.getProperty(localSyncMemberNode, SyncModel.PROP_DIRECT_SYNC);
            result = isDirect != null && ( (Boolean)isDirect);
        }
        return result;
    }
    
    /**
     * This method determines whether the specified node is a member of the specified SyncSet.
     */
    public boolean isSyncSetMemberNode(NodeRef nodeRef, SyncSetDefinition ssd)
    {
        SyncSetDefinition actualSsd = getSyncSetDefinition(nodeRef);
        
        return isSyncSetMemberNode(nodeRef) && actualSsd != null && actualSsd.getId().equals(ssd.getId());
    }
}
