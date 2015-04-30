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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.pvm.delegate.ExecutionListenerExecution;
import org.alfresco.model.ContentModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowCloudType;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowStatus;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.workflow.WorkflowConstants;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.WorkflowQNameConverter;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.BaseExecutionListener;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;

/**
 * A {@link ExecutionListener} called when cloud-process ends or is cancelled. Updates
 * the aspect properties on the content to notify on-premise workflow.
 *
 * @author Frederik Heremans
 */
public class HybridWorkflowCleanupListener extends BaseExecutionListener
{
    private HybridWorkflowCloudAspect hybridAspect;
    
    private WorkflowQNameConverter workflowQNameConverter;
    
    @Override
    public void notify(final DelegateExecution execution) throws Exception
    {
        String deleteReason = ((ExecutionListenerExecution) execution).getDeleteReason();
        final boolean cancelledOrDeleted = StringUtils.isNotEmpty(deleteReason);
        
        String initiatorUserName = null;
        ActivitiScriptNode initiatorNode = (ActivitiScriptNode) execution.getVariable(WorkflowConstants.PROP_INITIATOR);
        if(initiatorNode != null)
        {
            initiatorUserName = (String) initiatorNode.getProperties().get(ContentModel.PROP_USERNAME);
        }
        else
        {
            initiatorUserName = AuthenticationUtil.getSystemUserName();
        }
        
        // Run the workflow-handling as the initiator, since this person has guaranteed
        // permissions to alter the workflow-content props
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                if(cancelledOrDeleted)
                {
                   // Notify the on-premise workflow about cancellation of process
                    handleWorkflowCancelled(execution);
                }
                else
                {
                    // Process ended normal
                    handleWorklfowFinished(execution);
                }
                return null;
            }
        }, initiatorUserName);
    }
    
    @Override
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        super.setServiceRegistry(serviceRegistry);
        workflowQNameConverter = new WorkflowQNameConverter(serviceRegistry.getNamespaceService());
    }
    
    public void setHybridAspect(HybridWorkflowCloudAspect hybridAspect)
    {
        this.hybridAspect = hybridAspect;
    }
    
    protected void handleWorkflowCancelled(DelegateExecution execution)
    {
        applyPropertiesToContent(
            Collections.singletonMap(
                HybridWorkflowModel.PROP_WORKFLOW_STATUS, 
                (Serializable) HybridWorkflowStatus.CANCELLED_ON_CLOUD.getPropertyValue()),
            execution);
    }
    
    protected void handleWorklfowFinished(DelegateExecution execution)
    {
        // Extract type and handler
        String typeString = (String) execution.getVariable(workflowQNameConverter.mapQNameToName(
                    HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE));
        HybridWorkflowCloudType type = hybridAspect.getWorkflowType((Serializable) typeString);
        HybridWorkflowTypeHandler typeHandler = hybridAspect.getHandlerForType(type);
        
        // Alter local process-status
        execution.setVariable(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS), 
                    HybridWorkflowStatus.FINISHED_ON_CLOUD.getPropertyValue());
        
        // Get result-properties
        Map<QName, Serializable> properties = typeHandler.getResultsAndValidate(execution);
        properties.put(HybridWorkflowModel.PROP_WORKFLOW_STATUS, HybridWorkflowStatus.FINISHED_ON_CLOUD.getPropertyValue());
        
        applyPropertiesToContent(properties, execution);
    }
    
    protected void applyPropertiesToContent( Map<QName, Serializable> properties, DelegateExecution execution)
    {
        // Get package and contents
        NodeRef packageRef = ((ScriptNode) execution.getVariable(
                    workflowQNameConverter.mapQNameToName(WorkflowModel.ASSOC_PACKAGE))).getNodeRef();
        List<NodeRef> contents = getServiceRegistry().getWorkflowService()
            .getPackageContents(packageRef);
        
        // Set properties on each node
        for(NodeRef content : contents)
        {
            getServiceRegistry().getNodeService().addProperties(content, properties);
        }
    }
}
