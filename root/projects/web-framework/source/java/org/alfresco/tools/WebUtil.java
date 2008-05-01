/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * Static helper methods for working with query strings and maps.
 * 
 * @author muzquiano
 */
public class WebUtil
{
    /**
     * Creates a Map of query string key and value parameters from the
     * given request
     * 
     * @param request the request
     * 
     * @return the query string map
     */
    public static Map getQueryStringMap(HttpServletRequest request)
    {
        HashMap map = new HashMap();
        String queryString = request.getQueryString();
        if (queryString != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(queryString, "&");
            while (tokenizer.hasMoreTokens())
            {
                String combo = (String) tokenizer.nextToken();
                int c = combo.indexOf("=");
                if (c > -1)
                {
                    String key = combo.substring(0, c);
                    String value = combo.substring(c + 1, combo.length());
                    map.put(key, value);
                }
            }
        }
        return map;

    }

    /**
     * Returns the query string for a given map of key and value pairs
     * 
     * @param map the map
     * 
     * @return the query string for map
     */
    public static String getQueryStringForMap(Map map)
    {
        if (map == null)
        {
            return "";
        }

        boolean first = true;
        String result = "";

        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = (String) map.get(key);

            if (!first)
                result = result + "&";

            result = result + key + "=" + value;
            first = false;
        }
        return result;
    }
}
