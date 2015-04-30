/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowCloudType;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowStatus;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.node.NodeServicePolicies.BeforeRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.repo.workflow.WorkflowQNameConverter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;


/**
 * Hybrid workflow aspect, containing on-premise policies.
 *
 * @author Frederik Heremans
 */
public class HybridWorkflowOnPremiseAspect implements OnUpdatePropertiesPolicy, BeforeRemoveAspectPolicy
{
    private static final Logger logger = Logger.getLogger(HybridWorkflowOnPremiseAspect.class);
    
    private static final QName PROP_REVIEW_RESULT = QName.createQName(NamespaceService.WORKFLOW_MODEL_1_0_URI, "reviewOutcome");
    private static final String ERROR_UNSYNC_NOT_ALLOWED = "hybrid.workflow.error.unsync.not.allowed";
    
    private PolicyComponent policyComponent;
    private ProcessEngine activitiProcessEngine;
    private WorkflowQNameConverter workflowQNameConverter;
    private NodeService nodeService;
    private MessageService messageService;
    
    /**
     * Registers all behaviours needed for Hybrid Workflows.
     */
    public void init()
    {
        // Aspect properties changed, possible status-change
        policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME, 
                    HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW, new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
        
        // SSD deleted, check if this is triggered by the workflow
        policyComponent.bindClassBehaviour(BeforeRemoveAspectPolicy.QNAME, 
                    SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, new JavaBehaviour(this, "beforeRemoveAspect", NotificationFrequency.TRANSACTION_COMMIT));
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
            
            if(oldStatus != newStatus)
            {
                handleStatusChange(oldStatus, newStatus, nodeRef, after);
            }
        }
    }
    
    @Override
    public void beforeRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if(nodeService.exists(nodeRef) && nodeService.hasAspect(nodeRef, HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW))
        {
            throw new WorkflowException(messageService.getMessage(ERROR_UNSYNC_NOT_ALLOWED));
        }
    }
    
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    public void setActivitiProcessEngine(ProcessEngine activitiProcessEngine)
    {
        this.activitiProcessEngine = activitiProcessEngine;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.workflowQNameConverter = new WorkflowQNameConverter(namespaceService);
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setMessageService(MessageService messageService)
    {
        this.messageService = messageService;
    }
    
    /**
     * @param oldStatus old status
     * @param newStatus new status
     * @param nodeRef content the change is picked up from
     * @param properties new properties set on updated node
     */
    protected void handleStatusChange(HybridWorkflowStatus oldStatus, HybridWorkflowStatus newStatus,
                NodeRef nodeRef, Map<QName, Serializable> properties)
    {
        String workflowId = getWorkflowId(properties, true);
        String processInstanceId = BPMEngineRegistry.getLocalId(workflowId);
        
        // Validate if the workflow still exists
        boolean processExists = activitiProcessEngine.getRuntimeService()
            .createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .count() == 1;
        
        if(processExists)
        {
            if(newStatus == HybridWorkflowStatus.STARTED_ON_CLOUD)
            {
                handleCloudStart(processInstanceId, properties);
            }
            else if(newStatus == HybridWorkflowStatus.CANCELLED_ON_CLOUD)
            {
                handleCloudCancel(processInstanceId, properties);
            }
            else if(newStatus == HybridWorkflowStatus.FINISHED_ON_CLOUD)
            {
                handleCloudFinish(processInstanceId, properties);
            }
        } 
        else
        {
            // Local worklfow doesn't exist anymore and cloud-part triggered the action. This is a highly unusual
            // situation and shouldn't occur in normal circumstances
            if(newStatus == HybridWorkflowStatus.FINISHED_ON_CLOUD || 
                       newStatus == HybridWorkflowStatus.STARTED_ON_CLOUD || 
                       newStatus == HybridWorkflowStatus.CANCELLED_ON_CLOUD)
            {
                logger.warn(MessageFormat.format("Received status change ({0}) for hybrid workflow from Alfresco Cloud, but local workflow ({1}) no longer exists.",
                            newStatus, workflowId));
                
            }
        }
    }
    
    /**
     * Handle workflow that is started on the cloud.
     * 	
     * @param processInstanceId
     * @param properties aspect properties
     */
    protected void handleCloudStart(String processInstanceId, Map<QName, Serializable> properties)
    {
       // Check if we need to update the worklfow-status on the workflow itself
        String currentWorkflowStatus = (String) activitiProcessEngine.getRuntimeService()
            .getVariable(processInstanceId, workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS));
        
        if(currentWorkflowStatus == null || !currentWorkflowStatus.equals(HybridWorkflowStatus.STARTED_ON_CLOUD.getPropertyValue()))
        {
            activitiProcessEngine.getRuntimeService().setVariable(
                        processInstanceId, 
                        workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS),
                        HybridWorkflowStatus.STARTED_ON_CLOUD.getPropertyValue());
        }
    }
    
    /**
     * Handle workflow that is cancelled on the cloud.
     * 
     * @param processInstanceId
     * @param properties aspect properties
     */
    protected void handleCloudCancel(String processInstanceId, Map<QName, Serializable> properties)
    {
        String currentWorkflowStatus = (String) activitiProcessEngine.getRuntimeService()
            .getVariable(processInstanceId, workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS));
        
        HybridWorkflowStatus status = getStatus(currentWorkflowStatus);
        if(status != HybridWorkflowStatus.FINISHED_ON_CLOUD && status != HybridWorkflowStatus.CANCELLED_ON_CLOUD)
        {
            // Set new status on process and signal it
            Map<String, Object> variablesForSignal = Collections.singletonMap(
                        workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS), 
                        (Object) HybridWorkflowStatus.CANCELLED_ON_CLOUD.getPropertyValue());
            activitiProcessEngine.getRuntimeService().signal(processInstanceId, variablesForSignal);
        }
        else
        {
            logger.warn("Ignore cloud-cancel request, worklfow already received cancel or finish from cloud (" + processInstanceId + ")");
        }
    }
    
    /**
     * Handle workflow that is finished on the cloud.
     * 
     * @param processInstanceId
     * @param properties aspect properties
     */
    protected void handleCloudFinish(String processInstanceId, Map<QName, Serializable> properties)
    {
        String currentWorkflowStatus = (String) activitiProcessEngine.getRuntimeService()
        .getVariable(processInstanceId, workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS));
    
        // Check if process state hasn't already been altered
        if(currentWorkflowStatus == null || !currentWorkflowStatus.equals(HybridWorkflowStatus.FINISHED_ON_CLOUD.getPropertyValue()))
        {
            Map<String, Object> variablesToSet = new HashMap<String, Object>();
            
            // Update status on workflow itself
            variablesToSet.put(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS),
                        HybridWorkflowStatus.FINISHED_ON_CLOUD.getPropertyValue());
            
            // Handle outcome
            handleCloudWorkflowOutcome(variablesToSet, properties);
            
            // Set variables before signalling
            activitiProcessEngine.getRuntimeService().setVariables(processInstanceId, variablesToSet); 
            
            // Signal the workflow to carry on now cloud-part is complete
            activitiProcessEngine.getRuntimeService().signal(processInstanceId);
        }
    }
    
    protected void handleCloudWorkflowOutcome(Map<String, Object> processVariablesToSet,
                Map<QName, Serializable> properties)
    {
        HybridWorkflowCloudType type = getWorkflowType(properties.get(HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE));
        
        switch(type)
        {
            case REVIEW:
                processVariablesToSet.put(workflowQNameConverter.mapQNameToName(PROP_REVIEW_RESULT), 
                            properties.get(HybridWorkflowModel.PROP_RESULT));
                processVariablesToSet.put(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_ACTUAL_APPROVAL_PERCENTAGE), 
                            properties.get(HybridWorkflowModel.PROP_ACTUAL_APPROVAL_PERCENTAGE));
                processVariablesToSet.put(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_ACTUAL_APPROVAL_PERCENTAGE), 
                        properties.get(HybridWorkflowModel.PROP_ACTUAL_APPROVAL_PERCENTAGE));
                break;
            default:
            	break;
            
        }
        
        // Comments should be copied, regardless of the type
        processVariablesToSet.put(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_COMMENTS), 
                    properties.get(HybridWorkflowModel.PROP_COMMENTS));
    }

    /**
     * Extract status from property-value
     * 
     * @param propValue property value
     * @return status, or null
     */
    protected HybridWorkflowStatus getStatus(Serializable propValue)
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
     * Extract worklfow-id from properties.
     * 
     * @param properties
     * @param failWhenMissing
     * @return worklfow-id or null if not found and failWhenMissing is false;
     * @throws IllegalArgumentException if not found and failWhenMissing is true.
     */
    protected String getWorkflowId(Map<QName, Serializable> properties, boolean failWhenMissing) {
        // Extract the workflow-id
        String workflowId = (String) properties.get(HybridWorkflowModel.PROP_ON_PREMISE_WORKFLOW_ID);
        if(workflowId == null && failWhenMissing)
        {
            throw new IllegalArgumentException("No workflow-id set on changed node");
        }
        return workflowId;
    }
}
