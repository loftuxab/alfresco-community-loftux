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
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.registration.NoRegistrationWorkflowException;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the signup-status.get web script.
 * It is provided in order to detect and help prevent a Cloud Signup from being activated twice.
 * The UI tier can call this webscript when a specific account signup is ready for activation in order
 * to determine if this has already been completed.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class SignupStatusGet extends AbstractAccountSignupWebscript
{
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String signupId = templateVars.get(PARAM_ID);
        final String key = req.getParameter(PARAM_KEY);
        
        ParameterCheck.mandatoryString(PARAM_ID, signupId);
        ParameterCheck.mandatoryString(PARAM_KEY, key);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        String email;
        try
        {
            email = getRegistrationService().getEmailSigningUp(signupId, key);
        }
        catch (NoRegistrationWorkflowException nrwx)
        {
            // We intentionally do not say what the reason is here.
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Email not found.");
        }
        
        boolean isRegistered = getRegistrationService().isRegisteredEmailAddress(email);
        boolean isActivated = getRegistrationService().isActivatedEmailAddress(email);
        boolean isPreRegistered = getRegistrationService().isPreRegistered(signupId, key);
        Registration registration = getRegistrationService().getRegistration(email);
        // complete/incomplete
        
        model.put("email", email);
        model.put("id", signupId);
        model.put("key", key);
        model.put("isRegistered", isRegistered);
        model.put("isActivated", isActivated);
        model.put("isPreRegistered", isPreRegistered);
        if (registration.getInitiatorEmailAddress() != null)
        {
            model.put("initiatorEmailAddress", registration.getInitiatorEmailAddress());
            model.put("initiatorFirstName", registration.getInitiatorFirstName());
            model.put("initiatorLastName", registration.getInitiatorLastName());
        }
        
        return model;
    }
}
