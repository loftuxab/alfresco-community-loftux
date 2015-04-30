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

import org.alfresco.service.cmr.workflow.WorkflowService;

/**
 * A Management Interface exposing metrics of {@link WorkflowService} for monitoring.
 *
 * @author Gavin Cornwell
 * @since 4.0
 */
public interface WorkflowMBean
{
    /**
     * Determines if the "activiti" workflow engine is enabled.
     * 
     * @return true if the engine is enabled
     */
    public boolean isActivitiEngineEnabled();
    
    /**
     * Enables or disables "activiti" workflow engine.
     * 
     * @param isEnabled boolean to set the status of the "activiti" workflow engine.
     */
    public void setActivitiEngineEnabled(boolean isEnabled);
    
    /**
     * Determines if the "jbpm" workflow engine is enabled.
     * 
     * @return true if the engine is enabled
     */
    public boolean isJBPMEngineEnabled();
    
    /**
     * Enables or disables "jbpm" workflow engine.
     * 
     * @param isEnabled boolean to set the status of the "jbpm" workflow engine.
     */
    public void setJBPMEngineEnabled(boolean isEnabled);
    
    /**
     * Determines whether the Activiti workflow definitions are visible
     * when the Activiti engine is enabled.
     * 
     * NOTE: Workflow definitions can always be retrieved directly 
     * i.e. via name or id
     * 
     * @return true if the definitions are visible
     */
    public boolean isActivitiWorkflowDefinitionsVisible();
    
    /**
     * Sets whether the Activiti workflow definitions are visible
     * when the Activiti engine is enabled.
     * 
     * NOTE: Workflow definitions can always be retrieved directly 
     * i.e. via name or id
     * 
     * @param isEnabled to set the visibility of the definitions.
     */
    public void setActivitiWorkflowDefinitionsVisible(boolean isVisible);
    
    /**
     * Determines whether the JBPM workflow definitions are visible
     * when the JBPM engine is enabled.
     * 
     * NOTE: Workflow definitions can always be retrieved directly 
     * i.e. via name or id
     * 
     * @return true if the definitions are visible
     */
    public boolean isJBPMWorkflowDefinitionsVisible();
    
    /**
     * Sets whether the jBPM workflow definitions are visible
     * when the Activiti engine is enabled.
     * 
     * NOTE: Workflow definitions can always be retrieved directly 
     * i.e. via name or id
     * 
     * @param isEnabled to set the visibility of the definitions.
     */
    public void setJBPMWorkflowDefinitionsVisible(boolean isVisible);
    
    /**
     * Returns the number of Activiti workflow definitions currently deployed.
     * 
     * @return The number of Activiti workflow definitions currently deployed
     */
    public int getNumberOfActivitiWorkflowDefinitionsDeployed();
    
    /**
     * Returns the number of Activiti workflow instances currently deployed.
     * 
     * @return The number of Activiti workflow instances currently deployed
     */
    public int getNumberOfActivitiWorkflowInstances();
    
    /**
     * Returns the number of Activiti task instances currently deployed.
     * 
     * @return The number of Activiti task instances currently deployed
     */
    public int getNumberOfActivitiTaskInstances();
    
    /**
     * Returns the number of JBPM workflow definitions currently deployed.
     * 
     * @return The number of JBPM workflow definitions currently deployed
     */
    public int getNumberOfJBPMWorkflowDefinitionsDeployed();
    
    /**
     * Returns the number of JBPM workflow instances currently deployed.
     * 
     * @return The number of JBPM workflow instances currently deployed
     */
    // MNT-9371, should be returned, when the workflow enumeration will be reworked
    //public int getNumberOfJBPMWorkflowInstances();
    
    /**
     * Returns the number of JBPM task instances currently deployed.
     * 
     * @return The number of JBPM task instances currently deployed
     */
    public int getNumberOfJBPMTaskInstances();
}
