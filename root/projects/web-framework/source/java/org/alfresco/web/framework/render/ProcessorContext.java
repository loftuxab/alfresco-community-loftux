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
package org.alfresco.web.framework.render;

import java.util.HashMap;
import java.util.Map;

/**
 * @author muzquiano
 */
final public class ProcessorContext 
{
    final private RenderContext context;
    final private Map<RenderMode, ProcessorDescriptor> descriptors = new HashMap<RenderMode, ProcessorDescriptor>(4, 1.0f);
    
    public ProcessorContext(RenderContext context)
    {
        this.context = context;
    }
    
    public RenderContext getRenderContext()
    {
        return this.context;
    }

    public ProcessorDescriptor getDescriptor(RenderMode renderMode)
    {
        return this.descriptors.get(renderMode);
    }
    
    public void putDescriptor(RenderMode renderMode, ProcessorDescriptor descriptor)
    {
        this.descriptors.put(renderMode, descriptor);
    }
        
    public void removeDescriptor(RenderMode renderMode)
    {
        this.descriptors.remove(renderMode);
    }
    
    public void addDescriptor(RenderMode renderMode, Map<String, String> properties)
    {
        ProcessorDescriptor descriptor = new ProcessorDescriptor(properties);
        putDescriptor(renderMode, descriptor);        
    }
    
    public void load(Renderable renderable)
    {
        RenderMode[] renderModes = renderable.getRenderModes();
        
        for (int i = 0; i < renderModes.length; i++)
        {
            Map<String, String> properties = renderable.getProcessorProperties(renderModes[i]);
            ProcessorDescriptor descriptor = new ProcessorDescriptor(properties);
            
            putDescriptor(renderModes[i], descriptor);
        }
    }

    public final static class ProcessorDescriptor
    {
        public final Map<String, String> properties;
        
        public ProcessorDescriptor()
        {
            this.properties = new HashMap<String, String>(4, 1.0f);
        }
        
        public ProcessorDescriptor(Map<String, String> properties)
        {
            this.properties = properties;
        }
        
        public void put(String key, String value)
        {
            this.properties.put(key, value);
        }
        
        public String get(String key)
        {
            return (String) this.properties.get(key);
        }
        
        public void remove(String key)
        {
            this.properties.remove(key);
        }
        
        public Map<String, String> map()
        {
            return this.properties;
        }
    }
}
