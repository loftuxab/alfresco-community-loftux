/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Record Management Action
 * 
 * TODO Tempory implementation, provide way of defining custom behaviours for various RM events
 *      for now hard code
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementBehaviour implements RecordsManagementModel
{
    /** Node service */
    private NodeService nodeService;
    
    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Records management action service */
    private RecordsManagementActionService rmService;
    
    /** TODO this is temporarily hard coded */
    private List<QName> mandatoryRecordProperties;
    
    /**
     * Set the node service
     * 
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set policy component
     * 
     * @param policyComponent   policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Set records management action service
     * 
     * @param rmService 
     */
    public void setRecordsManagementActionService(RecordsManagementActionService rmService)
    {
        this.rmService = rmService;
    }
    
    /**
     * Init method.  Registered behaviours.
     */
    public void init()
    {
        // TODO we should be able to calculate this list for a basic record
        // Build a list of properties that need to be present for declaration to take place
        this.mandatoryRecordProperties = new ArrayList<QName>(5);
        this.mandatoryRecordProperties.add(ContentModel.PROP_TITLE);
        this.mandatoryRecordProperties.add(PROP_IDENTIFIER);
        this.mandatoryRecordProperties.add(PROP_DATE_FILED);
        this.mandatoryRecordProperties.add(PROP_PUBLICATION_DATE);
        this.mandatoryRecordProperties.add(PROP_ORIGINATOR);
        this.mandatoryRecordProperties.add(PROP_ORIGINATING_ORGANIZATION);
        
        // Register the association behaviours
        this.policyComponent.bindAssociationBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"), 
                                                      TYPE_RECORD_FOLDER, 
                                                      ContentModel.ASSOC_CONTAINS, 
                                                      new JavaBehaviour(this, "onCreateNodeAssociation", NotificationFrequency.TRANSACTION_COMMIT));        
        
        // Register the class behaviours
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"), 
                                                ASPECT_UNDECLARED_RECORD, 
                                                new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * Try to file any record created in a record folder
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(org.alfresco.service.cmr.repository.ChildAssociationRef, boolean)
     */
    public void onCreateNodeAssociation(ChildAssociationRef childAssocRef, boolean random)
    {
        // File the document
        rmService.executeRecordAction(childAssocRef.getChildRef(), "file", null);
    }

    /**
     * See if an edited undeclared record can be considered declared
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy#onUpdateProperties(org.alfresco.service.cmr.repository.NodeRef, java.util.Map, java.util.Map)
     */
    public void onUpdateProperties(NodeRef record, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        // TODO need to take into consideration madatory properties found on applied custom record aspects
        //      calculate on demand for each aspect and cache
        
        boolean recordDeclared = false;
        
        // Check the properties
        Map<QName, Serializable> recordProps = this.nodeService.getProperties(record);
        for (QName prop : this.mandatoryRecordProperties)
        {
            Serializable value = recordProps.get(prop);
            if (value == null)
            {
                recordDeclared = false;
                break;
            }
        }
        
        // If all set remove the undeclared aspect
        if (recordDeclared == true)
        {
            this.nodeService.removeAspect(record, ASPECT_UNDECLARED_RECORD);
        }
        
    }
}
