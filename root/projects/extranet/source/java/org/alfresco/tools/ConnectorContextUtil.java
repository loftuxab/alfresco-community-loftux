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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The Class ConnectorContextUtil.
 * 
 * @author muzquiano
 */
public class ConnectorContextUtil 
{
    
    /**
     * Builds the request post data.
     * 
     * @param keyValuePairs the key value pairs
     * 
     * @return the string
     */
    public static String buildRequestPostData(Map keyValuePairs)
    {
        // Create x-www-url-encoded parameter string
        StringBuffer result = new StringBuffer(20*keyValuePairs.size());

        Set entrySet = keyValuePairs.entrySet();
        Iterator iter = entrySet.iterator();

        while (iter.hasNext()) 
        {
            // Iterate over all key-value pairs
            Map.Entry keyValuePair = (Map.Entry)iter.next();

            String key   = (String)keyValuePair.getKey();
            String value = (String)keyValuePair.getValue();

            // Escape both key and value and combine them with an '='
            _formUrlEncode(key, result);
            result.append('=');
            _formUrlEncode(value, result);

            // If there are more key-value pairs, append an '&'
            if (iter.hasNext()) 
            {
                result.append('&');
            }
        }

        return result.toString();
    }

    /**
     * Form url encode.
     * 
     * @param s the s
     * 
     * @return the string
     */
    public static String formUrlEncode(String s)
    {
        StringBuffer buffer = new StringBuffer(256);
        _formUrlEncode(s, buffer);
        
        return buffer.toString();
    }
    
    /**
     * _form url encode.
     * 
     * @param s the s
     * @param buf the buf
     */
    protected static void _formUrlEncode(String s, StringBuffer buf) 
    {
        for (int i = 0; i < s.length(); i++) 
        {
            char c = s.charAt(i);
            int cInt = (int)c;

            // Only characters in the range 48 - 57 (numbers), 65 - 90 (upper
            // case letters), 97 - 122 (lower case letters) can be left
            // unencoded. The rest needs to be escaped.

            if (cInt >= 48 && cInt <= 57 ||
                cInt >= 65 && cInt <= 90 ||
                cInt >= 97 && cInt <= 122)
            {
                // alphanumeric character
                buf.append(c);
            }
            else 
            {
                // Escape all non-alphanumerics
                buf.append('%');
                String hexVal = Integer.toHexString(cInt);

                // Ensure use of two characters
                if (hexVal.length() == 1) 
                {
                    buf.append('0');
                }

                buf.append(hexVal);
            }
        }
    }
}
