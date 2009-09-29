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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
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
 * Search Behaviour class.
 * 
 * Manages the collapse of data onto the supporting aspect on the record/record folder
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementSearchBehaviour implements RecordsManagementModel
{
    /** Search specific elements of the RM model */
    public static final QName ASPECT_RM_SEARCH = QName.createQName(RM_URI, "recordSearch");
    public static final QName PROP_RS_DISPOSITION_ACTION_NAME = QName.createQName(RM_URI, "recordSearchDispositionActionName");
    public static final QName PROP_RS_DISPOSITION_ACTION_AS_OF = QName.createQName(RM_URI, "recordSearchDispositionActionAsOf");
    public static final QName PROP_RS_DISPOSITION_EVENTS_ELIGIBLE = QName.createQName(RM_URI, "recordSearchDispositionEventsEligible");
    public static final QName PROP_RS_DISPOSITION_EVENTS = QName.createQName(RM_URI, "recordSearchDispositionEvents");
    public static final QName PROP_RS_VITAL_RECORD_REVIEW_PERIOD = QName.createQName(RM_URI, "recordSearchVitalRecordReviewPeriod");
    public static final QName PROP_RS_VITAL_RECORD_REVIEW_PERIOD_EXPRESSION = QName.createQName(RM_URI, "recordSearchVitalRecordReviewPeriodExpression");
    public static final QName PROP_RS_DISPOSITION_PERIOD = QName.createQName(RM_URI, "recordSearchDispositionPeriod");
    public static final QName PROP_RS_DISPOSITION_PERIOD_EXPRESSION = QName.createQName(RM_URI, "recordSearchDispositionPeriodExpression");
    public static final QName PROP_RS_HAS_DISPOITION_SCHEDULE = QName.createQName(RM_URI, "recordSearchHasDispositionSchedule");
    public static final QName PROP_RS_DISPOITION_INSTRUCTIONS = QName.createQName(RM_URI, "recordSearchDispositionInstructions");
    public static final QName PROP_RS_DISPOITION_AUTHORITY = QName.createQName(RM_URI, "recordSearchDispositionAuthority");
    
    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Node service */
    private NodeService nodeService;
    
    /**  Records management service */
    private RecordsManagementService recordsManagementService;
    
    /** Records management service registry */
    private RecordsManagementServiceRegistry recordsManagementServiceRegistry;

    /**
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * @param policyComponent the policyComponent to set
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * @param recordsManagementService  the records management service
     */
    public void setRecordsManagementService(RecordsManagementService recordsManagementService)
    {
        this.recordsManagementService = recordsManagementService;
    }
    
    /**
     * @param recordsManagementServiceRegistry  the records management service registry
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry recordsManagementServiceRegistry)
    {
        this.recordsManagementServiceRegistry = recordsManagementServiceRegistry;
    }
    
    /** Java behaviour */
    private JavaBehaviour onAddSearchAspect = new JavaBehaviour(this, "rmSearchAspectAdd", NotificationFrequency.TRANSACTION_COMMIT);
    
    /**
     * Initialisation method
     */
    public void init()
    {
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"), 
                TYPE_DISPOSITION_ACTION, 
                new JavaBehaviour(this, "dispositionActionCreate", NotificationFrequency.TRANSACTION_COMMIT));
        
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"), 
                TYPE_DISPOSITION_ACTION, 
                new JavaBehaviour(this, "dispositionActionPropertiesUpdate", NotificationFrequency.TRANSACTION_COMMIT));

        this.policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"), 
                    TYPE_DISPOSITION_SCHEDULE, 
                    new JavaBehaviour(this, "dispositionSchedulePropertiesUpdate", NotificationFrequency.TRANSACTION_COMMIT));
        
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"), 
                TYPE_DISPOSITION_ACTION, 
                ASSOC_EVENT_EXECUTIONS,
                new JavaBehaviour(this, "eventExecutionUpdate", NotificationFrequency.TRANSACTION_COMMIT));

        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"), 
                TYPE_EVENT_EXECUTION, 
                new JavaBehaviour(this, "eventExecutionDelete", NotificationFrequency.TRANSACTION_COMMIT));
        
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"), 
                ASPECT_RM_SEARCH, 
                onAddSearchAspect);  
        
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"), 
                ASPECT_RECORD, 
                new JavaBehaviour(this, "onAddRecordAspect", NotificationFrequency.TRANSACTION_COMMIT));  
        
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"), 
                TYPE_RECORD_FOLDER, 
                new JavaBehaviour(this, "recordFolderCreate", NotificationFrequency.TRANSACTION_COMMIT));
        
        // Vital Records Review Details Rollup
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"), 
                ASPECT_VITAL_RECORD_DEFINITION, 
                new JavaBehaviour(this, "vitalRecordDefintionAddAspect", NotificationFrequency.TRANSACTION_COMMIT));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"), 
                ASPECT_VITAL_RECORD_DEFINITION, 
                new JavaBehaviour(this, "vitalRecordDefintionUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * Updates the disposition action properties
     * 
     * @param nodeRef
     * @param before
     * @param after
     */
    public void dispositionActionPropertiesUpdate(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        if (this.nodeService.exists(nodeRef) == true)
        {
            ChildAssociationRef assoc = this.nodeService.getPrimaryParent(nodeRef);
            if (assoc.getTypeQName().equals(ASSOC_NEXT_DISPOSITION_ACTION) == true)
            {
                // Get the record (or record folder)
                NodeRef record = assoc.getParentRef();
                 
                // Apply the search aspect
                applySearchAspect(record);
                
                // Update disposition properties
                updateDispositionActionProperties(record, nodeRef);
            }
        }
    }
    
    private void applySearchAspect(NodeRef nodeRef)
    {
        onAddSearchAspect.disable();
        try
        {
            if (this.nodeService.hasAspect(nodeRef, ASPECT_RM_SEARCH) == false)
            {
                this.nodeService.addAspect(nodeRef, ASPECT_RM_SEARCH , null);
            }
        }
        finally
        {
            onAddSearchAspect.enable();
        }
    }
    
    public void onAddRecordAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (nodeService.exists(nodeRef) == true)
        {
            applySearchAspect(nodeRef);
            
            DispositionSchedule ds = recordsManagementService.getDispositionSchedule(nodeRef);
            if (ds == null)
            {
                nodeService.setProperty(nodeRef, PROP_RS_HAS_DISPOITION_SCHEDULE, false);
            }
            else
            {
                nodeService.setProperty(nodeRef, PROP_RS_HAS_DISPOITION_SCHEDULE, true);
                setDispositionScheduleProperties(nodeRef, ds);
            }
        }
    }
    
    public void recordFolderCreate(ChildAssociationRef childAssocRef)
    {
        NodeRef nodeRef = childAssocRef.getChildRef();
        if (nodeService.exists(nodeRef) == true)
        {
            applySearchAspect(nodeRef);
            
            DispositionSchedule ds = recordsManagementService.getDispositionSchedule(nodeRef);
            if (ds == null)
            {
                nodeService.setProperty(nodeRef, PROP_RS_HAS_DISPOITION_SCHEDULE, false);
            }
            else
            {
                nodeService.setProperty(nodeRef, PROP_RS_HAS_DISPOITION_SCHEDULE, true);
                setDispositionScheduleProperties(nodeRef, ds);
            }
        }
       
    }
    
    public void dispositionActionCreate(ChildAssociationRef childAssocRef)
    {
        NodeRef child = childAssocRef.getChildRef();
        if (nodeService.exists(child) == true &&
            childAssocRef.getTypeQName().equals(ASSOC_NEXT_DISPOSITION_ACTION) == true)
        {
            // Get the record (or record folder)
            NodeRef record = childAssocRef.getParentRef();
             
            // Apply the search aspect
            applySearchAspect(record);
            
            // Update disposition properties
            updateDispositionActionProperties(record, childAssocRef.getChildRef());
            
            // Clear the events
            this.nodeService.setProperty(record, PROP_RS_DISPOSITION_EVENTS, null);
        }        
    }
    
    /**
     * 
     * @param record
     * @param dispositionAction
     */
    private void updateDispositionActionProperties(NodeRef record, NodeRef dispositionAction)
    {
        Map<QName, Serializable> props = nodeService.getProperties(record);
        
        DispositionAction da = new DispositionActionImpl(recordsManagementServiceRegistry, dispositionAction);
        
        props.put(PROP_RS_DISPOSITION_ACTION_NAME, da.getName()); 
        props.put(PROP_RS_DISPOSITION_ACTION_AS_OF, da.getAsOfDate()); 
        props.put(PROP_RS_DISPOSITION_EVENTS_ELIGIBLE, this.nodeService.getProperty(dispositionAction, PROP_DISPOSITION_EVENTS_ELIGIBLE));
        
        DispositionActionDefinition daDefinition = da.getDispositionActionDefinition();        
        Period period = daDefinition.getPeriod();
        if (period != null)
        {
            props.put(PROP_RS_DISPOSITION_PERIOD, period.getPeriodType());
            props.put(PROP_RS_DISPOSITION_PERIOD_EXPRESSION, period.getExpression());            
        }
        else
        {
            props.put(PROP_RS_DISPOSITION_PERIOD, null);
            props.put(PROP_RS_DISPOSITION_PERIOD_EXPRESSION, null);
        }
        
        nodeService.setProperties(record, props);       
    }

    public void eventExecutionUpdate(ChildAssociationRef childAssocRef, boolean isNewNode)
    {
        NodeRef dispositionAction = childAssocRef.getParentRef();
        NodeRef eventExecution = childAssocRef.getChildRef();
        
        if (this.nodeService.exists(dispositionAction) == true &&
            this.nodeService.exists(eventExecution) == true)
        {        
            ChildAssociationRef assoc = this.nodeService.getPrimaryParent(dispositionAction);
            if (assoc.getTypeQName().equals(ASSOC_NEXT_DISPOSITION_ACTION) == true)
            {
                // Get the record (or record folder)
                NodeRef record = assoc.getParentRef();

                // Apply the search aspect
                applySearchAspect(record);
                
                Collection<String> events = (List<String>)this.nodeService.getProperty(record, PROP_RS_DISPOSITION_EVENTS);
                if (events == null)
                {
                    events = new ArrayList<String>(1);
                }
                events.add((String)this.nodeService.getProperty(eventExecution, PROP_EVENT_EXECUTION_NAME));
                this.nodeService.setProperty(record, PROP_RS_DISPOSITION_EVENTS, (Serializable)events);
            }
        }
    }
    
    public void eventExecutionDelete(ChildAssociationRef childAssocRef, boolean isNodeArchived)
    {
        NodeRef dispositionActionNode = childAssocRef.getParentRef();
        
        if (this.nodeService.exists(dispositionActionNode))
        {
            ChildAssociationRef assoc = this.nodeService.getPrimaryParent(dispositionActionNode);
            if (assoc.getTypeQName().equals(ASSOC_NEXT_DISPOSITION_ACTION) == true)
            {
                // Get the record (or record folder)
                NodeRef record = assoc.getParentRef();

                // Apply the search aspect
                applySearchAspect(record);
                
                // make sure the list of events match the action definition
                DispositionAction nextAction = recordsManagementService.getNextDispositionAction(record);
                if (nextAction != null)
                {
                    List<String> eventNames = null;
                    List<EventCompletionDetails> eventsList = nextAction.getEventCompletionDetails();
                    if (eventsList.size() > 0)
                    {
                        eventNames = new ArrayList<String>(eventsList.size());
                        for (EventCompletionDetails event : eventsList)
                        {
                            eventNames.add(event.getEventName());
                        }
                    }
                    
                    // set the property
                    this.nodeService.setProperty(record, PROP_RS_DISPOSITION_EVENTS, (Serializable)eventNames);
                }
            }
        }
    }
    
    public void rmSearchAspectAdd(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (nodeService.exists(nodeRef) == true)
        {
            // Initialise the search parameteres as required
            setVitalRecordDefintionDetails(nodeRef);
        }        
    }

    public void vitalRecordDefintionAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        // Only care about record folders
        if (recordsManagementService.isRecordFolder(nodeRef) == true)
        {
            updateVitalRecordDefinitionValues(nodeRef);         
        }
    }
    
    public void vitalRecordDefintionUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        // Only care about record folders
        if (recordsManagementService.isRecordFolder(nodeRef) == true)
        {
            Set<QName> props = new HashSet<QName>(1);
            props.add(PROP_REVIEW_PERIOD);
            Set<QName> changed = determineChangedProps(before, after);
            changed.retainAll(props);
            if (changed.isEmpty() == false)
            {
                updateVitalRecordDefinitionValues(nodeRef);
            }
            
        }
    }
    
    private void updateVitalRecordDefinitionValues(NodeRef nodeRef)
    {
        List<NodeRef> records = recordsManagementService.getRecords(nodeRef);
        for (NodeRef record : records)
        {
            // Apply the search aspect
            applySearchAspect(record);
            
            // Set the vital record definition details
            setVitalRecordDefintionDetails(record);
        }
    }
    
    private void setVitalRecordDefintionDetails(NodeRef nodeRef)
    {
        VitalRecordDefinition vrd = recordsManagementService.getVitalRecordDefinition(nodeRef);
        
        if (vrd != null)
        {
            // Set the property values
            nodeService.setProperty(nodeRef, PROP_RS_VITAL_RECORD_REVIEW_PERIOD, vrd.getReviewPeriod().getPeriodType());
            nodeService.setProperty(nodeRef, PROP_RS_VITAL_RECORD_REVIEW_PERIOD_EXPRESSION, vrd.getReviewPeriod().getExpression());
        }
    }
    
    /**
     * Updates the disposition schedule properties
     * 
     * @param nodeRef
     * @param before
     * @param after
     */
    public void dispositionSchedulePropertiesUpdate(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        if (this.nodeService.exists(nodeRef) == true)
        {
            // create the schedule object and get the record category for it
            DispositionSchedule schedule = new DispositionScheduleImpl(this.recordsManagementServiceRegistry, nodeRef);
            NodeRef recordCategoryNode = this.nodeService.getPrimaryParent(schedule.getNodeRef()).getParentRef();
            
            if (schedule.isRecordLevelDisposition())
            {
                for (NodeRef recordFolder : this.getRecordFolders(recordCategoryNode))
                {
                    for (NodeRef record : this.recordsManagementService.getRecords(recordFolder))
                    {
                        applySearchAspect(record);
                        setDispositionScheduleProperties(record, schedule);
                    }
                }
            }
            else
            {
                for (NodeRef recordFolder : this.getRecordFolders(recordCategoryNode))
                {
                    applySearchAspect(recordFolder);
                    setDispositionScheduleProperties(recordFolder, schedule);
                }
            }
        }
    }
    
    private void setDispositionScheduleProperties(NodeRef recordOrFolder, DispositionSchedule schedule)
    {
        if (schedule != null)
        {
            this.nodeService.setProperty(recordOrFolder, PROP_RS_DISPOITION_AUTHORITY, schedule.getDispositionAuthority());
            this.nodeService.setProperty(recordOrFolder, PROP_RS_DISPOITION_INSTRUCTIONS, schedule.getDispositionInstructions());
        }
    }
    
    /**
     * This method compares the oldProps map against the newProps map and returns
     * a set of QNames of the properties that have changed. Changed here means one of
     * <ul>
     * <li>the property has been removed</li>
     * <li>the property has had its value changed</li>
     * <li>the property has been added</li>
     * </ul>
     */
    private Set<QName> determineChangedProps(Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        Set<QName> result = new HashSet<QName>();
        for (QName qn : oldProps.keySet())
        {
            if (newProps.get(qn) == null ||
                newProps.get(qn).equals(oldProps.get(qn)) == false)
            {
                result.add(qn);
            }
        }
        for (QName qn : newProps.keySet())
        {
            if (oldProps.get(qn) == null)
            {
                result.add(qn);
            }
        }
        
        return result;
    }
    
    private List<NodeRef> getRecordFolders(NodeRef recordCategoryNode)
    {
        List<NodeRef> results = new ArrayList<NodeRef>(8);
        
        List<ChildAssociationRef> folderAssocs = nodeService.getChildAssocs(recordCategoryNode, 
                    ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef folderAssoc : folderAssocs)
        {
            NodeRef folder = folderAssoc.getChildRef();
            if (this.recordsManagementService.isRecordFolder(folder))
            {
                results.add(folder);
            }
        }
        
        return results;
    }
}
