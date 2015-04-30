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
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitation;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the pending-invitations-site.get web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class PendingInvitationsSiteGet extends AbstractCloudInvitationWebscript
{
    private static final String FTL_MODEL_INVITATIONS = "invitations";
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String siteShortName = templateVars.get("shortname");
        
        final String invitee = req.getParameter("inviteeUserName");
        
        ParameterCheck.mandatoryString("shortname", siteShortName);
        // invitee is allowed to not exist. (external invitees)
        int limit = getRequestedPageSize(req);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        List<CloudInvitation> invitations = getCloudInvitationService().listPendingInvitationsForSite(siteShortName, invitee, limit);
        
        model.put(FTL_MODEL_INVITATIONS, invitations);
        
        return model;
    }
}
