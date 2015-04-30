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
package org.alfresco.module.org_alfresco_module_cloud.emailaddress.webscripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the email-validation.post web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module
 * 
 * @deprecated consider using EmailDomainsValidationPost (can validate one or more domains in bulk)
 */
public class EmailValidationPost extends DeclarativeWebScript
{
    public static final String FTL_MODEL_EMAIL_ADDRESS = "emailAddress";
    public static final String FTL_MODEL_VALIDITY_CHECK = "validityCheck";
    
    private EmailAddressService emailAddressService;

    public void setEmailAddressService(EmailAddressService emailAddressService)
    {
        this.emailAddressService = emailAddressService;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        JSONObject json = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            String email = json.getString("email");
            
            String domain = emailAddressService.getDomain(email);
            DomainValidityCheck validity = emailAddressService.validateDomain(domain);
            
            model.put(FTL_MODEL_EMAIL_ADDRESS, email);
            model.put(FTL_MODEL_VALIDITY_CHECK, validity);
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
