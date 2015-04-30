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

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 1
 */
public abstract class AbstractInvalidDomainWebscript extends DeclarativeWebScript
{
    public static final String PARAM_DOMAIN = "domain";
    public static final String PARAM_TYPE   = "type";
    public static final String PARAM_NOTES  = "notes";
    
    protected EmailAddressService emailAddressService;
    
    public void setEmailAddressService(EmailAddressService emailAddressService)
    {
        this.emailAddressService = emailAddressService;
    }
    
    /**
     * This method extracts relevant invalidDomain data from a HTTP method body (domain, type, notes).
     */
    protected Map<String, String> extractInvalidDomainDataFromReqBody(WebScriptRequest req)
    {
        String bodyContent;
        try
        {
            bodyContent = req.getContent().getContent();
        } catch (IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from HTTP request body.", e);
        }
        
        JSONObject jsonContent = (JSONObject) JSONValue.parse(bodyContent);
        
        Map<String, String> result = new HashMap<String, String>();
        result.put(PARAM_DOMAIN, (String) jsonContent.get(PARAM_DOMAIN));
        result.put(PARAM_TYPE, (String) jsonContent.get(PARAM_TYPE));
        result.put(PARAM_NOTES, (String) jsonContent.get(PARAM_NOTES));
        
        return result;
    }
    
    protected String getDomainFromURL(WebScriptRequest req)
    {
        return req.getServiceMatch().getTemplateVars().get(PARAM_DOMAIN);
    }
}
