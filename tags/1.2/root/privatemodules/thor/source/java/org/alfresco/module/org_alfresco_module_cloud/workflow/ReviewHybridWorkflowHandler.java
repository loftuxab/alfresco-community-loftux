/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.workflow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.model.ContentModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowCloudType;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * {@link HybridWorkflowTypeHandler} for simple tasks.
 *
 * @author Frederik Heremans
 */
public class ReviewHybridWorkflowHandler extends BaseHybridWorkflowTypeHandler
{
	protected static final String ERROR_INVALID_REVIEW = "hybrid.workflow.error.invalid.review";
	
    private static final String WORKFLOW_DEFINITION_NAME = BPMEngineRegistry.createGlobalId(ActivitiConstants.ENGINE_ID, "hybridReview");
    private static final QName PROP_REQUIRED_APPROVE_PERCENT = QName.createQName(NamespaceService.WORKFLOW_MODEL_1_0_URI, "requiredApprovePercent");
    private static final QName PROP_ACTUAL_PERCENT = QName.createQName(NamespaceService.WORKFLOW_MODEL_1_0_URI, "actualPercent");
    private static final String REVIEW_APPROVED = "Approve";
    private static final String REVIEW_REJECTED = "Reject";
    
    @Override
    public HybridWorkflowCloudType getType()
    {
        return HybridWorkflowCloudType.REVIEW;
    }

    @Override
    public String getWorkflowDefinitionName()
    {
        return WORKFLOW_DEFINITION_NAME;
    }
    
    @Override
    public Map<QName, Serializable> getResultsAndValidate(DelegateExecution execution)
                throws IllegalStateException
    {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(); 
        
        Number requiredPercentage = (Number) execution.getVariable(workflowQNameConverter.mapQNameToName(PROP_REQUIRED_APPROVE_PERCENT));
        Number actualPercentage = (Number) execution.getVariable(workflowQNameConverter.mapQNameToName(PROP_ACTUAL_PERCENT));
        
        if(actualPercentage == null || requiredPercentage == null) 
        {
        	 throw new WorkflowException(ERROR_INVALID_REVIEW);
        }
        
        // Set result variable, based on percentages
        if(actualPercentage.doubleValue() >= requiredPercentage.doubleValue())
        {
            properties.put(HybridWorkflowModel.PROP_RESULT, REVIEW_APPROVED);
        }
        else
        {
            properties.put(HybridWorkflowModel.PROP_RESULT, REVIEW_REJECTED);
        }
        
        properties.put(HybridWorkflowModel.PROP_REQUIRED_APPROVAL_PERCENTAGE, requiredPercentage);
        properties.put(HybridWorkflowModel.PROP_ACTUAL_APPROVAL_PERCENTAGE, actualPercentage);
        
        
        populateComments(execution, properties);
        return properties;
    }
    
    @Override
    public Map<QName, Serializable> getStartProperties(NodeRef content,
                Map<QName, Serializable> properties)
    {
        Map<QName, Serializable> startProps =  super.getStartProperties(content, properties);
        
        startProps.put(PROP_REQUIRED_APPROVE_PERCENT, properties.get(HybridWorkflowModel.PROP_REQUIRED_APPROVAL_PERCENTAGE));
        return startProps;
    }
    

    @Override
    protected void handleAndValidateAssignees(List<NodeRef> assigneeNodes,
                Map<QName, Serializable> startProps, NodeRef content)
    {

        if(assigneeNodes.size() < 1)
        {
            throw new WorkflowException(ERROR_NO_ASSIGNEE_SELECTED);
        }
        
        // Convert to correct type to use in activiti
        ActivitiScriptNodeList nodeList = new ActivitiScriptNodeList();
        for(NodeRef nodeRef : assigneeNodes)
        {
            nodeList.add(new ActivitiScriptNode(nodeRef, serviceRegistry));
        }
        
        // Populate start-properties
        startProps.put(WorkflowModel.ASSOC_ASSIGNEES, nodeList);
        
        // Make sure assignees have right permissions on document in order to write meta-data
        // and write content
        for(NodeRef assignee : assigneeNodes)
        {
            String userName = (String) serviceRegistry.getNodeService().getProperty(assignee, ContentModel.PROP_USERNAME);
            if(userName != null && !userName.equals(AuthenticationUtil.getRunAsUser()))
            {
                serviceRegistry.getPermissionService().setPermission(content, userName, PermissionService.READ, true);
                serviceRegistry.getPermissionService().setPermission(content, userName, PermissionService.READ_CONTENT, true);
            }
        }
    }
}
