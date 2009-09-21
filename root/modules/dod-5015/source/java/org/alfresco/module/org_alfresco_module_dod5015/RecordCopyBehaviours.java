/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Class containing behaviour for the vitalRecordDefinition aspect.
 * 
 * @author neilm
 */
public class RecordCopyBehaviours implements RecordsManagementModel
{
    /** The policy component */
    private PolicyComponent policyComponent;
    
    /** The rm service registry */
    private RecordsManagementServiceRegistry rmServiceRegistry;
    
    /**
     * Set the policy component
     * 
     * @param policyComponent   the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Set the rm service registry.
     * 
     * @param recordsManagementServiceRegistry   the rm service registry.
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry recordsManagementServiceRegistry)
    {
        this.rmServiceRegistry = recordsManagementServiceRegistry;
    }

    /**
     * Initialise the vitalRecord aspect policies
     */
    public void init()
    {
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "getCopyCallback"),
                RecordsManagementModel.ASPECT_VITAL_RECORD,
                new JavaBehaviour(this, "getVitalRecordCopyCallback", NotificationFrequency.TRANSACTION_COMMIT));

        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "getCopyCallback"),
                RecordsManagementSearchBehaviour.ASPECT_RM_SEARCH,
                new JavaBehaviour(this, "getRecordSearchCopyCallback", NotificationFrequency.TRANSACTION_COMMIT));
        
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onMoveNode"),
                RecordsManagementModel.ASPECT_VITAL_RECORD, new JavaBehaviour(this, "onMoveNode", NotificationFrequency.TRANSACTION_COMMIT));
    }
    
    public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef)
    {
        final NodeService nodeService = rmServiceRegistry.getNodeService();
        
        final VitalRecordDefinition targetVrd = rmServiceRegistry.getRecordsManagementService().getVitalRecordDefinition(newChildAssocRef.getParentRef());
        if (targetVrd != null)
        {
            // Do not copy the rma:vitalRecord aspect. Or as it is here, remove it if it's there.
            //
            // This policy is called after the node has been moved.
            NodeRef newlyMovedNode = newChildAssocRef.getChildRef();
            if (nodeService.exists(newlyMovedNode) && nodeService.hasAspect(newlyMovedNode, ASPECT_VITAL_RECORD))
            {
                nodeService.removeAspect(newlyMovedNode, ASPECT_VITAL_RECORD);
            }
        }
    }
    
    /**
     * @return          Returns the {@link VitalRecordAspectCopyBehaviourCallback}
     */
    public CopyBehaviourCallback getVitalRecordCopyCallback(QName classRef, CopyDetails copyDetails)
    {
        return new VitalRecordAspectCopyBehaviourCallback();
    }

    /**
     * @return          Returns the {@link RecordSearchAspectCopyBehaviourCallback}
     */
    public CopyBehaviourCallback getRecordSearchCopyCallback(QName classRef, CopyDetails copyDetails)
    {
        return new RecordSearchAspectCopyBehaviourCallback();
    }

    /**
     * Copy behaviour for the <b>rma:vitalRecord</b> aspect
     */
    private class VitalRecordAspectCopyBehaviourCallback extends DefaultCopyBehaviourCallback
    {
        @Override
        public boolean getMustCopy(QName classQName, CopyDetails copyDetails)
        {
            // The rma:vitalRecord aspect should only be copied if the target copy would be
            // a vital record itself.
            NodeRef targetParentNodeRef = copyDetails.getTargetParentNodeRef();
            VitalRecordDefinition vrd = rmServiceRegistry.getRecordsManagementService().getVitalRecordDefinition(targetParentNodeRef);
            
            boolean targetNodeIsVital = (vrd != null && vrd.isVitalRecord());
            
            return targetNodeIsVital;
        }
    }
    
    /**
     * Copy behaviour for the <b>rma:recordSearch</b> aspect
     */
    private class RecordSearchAspectCopyBehaviourCallback extends DefaultCopyBehaviourCallback
    {
        @Override
        public boolean getMustCopy(QName classQName, CopyDetails copyDetails)
        {
            return false;
        }
    }
}
