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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * An implementation of HttpSession which provides a local notion of a session.
 * All session data is stored locally within the class instance.
 * 
 * @author muzquiano
 */
public class FakeHttpSession implements HttpSession
{
    
    /**
     * Instantiates a new fake http session.
     */
    public FakeHttpSession()
    {
        super();
        creationTime = System.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getCreationTime()
     */
    public long getCreationTime()
    {
        return creationTime;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getId()
     */
    public String getId()
    {
        return "alfresco";
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getLastAccessedTime()
     */
    public long getLastAccessedTime()
    {
        return creationTime;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getServletContext()
     */
    public ServletContext getServletContext()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
     */
    public void setMaxInactiveInterval(int maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     */
    public int getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getSessionContext()
     */
    public javax.servlet.http.HttpSessionContext getSessionContext()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    public Object getValue(String name)
    {
        return attributes.get(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
        return Collections.enumeration(attributes.keySet());
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    public String[] getValueNames()
    {
        return (String[]) attributes.keySet().toArray(
                new String[attributes.keySet().size()]);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
     */
    public void putValue(String name, Object value)
    {
        attributes.put(name, value);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    public void removeValue(String name)
    {
        attributes.remove(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#invalidate()
     */
    public void invalidate()
    {
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#isNew()
     */
    public boolean isNew()
    {
        return true;
    }

    /** The attributes. */
    private Map attributes = new HashMap();

    /** The creation time. */
    private long creationTime;

    /** The max inactive interval. */
    private int maxInactiveInterval = 30 * 60 * 1000;
}
