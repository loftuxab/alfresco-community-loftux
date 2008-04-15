/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.tools;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class FakeHttpSession implements HttpSession
{
    public FakeHttpSession()
    {
        super();
        creationTime = System.currentTimeMillis();
    }

    public long getCreationTime()
    {
        return creationTime;
    }

    public String getId()
    {
        return "alfresco";
    }

    public long getLastAccessedTime()
    {
        return creationTime;
    }

    public ServletContext getServletContext()
    {
        return null;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public int getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }

    public javax.servlet.http.HttpSessionContext getSessionContext()
    {
        return null;
    }

    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    public Object getValue(String name)
    {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames()
    {
        return Collections.enumeration(attributes.keySet());
    }

    public String[] getValueNames()
    {
        return (String[]) attributes.keySet().toArray(
                new String[attributes.keySet().size()]);
    }

    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    public void putValue(String name, Object value)
    {
        attributes.put(name, value);
    }

    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }

    public void removeValue(String name)
    {
        attributes.remove(name);
    }

    public void invalidate()
    {
    }

    public boolean isNew()
    {
        return true;
    }

    private Map attributes = new HashMap();

    private long creationTime;

    private int maxInactiveInterval = 30 * 60 * 1000;
}
