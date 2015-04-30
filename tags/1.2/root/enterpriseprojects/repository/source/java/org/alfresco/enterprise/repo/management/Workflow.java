/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.enterprise.repo.management;

import java.util.List;

import org.alfresco.repo.workflow.WorkflowDeployer;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.repo.workflow.jbpm.JBPMEngine;
import org.alfresco.service.cmr.workflow.WorkflowAdminService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowInstanceQuery;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;

/**
 * An implementation of the {@link WorkflowMBean} interface exposing workflow metrics.
 *
 * @author Gavin Cornwell
 * @since 4.0
 */
public class Workflow implements WorkflowMBean
{
    private WorkflowService workflowService;
    private WorkflowAdminService workflowAdminService;
    private WorkflowDeployer workflowDeployer;
    
    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
    
    public void setWorkflowAdminService(WorkflowAdminService workflowAdminService)
    {
        this.workflowAdminService = workflowAdminService;
    }
    
    public void setWorkflowDeployer(WorkflowDeployer workflowDeployer)
    {
        this.workflowDeployer = workflowDeployer;
    }
    
    @Override
    public boolean isActivitiEngineEnabled()
    {
        return workflowAdminService.isEngineEnabled(ActivitiConstants.ENGINE_ID);
    }

    @Override
    public void setActivitiEngineEnabled(boolean isEnabled)
    {
        workflowAdminService.setEngineEnabled(ActivitiConstants.ENGINE_ID, isEnabled);
    }

    @Override
    public boolean isJBPMEngineEnabled()
    {
        return workflowAdminService.isEngineEnabled(JBPMEngine.ENGINE_ID);
    }
    
    @Override
    public void setJBPMEngineEnabled(boolean isEnabled)
    {
        workflowAdminService.setEngineEnabled(JBPMEngine.ENGINE_ID, isEnabled);
        if (isEnabled)
        {
            // redeploy definitions
            workflowDeployer.init();
        }
    }

    public boolean isActivitiWorkflowDefinitionsVisible()
    {
        return workflowAdminService.isEngineVisible(ActivitiConstants.ENGINE_ID);
    }
    
    @Override
    public void setActivitiWorkflowDefinitionsVisible(boolean isVisible)
    {
        workflowAdminService.setEngineVisibility(ActivitiConstants.ENGINE_ID, isVisible);
    }

    @Override
    public boolean isJBPMWorkflowDefinitionsVisible()
    {
        return workflowAdminService.isEngineVisible(JBPMEngine.ENGINE_ID);
    }

    @Override
    public void setJBPMWorkflowDefinitionsVisible(boolean isVisible)
    {
        workflowAdminService.setEngineVisibility(JBPMEngine.ENGINE_ID, isVisible);
    }

    @Override
    public int getNumberOfActivitiTaskInstances()
    {
        return getNumberOfTaskInstances(ActivitiConstants.ENGINE_ID);
    }

    @Override
    public int getNumberOfActivitiWorkflowDefinitionsDeployed()
    {
        return getNumberOfWorkflowDefinitionsDeployed(ActivitiConstants.ENGINE_ID);
    }

    @Override
    public int getNumberOfActivitiWorkflowInstances()
    {
        return getNumberOfWorkflowInstances(ActivitiConstants.ENGINE_ID);
    }
    
    @Override
    public int getNumberOfJBPMTaskInstances()
    {
        return getNumberOfTaskInstances(JBPMEngine.ENGINE_ID);
    }

    @Override
    public int getNumberOfJBPMWorkflowDefinitionsDeployed()
    {
        return getNumberOfWorkflowDefinitionsDeployed(JBPMEngine.ENGINE_ID);
    }

//    @Override
//    public int getNumberOfJBPMWorkflowInstances()
//    {
//        return getNumberOfWorkflowInstances(JBPMEngine.ENGINE_ID);
//    }
    
    protected int getNumberOfTaskInstances(String engineId)
    {
        int taskCount = 0;
        
        if (workflowAdminService.isEngineEnabled(engineId))
        {
            // query for all active tasks in the system
            WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
            taskQuery.setTaskState(WorkflowTaskState.IN_PROGRESS);
            taskQuery.setEngineId(engineId);
            taskCount = (int) workflowService.countTasks(taskQuery);
        }
        
        return taskCount;
    }
    
    protected int getNumberOfWorkflowDefinitionsDeployed(String engineId)
    {
        int definitionCount = 0;
        
        if (workflowAdminService.isEngineEnabled(engineId))
        {
            List<WorkflowDefinition> workflowDefinitions = workflowService.getDefinitions();
            
            for (WorkflowDefinition workflowDefinition : workflowDefinitions)
            {
                if (workflowDefinition.getId().startsWith(engineId))
                {
                    definitionCount++;
                }
            }
        }
        
        return definitionCount;
    }
    
    protected int getNumberOfWorkflowInstances(String engineId)
    {
        int workflowInstancesCount = 0;
        
        if (workflowAdminService.isEngineEnabled(engineId))
        {
        	WorkflowInstanceQuery query = new WorkflowInstanceQuery();
        	query.setActive(true);
        	query.setEngineId(engineId);
        	workflowInstancesCount = (int) workflowService.countWorkflows(query);
        }
        
        return workflowInstancesCount;
    }
}
