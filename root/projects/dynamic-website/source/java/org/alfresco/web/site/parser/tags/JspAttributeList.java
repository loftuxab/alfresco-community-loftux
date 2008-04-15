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
package org.alfresco.web.site.parser.tags;

import java.util.Hashtable;

import org.alfresco.tools.Array;
import org.xml.sax.AttributeList;

/**
 * @author muzquiano
 */
public class JspAttributeList implements AttributeList
{
    public JspAttributeList()
    {
        names = new Array();
        values = new Hashtable();
    }

    public void putAttribute(String name, String value)
    {
        name = name.toLowerCase();
        names.add(name);
        values.put(name, value);
    }

    public int getLength()
    {
        return names.size();
    }

    public String getName(int i)
    {
        return (String) names.get(i);
    }

    public String getType(int i)
    {
        return null;
    }

    public String getType(java.lang.String name)
    {
        return null;
    }

    public String getValue(int i)
    {
        return getValue(getName(i));
    }

    public String getValue(java.lang.String name)
    {
        return (String) values.get(name);
    }

    Array names;
    Hashtable values;
}
