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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts.form;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.Content;
import org.alfresco.web.scripts.Format;
import org.alfresco.web.scripts.FormatReader;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

/**
 * Convert application/x-www-form-urlencoded content to a Map<String, String>
 * where the keys are parameter names and the values are their associated
 * unencoded values.
 * 
 * @author Neil McErlean
 */
public class UrlEncodedFormReader implements FormatReader<Object>
{
    //FIXME URL-encoded post of forms data is not yet working.
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DECODED_PARAMS = "decodedparams";

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
        return Format.XWWWFORMURLENCODED.mimetype();
    }

    /**
     * @see org.alfresco.web.scripts.FormatReader#read(org.alfresco.web.scripts.WebScriptRequest)
     */
    public Object read(WebScriptRequest req)
    {
        Content content = req.getContent();
        if (content == null)
        {
            throw new WebScriptException("Failed to convert request to URL-unencoded");
        }

        Object result = null;
        try
        {
            String contentString = content.getContent();
            String[] params = contentString.split("&");
            Map<String, String> decodedParams = new LinkedHashMap<String, String>(params.length);

            String encoding = content.getEncoding();
            String encodingToUse = encoding == null ? DEFAULT_ENCODING : encoding;

            for (String param : params)
            {
                String[] nameAndValue = param.split("=");

                String decodedName = URLDecoder.decode(nameAndValue[0], encodingToUse);
                String decodedValue = URLDecoder.decode(nameAndValue[1], encodingToUse);

                if (decodedName.length() > 0)
                {
                    decodedParams.put(decodedName, decodedValue);
                }
            }

            result = decodedParams;
        }
        catch (Exception exception)
        {
            throw new WebScriptException("Failed to convert request to URL-unencoded", exception);
        }        
        return result;
    }

    /**
     * @see org.alfresco.web.scripts.FormatReader#createScriptParameters(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    public Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(DECODED_PARAMS, read(req));
        return params;
    }
}
