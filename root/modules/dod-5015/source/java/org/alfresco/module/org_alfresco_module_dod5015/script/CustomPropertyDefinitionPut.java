/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Implementation for Java backed webscript to add/update RM custom property definitions
 * to the custom model.
 * 
 * @author Neil McErlean
 */
public class CustomPropertyDefinitionPut extends AbstractCustomPropertyDefnWrite
{
    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }

    protected boolean isRequestToCreateNewProp(Map<String, Serializable> params)
    {
        // If the URL includes an 'element', then it is a create, else an update
        return params.get(PARAM_ELEMENT) != null;
    }
    
    protected String getUrlResult(WebScriptRequest req, QName propQName)
    {
        return req.getServicePath();
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        JSONObject json = null;
        Map<String, Object> ftlModel = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            
            ftlModel = handlePropertyRequest(req, json);
        }
        catch (IOException iox)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Could not read content from req.", iox);
        }
        catch (JSONException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                        "Could not parse JSON from req.", je);
        }
        
        return ftlModel;
    }

    /**
     * Applies custom properties.
     */
    protected Map<String, Object> handlePropertyRequest(WebScriptRequest req, JSONObject json)
            throws JSONException
    {
        Map<String, Object> result = new HashMap<String, Object>();
        
        Map<String, Serializable> params = getParamsFromUrlAndJson(req, json);
        
        QName propertyQName;
        if (isRequestToCreateNewProp(params))
        {
            propertyQName = createNewPropertyDefinition(params);
        }
        else
        {
            propertyQName = updatePropertyDefinition(params);
        }
        String localName = propertyQName.getLocalName();
        
        result.put("success", true);
        result.put(PROP_ID, localName);
    
        String urlResult = getUrlResult(req, propertyQName);
        result.put(URL, urlResult);
    
        return result;
    }
}