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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressServiceImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the email-domains-validation.post web script.
 * 
 * @author janv
 * @since Alfresco Cloud Module
 */
public class EmailDomainsValidationPost extends DeclarativeWebScript
{
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
            JSONArray emails = json.getJSONArray("emails");
            
            if (emails != null)
            {
                // find common domains
                Set<String> domains = new HashSet<String>(emails.length());
                for (int i = 0; i < emails.length(); i++)
                {
                    String email = emails.getString(i);
                    
                    String domain = null;
                    if (! email.contains(EmailAddressServiceImpl.EMAIL_DOMAIN_SEPARATOR))
                    {
                        domain = email;
                    }
                    else
                    {
                        domain = emailAddressService.getDomain(email);
                    }
                    
                    // note: domains in the set will not be null or empty string
                    if ((domain != null) && (! domain.isEmpty()))
                    {
                        domains.add(domain);
                    }
                }
                

                HashMap<String, DomainValidityCheck> domainValidityChecks = new HashMap<String, DomainValidityCheck>(domains.size());
                for (String domain : domains)
                {
                    domainValidityChecks.put(domain, emailAddressService.validateDomain(domain));
                }
                
                model.put("domainValidityChecks", domainValidityChecks);
            }
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
