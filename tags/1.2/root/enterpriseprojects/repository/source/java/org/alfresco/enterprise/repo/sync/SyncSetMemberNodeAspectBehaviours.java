/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.io.Serializable;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.archive.NodeArchiveService;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

/**
 * This class is responsible for adding Alfresco behaviours to the {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE} aspect
 * - apart from any behaviours related to auditing, which are registered and controlled by the {@link SyncChangeMonitor}.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class SyncSetMemberNodeAspectBehaviours implements NodeServicePolicies.OnCreateAssociationPolicy,
                                                          NodeServicePolicies.OnDeleteAssociationPolicy,
                                                          NodeServicePolicies.OnDeleteNodePolicy
{
    // Required services
    private LockService        lockService;
    private NodeArchiveService nodeArchiveService;
    private NodeService        nodeService;
    private PolicyComponent    policyComponent;
    
    public void setLockService(LockService service)               { this.lockService = service; }
    public void setNodeService(NodeService service)               { this.nodeService = service; }
    public void setNodeArchiveService(NodeArchiveService service) { this.nodeArchiveService = service; }
    public void setPolicyComponent(PolicyComponent pc)            { this.policyComponent = pc; }
    
    public void init()
    {
        PropertyCheck.mandatory(this, "lockService", lockService);
        PropertyCheck.mandatory(this, "nodeArchiveService", nodeArchiveService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        
        this.policyComponent.bindAssociationBehaviour(
                OnCreateAssociationPolicy.QNAME,
                SyncModel.TYPE_SYNC_SET_DEFINITION,
                SyncModel.ASSOC_SYNC_MEMBERS,
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        this.policyComponent.bindAssociationBehaviour(
                OnDeleteAssociationPolicy.QNAME,
                SyncModel.TYPE_SYNC_SET_DEFINITION,
                SyncModel.ASSOC_SYNC_MEMBERS,
                new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        this.policyComponent.bindClassBehaviour(
                OnDeleteNodePolicy.QNAME,
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE,
                new JavaBehaviour(this, "onDeleteNode", Behaviour.NotificationFrequency.EVERY_EVENT));
    }
    
    @Override public void onCreateAssociation(AssociationRef assocRef)
    {
        String syncSetCreatorUsername = (String) nodeService.getProperty(assocRef.getSourceRef(), SyncModel.PROP_SYNC_CREATOR_USERNAME);
        Serializable lockOnPremise = nodeService.getProperty(assocRef.getSourceRef(), SyncModel.PROP_SYNC_SET_IS_LOCKED_ON_PREMISE);
        
        // Let locked nodes
        lockService.suspendLocks();
        
        if (syncSetCreatorUsername != null)
        {
            nodeService.setProperty(assocRef.getTargetRef(), SyncModel.PROP_SYNC_OWNER, syncSetCreatorUsername);
        }
        
        /**
         * Do we need to lock this node?
         */
        Boolean isLockOnPremise = DefaultTypeConverter.INSTANCE.convert(Boolean.class, lockOnPremise);
        if(isLockOnPremise)
        {
            /**
             * If the node is already locked then skip it
             */
            if(lockService.getLockStatus(assocRef.getTargetRef()) == LockStatus.NO_LOCK)
            {
                AuthenticationUtil.pushAuthentication();
                try
                {
                    // add a property to say that this was locked by sync - so we know we can unlock it after sync
                    nodeService.setProperty(assocRef.getTargetRef(), SyncModel.PROP_SYNC_LOCK, true);
                    AuthenticationUtil.setFullyAuthenticatedUser(syncSetCreatorUsername);
                    lockService.lock(assocRef.getTargetRef(), LockType.NODE_LOCK);
                }
                finally
                {
                    AuthenticationUtil.popAuthentication();
                }
            }
        }    
    }
    
    @Override public void onDeleteAssociation(AssociationRef assocRef)
    {
        Serializable lockOnPremise = nodeService.getProperty(assocRef.getSourceRef(), SyncModel.PROP_SYNC_SET_IS_LOCKED_ON_PREMISE);
        Serializable syncLock = nodeService.getProperty(assocRef.getTargetRef(), SyncModel.PROP_SYNC_LOCK);
        
        Boolean isLockOnPremise = DefaultTypeConverter.INSTANCE.convert(Boolean.class, lockOnPremise);
        Boolean isSyncLock = DefaultTypeConverter.INSTANCE.convert(Boolean.class, syncLock);
        
        if(isLockOnPremise != null && isLockOnPremise
                && isSyncLock != null && isSyncLock)
        {
            String syncSetCreatorUsername = (String) nodeService.getProperty(assocRef.getSourceRef(), SyncModel.PROP_SYNC_CREATOR_USERNAME);
            
            AuthenticationUtil.pushAuthentication();
            try
            {
                AuthenticationUtil.setFullyAuthenticatedUser(syncSetCreatorUsername);
                
                /**
                 * If the node is already locked then unlock it
                 */
                if(lockService.getLockStatus(assocRef.getTargetRef()) != LockStatus.NO_LOCK)
                {
                    lockService.unlock(assocRef.getTargetRef());
                }
            }
            finally
            {
                AuthenticationUtil.popAuthentication();
            }
        }
        
        lockService.suspendLocks();
        
        // Ensure that the syncOwner property is removed.
        nodeService.removeProperty(assocRef.getTargetRef(), SyncModel.PROP_SYNC_OWNER);
        nodeService.removeProperty(assocRef.getTargetRef(), SyncModel.PROP_SYNC_LOCK);
    }
    
    @Override public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived)
    {
        // If a hybrid synced node is deleted (moved to the trashcan) then we want all of its sync-related state to be stripped from it.
        if (isNodeArchived)
        {
            NodeRef archivedNode = nodeArchiveService.getArchivedNode(childAssocRef.getChildRef());
            
            if (archivedNode != null)
            {
                for (QName aspect : new QName[] { SyncModel.ASPECT_SYNC_FAILED, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, SyncModel.ASPECT_SYNCED })
                {
                    nodeService.removeAspect(archivedNode, aspect);
                }
            }
        }
        // if it has been hard-deleted, then we don't care about it. It's not going to be restored.
    }
}
