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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEvent;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventType;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.namespace.NamespaceService;
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
    private RecordsManagementServiceRegistry serviceRegistry;
    
    /** Dictionary service */
    private DictionaryService dictionaryService;
    
    /** Node service */
    private NodeService nodeService;

    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Records management action service */
    private RecordsManagementActionService rmActionService;
    
    /** Configured simple events */
    Properties configuredSimpleEvents;
    
    /**
     * Set the service registry service
     * 
     * @param serviceRegistry   service registry
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        this.nodeService = serviceRegistry.getNodeService();
        this.dictionaryService = serviceRegistry.getDictionaryService();
        this.rmActionService = serviceRegistry.getRecordsManagementActionService();
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
     * Init method.  Registered behaviours.
     */
    public void init()
    {        
        // Register the association behaviours
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"), 
                TYPE_RECORD_FOLDER, 
                ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onFileContent", NotificationFrequency.TRANSACTION_COMMIT));
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"), 
                TYPE_RECORDS_MANAGEMENT_CONTAINER, 
                ContentModel.ASSOC_CONTAINS, 
                new JavaBehaviour(this, "onCreateRecordFolder", NotificationFrequency.TRANSACTION_COMMIT));  
        // Register class behaviours.
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ASPECT_VITAL_RECORD_DEFINITION,
                new JavaBehaviour(this, "onChangeToVRDefinition", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * Try to file any record created in a record folder
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(org.alfresco.service.cmr.repository.ChildAssociationRef, boolean)
     */
    public void onFileContent(ChildAssociationRef childAssocRef, boolean bNew)
    {
        // File the document
        rmActionService.executeRecordsManagementAction(childAssocRef.getChildRef(), "file");
    }
    
    /**
     * Set's up the record folder upon creation
     * 
     * @param childAssocRef
     * @param bNew
     */
    public void onCreateRecordFolder(ChildAssociationRef childAssocRef, boolean bNew)
    {   
        // Setup record folder
        rmActionService.executeRecordsManagementAction(childAssocRef.getChildRef(), "setupRecordFolder");       
    }
    
    /**
     * Called after a vitalRecordDefinition property has been updated.
     */
    public void onChangeToVRDefinition(NodeRef node, Map<QName, Serializable> oldProps,
                                       Map<QName, Serializable> newProps)
    {
        rmActionService.executeRecordsManagementAction(node, "broadcastVitalRecordDefinition");
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
        return (this.nodeService.hasAspect(record, ASPECT_DECLARED_RECORD));
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
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getDispositionSchedule(org.alfresco.service.cmr.repository.NodeRef)
     */
    public DispositionSchedule getDispositionSchedule(NodeRef nodeRef)
    {   
        DispositionSchedule di = null;
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
            di = new DispositionScheduleImpl(serviceRegistry, diNodeRef);
        }
        
        return di;
    }
    
    /**
     * Get disposition instructions implementation
     * 
     * @param nodeRef
     * @return
     */
    private NodeRef getDispositionInstructionsImpl(NodeRef nodeRef)
    {
        NodeRef result = null;
        
        if (this.nodeService.hasAspect(nodeRef, ASPECT_SCHEDULED) == true)
        {
            result = this.nodeService.getChildAssocs(nodeRef, ASSOC_DISPOSITION_SCHEDULE, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
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
        DispositionSchedule di = getDispositionSchedule(nodeRef);
        if (di != null)
        {
            // Get the current action node
            NodeRef currentDispositionAction = null;
            if (this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_LIFECYCLE) == true)
            {
                List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL);
                if (assocs.size() > 0)
                {
                    currentDispositionAction = assocs.get(0).getChildRef();
                }
            }
            
            if (currentDispositionAction != null)
            {
                // Stamp it complete
                // TODO
            
                // Move it to the history association
                this.nodeService.moveNode(currentDispositionAction, nodeRef, ASSOC_DISPOSITION_ACTION_HISTORY, ASSOC_DISPOSITION_ACTION_HISTORY);
            }
           
            List<DispositionActionDefinition> dispositionActionDefinitions = di.getDispositionActionDefinitions();
            DispositionActionDefinition currentDispositionActionDefinition = null;
            DispositionActionDefinition nextDispositionActionDefinition = null;
            
            if (currentDispositionAction == null)
            {
                if (dispositionActionDefinitions.isEmpty() == false)
                {
                    // The next disposition action is the first action
                    nextDispositionActionDefinition = dispositionActionDefinitions.get(0);
                }
            }
            else
            {
                // Get the current action
                String currentADId = (String)this.nodeService.getProperty(currentDispositionAction, PROP_DISPOSITION_ACTION_ID);
                currentDispositionActionDefinition = di.getDispositionActionDefinition(currentADId);
                
                // Get the next disposition action
                int index = currentDispositionActionDefinition.getIndex();
                index++;
                if (index < dispositionActionDefinitions.size())
                {
                    nextDispositionActionDefinition = dispositionActionDefinitions.get(index);
                }
            }
            
            if (nextDispositionActionDefinition != null)
            {
                if (this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_LIFECYCLE) == false)
                {
                    // Add the disposition life cycle aspect
                    this.nodeService.addAspect(nodeRef, ASPECT_DISPOSITION_LIFECYCLE, null);
                }
                
                // Create the properties
                Map<QName, Serializable> props = new HashMap<QName, Serializable>(10);
                
                // Calculate the asOf date
                Date asOfDate = null;
                Period period = nextDispositionActionDefinition.getPeriod();
                if (period != null)
                {
                    // Use NOW as the default context date
                    Date contextDate = new Date();
                    
                    // Get the period properties value
                    QName periodProperty = nextDispositionActionDefinition.getPeriodProperty();
                    if (periodProperty != null)
                    {
                        contextDate = (Date)this.nodeService.getProperty(nodeRef, periodProperty);
                        
                        if (contextDate == null)
                        {
                            throw new AlfrescoRuntimeException("Date used to calculate disposition action asOf date is not set for property " + periodProperty.toString());
                        }
                    }
                    
                    // Calculate the as of date
                    asOfDate = period.getNextDate(contextDate);
                }            
                
                // Set the property values
                props.put(PROP_DISPOSITION_ACTION_ID, nextDispositionActionDefinition.getId());
                props.put(PROP_DISPOSITION_ACTION, nextDispositionActionDefinition.getName());
                if (asOfDate != null)
                {
                    props.put(PROP_DISPOSITION_AS_OF, asOfDate);
                }
                
                // Create a new disposition action object
                NodeRef dispositionActionNodeRef = this.nodeService.createNode(
                        nodeRef, 
                        ASSOC_NEXT_DISPOSITION_ACTION, 
                        ASSOC_NEXT_DISPOSITION_ACTION, 
                        TYPE_DISPOSITION_ACTION,
                        props).getChildRef();     
                
                // Create the events
                List<RecordsManagementEvent> events = nextDispositionActionDefinition.getEvents();
                for (RecordsManagementEvent event : events)
                {
                    // For every event create an entry on the action
                    Map<QName, Serializable> eventProps = new HashMap<QName, Serializable>(7);
                    eventProps.put(PROP_EVENT_EXECUTION_NAME, event.getName());
                    // TODO display label
                    RecordsManagementEventType eventType = this.serviceRegistry.
                                                                getRecordsManagementEventService().getEventType(event.getType());
                    eventProps.put(PROP_EVENT_EXECUTION_AUTOMATIC, eventType.isAutomaticEvent());
                    eventProps.put(PROP_EVENT_EXECUTION_COMPLETE, false);
                    
                    // Create the event execution object
                    this.nodeService.createNode(
                            dispositionActionNodeRef,
                            ASSOC_EVENT_EXECUTIONS,
                            ASSOC_EVENT_EXECUTIONS,
                            TYPE_EVENT_EXECUTION,
                            eventProps);
                }
            }
        }
    }
 
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isNextDispositionActionEligible(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isNextDispositionActionEligible(NodeRef nodeRef)
    {
        boolean result = false;
        
        // Get the disposition instructions
        DispositionSchedule di = getDispositionSchedule(nodeRef);
        NodeRef nextDa = getNextDispostionAction(nodeRef);
        if (di != null &&
            this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_LIFECYCLE) == true &&
            nextDa != null)
        {
            // If it has an asOf date and it is greater than now the action is eligible
            Date asOf = (Date)this.nodeService.getProperty(nextDa, PROP_DISPOSITION_AS_OF);
            if (asOf != null &&
                asOf.before(new Date()) == true)
            {
                result = true;
            }
            
            if (result == false)
            {
                // If all the events specified on the action have been completed the action is eligible
                List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nextDa, ASSOC_EVENT_EXECUTIONS, RegexQNamePattern.MATCH_ALL);
                for (ChildAssociationRef assoc : assocs)
                {
                    NodeRef eventExecution = assoc.getChildRef();
                    Boolean isCompleteValue = (Boolean)this.nodeService.getProperty(eventExecution, PROP_EVENT_EXECUTION_COMPLETE);
                    boolean isComplete = false;
                    if (isCompleteValue != null)
                    {
                        isComplete = isCompleteValue.booleanValue();
                        
                        // TODO this only works for the OR use case .. need to handle optional AND handling
                        if (isComplete == true)
                        {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Get the next disposition action node.  Null if none present.
     * 
     * @param nodeRef       the disposable node reference
     * @return NodeRef      the next disposition action, null if none
     */
    private NodeRef getNextDispostionAction(NodeRef nodeRef)
    {
        NodeRef result = null;
        List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL);
        if (assocs.size() != 0)
        {
            result = assocs.get(0).getChildRef();
        }
        return result;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getVitalRecordDefinition(org.alfresco.service.cmr.repository.NodeRef)
     */
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
                // Get all the vital record definitions
                NodeRef temp = getVitalRecordDefinitionImpl(recordFolder);
                if (temp != null)
                {
                    vrdNodeRefs.add(temp);
                }
                
            }
            if (vrdNodeRefs.size() != 0)
            {
                // TODO figure out which vital record definition object is most relevant
                //      for now just take the first!
                vrdNodeRef = vrdNodeRefs.get(0);
            }
        }
        else
        {
            // Get the vital record definition for the node reference provided
            vrdNodeRef = getVitalRecordDefinitionImpl(nodeRef);
        }
        
        VitalRecordDefinition result = null;
        if (vrdNodeRef != null)
        {
            result = new VitalRecordDefinitionImpl(serviceRegistry, vrdNodeRef);
        }
        return result;
    }
    
    /**
     * Get vital record definition implementation
     * 
     * @param nodeRef   node reference
     * @return NodeRef  vital record definition
     */
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
            if (isRecordsManagementContainer(parent) == true) // TODO For recordFolder, I'm getting false here.
            {
                result = getVitalRecordDefinitionImpl(parent);
            }
        }
        return result;
    }
}
