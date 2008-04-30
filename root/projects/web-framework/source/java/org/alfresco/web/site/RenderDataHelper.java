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

import java.util.Map;
import java.util.Stack;

import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.TemplateInstance;

/**
 * @author muzquiano
 */
public class RenderDataHelper
{
    public static void push(RequestContext requestContext, RenderData renderData)
    {
        Stack<RenderData> stack = getStack(requestContext);
        stack.push(renderData);
    }
    
    public static RenderData pop(RequestContext requestContext)
    {
        Stack stack = getStack(requestContext);
        RenderData renderData = (RenderData) stack.pop();

        return renderData;
    }
    
    public static RenderData peek(RequestContext requestContext)
    {
        Stack stack = getStack(requestContext);
        RenderData renderData = (RenderData) stack.peek();
        
        return renderData;
    }
    
    protected static Stack<RenderData> getStack(RequestContext requestContext)
    {
        Stack stack = (Stack) requestContext.getValue(WebFrameworkConstants.RENDER_DATA_REQUEST_CONTEXT_STACK_KEY);
        if(stack == null)
        {
            stack = new Stack<RenderData>();
            
            // push a "root" renderData into the stack
            RenderData rootRenderData = new RenderData();
            stack.push(rootRenderData);
            
            requestContext.setValue(WebFrameworkConstants.RENDER_DATA_REQUEST_CONTEXT_STACK_KEY, stack); 
        }
        return stack;        
    }
    
    public static RenderData bind(RequestContext context, ModelObject object)
    {
        // the current context
        RenderData currentRenderData = peek(context);
        
        // create a new context
        RenderData newRenderData = null;
        if(currentRenderData != null)
        {
            // clone the current render data
            newRenderData = currentRenderData.clone();
        }
        else
        {
            // create a new render data
            newRenderData = new RenderData();
        }
        newRenderData.setRequestContext(context);
        newRenderData.setObject(object);
        
        // call generate
        RenderData generatedRenderData = generate(context, object);

        // populate into newRenderData
        newRenderData.putAll(generatedRenderData);
        
        // push onto the stack
        push(context, newRenderData);

        return newRenderData;                
    }
    
    public static RenderData unbind(RequestContext requestContext)
    {
        // pop the current context
        return pop(requestContext);
    }
    
    public static RenderData current(RequestContext context)
    {
        return peek(context);
    }
    
    public static RenderData generate(RequestContext context, ModelObject object)
    {
        RenderData newData = null;

        // switch on object type
        if (object instanceof Component)
        {
            newData = generate(context, (Component) object);
        }
        if (object instanceof Page)
        {
            newData = generate(context, (Page) object);
        }
        if (object instanceof TemplateInstance)
        {
            newData = generate(context, (TemplateInstance) object);
        }
        
        if(newData != null)
        {
            // populate with custom properties settings
            Map<String, Object> properties = object.getCustomProperties();
            newData.putAll(properties);
        }
        
        return newData;
    }

    /**
     * Populates the configuration for a page
     * 
     * @param context
     * @param template
     * @param config
     */
    protected static RenderData generate(RequestContext context, Page page)
    {
        RenderData data = new RenderData(context, page);
                
        // properties about the page
        data.put(WebFrameworkConstants.RENDER_DATA_PAGE_ID, page.getId());
        data.put(WebFrameworkConstants.RENDER_DATA_PAGE_TYPE_ID, page.getPageTypeId());

        // properties about the html binding id
        String htmlBindingId = page.getId();
        data.put(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID, htmlBindingId);
        
        return data;
    }
    
    
    /**
     * Populates the configuration for a template instance.
     * 
     * @param context
     * @param template
     * @param config
     */
    protected static RenderData generate(RequestContext context, TemplateInstance template)
    {
        RenderData data = new RenderData(context, template);
        
        // properties about the template
        data.put(WebFrameworkConstants.RENDER_DATA_TEMPLATE_ID, template.getId());
        data.put(WebFrameworkConstants.RENDER_DATA_TEMPLATE_TYPE_ID, template.getTemplateType());
        
        // properties about the html binding id
        String htmlBindingId = template.getId();
        data.put(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID, htmlBindingId);
        
        return data;
    }

    /**
     * Populates the configuration for a component.
     * 
     * @param context
     * @param component
     * @param config
     */
    protected static RenderData generate(RequestContext context, Component component)
    {
        RenderData data = new RenderData(context, component);
        
        data.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID, component.getId());
        data.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_TYPE_ID, component.getComponentTypeId());
        data.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_REGION_ID, component.getRegionId());
        data.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_SOURCE_ID, component.getSourceId());
        data.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_SCOPE_ID, component.getScope());

        // properties about the html binding id
        String htmlBindingId = component.getScope() + "." + component.getRegionId();
        htmlBindingId += (component.getScope().equalsIgnoreCase(WebFrameworkConstants.REGION_SCOPE_GLOBAL) ? "" : ("." + component.getSourceId()));
        data.put(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID, htmlBindingId);
        
        return data;
    }    
}
