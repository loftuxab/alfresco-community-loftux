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
package org.alfresco.web.framework.render.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.framework.render.AbstractRenderContext;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderContextProvider;
import org.alfresco.web.framework.render.RenderMode;
import org.alfresco.web.site.RequestContext;

/**
 * A render context instance is available to all rendering engines
 * and provides a convenient grab bag of things that are useful to
 * component or template developer.
 * 
 * @author muzquiano
 */
public final class DefaultRenderContext extends AbstractRenderContext
{
    private final Map<String, Serializable> ourValuesMap;
    
    /**
     * Constructor
     * 
     * @param provider      RenderContextProvider
     * @param context       RequestContext to wrap
     */
    public DefaultRenderContext(RenderContextProvider provider, RequestContext context)
    {
        super(provider, context);
        
        if (context instanceof RenderContext)
        {
            RenderContext renderContext = (RenderContext) context;
            
            this.setRenderMode(renderContext.getRenderMode());
            this.setObject(renderContext.getObject());
            this.setPassiveMode(renderContext.isPassiveMode());            
        }
        else
        {
            this.setRenderMode(RenderMode.VIEW);            
        }
        
        this.ourValuesMap = new HashMap<String, Serializable>(4, 1.0f);
    }
    
    @Override
    public void setValue(String key, Serializable value)
    {
        this.ourValuesMap.put(key, value);   
    }

    @Override
    public Serializable getValue(String key)
    {
        Serializable value = (Serializable)this.ourValuesMap.get(key);
        if (value == null)
        {
            // check if a wrapped context has the value
            value = this.getOriginalContext().getValue(key);
        }
        return value;
    }

    @Override
    public void removeValue(String key)
    {
        this.ourValuesMap.remove(key);
    }
    
    @Override
    public boolean hasValue(String key)
    {
        return this.ourValuesMap.containsKey(key);
    }
    
    @Override
    public synchronized Map<String, Serializable> getValuesMap()
    {
        Map<String, Serializable> normalizedValuesMap = new HashMap<String, Serializable>(this.ourValuesMap);
        normalizedValuesMap.putAll(this.getOriginalContext().getValuesMap());
        return normalizedValuesMap;
    }
}
    