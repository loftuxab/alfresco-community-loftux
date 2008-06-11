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
package org.alfresco.web.site.renderer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.connector.User;
import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.RequestContext;

/**
 * A render context instance is available to all rendering engines
 * and provides a convenient grab bag of things that are useful to
 * component or template developer.
 * 
 * @author muzquiano
 */
public final class RendererContext implements Serializable
{
    private Map<String, Serializable> map;
    private ModelObject object;
    private RequestContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public RendererContext(RequestContext context)
    {
        this.map = new HashMap<String, Serializable>(8, 1.0f);
        if (context == null)
        {
            throw new IllegalArgumentException("RequestContext is mandatory.");
        }
        this.context = context;
    }
    
    public RendererContext(RequestContext context, ModelObject object)
    {
        this(context);
        this.object = object;
    }
    
    public RendererContext(RequestContext context, ModelObject object, HttpServletRequest request, HttpServletResponse response)
    {
        this(context, object);
        this.request = request;
        this.response = response;
    }
    
    public RequestContext getRequestContext()
    {
        return this.context;
    }
    
    public void setRequestContext(RequestContext context)
    {
        this.context = context;        
    }
    
    public HttpServletRequest getRequest()
    {
        return this.request;
    }
    
    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }
    
    public HttpServletResponse getResponse()
    {
        return this.response;
    }
    
    public void setResponse(HttpServletResponse response)
    {
        this.response = response;
    }
    
    public ModelObject getObject()
    {
        return this.object;
    }
    
    public void setObject(ModelObject object)
    {
        this.object = object;
    }
    
    public User getUser()
    {
        return this.context.getUser();
    }
    
    public void put(String key, Serializable value)
    {
        this.map.put(key, value);
    }
    
    public Object get(String key)
    {
        return this.map.get(key);
    }
    
    public void remove(String key)
    {
        this.map.remove(key);
    }
    
    public String getId()
    {
        if(this.object != null)
        {
            return this.object.getTypeId() + "___" + this.object.getId();
        }
        return "unknown";        
    }
    
    // Helper Methods
    
    public RendererContext clone()
    {
        RendererContext c = new RendererContext(this.getRequestContext());
        c.setObject(this.getObject());
        c.putAll(this);
        
        return c;
    }

    public void putAll(Map<String, Serializable> map)
    {
        if(map != null)
        {
            this.map.putAll(map);
        }
    }

    public void putAll(RendererContext rendererContext)
    {
        if(rendererContext != null)
        {
            putAll(rendererContext.map);
        }
    }
    
    public Iterator iter()
    {
        return map.keySet().iterator();
    }
    
    public Map<String, Serializable> map()
    {
        return this.map;        
    }

    @Override
    public String toString()
    {
        return this.map.toString();
    }
}
