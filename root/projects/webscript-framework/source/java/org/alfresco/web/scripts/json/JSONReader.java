/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts.json;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.Content;
import org.alfresco.web.scripts.Format;
import org.alfresco.web.scripts.FormatReader;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Convert application/json to org.json.JSONObject or org.json.JSONArray
 * 
 * @author Roy Wetherall
 */
public class JSONReader implements FormatReader<Object>
{
    /**
     * @see org.alfresco.web.scripts.FormatReader#getDestinationClass()
     */
    public Class<? extends Object> getDestinationClass()
    {
        return Object.class;
    }

    /**
     * @see org.alfresco.web.scripts.FormatReader#getSourceMimetype()
     */
    public String getSourceMimetype()
    {
        return Format.JSON.mimetype();
    }

    /**
     * @see org.alfresco.web.scripts.FormatReader#read(org.alfresco.web.scripts.WebScriptRequest)
     */
    public Object read(WebScriptRequest req)
    {
        Content content = req.getContent();
        if (content == null)
        {
            throw new WebScriptException("Failed to convert request to JSON");
        }
        
        Object result = null;
        try
        {
            String jsonString = content.getContent();
            if (jsonString.startsWith("[") == true)
            {
                result = new JSONArray(jsonString);
            }
            else
            {    
                result = new JSONObject(jsonString);
            }
        }
        catch (Exception exception)
        {
            throw new WebScriptException("Failed to convert request to JSON", exception);
        }        
        return result;
    }
    
    /**
     * @see org.alfresco.web.scripts.FormatReader#createScriptParameters(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    public Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("json", read(req));
        return params;
    }
}
