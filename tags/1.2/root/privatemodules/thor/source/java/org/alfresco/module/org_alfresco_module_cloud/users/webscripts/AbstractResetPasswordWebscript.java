/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.users.webscripts;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.users.WorkflowModelResetPassword;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;

/**
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class AbstractResetPasswordWebscript extends DeclarativeWebScript
{
    protected static final String PARAM_ID = "id";
    protected static final String PARAM_KEY = "key";
    
    protected WorkflowService workflowService;
    
    protected String timerEnd = "P28D";

    
    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
    
    public void setTimerEnd(String timerEnd)
    {
        this.timerEnd = timerEnd;
    }
    
    /**
     * This method ensures that the id refers to an in-progress workflow and that the key matches
     * that stored in the workflow.
     * @throws WebScriptException a 404 if any of the above is not true.
     */
    protected void validateIdAndKey(String id, String key)
    {
        WorkflowInstance workflowInstance = null;
        try
        {
            workflowInstance = workflowService.getWorkflowById(id);
        }
        catch (WorkflowException ignored)
        {
            // Intentionally empty.
        }
        
        if (workflowInstance == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Reset Password cannot be linked to an ongoing secure process.");
        }
        
        String recoveredKey;
        String username;
        if ( workflowInstance.isActive())
        {
            // If the workflow is active we will be able to read the path properties.
            Map<QName, Serializable> pathProps = workflowService.getPathProperties(id);
            
            username = (String) pathProps.get(WorkflowModelResetPassword.WF_PROP_USERNAME);
            recoveredKey = (String) pathProps.get(WorkflowModelResetPassword.WF_PROP_KEY);
        }
        else
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Reset Password cannot be linked to an ongoing secure process.");
        }
        if (username == null || recoveredKey == null || !recoveredKey.equals(key))
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Reset Password cannot be linked to an ongoing secure process.");
        }
    }
}
