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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitation;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitationImpl;
import org.alfresco.util.ParameterCheck;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

/**
 * This class is the controller for the cloud-invitation.post web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class StartCloudSiteInvitationPost extends AbstractCloudInvitationWebscript
{
    private static final String FTL_MODEL_INVITATIONS = "invitations";
    private static final String SITE_SHORT_NAME_VAR = "shortname";
    
    private static final String PARAM_INVITER_EMAIL = "inviterEmail";
    public static final String PARAM_INVITEE_EMAILS = "inviteeEmails";
    private static final String PARAM_INVITEE_ROLE = "role";
    private static final String PARAM_INVITER_MESSAGE = "inviterMessage";
    
    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String siteShortName = templateVars.get(SITE_SHORT_NAME_VAR);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        try
        {
            JSONObject json = (JSONObject) JSONValue.parse(req.getContent().getContent());
            
            String inviterEmail = (String) json.get(PARAM_INVITER_EMAIL);
            JSONArray inviteeEmails = (JSONArray) json.get(PARAM_INVITEE_EMAILS);
            String inviteeRole = (String) json.get(PARAM_INVITEE_ROLE);
            String inviterMessage = json.containsKey(PARAM_INVITER_MESSAGE) ? (String) json.get(PARAM_INVITER_MESSAGE) : "";
            inviterMessage = StringUtils.encode(inviterMessage);
            
            ParameterCheck.mandatoryString(PARAM_INVITER_EMAIL, inviterEmail);
            ParameterCheck.mandatory(PARAM_INVITEE_EMAILS, inviteeEmails);
            ParameterCheck.mandatoryString(PARAM_INVITEE_ROLE, inviteeRole);
            
            List<CloudInvitation> invitationsIssued = new ArrayList<CloudInvitation>();
            for (Iterator iter = inviteeEmails.iterator(); iter.hasNext(); )
            {
                String nextInvitee = (String) iter.next();
                CloudInvitation invitation = getCloudInvitationService().startInvitation(inviterEmail, nextInvitee, inviteeRole, siteShortName, inviterMessage);
                
                // We may know at this stage that the user is activated and if so we'll put that in the response.
                CloudInvitationImpl invitationImpl = (CloudInvitationImpl) invitation;
                boolean inviteeIsActivated = getRegistrationService().isActivatedEmailAddress(nextInvitee);
                invitationImpl.setInviteeIsActivated(Boolean.valueOf(inviteeIsActivated));
                
                invitationsIssued.add(invitation);
            }
            
            model.put(FTL_MODEL_INVITATIONS, invitationsIssued);
        }
        catch (InvalidEmailAddressException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid Email Address.", e);
        }
        catch (IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
        }
        
        return model;
    }
}
