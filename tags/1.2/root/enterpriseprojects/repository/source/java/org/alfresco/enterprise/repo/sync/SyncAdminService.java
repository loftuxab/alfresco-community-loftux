/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.util.List;

import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * This interface defines the service for CRUD-management of {@link SyncSetDefinition Sync Set Definitions (SSDs)}.
 * The same code is responsible for managing these definition objects in both the Source (On Premise) and Target (Cloud)
 * Alfresco instances - although it will do so in response to different of inputs:
 * <ul>
 *   <li>User creates a SyncSetDefinition within the On Premise (Source) Alfresco.
 *       This would mean a webscript call from On Premise Share to On Premise Alfresco and the creation of a local SSD.</li>
 *   <li>After the above, the On Premise Alfresco will make webscript calls to the Cloud (Target) Alfresco
 *       instance requesting the creation of an analagous, remote SyncSetDefinition in the Cloud.</li>
 * </ul>
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public interface SyncAdminService
{
    /**
     * This method creates a {@link SyncSetDefinition} object within the local Alfresco.
     * This method should only be called from within the Source Alfresco instance (which includes the Share tier).
     * A {@link SyncSetDefinition#getId() unique ID} will be generated for the Sync Set Definition.
     * 
     * The Sync Set will be associated with the current user's Cloud Credentials, which must be defined
     * through the {@link CloudConnectorService} before creating the Sync Set 
     * 
     * @param syncSetMembers the local (On Premise) NodeRefs which are to be members of this syncset.
     * @param remoteTenantId the tenant id on the Target (remote, Cloud) Alfresco, which contains the targetFolder.
     * @param targetFolderNodeRef the NodeRef (as a String as it is not a valid NodeRef on this Alfresco Server)
     *                            on the Target (remote, Cloud) Alfresco of the targetFolder for this sync set.  
     * @param lockSourceCopy should the sync set members in the source instance be locked?
     * @param includeSubFolders should the sync set include sub folders?
     * @param isDeleteOnCloud true content can be deleted on cloud,  false it is protected against deletion
     * @param isDeleteOnPrem true when content is deleted on cloud then it should also be delete on premise
     * @return the created SyncSetDefinition object.
     */
    SyncSetDefinition createSourceSyncSet(List<NodeRef> syncSetMembers,
                                          String remoteTenantId,
                                          String targetFolderNodeRef,
                                          boolean lockSourceCopy,
                                          boolean includeSubFolders,
                                          boolean isDeleteOnCloud,
                                          boolean isDeleteOnPrem);
    /**
     * This method creates a {@link SyncSetDefinition} object within the local Alfresco.
     * This method should only be called from within the Source Alfresco instance (which includes the Share tier).
     * A {@link SyncSetDefinition#getId() unique ID} will be generated for the Sync Set Definition.
     * <p>
     * The Sync Set will be associated with the current user's Cloud Credentials, which must be defined
     * through the {@link CloudConnectorService} before creating the Sync Set 
     * <p>
     * This method does not includeSubFolders, it needs nodes to be individually added to the sync set.
     * 
     * @param syncSetMembers the local (On Premise) NodeRefs which are to be members of this syncset.
     * @param remoteTenantId the tenant id on the Target (remote, Cloud) Alfresco, which contains the targetFolder.
     * @param targetFolderNodeRef the NodeRef (as a String as it is not a valid NodeRef on this Alfresco Server)
     *                            on the Target (remote, Cloud) Alfresco of the targetFolder for this sync set.  
     * @param lockSourceCopy should the sync set members in the source instance be locked?
     * @param isDeleteOnCloud true content can be deleted on cloud,  false it is protected against deletion
     * @param isDeleteOnPrem true when content is deleted on cloud then it should also be delete on premise
     * @return the created SyncSetDefinition object.
     */
    SyncSetDefinition createSourceSyncSet(List<NodeRef> syncSetMembers,
                                         String remoteTenantId,
                                         String targetFolderNodeRef,
                                         boolean lockSourceCopy,
                                         boolean isDeleteOnCloud,
                                         boolean isDeleteOnPrem);
    
    /**
     * This method creates a {@link SyncSetDefinition} object within the local Alfresco.
     * This method should only be called from within the Target (Cloud) Alfresco instance (remotely from the Source/On Premise Alfresco.
     * The {@link SyncSetDefinition#getId() unique ID} must be equal to a SyncSetDefinition ID which already exists in the Source.
     * 
     * @param guid                the GUID to assign to the Target SyncSetDefinition - which must match the corresponding ID in the Source.
     * @param sourceRepoId
     * @param targetFolderNodeRef the NodeRef of a folder on the Target Alfresco (local, Cloud) under which synced content will be written.
     *                            Unlike {@link #createSourceSyncSet(String, String, List, String, String, boolean)} this parameter is a
     *                            NodeRef and not a String, as this *is* a valid, local NodeRef on this Alfresco.
     * @param includeSubFolders   should the sync set include the sub folders                           
     * @return the SyncSetDefinition object matching that on the Source Alfresco.
     */
    SyncSetDefinition createTargetSyncSet(String guid, 
    		String sourceRepoId, 
    		NodeRef targetFolderNodeRef, 
    		boolean includeSubFolders,
    		boolean isDeleteOnCloud,
    		boolean isDeleteOnPrem);
    
    /**
     * This method deletes a {@link SyncSetDefinition} object within the local Alfresco.
     * This method should only be called from within the Source Alfresco instance (which includes the Share tier).
     * 
     * @param ssdId the ssd id.
     * @throws NoSuchSyncSetDefinitionException if the ssdId is not recognised.
     */
    void deleteSourceSyncSet(String ssdId);
    
    /**
     * This method deletes a {@link SyncSetDefinition} object within the local Alfresco.
     * This method should only be called from within the Target (Cloud) Alfresco instance (remotely from the Target/On Premise Alfresco.
     * The {@link SyncSetDefinition#getId() unique ID} must be equal to a SyncSetDefinition ID which already exists in the Source.
     * 
     * @param ssdId the ssd id.
     * @throws NoSuchSyncSetDefinitionException if the ssdId is not recognised.
     */
    void deleteTargetSyncSet(String ssdId);
    
    /**
     * This method deletes a {@link SyncSetDefinition} object within the local Alfresco.
     * This method should only be called from within the Target (Cloud) Alfresco instance (remotely from the Source/On Premise Alfresco.
     * The {@link SyncSetDefinition#getId() unique ID} must be equal to a SyncSetDefinition ID which already exists in the Source.
     * 
     * @param ssdId the GUID for the desired SyncSetDefinition.
     * @return the SyncSetDefinition if it exists, else <code>null</code>.
     */
    SyncSetDefinition getSyncSetDefinition(String ssdId);
    
    /**
     * This method retrieves a {@link SyncSetDefinition} object from the specified NodeRef, if possible.
     * @param nodeRef a NodeRef which is of type {@link SyncModel#TYPE_SYNC_SET_DEFINITION} or has the
     *                {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE} aspect.
     * @return the SyncSetDefinition if this nodeRef is valid, else <code>null</code>.
     */
    SyncSetDefinition getSyncSetDefinition(NodeRef nodeRef);
    
    /**
     * This method added the specified node to the specified {@link SyncSetDefinition}.
     * 
     * @param ssd
     * @param newMemberNode
     * @throws SyncAdminServiceException if the node is already a member of any SyncSet.
     */
    void addSyncSetMember(SyncSetDefinition ssd, NodeRef newMemberNode);
    void addSyncSetMembers(SyncSetDefinition ssd, List<NodeRef> syncSetMembers, boolean directSync);
    void addSyncSetMembers(SyncSetDefinition ssd, NodeRef newMemberNode, boolean directSync, boolean includeSubFolders);
    
    /**
     * This method removes the specified member node from the specified {@link SyncSetDefinition}.
     * 
     * @param ssd
     * @param existingMemberNode
     * @throws SyncAdminServiceException if the node is not a member of any SyncSet.
     */
    void removeSyncSetMember(SyncSetDefinition ssd, NodeRef existingMemberNode);
    void removeSyncSetMember(SyncSetDefinition ssd, NodeRef existingMemberNode, boolean deleteRemote);
    
    /**
     * This method gets the {@link NodeRef} of the SyncSetDefinitions container.
     * 
     * @return a NodeRef representing the SyncSetDefinitions folder.
     */
    NodeRef getSyncSetDefinitionsFolder();
    
    /**
     * Does the system have any {@link SyncSetDefinition SSDs} defined?
     * 
     * @return <code>true</code> if there as SSDs, else <code>false</code>
     */
    boolean hasSyncSetDefintions();
    
    /**
     * This method returns a List of all {@link SyncSetDefinition SSDs} in the system.
     * TODO Should we page this?
     */
    List<SyncSetDefinition> getSyncSetDefinitions();
    
    /**
     * This method returns the {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE member nodes} of the specified {@link SyncModel#TYPE_SYNC_SET_DEFINITION SSD}.
     * @param ssd the SSD
     * @return a List of all member nodes.
     * TODO Should we page this?
     */
    List<NodeRef> getMemberNodes(SyncSetDefinition ssd);
    
    /**
     * This method checks whether the given node is a {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE member node} of any SyncSet.
     * @param localSyncMemberNode the node to check.
     * @return <code>true</code> if an SSMN, else <code>false</code>
     */
    
    boolean isSyncSetMemberNode(NodeRef localSyncMemberNode);
    
    /**
     * This method checks whether the given node is a {@link SyncModel#PROP_DIRECT_SYNC direct} member node of any SyncSet.
     * This is equivalent to the node being a root member of a sync set.
     * 
     * @param localSyncMemberNode the node to check.
     * @return <code>true</code> if the node is an SSMN and is directly synced, else <code>false</false>.
     */
    boolean isDirectSyncSetMemberNode(NodeRef localSyncMemberNode);
    
    /**
     * This method checks whether the given node is a {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE member node} of the specified SyncSet.
     * @param localSyncMemberNode the node to check.
     * @param ssd the SyncSetDefinition to check.
     * @return <code>true</code> if an SSMN, else <code>false</code>
     */
    boolean isSyncSetMemberNode(NodeRef localSyncMemberNode, SyncSetDefinition ssd);
    
    /**
     * This method returns the NodeRef which represents the root of the provided localSyncMemberNode's {@link SyncModel#TYPE_SYNC_SET_DEFINITION sync set}.
     * It will have a value as follows:
     * <ul>
     *   <li>For unsynced nodes, it will return <code>null</code>.</li>
     *   <li>For directly synced nodes, it will return the same NodeRef.</li>
     *   <li>For indirectly synced nodes, it will return the ancestor NodeRef on the primary parent path which is itself directly synced.</li>
     * </ul>
     * 
     * @param localSyncMemberNode
     * @return
     */
    NodeRef getRootNodeRef(NodeRef localSyncMemberNode);
    
    /**
     * Are we on premise or not
     * @return true - we are onPremise
     */
    boolean isOnPremise();
    
    /**
     * Is sync enabled
     * @return true - sync is enabled
     */
    boolean isEnabled();
    
    /**
     * What is the mode of sync?
     */
    SyncMode getMode();
    
    /**
     * Is the given tenant allowed to be sync for this instance ?
     * 
     * @param tenantDomain
     * @return
     */
    boolean isTenantEnabledForSync(String tenantDomain);
    
}
