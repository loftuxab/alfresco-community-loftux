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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.users.WorkflowModelResetPassword;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the change-password.post web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class ChangePasswordPost extends AbstractResetPasswordWebscript
{
    private static final Log log = LogFactory.getLog(ChangePasswordPost.class);
    
    private HistoryService activitiHistoryService;
    
    public void setHistoryService(HistoryService service)
    {
        this.activitiHistoryService = service;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // Extract the various data from the body content.
        String jsonBodyString;
        try
        {
            jsonBodyString = req.getContent().getContent();
        }
        catch (IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
        }
        
        JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonBodyString);
        String newPassword = (String) jsonObject.get("password");
        String id = (String) jsonObject.get(PARAM_ID);
        String key = (String) jsonObject.get(PARAM_KEY);
        
        // These are all mandatory parameters
        ParameterCheck.mandatoryString("password", newPassword);
        ParameterCheck.mandatoryString(PARAM_ID, id);
        ParameterCheck.mandatoryString(PARAM_KEY, key);
        
        validateIdAndKey(id, key);
        
        // So now we know that the workflow instance exists, is active and has the correct key. We can proceed.
        WorkflowTaskQuery processTaskQuery = new WorkflowTaskQuery();
    	processTaskQuery.setProcessId(id);
    	List<WorkflowTask> tasks = workflowService.queryTasks(processTaskQuery, false);
        
        if (tasks == null || tasks.size() == 0)
        {
            throw new AlfrescoRuntimeException("Invalid workflow identifier: " + id + ", " + key);
        }
        WorkflowTask task = tasks.get(0);
        
        // Set the provided password into the task. We will remove this after we have updated the user's authentication details.
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(WorkflowModelResetPassword.WF_PROP_PASSWORD, newPassword);
        
        // Note the taskId as taken from the WorkflowService will include a "activiti$" prefix.
        final String taskId = task.getId();
        workflowService.updateTask(taskId, props, null, null);
        workflowService.endTask(taskId, null);
        
        
        // Remove the previous task from Activiti's history - so that the password will not be in the database.
        // See http://www.activiti.org/userguide/index.html#history for a description of how Activiti stores historical records of
        // processes, tasks and properties.
        // The activitiHistoryService does not expect the activiti$ prefix.
        final String activitiTaskId = taskId.replace("activiti$", "");
        activitiHistoryService.deleteHistoricTaskInstance(activitiTaskId);
        
        if (log.isDebugEnabled())
        {
            log.debug("Deleting historical task for security reasons " + activitiTaskId);
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("success", true);
        
        return model;
    }
}
