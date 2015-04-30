/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.users.WorkflowModelResetPassword;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.ParameterCheck;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the request-reset-password.post web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class RequestPasswordResetPost extends AbstractResetPasswordWebscript
{
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // Extract the username from the body content. This is the user whose password is to be reset.
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
        String username = (String) jsonObject.get("username");
        
        // The username must be a non-empty String.
        ParameterCheck.mandatoryString("username", username);
        
        // Get the (latest) workflow definition for reset-password.
        WorkflowDefinition wfDefinition = workflowService.getDefinitionByName(WorkflowModelResetPassword.WORKFLOW_DEFINITION_NAME);
        
        // create workflow properties
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, I18NUtil.getMessage("resetpasswordwf_resetpassword.resetpassword.workflow.description"));
        props.put(WorkflowModelResetPassword.WF_PROP_USERNAME, username.toLowerCase());
        props.put(WorkflowModel.ASSOC_PACKAGE, workflowService.createPackage(null));
        
        String guid = GUID.generate();
        props.put(WorkflowModelResetPassword.WF_PROP_KEY, guid);
        props.put(WorkflowModelResetPassword.WF_PROP_TIMER_END, timerEnd);
        
        // start the workflow
        WorkflowPath path = workflowService.startWorkflow(wfDefinition.getId(), props);
        if (path.isActive())
        {
            WorkflowTask startTask = workflowService.getStartTask(path.getInstance().getId());
            workflowService.endTask(startTask.getId(), null);
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("success", true);
        
        return model;
    }
}
