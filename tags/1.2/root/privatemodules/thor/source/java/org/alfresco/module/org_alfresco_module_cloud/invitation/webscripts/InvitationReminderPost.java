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
package org.alfresco.module.org_alfresco_module_cloud.invitation.webscripts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.invitation.NoInvitationWorkflowException;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the invitatioreminder.post web script.
 *
 * @author Frederik Heremans
 */
public class InvitationReminderPost extends AbstractCloudInvitationWebscript
{
    private static final String MODEL_KEY_SUCCESS = "success";
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String inviteId = templateVars.get(VAR_INVITE_ID);
        final String key = req.getParameter(PARAM_KEY);
        
        ParameterCheck.mandatoryString(PARAM_KEY, key);
        ParameterCheck.mandatoryString(VAR_INVITE_ID, inviteId);
        
        try
        {
            getCloudInvitationService().remindInvitee(inviteId, key);
        }
        catch (NoInvitationWorkflowException niwx)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Invalid Invitation.", niwx);
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(MODEL_KEY_SUCCESS,  Boolean.TRUE);
        return model;
    }
}
