/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowCloudType;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowStatus;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorklfowContentRetainStrategy;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.BeforeRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.WorkflowQNameConverter;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class responsible for adding the behaviors related to the hybrid workflow aspect.
 *
 * @author Frederik Heremans
 */
public class HybridWorkflowCloudAspect implements OnUpdatePropertiesPolicy, BeforeRemoveAspectPolicy
{
    private static final Log logger = LogFactory.getLog(HybridWorkflowCloudAspect.class);
    private static final String ERROR_INVALID_START_PROPERTY = "hybrid.workflow.error.invalid.start.property";
    
    private PolicyComponent policyComponent;
    
    private ProcessEngine activitiProcessEngine; 
    
    private WorkflowQNameConverter workflowQNameConverter;
    
    private Map<HybridWorkflowCloudType, HybridWorkflowTypeHandler> typeHandlers;
    
    private NodeService nodeService;
    
    private WorkflowService workflowService;
    
    private Set<String> storesToIgnore;
    
    
    /**
     * Binds all behaviours needed for Hybrid Workflow.
     */
    public void init()
    {
        // Hybrid worklfow aspect removed
        policyComponent.bindClassBehaviour(BeforeRemoveAspectPolicy.QNAME,
                    HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW, 
                    new JavaBehaviour(this, "beforeRemoveAspect", NotificationFrequency.TRANSACTION_COMMIT));
        
        // Sync removed
        policyComponent.bindClassBehaviour(BeforeRemoveAspectPolicy.QNAME,
                    SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, 
                    new JavaBehaviour(this, "beforeRemoveAspect", NotificationFrequency.TRANSACTION_COMMIT));
        
        // Aspect properties changed
        policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME, 
                    HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW, 
                    new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
    }
    
    /**
     * @param handlers list of handlers that are used when workflow actions are required for
     * a certain type of workflow.
     */
    public void setTypeHandlers(List<HybridWorkflowTypeHandler> handlers)
    {
        typeHandlers = new HashMap<HybridWorkflowModel.HybridWorkflowCloudType, HybridWorkflowTypeHandler>();
        for(HybridWorkflowTypeHandler handler : handlers)
        {
            typeHandlers.put(handler.getType(), handler);
        }
    }
    
