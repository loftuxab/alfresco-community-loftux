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
import org.alfresco.web.framework.model.Component;
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
    private final Map<String, Serializable> map = new HashMap<String, Serializable>(8, 1.0f);
    private RequestContext context;
    private ModelObject object;
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    private Map<String, Component> components = null;
    
    
    /**
     * Private copy constructor - used in clone()
     *
     */
    private RendererContext()
    {
    }
    
    public RendererContext(RequestContext context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("RequestContext is mandatory.");
        }
        this.context = context;
        this.components = new HashMap<String, Component>(16, 1.0f);
    }
    
    public RendererContext(RequestContext context, ModelObject object)
    {
        this(context);
        this.object = object;
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
        RendererContext c = new RendererContext();
        c.context = this.getRequestContext();
        c.object = this.getObject();
        c.map.putAll(this.map);
        
        // NOTE: we want this reference to travel with all child render context objects
        //       it describes all components that are rendering for this and all parent context
        c.components = this.components;
        
        return c;
    }

    public void putAll(Map<String, Serializable> map)
    {
        if (map != null)
        {
            this.map.putAll(map);
        }
    }

    public void putAll(RendererContext rendererContext)
    {
        if (rendererContext != null)
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
    
    /**
     * Returns the components that were bound to this and any of its parent context
     * during the rendering.  This is useful to determine what other components
     * are configured on the current page.
     * 
     * If no rendering components are set, null will be returned
     * 
     * @return  An array of Component objects
     */
    public Component[] getRenderingComponents()
    {
        if (this.components.size() == 0)
        {
            return null;
        }
        else
        {
            return this.components.values().toArray(new Component[this.components.size()]);
        }
    }
    
    /**
     * Indicates that the given component is being rendered as part of
     * the rendering execution for this and any parent rendering context.
     *  
     * @param component The component that is being rendered
     */
    public void setRenderingComponent(Component component)
    {
        this.components.put(component.getId(), component);        
    }

    @Override
    public String toString()
    {
        return this.map.toString();
    }
}
