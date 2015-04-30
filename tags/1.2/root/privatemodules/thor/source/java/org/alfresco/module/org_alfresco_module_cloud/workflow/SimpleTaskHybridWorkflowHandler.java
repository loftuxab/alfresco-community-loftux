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
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowCloudType;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.namespace.QName;

/**
 * {@link HybridWorkflowTypeHandler} for simple tasks.
 *
 * @author Frederik Heremans
 */
public class SimpleTaskHybridWorkflowHandler extends BaseHybridWorkflowTypeHandler
{
    private static final String WORKFLOW_DEFINITION_NAME = BPMEngineRegistry.createGlobalId(ActivitiConstants.ENGINE_ID, "hybridAdhoc");
    private static final String ERROR_MULTIPLE_ASSIGNEES_SELECTED = "hybrid.workflow.error.multiple.assignees";
    
    @Override
    public HybridWorkflowCloudType getType()
    {
        return HybridWorkflowCloudType.SIMPLE_TASK;
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
        properties.put(HybridWorkflowModel.PROP_RESULT, ActivitiConstants.DEFAULT_TRANSITION_NAME);
        populateComments(execution, properties);
        return properties;
    }
    
    @Override
    protected void handleAndValidateAssignees(List<NodeRef> assigneeNodes,
                Map<QName, Serializable> startProps, NodeRef content)
    {
        if(assigneeNodes.size() == 0)
        {
            throw new WorkflowException(ERROR_NO_ASSIGNEE_SELECTED);
        }
        else if(assigneeNodes.size() > 1)
        {
            throw new WorkflowException(ERROR_MULTIPLE_ASSIGNEES_SELECTED);
        }
        
        startProps.put(WorkflowModel.ASSOC_ASSIGNEE, new ActivitiScriptNode(assigneeNodes.get(0), serviceRegistry));
    }

}
