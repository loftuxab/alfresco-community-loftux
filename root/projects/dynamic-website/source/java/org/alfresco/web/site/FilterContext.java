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
package org.alfresco.web.site;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FilterContext contains the context needed by content filters (which implement IFilter) to
 * work.  It contains the request, response, servlet context, and server instance that pertain
 * to filtering the content.  It also has a mechanism to set and get arbitrary values, much 
 * as the JSP PageContext object does.

 * @author muzquiano
 */
public class FilterContext
{
    /**
     * Create the FilterContext.  These parameters are just stored for later retrieval
     * by filters.
     */
    public FilterContext(HttpServletRequest request,
            HttpServletResponse response, ServletContext cxt)
    {
        values = new Hashtable();
        this.request = request;
        this.response = response;
        this.servletContext = cxt;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Set an arbitrary value on this FilterContext object to be retrieved later by
     * getValue()
     */
    public void setValue(Object key, Object value)
    {
        values.put(key, value);
    }

    /**
     * Get a value previously set by setValue()
     */
    public Object getValue(Object key)
    {
        return values.get(key);
    }

    /**
     * Remove a value previously set by setValue()
     */
    public void removeValue(Object key)
    {
        values.remove(key);
    }

    /**
     * Same as getValue(), but returns a String instead of an Object.
     * 
     * @throws ClassCastException if the value is not a String
     */
    public String getStringValue(Object key)
    {
        return (String) values.get(key);
    }

    /**
     * Get the names of all of the values that were set using setValue()
     */
    public Enumeration getNames()
    {
        return values.keys();
    }

    /**
     * Return the stored request object.
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }

    /**
     * Return the stored response object.
     */
    public HttpServletResponse getResponse()
    {
        return response;
    }

    /**
     * Return the stored servlet context.
     */
    public ServletContext getServletContext()
    {
        return servletContext;
    }

    /**
     * Return the timestamp of when this FilterContext was created.
     */
    public long getTimeStamp()
    {
        return timestamp;
    }
        
    public void setRequestContext(RequestContext requestContext)
    {
        this.requestContext = requestContext;
    }
    
    public RequestContext getRequestContext()
    {
        return requestContext;
    }

    private RequestContext requestContext;
    private Hashtable values;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
    private long timestamp;

    public static String CONTENT_ITEM_ID = "content.item.id";
}
