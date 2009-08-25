
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Records management service implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementServiceImpl implements RecordsManagementService,
                                                     RecordsManagementModel,
                                                     ApplicationContextAware
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
    private Properties configuredSimpleEvents;

    /** Well-known location of the scripts folder. */
    private NodeRef scriptsFolderNodeRef = new NodeRef("workspace", "SpacesStore", "rm_scripts");

    /** Application context */
    private ApplicationContext applicationContext;
    
    /**
     * Set the service registry service
     * 
     * @param serviceRegistry   service registry
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry serviceRegistry)
    {
        // Internal ops use the unprotected services from the voter (e.g. nodeService)
        this.serviceRegistry = serviceRegistry;
        this.dictionaryService = serviceRegistry.getDictionaryService();
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
     * Set search service
     * 
     * @param nodeService   search service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set records management action service
     * 
     * @param rmActionService   records management action service
     */
    public void setRmActionService(RecordsManagementActionService rmActionService)
    {
        this.rmActionService = rmActionService;
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
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"), 
                ASPECT_SCHEDULED, 
                new JavaBehaviour(this, "onAddAspect", NotificationFrequency.TRANSACTION_COMMIT));
        
        // Register script execution behaviour on RM property update.
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
        		ASPECT_FILE_PLAN_COMPONENT,
                new JavaBehaviour(this, "onChangeToAnyRmProperty", NotificationFrequency.TRANSACTION_COMMIT));
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
     * Called after any Records Management property has been updated.
     */
    public void onChangeToAnyRmProperty(NodeRef node, Map<QName, Serializable> oldProps,
                                       Map<QName, Serializable> newProps)
    {
    	this.lookupAndExecuteScripts(node, oldProps, newProps);
    }
    
    /**
     * Called when the rma:scheduled aspect is applied
     * 
     * @param nodeRef The node the aspect is being applied to
     * @param aspectTypeQName The type of aspect being applied (should be rma:scheduled)
     */
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        // ensure the aspect is the one we expect
        if (aspectTypeQName.equals(ASPECT_SCHEDULED))
        {
            // Check whether there is already a disposition schedule object present
            List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, ASSOC_DISPOSITION_SCHEDULE, RegexQNamePattern.MATCH_ALL);            
            if (assocs.size() == 0)
            {
                // Create the disposition scedule object
                this.nodeService.createNode(nodeRef, ASSOC_DISPOSITION_SCHEDULE, 
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("dispositionSchedule")),
                            TYPE_DISPOSITION_SCHEDULE);
            }
        }
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordsManagementRoots()
     */
    public List<NodeRef> getRecordsManagementRoots()
    {
        SearchService searchService = (SearchService)applicationContext.getBean("searchService");
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
        String query = "ASPECT:\"" + ASPECT_RECORDS_MANAGEMENT_ROOT + "\"";        
        ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
        return resultSet.getNodeRefs();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordsManagementRoot(org.alfresco.service.cmr.repository.NodeRef)
     */
    public NodeRef getRecordsManagementRoot(NodeRef nodeRef)
    {
        NodeRef result = null;
        
        if (this.nodeService.hasAspect(nodeRef, ASPECT_FILE_PLAN_COMPONENT) == true)
        {
            if (this.nodeService.hasAspect(nodeRef, ASPECT_RECORDS_MANAGEMENT_ROOT) == true)
            {
                result = nodeRef;
            }
            else
            {
                result = getRecordsManagementRoot(this.nodeService.getPrimaryParent(nodeRef).getParentRef());
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Can not find the records management root for a node that is not a file plan component");
        }
        
        return result;
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
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#addDispositionActionDefinition(org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule, java.util.Map)
     */
    public DispositionActionDefinition addDispositionActionDefinition(DispositionSchedule schedule,
                Map<QName, Serializable> actionDefinitionParams)
    {
        // make sure at least a name has been defined
        String name = (String)actionDefinitionParams.get(PROP_DISPOSITION_ACTION_NAME);
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("'name' parameter is manatory when creating a disposition action definition");
        }
        
        // TODO: also check the action name is valid?
        
        // create the child association from the schedule to the action definition
        NodeRef actionNodeRef = this.nodeService.createNode(schedule.getNodeRef(), 
                    RecordsManagementModel.ASSOC_DISPOSITION_ACTION_DEFINITIONS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, 
                    QName.createValidLocalName(name)),
                    RecordsManagementModel.TYPE_DISPOSITION_ACTION_DEFINITION, actionDefinitionParams).getChildRef();
        
        // get the updated disposition schedule and retrieve the new action definition
        NodeRef scheduleParent = this.nodeService.getPrimaryParent(schedule.getNodeRef()).getParentRef();
        DispositionSchedule updatedSchedule = this.getDispositionSchedule(scheduleParent);
        return updatedSchedule.getDispositionActionDefinition(actionNodeRef.getId());
    }

    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#removeDispositionActionDefinition(org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule, org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition)
     */
    public void removeDispositionActionDefinition(DispositionSchedule schedule,
                DispositionActionDefinition actionDefinition)
    {
        // remove the child node representing the action definition
        this.nodeService.removeChild(schedule.getNodeRef(), actionDefinition.getNodeRef());
    }

    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#updateDispositionActionDefinition(org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule, org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition, java.util.Map)
     */
    public DispositionActionDefinition updateDispositionActionDefinition(DispositionSchedule schedule,
                DispositionActionDefinition actionDefinition, Map<QName, Serializable> actionDefinitionParams)
    {
        // update the node with properties
        this.nodeService.addProperties(actionDefinition.getNodeRef(), actionDefinitionParams);
        
        // get the updated disposition schedule and retrieve the updated action definition
        NodeRef scheduleParent = this.nodeService.getPrimaryParent(schedule.getNodeRef()).getParentRef();
        DispositionSchedule updatedSchedule = this.getDispositionSchedule(scheduleParent);
        return updatedSchedule.getDispositionActionDefinition(actionDefinition.getId());
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
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getNextDispositionAction(org.alfresco.service.cmr.repository.NodeRef)
     */
    public DispositionAction getNextDispositionAction(NodeRef nodeRef)
    {
        DispositionAction result = null;
        NodeRef dispositionActionNodeRef = getNextDispostionAction(nodeRef);
        if (dispositionActionNodeRef != null)
        {
            result = new DispositionActionImpl(this.serviceRegistry, dispositionActionNodeRef);
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
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getNextRecordIdentifier(org.alfresco.service.cmr.repository.NodeRef)
     */
    public String getNextRecordIdentifier(NodeRef container)
    {
        if(nodeService.hasAspect(container, ASPECT_RECORD_COMPONENT_ID))
        {
            String parentIdentifier = (String)nodeService.getProperty(container, PROP_IDENTIFIER);  
            return parentIdentifier + "-" + GUID.generate();
        }
        else
        {
            return GUID.generate();
        }
    }
    
    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    
    /**
     * This method examines the old and new property sets and for those properties which
     * have changed, looks for script resources corresponding to those properties.
     * Those scripts are then called via the ScriptService.
     * 
     * @param nodeWithChangedProperties the node whose properties have changed.
     * @param oldProps the old properties and their values.
     * @param newProps the new properties and their values.
     * 
     * @see #lookupScripts(Map<QName, Serializable>, Map<QName, Serializable>)
     */
    private void lookupAndExecuteScripts(NodeRef nodeWithChangedProperties,
    		Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
	    List<NodeRef> scriptRefs = lookupScripts(oldProps, newProps);
	    
        Map<String, Object> objectModel = new HashMap<String, Object>(1);
        objectModel.put("node", nodeWithChangedProperties);
        objectModel.put("oldProperties", oldProps);
        objectModel.put("newProperties", newProps);

        for (NodeRef scriptRef : scriptRefs)
        {
            serviceRegistry.getScriptService().executeScript(scriptRef, null, objectModel);
        }
    }
    
    /**
     * This method determines which properties have changed and for each such property
     * looks for a script resource in a well-known location.
     * 
     * @param oldProps the old properties and their values.
     * @param newProps the new properties and their values.
     * @return A list of nodeRefs corresponding to the Script resources.
     * 
     * @see #determineChangedProps(Map<QName, Serializable>, Map<QName, Serializable>)
     */
    private List<NodeRef> lookupScripts(Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        List<NodeRef> result = new ArrayList<NodeRef>();

        Set<QName> changedProps = determineChangedProps(oldProps, newProps);
        for (QName propQName : changedProps)
        {
            QName prefixedQName = propQName.getPrefixedQName(serviceRegistry.getNamespaceService());

            String [] splitQName = QName.splitPrefixedQName(prefixedQName.toPrefixString());
            final String shortPrefix = splitQName[0];
            final String localName = splitQName[1];

            // This is the filename pattern which is assumed.
            // e.g. a script file cm_name.js would be called for changed to cm:name
            String expectedScriptName = shortPrefix + "_" + localName + ".js";
            
            NodeRef nextElement = nodeService.getChildByName(scriptsFolderNodeRef, ContentModel.ASSOC_CONTAINS, expectedScriptName);
            if (nextElement != null) result.add(nextElement);
        }

    	return result;
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
    		if (!newProps.containsKey(qn) ||
    		        !oldProps.get(qn).equals(newProps.get(qn)))
    		{
    		    result.add(qn);
    		}
    	}
        for (QName qn : newProps.keySet())
        {
            if (!oldProps.containsKey(qn))
            {
                result.add(qn);
            }
        }
    	
    	return result;
    }
}
