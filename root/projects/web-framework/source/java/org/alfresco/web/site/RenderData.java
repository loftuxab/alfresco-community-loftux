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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.web.site.model.ModelObject;

/**
 * A render context instance is available to all rendering engines
 * and provides a convenient grab bag of things that are useful to
 * component or template developer.
 * 
 * @author muzquiano
 */
public class RenderData
{
    protected Map<String, Object> map;
    protected ModelObject object;
    protected RequestContext context;

    protected RenderData()
    {
        this.map = new HashMap<String, Object>();
    }

    protected RenderData(RequestContext context, ModelObject object)
    {
        this();
        this.context = context;
        this.object = object;
    }
    
    public RequestContext getRequestContext()
    {
        return this.context;        
    }
    
    protected void setRequestContext(RequestContext context)
    {
        this.context = context;        
    }
    
    public ModelObject getObject()
    {
        return this.object;
    }
    
    protected void setObject(ModelObject object)
    {
        this.object = object;
    }
    
    public User getUser()
    {
        return this.context.getUser();
    }
    
    public void put(String key, Object value)
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
            return this.object.getTypeName() + "___" + this.object.getId();
        }
        return "unknown";        
    }
    
    // Helper Methods
    
    public RenderData clone()
    {
        RenderData c = new RenderData();
        c.setObject(this.getObject());
        c.setRequestContext(this.getRequestContext());
        c.putAll(this);
        
        return c;
    }

    public void putAll(Map<String, Object> map)
    {
        if(map != null)
        {
            this.map.putAll(map);
        }
    }

    public void putAll(RenderData renderData)
    {
        if(renderData != null)
        {
            putAll(renderData.map);
        }
    }
    
    public Iterator iter()
    {
        return map.keySet().iterator();
    }
    
    public Map<String, Object> map()
    {
        return this.map;        
    }
}
