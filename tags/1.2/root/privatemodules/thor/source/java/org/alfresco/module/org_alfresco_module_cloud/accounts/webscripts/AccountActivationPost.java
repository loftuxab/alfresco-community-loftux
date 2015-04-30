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
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.registration.NoRegistrationWorkflowException;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceException;
import org.alfresco.module.org_alfresco_module_cloud.registration.UnauthorisedException;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the account-activation.post web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class AccountActivationPost extends AbstractAccountSignupWebscript
{
    private static final Log log = LogFactory.getLog(AccountActivationPost.class);
    
    protected static final String PARAM_FIRST_NAME = "firstName";
    protected static final String PARAM_LAST_NAME = "lastName";
    protected static final String PARAM_PASSWORD = "password";
    
    @SuppressWarnings("boxing")
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        JSONObject json = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            
            final String firstName = json.optString(PARAM_FIRST_NAME);
            final String lastName = json.optString(PARAM_LAST_NAME);
            final String password = json.getString(PARAM_PASSWORD);
            final String key = json.getString(PARAM_KEY);
            final String id = json.getString(PARAM_ID);
            
            // Validate POSTed data.
            // 1. Required data.
            ParameterCheck.mandatoryString(PARAM_KEY, key);
            ParameterCheck.mandatoryString(PARAM_ID, id);
            
            if (log.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("requestAccountActivation for ").append(id).append(",").append(key);
                log.debug(msg.toString());
            }
            
            // activate registration
            Registration registration;
            try
            {
                registration = getRegistrationService().activateRegistration(id, key, firstName, lastName, password);
            }
            catch (NoRegistrationWorkflowException nrwx)
            {
                // We intentionally do not say what the reason is here (however, log message details are useful - obviously we do not log the password !)
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "Registration not found: "+nrwx.getMessage()+" [fn="+firstName+",ln="+lastName+"]");
            }
            
            // return account details for user
            String userId = registration.getEmailAddress();
            model.put("registration", registration);
            model.put("defaultAccount", getDirectoryService().getDefaultAccount(userId));
            model.put("homeAccount", getRegistrationService().getHomeAccount(userId));
            model.put("secondaryAccounts", getRegistrationService().getSecondaryAccounts(userId));
        }
        catch(UnauthorisedException e)
        {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, "", e);
        }
        catch(RegistrationServiceException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Failed to activate registration.", e);
        }
        catch(IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
        }
        catch(JSONException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from req.", e);
        }
        
        return model;
    }
}
