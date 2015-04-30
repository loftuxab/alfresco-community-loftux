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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.users.WorkflowModelResetPassword;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the reset-status.get web script.
 * The UI tier can call this webscript when a specific 'reset password' is ongoing in order
 * to determine if it is still active and what the username is.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class ResetPasswordStatusGet extends AbstractResetPasswordWebscript
{
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String id = templateVars.get(PARAM_ID);
        final String key = req.getParameter(PARAM_KEY);
        
        ParameterCheck.mandatoryString(PARAM_ID, id);
        ParameterCheck.mandatoryString(PARAM_KEY, key);
        
        validateIdAndKey(id, key);
        
        // So now we know that the workflow instance exists, is active and has the correct key. We can proceed.
        // locate activate tasks
        WorkflowTaskQuery processTaskQuery = new WorkflowTaskQuery();
    	processTaskQuery.setProcessId(id);
    	List<WorkflowTask> tasks = workflowService.queryTasks(processTaskQuery, false);
    	
        if (tasks == null || tasks.size() == 0)
        {
            throw new AlfrescoRuntimeException("Invalid workflow identifier: " + id + ", " + key);
        }
        WorkflowTask task = tasks.get(0);
        
        String username = (String) task.getProperties().get(WorkflowModelResetPassword.WF_PROP_USERNAME);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        return model;
    }
}
