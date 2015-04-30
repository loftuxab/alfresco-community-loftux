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
package org.alfresco.module.org_alfresco_module_cloud.invitation.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitation;
import org.alfresco.module.org_alfresco_module_cloud.invitation.NoInvitationWorkflowException;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the invitation-status.get web script.
 * It is provided in order to detect and help prevent a {@link CloudInvitation site invitation} from
 * being accepted twice (or indeed accepted and then rejected or other combinations).
 * The UI tier can call this webscript when a specific invitation is accepted or rejected in order
 * to determine if the invitation has already been completed.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class InvitationStatusGet extends AbstractCloudInvitationWebscript
{
    private static final String FTL_MODEL_INVITATION = "invitation";
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String inviteId = templateVars.get(VAR_INVITE_ID);
        final String key = req.getParameter(PARAM_KEY);
        
        ParameterCheck.mandatoryString(PARAM_KEY, key);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        CloudInvitation invitation;
        try
        {
            invitation = getCloudInvitationService().getInvitationStatus(inviteId, key);
        }
        catch (NoInvitationWorkflowException niwx)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Invalid Invitation.", niwx);
        }
        model.put(FTL_MODEL_INVITATION, invitation);
        
        return model;
    }
}
