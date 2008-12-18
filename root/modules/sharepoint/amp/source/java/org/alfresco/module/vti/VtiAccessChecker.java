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

package org.alfresco.module.vti;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.module.vti.handler.VtiMethodHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;

/**
*
* @author Stas Sokolovsky
*
*/
public class VtiAccessChecker
{

    private Map<String, Pattern> accessRules = new HashMap<String, Pattern>();

    private static final String URL_PARAM_NAME = "URL";
    
    private VtiMethodHandler vtiHandler;
    
    public void setVtiHandler(VtiMethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;        
    }

    public void setAcceptRules(Map<String, String> acceptRules)
    {
        Set<Entry<String, String>> entries = acceptRules.entrySet();
        for (Entry<String, String> entry : entries)
        {
            String requestparam = entry.getKey();
            String regexp = entry.getValue();
            this.accessRules.put(requestparam, Pattern.compile(regexp));
        }
    }

    public boolean isRequestAcceptableForRoot(HttpServletRequest request)
    {        
        if (validSiteUri(request))
            return true;
        
        Set<Entry<String, Pattern>> entries = accessRules.entrySet();
        boolean result = false;
        for (Entry<String, Pattern> entry : entries)
        {
            String analyzedData = null;
            if (entry.getKey().equals(URL_PARAM_NAME))
            {
                analyzedData = request.getRequestURL() != null ? request.getRequestURL().toString() : null;
            }
            else
            {
                analyzedData = request.getHeader(entry.getKey());
            }
            if (analyzedData == null)
                analyzedData = "";
            Matcher matcher = entry.getValue().matcher(analyzedData);
            if (matcher.find())
            {
                result = true;
                break;
            }
        }
        return result;
    }
    
    public boolean validSiteUri(HttpServletRequest request)
    {
        if (!request.getMethod().equals("GET"))
            return false;
        
        String[] result;
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        
        String[] parts = VtiPathHelper.removeSlashes(uri).split("/");
        
        if (parts[parts.length - 1].indexOf('.') != -1)
            return false;
        
        try
        {
            result = vtiHandler.decomposeURL(uri, context);
            if (result[0].length() > context.length())
            {
                request.setAttribute("VALID_SITE_URL", "true");
                return true;                
            }
            else
            {
                return false;
            }
        }
        catch(Throwable e)
        {
            return false;
        }
    }
    
}
