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
import org.alfresco.repo.copy.CopyServicePolicies;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Class containing behaviour for the vitalRecordDefinition aspect.
 * 
 * TODO Many aspects to consider copying:
{http://www.alfresco.org/model/recordsmanagement/1.0}vitalRecord
{http://www.alfresco.org/model/recordsmanagement/1.0}recordSearch
{http://www.alfresco.org/model/recordsmanagement/1.0}declaredRecord

{http://www.alfresco.org/model/dod5015/1.0}digitalPhotographRecord
{http://www.alfresco.org/model/recordsmanagement/1.0}filePlanComponent
{http://www.alfresco.org/model/content/1.0}titled
{http://www.alfresco.org/model/content/1.0}auditable
{http://www.alfresco.org/model/recordsmanagement/1.0}recordComponentIdentifier
{http://www.alfresco.org/model/rmcustom/1.0}customSupplementalMarkingList
{http://www.alfresco.org/model/recordsmanagement/1.0}commonRecordDetails
{http://www.alfresco.org/model/recordsmanagement/1.0}record
{http://www.alfresco.org/model/content/1.0}versionable
{http://www.alfresco.org/model/system/1.0}referenceable
{http://www.alfresco.org/model/content/1.0}copiedfrom
 * 
 * @author neilm
 */
public class RecordCopyBehaviours implements CopyServicePolicies.OnCopyNodePolicy,
                                          RecordsManagementModel
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
                new JavaBehaviour(this, "getCopyCallback"));
    }
    
    /**
     * @return          Returns the {@link VitalRecordAspectCopyBehaviourCallback}
     */
    public CopyBehaviourCallback getCopyCallback(QName classRef, CopyDetails copyDetails)
    {
        return new VitalRecordAspectCopyBehaviourCallback();
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
}
