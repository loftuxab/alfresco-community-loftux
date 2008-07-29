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
package org.alfresco.module.vti.metadata.soap;

import java.util.Map;

/**
 * @author AndreyAk
 *
 */
public class SoapUtils
{
    private static final StringBuilder LBRACKET = new StringBuilder("<");
    private static final StringBuilder RBRACKET = new StringBuilder(">");
    private static final StringBuilder LCLOSEBRACKET = new StringBuilder("</");
    private static final StringBuilder RCLOSEBRACKET = new StringBuilder("/>");
    
    public static StringBuilder startTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LBRACKET).append(tagName).append(RBRACKET);
    }
    
    public static StringBuilder startTag(String tagName, Map<String, Object> attributes)
    {
        StringBuilder result = new StringBuilder("");
        result.append(LBRACKET).append(tagName).append(" ");
        for (String key : attributes.keySet())
        {
            if (attributes.get(key) != null)
            {
                if (!attributes.get(key).equals(""))
                {
                    result.append(key).append("=\"").append(attributes.get(key)).append("\" ");
                }
            }
        }
        result.append(RBRACKET);
        return result;
    }
    
    public static StringBuilder endTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LCLOSEBRACKET).append(tagName).append(RBRACKET);
    }
    
    public static StringBuilder singleTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LBRACKET).append(tagName).append(RCLOSEBRACKET);
    }
    
    public static StringBuilder singleTag(String tagName, Map<String, Object> attributes)
    {
        StringBuilder result = new StringBuilder("");
        result.append(LBRACKET).append(tagName).append(" ");
        for (String key : attributes.keySet())
        {
            if (attributes.get(key) != null)
            {
                if (!attributes.get(key).equals(""))
                {
                    result.append(key).append("=\"").append(attributes.get(key)).append("\" ");
                }
            }
        }
        result.append(RCLOSEBRACKET);
        return  result;
    }
    
    public static StringBuilder proccesTag(String tagName, Object value)
    {
        StringBuilder result = new StringBuilder("");
        
        if (value == null)
        {
            return result;
        } else if (value.toString().equals(""))
        {
            return result.append(singleTag(tagName));
        }
        else
        {
            return result.append(startTag(tagName)).append(value).append(endTag(tagName));
        }
    }
}
