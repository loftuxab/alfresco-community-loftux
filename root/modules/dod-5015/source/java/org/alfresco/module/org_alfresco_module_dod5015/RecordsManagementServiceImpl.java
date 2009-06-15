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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Records management service implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementServiceImpl implements RecordsManagementService,
                                                     RecordsManagementModel
{
    /** Service registry */
    private ServiceRegistry serviceRegistry;
    
    /** Dictionary service */
    private DictionaryService dictionaryService;
    
    /** Node service */
    private NodeService nodeService;
    
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        this.nodeService = serviceRegistry.getNodeService();
        this.dictionaryService = serviceRegistry.getDictionaryService();
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecord(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecord(NodeRef nodeRef)
    {
        return this.nodeService.hasAspect(nodeRef, ASPECT_RECORD);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordsManagementContainer(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordsManagementContainer(NodeRef nodeRef)
    {
        QName nodeType = this.nodeService.getType(nodeRef);
        return this.dictionaryService.isSubClass(nodeType, TYPE_RECORDS_MANAGEMENT_CONTAINER);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordFolder(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordFolder(NodeRef nodeRef)
    {
        QName nodeType = this.nodeService.getType(nodeRef);
        return this.dictionaryService.isSubClass(nodeType, TYPE_RECORD_FOLDER);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecord(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    public boolean isRecordDeclared(NodeRef record)
    {
        if (isRecord(record) == false)
        {
            throw new AlfrescoRuntimeException("Expecting a record.  Node is not a record. (" + record.toString() + ")");
        }
        return (this.nodeService.hasAspect(record, ASPECT_UNDECLARED_RECORD) == false);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordFolderDeclared(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordFolderDeclared(NodeRef recordFolder)
    {
        // Check we have a record folder 
        if (isRecordFolder(recordFolder) == false)
        {
            throw new AlfrescoRuntimeException("Expecting a record folder.  Node is not a record folder. (" + recordFolder.toString() + ")");
        }
        
        boolean result = true;
        
        // Check that each record in the record folder in declared
        List<NodeRef> records = getRecords(recordFolder);
        for (NodeRef record : records)
        {
            if (isRecordDeclared(record) == false)
            {
                result = false;
                break;
            }
        }
        
        return result;
        
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordFolders(org.alfresco.service.cmr.repository.NodeRef)
     */
    public List<NodeRef> getRecordFolders(NodeRef record)
    {
        List<NodeRef> result = new ArrayList<NodeRef>(1);
        if (isRecord(record) == true)
        {
            List<ChildAssociationRef> assocs = this.nodeService.getParentAssocs(record, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef assoc : assocs)
            {
                NodeRef parent = assoc.getParentRef();
                if (isRecordFolder(parent) == true)
                {
                    result.add(parent);
                }
            }
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecords(org.alfresco.service.cmr.repository.NodeRef)
     */
    public List<NodeRef> getRecords(NodeRef recordFolder)
    {
        List<NodeRef> result = new ArrayList<NodeRef>(1);
        if (isRecordFolder(recordFolder) == true)
        {
            List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(recordFolder, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef assoc : assocs)
            {
                NodeRef child = assoc.getChildRef();
                if (isRecord(child) == true)
                {
                    result.add(child);
                }
            }
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getDispositionInstructions(org.alfresco.service.cmr.repository.NodeRef)
     */
    public DispositionInstructions getDispositionInstructions(NodeRef nodeRef)
    {   
        DispositionInstructions di = null;
        NodeRef diNodeRef = null;
        if (isRecord(nodeRef) == true)
        {
            // Get the record folders for the record
            List<NodeRef> recordFolders = getRecordFolders(nodeRef);
            List<NodeRef> diNodeRefs = new ArrayList<NodeRef>(recordFolders.size());
            for (NodeRef recordFolder : recordFolders)
            {
                // Get all the disposition instructions
                NodeRef temp = getDispositionInstructionsImpl(recordFolder);
                if (temp != null)
                {
                    diNodeRefs.add(temp);
                }
                
                if (diNodeRefs.size() != 0)
                {
                    // TODO figure out which disposition instruction object is most relevant
                    //      for now just take the first!
                    diNodeRef = diNodeRefs.get(0);
                }
            }
        }
        else
        {
            // Get the disposition instructions for the node reference provided
            diNodeRef = getDispositionInstructionsImpl(nodeRef);
        }
        
        if (diNodeRef != null)
        {
            di = new DispositionInstructionsImpl(serviceRegistry, diNodeRef);
        }
        
        return di;
    }
    
    /**
     * 
     * @param nodeRef
     * @return
     */
    private NodeRef getDispositionInstructionsImpl(NodeRef nodeRef)
    {
        NodeRef result = null;
        
        if (this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_INSTRUCTIONS) == true)
        {
            result = this.nodeService.getChildAssocs(nodeRef, ASSOC_DISPOSITION_INSTRUCTIONS, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        }
        else
        {
            NodeRef parent = this.nodeService.getPrimaryParent(nodeRef).getParentRef();
            if (isRecordsManagementContainer(parent) == true)
            {
                result = getDispositionInstructionsImpl(parent);
            }
        }
        return result;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#updateNextDispositionAction(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void updateNextDispositionAction(NodeRef nodeRef)
    {
        // Get this disposition instructions for the node
        DispositionInstructions di = getDispositionInstructions(nodeRef);
        if (di != null)
        {        
            DispositionAction currentDispositionAction = null;
            DispositionAction nextDispositionAction = null;
            
            List<DispositionAction> dispositionActions = di.getDispositionActions();
            
            String currentDAId = (String)this.nodeService.getProperty(nodeRef, RecordsManagementModel.PROP_DISPOSITION_ACTION_ID);
            
            if (currentDAId == null)
            {
                if (dispositionActions.isEmpty() == false)
                {
                    // The next disposition action is the first action
                    nextDispositionAction = dispositionActions.get(0);
                }
            }
            else
            {
                // Get the current action
                currentDispositionAction = di.getDispositionAction(currentDAId);
                
                // Get the next disposition action
                int index = currentDispositionAction.getIndex();
                index++;
                if (index < dispositionActions.size())
                {
                    nextDispositionAction = dispositionActions.get(index);
                }
            }
            
            // Get the properties of the record
            Map<QName, Serializable> recordProps = this.nodeService.getProperties(nodeRef);
            
            if (currentDispositionAction != null)
            {
                // Set the previous action details
                recordProps.put(PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION, currentDispositionAction.getName());
                recordProps.put(PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE, new Date());
            }
            
            if (nextDispositionAction != null)
            {
                if (this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_SCHEDULE) == false)
                {
                    // Add the disposition schedule aspect
                    this.nodeService.addAspect(nodeRef, ASPECT_DISPOSITION_SCHEDULE, null);
                }
                
                // Calculate the asOf date
                Date asOfDate = null;
                Period period = nextDispositionAction.getPeriod();
                if (period != null)
                {
                    // Use NOW as the default context date
                    Date contextDate = new Date();
                    
                    // Get the period properties value
                    QName periodProperty = nextDispositionAction.getPeriodProperty();
                    if (periodProperty != null)
                    {
                        contextDate = (Date)this.nodeService.getProperty(nodeRef, periodProperty);
                        
                        if (contextDate == null)
                        {
                            throw new AlfrescoRuntimeException("Date used to calculate disposition action asOf date is not set for property " + periodProperty.toString());
                        }
                    }
                    
                    // Calculate the asof date
                    asOfDate = period.getNextDate(contextDate);
                }            
                
                // Set the property values
                recordProps.put(PROP_DISPOSITION_ACTION_ID, nextDispositionAction.getId());
                recordProps.put(PROP_DISPOSITION_ACTION, nextDispositionAction.getName());
                if (asOfDate != null)
                {
                    recordProps.put(PROP_DISPOSITION_AS_OF, asOfDate);
                }
                
            }
            else
            {
                // Clear the next disposition properties
                recordProps.put(PROP_DISPOSITION_ACTION_ID, null);
                recordProps.put(PROP_DISPOSITION_ACTION, null);
                recordProps.put(PROP_DISPOSITION_AS_OF, null);
            }
            
            // Set the properties of the record
            this.nodeService.setProperties(nodeRef, recordProps);
        }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isNextDispositionActionEligible(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isNextDispositionActionEligible(NodeRef nodeRef)
    {
        boolean result = false;
        
        // Get the disposition instructions
        DispositionInstructions di = getDispositionInstructions(nodeRef);
        if (di != null &&
            this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_SCHEDULE) == true)
        {
            // If it has an asOf date and it is greater than now the action is eligiable
            Date asOf = (Date)this.nodeService.getProperty(nodeRef, PROP_DISPOSITION_AS_OF);
            if (asOf != null &&
                asOf.before(new Date()) == true)
            {
                result = true;
            }
            
            // If all the events specified on the action have been completed the action is eligiable
            // TODO
        }
        
        return result;
    }

    public VitalRecordDefinition getVitalRecordDefinition(NodeRef nodeRef)
    {
        NodeRef vrdNodeRef = null;
        if (isRecord(nodeRef) == true)
        {
            // Get the record folders for the record
            List<NodeRef> recordFolders = getRecordFolders(nodeRef);
            List<NodeRef> vrdNodeRefs = new ArrayList<NodeRef>(recordFolders.size());
            for (NodeRef recordFolder : recordFolders)
            {
                // Get all the disposition instructions
                NodeRef temp = getVitalRecordDefinitionImpl(recordFolder);
                if (temp != null)
                {
                    vrdNodeRefs.add(temp);
                }
                
                if (vrdNodeRefs.size() != 0)
                {
                    // TODO figure out which disposition instruction object is most relevant
                    //      for now just take the first!
                    vrdNodeRef = vrdNodeRefs.get(0);
                }
            }
        }
        else
        {
            // Get the disposition instructions for the node reference provided
            vrdNodeRef = getVitalRecordDefinitionImpl(nodeRef);
        }
        
        // return the diNode encapsulated in the class
        return new VitalRecordDefinitionImpl(serviceRegistry, vrdNodeRef);
    }
    
    private NodeRef getVitalRecordDefinitionImpl(NodeRef nodeRef)
    {
        NodeRef result = null;
        
        if (this.nodeService.hasAspect(nodeRef, ASPECT_VITAL_RECORD_DEFINITION) == true)
        {
            result = nodeRef;
        }
        else
        {
            NodeRef parent = this.nodeService.getPrimaryParent(nodeRef).getParentRef();
            if (isRecordsManagementContainer(parent) == true)
            {
                result = getVitalRecordDefinitionImpl(parent);
            }
        }
        return result;
    }
}