    public void setStoresToIgnore(List<String> toIgnore)
    {
        this.storesToIgnore = new HashSet<String>();
        this.storesToIgnore.addAll(toIgnore);
    }
    
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    public void setActivitiProcessEngine(ProcessEngine activitiProcessEngine)
    {
        this.activitiProcessEngine = activitiProcessEngine;
    }

    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
   
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.workflowQNameConverter = new WorkflowQNameConverter(namespaceService);
    }
    
    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before,
                Map<QName, Serializable> after)
    {
        // Filter based on hybrid-related status-property change
        if(after.containsKey(HybridWorkflowModel.PROP_WORKFLOW_STATUS))
        {
            HybridWorkflowStatus newStatus = getStatus(after.get(HybridWorkflowModel.PROP_WORKFLOW_STATUS));
            HybridWorkflowStatus oldStatus = getStatus(before.get(HybridWorkflowModel.PROP_WORKFLOW_STATUS));
            
            if(oldStatus != newStatus || newStatus == HybridWorkflowStatus.STARTED_ON_PREMISE)
            {
                handleStatusChange(oldStatus, newStatus, nodeRef, after);
            }
        }
    }
    
    /**
     * Handles both the removal of hybrid-workflow aspect and sync-aspect.
     */
    @Override
    public void beforeRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if(HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW.equals(aspectTypeQName))
        {
            // Hybrid aspect has been removed, worklfow is finished or cancelled on-premise. We need to
            // check if the cloud-workflow is still running
            removeHybridWorkflowForNode(nodeRef);
        }
        else if(SyncModel.ASPECT_SYNC_SET_MEMBER_NODE.equals(aspectTypeQName))
        {
            // We're only interested in content which is part of a hybrid workflow which is being "unsynced"
            if(nodeService.hasAspect(nodeRef, HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW)) 
            {
                removeHybridWorkflowForNode(nodeRef);
                boolean deleted = false;
                Serializable property = nodeService.getProperty(nodeRef, HybridWorkflowModel.PROP_RETAIN_STRATEGY);
				if (property != null && property instanceof String) 
				{
					HybridWorklfowContentRetainStrategy strategy = 
							HybridWorklfowContentRetainStrategy.getStrategyFromPropertyValue((String) property);
					
					if(strategy == HybridWorklfowContentRetainStrategy.DOCUMENTS_DELETE) {
						// Also delete the document
						nodeService.deleteNode(nodeRef);
						deleted = true;
					}
				}
				
				if(!deleted) {
					nodeService.removeAspect(nodeRef, HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW);
				}
            }
        }
    }
    
    
    /**
     * @param worklfowInstance
     * @return whether or not the given workflowInstance is a hybrid workflow.
     */
    public boolean isHybridWorklfow(WorkflowInstance worklfowInstance)
    {
        for(HybridWorkflowTypeHandler handler : typeHandlers.values())
        {
            if(worklfowInstance.getDefinition().getName().equals(handler.getWorkflowDefinitionName()))
            {
                return true;
            }
        }
        
        return false;
    }

    /**
     * @param propValue
     * @return get status from property value
     * @throws IllegalArgumentException when property-value is not a valid staus
     */
    public HybridWorkflowStatus getStatus(Serializable propValue)
    {
        HybridWorkflowStatus status = null;
        if(propValue != null)
        {
            if(!(propValue instanceof String)) {
                throw new IllegalArgumentException("String expected for hybrid workflow status property, but was: " + propValue.getClass());
            }
            
            status = HybridWorkflowStatus.getStatusFromPropertyValue((String) propValue);
        }
        return status;
    }
    
    /**
     * @param propValue
     * @return get cloud worklfow type from property value
     * @throws IllegalArgumentException when property-value is not a valid type
     */
    public HybridWorkflowCloudType getWorkflowType(Serializable propValue)
    {
        HybridWorkflowCloudType status = null;
        if(propValue != null)
        {
            if(!(propValue instanceof String)) {
                throw new IllegalArgumentException("String expected for hybrid workflow type property, but was: " + propValue.getClass());
            }
            
            status = HybridWorkflowCloudType.getTypeFromPropertyValue((String) propValue);
        }
        return status;
    }
    
    /**
     * @param type
     * @return the handler for the given type. Returns null if the type has no handler associated.
     */
    public HybridWorkflowTypeHandler getHandlerForType(HybridWorkflowCloudType type)
    {
        return typeHandlers.get(type);
    }
    
    protected void removeHybridWorkflowForNode(NodeRef content)
    {
        List<WorkflowInstance> activeWorklfows = workflowService.getWorkflowsForContent(content, true);
        if(activeWorklfows != null && activeWorklfows.size() > 0)
        {
            for(WorkflowInstance worklfowInstance : activeWorklfows)
            {
                // Only cancel 'hybrid' workflows related to the node. Potentially, another worklfow
                // could have been started containing the node, initiated by a cloud-user
                if(isHybridWorklfow(worklfowInstance))
                {
                    // Cancel the process
                    workflowService.cancelWorkflow(worklfowInstance.getId());
                    logger.info("Cancelled cloud-workflow, triggered on-premise: " + worklfowInstance.getId());
                }
            }
        }
    }

    protected void handleStatusChange(HybridWorkflowStatus oldStatus, HybridWorkflowStatus newStatus, NodeRef content, Map<QName, Serializable> properties)
    {
        // Ignore some stores, we're only interested in the live-store. This prevent adding
        // additional node-refs that point to the same content in a different store (eg. version2store)
        if(storesToIgnore.contains(content.getStoreRef().toString()))
        {
            return;
        }
        
        // Extract the on-premise workflow ID, type and handler
        String onPremiseWorkflowId = (String) properties.get(HybridWorkflowModel.PROP_ON_PREMISE_WORKFLOW_ID);
        String typePropertyValue = (String) properties.get(HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE);
        HybridWorkflowCloudType workflowType = getWorkflowType(typePropertyValue);
        
        HybridWorkflowTypeHandler handler = typeHandlers.get(workflowType);
        if(handler == null)
        {
            throw new WorkflowException(ERROR_INVALID_START_PROPERTY, HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE, typePropertyValue);
        }
        
        if(newStatus == HybridWorkflowStatus.STARTED_ON_PREMISE)
        {
            // Check if a cloud-workflow exists for the on-premise-workflow associated with this node
            ProcessInstance existingProcess = activitiProcessEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .variableValueEquals(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_ON_PREMISE_WORKFLOW_ID), onPremiseWorkflowId)
                .singleResult();
            
            NodeRef workflowPackage = null;
            List<NodeRef> existingPackageContent = null;
            
            if(existingProcess != null)
            {
                // Lookup package of existing workflow to allow the content to be added
                ActivitiScriptNode scriptNode = (ActivitiScriptNode) activitiProcessEngine.getRuntimeService()
                    .getVariable(existingProcess.getId(), workflowQNameConverter.mapQNameToName(WorkflowModel.ASSOC_PACKAGE));
                workflowPackage = scriptNode.getNodeRef();
                
                existingPackageContent = workflowService.getPackageContents(workflowPackage);
            }
            else
            {
                // First content for the on-premise workflow, create a new workflow
                workflowPackage = createWorkflowAndPackage(handler, content, properties);
                existingPackageContent = new ArrayList<NodeRef>();
            }
            
            if(!existingPackageContent.contains(content))
            {
                // Add document to package of existing/created workflow
                nodeService.addChild(workflowPackage, content, 
                            WorkflowModel.ASSOC_PACKAGE_CONTAINS, 
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                                        QName.createValidLocalName((String)nodeService.getProperty(content, ContentModel.PROP_NAME))));
                logger.info("Adding child to cloud-workflow: " + content.getId());
                
                // Update property, indicating worklow associated with this content is started on the cloud
                nodeService.setProperty(content, HybridWorkflowModel.PROP_WORKFLOW_STATUS, HybridWorkflowStatus.STARTED_ON_CLOUD.getPropertyValue());
            }
        }
    }
    
    /**
     * @param handler
     * @param content ref to content
     * @param properties content properties
     * @return a reference to a worklfow-package that belongs to the newly created worklfow.
     */
    protected NodeRef createWorkflowAndPackage(HybridWorkflowTypeHandler handler, NodeRef content, Map<QName, Serializable> properties)
    {
        Map<QName, Serializable> startProps =  handler.getStartProperties(content, properties);
        
        NodeRef workflowPackage = workflowService.createPackage(null);
        startProps.put(WorkflowModel.ASSOC_PACKAGE, workflowPackage);
        
        // Actually start the workflow
        WorkflowDefinition definition = workflowService.getDefinitionByName(handler.getWorkflowDefinitionName());
        WorkflowPath startedPath = workflowService.startWorkflow(definition.getId(), startProps);
        
        logger.info("Starting new hybrid wokflow-process: " + handler.getWorkflowDefinitionName());
        
        // Finish the start-task for the workflow
        WorkflowTask startTask = workflowService.getStartTask(startedPath.getInstance().getId());
        workflowService.endTask(startTask.getId(), ActivitiConstants.DEFAULT_TRANSITION_NAME);
        
        return workflowPackage;
    }
}
