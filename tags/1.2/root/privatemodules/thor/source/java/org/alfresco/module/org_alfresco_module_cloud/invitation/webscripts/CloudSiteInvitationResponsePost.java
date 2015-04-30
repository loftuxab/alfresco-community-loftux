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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitation;
import org.alfresco.module.org_alfresco_module_cloud.invitation.InvalidInvitationAcceptanceException;
import org.alfresco.module.org_alfresco_module_cloud.invitation.NoInvitationWorkflowException;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.ParameterCheck;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the cloud-invitation-response.post web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class CloudSiteInvitationResponsePost extends AbstractCloudInvitationWebscript
{
    private static final String FTL_MODEL_INVITATION = "invitation";
    
    static final String JSON_VAR_RESPONSE                  = "response";
    static final String JSON_VAR_RESPONSE_ACCEPT           = "accept";
    static final String JSON_VAR_RESPONSE_ACCEPT_ALT_EMAIL = "accept-alt-email";
    static final String JSON_VAR_RESPONSE_REJECT           = "reject";
    
    static final String JSON_VAR_FIRST_NAME                = "firstName";
    static final String JSON_VAR_LAST_NAME                 = "lastName";
    static final String JSON_VAR_PASSWORD                  = "password";
    static final String JSON_VAR_ALT_EMAIL                 = "alt-email";
    
    private AuthenticationService authenticationService;
    private DirectoryService      directoryService;
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String inviteId = templateVars.get(VAR_INVITE_ID);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        JSONObject json = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            // Mandatory parameters
            final String invitationResponse = json.getString(JSON_VAR_RESPONSE);
            final String key                = json.getString(PARAM_KEY);
            
            ParameterCheck.mandatoryString(JSON_VAR_RESPONSE, invitationResponse);
            ParameterCheck.mandatoryString(PARAM_KEY, key);
            
            
            // The following JSON parameters are mandatory for some code paths and ignored for others. See below.
            String inviteeFirstName = json.has(JSON_VAR_FIRST_NAME) ? json.getString(JSON_VAR_FIRST_NAME) : null;
            String inviteeLastName  = json.has(JSON_VAR_LAST_NAME) ? json.getString(JSON_VAR_LAST_NAME) : null;
            final String inviteePassword  = json.has(JSON_VAR_PASSWORD) ? json.getString(JSON_VAR_PASSWORD) : null;
            
            final String alternativeEmail  = json.has(JSON_VAR_ALT_EMAIL) ? json.getString(JSON_VAR_ALT_EMAIL) : null;
            
            CloudInvitation invitation;
            try
            {
                if (JSON_VAR_RESPONSE_ACCEPT.equals(invitationResponse))
                {
                    // There are two flavours of "accept": the invitee can already exist in the system or not.
                    // If the latter, then they must be signed up as part of the invitation acceptance.
                    
                    // We'll take the presence of inviteeFirstName as the indicator that the caller intends an invitation-with-signup
                    if (inviteeFirstName != null)
                    {
                        // Then we'll need all three params.
                        ParameterCheck.mandatoryString(JSON_VAR_LAST_NAME, inviteeLastName);
                        ParameterCheck.mandatoryString(JSON_VAR_PASSWORD, inviteePassword);
                        
                        // This will lead to the creation of a new user with these metadata.
                        invitation = getCloudInvitationService().acceptInvitationWithSignup(inviteId, key, inviteeFirstName, inviteeLastName, inviteePassword);
                    }
                    else
                    {
                        // It is assumed to be an accept without signup = the normal, simple flow.
                        invitation = getCloudInvitationService().acceptInvitation(inviteId, key);
                    }
                }
                else if (JSON_VAR_RESPONSE_ACCEPT_ALT_EMAIL.equals(invitationResponse))
                {
                    // This requires the user with this password to exist.
                    ParameterCheck.mandatoryString(JSON_VAR_ALT_EMAIL, alternativeEmail);
                    ParameterCheck.mandatoryString(JSON_VAR_PASSWORD, inviteePassword);
                    
                    // Does the user actually exist? (this is checked within the CloudInvitationService too.)
                    if ( !directoryService.userExists(alternativeEmail))
                    {
                        // Return a 403. But don't return anything that would tell the caller that the user doesnt' exist.
                        throw new WebScriptException(Status.STATUS_FORBIDDEN, "Invalid Invitation acceptance.");
                    }
                    else
                    {
                        // I better explain the code below...
                        //
                        // We need to authenticate in order to ensure that the username/password we've been given is correct.
                        // However authenticationService.authenticate() actually sets the current user which is a side-effect we don't want.
                        // So we need to undo that change and there are a number of ways in which we might try that.
                        //
                        // AuthenticationUtil.clearCurrentSecurityContext() also clears the current tenant context, which we do not want.
                        // We only want to clear the user/authentication context.
                        //
                        // Wrapping the clearCurrentSecurityContext() call in TenantContextHolder.get/setTenantDomain()
                        // has two problems: that class is not intended for general use; and it doesn't work here anyway.
                        //
                        // Therefore we have decided to wrap the authenticate() call in a RunAs System.
                        // We have done this purely to ensure that the authentication & tenant context is the same
                        // after the call as it was before.
                        AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
                        {
                            @Override public Void doWork() throws Exception
                            {
                              try
                              {
                                   authenticationService.authenticate(alternativeEmail, inviteePassword.toCharArray());
                              }
                              catch (AuthenticationException ae)
                              {
                                  // Return a 403. But don't return anything that would tell the caller that the user id was ok.
                                  throw new WebScriptException(Status.STATUS_FORBIDDEN, "Invalid Invitation acceptance.");
                              }
                              return null;
                            }
                        });
                    }
                    
                    invitation = getCloudInvitationService().acceptInvitationAtAlternativeEmail(inviteId, key, alternativeEmail);
                }
                else if (JSON_VAR_RESPONSE_REJECT.equals(invitationResponse))
                {
                    invitation = getCloudInvitationService().rejectInvitation(inviteId, key);
                }
                else
                {
                    throw new WebScriptException("Illegal invitation response: " + invitationResponse);
                }
            }
            catch (InvalidInvitationAcceptanceException iiax)
            {
                throw new WebScriptException(Status.STATUS_FORBIDDEN, "Invalid Invitation acceptance.");
            }
            catch (NoInvitationWorkflowException niwx)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "Invalid Invitation.");
            }
            model.put(FTL_MODEL_INVITATION, invitation);
        }
        catch (InvalidEmailAddressException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid Email Address.", e);
        }
        catch (IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
        }
        catch (JSONException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from req.", e);
        }
        
        return model;
    }
}
