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
 * This class is the controller for the account-cancellation.post web script.
 * 
 * @author Frederik Heremans
 */
public class AccountCancellationPost extends AbstractAccountSignupWebscript
{
    private static final Log log = LogFactory.getLog(AccountCancellationPost.class);
    private static final String JSON_KEY_SUCCESS = "success";
    
    @SuppressWarnings("boxing")
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        JSONObject json = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            
            final String key = json.getString(PARAM_KEY);
            final String id = json.getString(PARAM_ID);
            
            // Validate POSTed data.
            ParameterCheck.mandatoryString(PARAM_KEY, key);
            ParameterCheck.mandatoryString(PARAM_ID, id);
            
            if (log.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("requestAccountCancellation for ").append(id).append(",").append(key);
                log.debug(msg.toString());
            }
            
            // Cancel registration
            try
            {
                getRegistrationService().cancelRegistration(id, key);
            }
            catch (NoRegistrationWorkflowException nrwx)
            {
                // We intentionally do not say what the reason is here (however, log message details are useful - obviously we do not log the password !)
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "Registration not found: " + nrwx.getMessage());
            }
            
           // Return success indicator
            model.put(JSON_KEY_SUCCESS, Boolean.TRUE);
        }
        catch(UnauthorisedException e)
        {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, "", e);
        }
        catch(RegistrationServiceException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Failed to cancel registration.", e);
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
