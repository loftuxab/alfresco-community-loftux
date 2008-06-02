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
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.TemplateInstance;

/**
 * Provides stack management for renderer context objects.
 * 
 * A rendering context is manufactured for each execution of a rendering
 * engine.  These rendering contexts are stored in a stack that is bound
 * to the current request context.  Thus, executing thread will have its
 * own private rendering stack.
 * 
 * Each stack may contain one or more layered rendering contexts.
 * 
 * When a template begins to execute, a rendering context is pushed onto the
 * stack for the given request context.  This rendering context is used
 * while working the present template.  The context is consulted to retrieve
 * execution context which includes the current object, custom properties,
 * access to request information and more.
 * 
 * When the template then discovers a component to execute, a new renderer
 * context is manufactured and pushed onto the stack.  This then becomes the
 * current renderer context.
 * 
 * The context is available for the full execution of the component.  When
 * the component finishes running, the context is popped off the stack and
 * the template context is restored.
 * 
 * @author muzquiano
 */
public class RendererContextHelper
{
    /**
     * Push.
     * 
     * @param requestContext the request context
     * @param rendererContext the renderer context
     */
    public static void push(RequestContext requestContext, RendererContext rendererContext)
    {
        Stack<RendererContext> stack = getStack(requestContext);
        stack.push(rendererContext);
    }
    
    /**
     * Pop.
     * 
     * @param requestContext the request context
     * 
     * @return the renderer context
     */
    public static RendererContext pop(RequestContext requestContext)
    {
        Stack<RendererContext> stack = getStack(requestContext);
        return stack.pop();
    }
    
    /**
     * Peek.
     * 
     * @param requestContext the request context
     * 
     * @return the renderer context
     */
    public static RendererContext peek(RequestContext requestContext)
    {
        Stack stack = getStack(requestContext);
        RendererContext rendererContext = (RendererContext) stack.peek();
        
        return rendererContext;
    }
    
    /**
     * Gets the stack.
     * 
     * @param requestContext the request context
     * 
     * @return the stack
     */
    protected static Stack<RendererContext> getStack(RequestContext requestContext)
    {
        Stack<RendererContext> stack = (Stack)requestContext.getValue(WebFrameworkConstants.RENDER_DATA_REQUEST_CONTEXT_STACK_KEY);
        if (stack == null)
        {
            stack = new Stack<RendererContext>();
            
            // push a "root" rendererContext into the stack
            RendererContext rootRendererContext = new RendererContext(requestContext);
            stack.push(rootRendererContext);
            
            requestContext.setValue(WebFrameworkConstants.RENDER_DATA_REQUEST_CONTEXT_STACK_KEY, stack); 
        }
        return stack;        
    }

    public static RendererContext bind(RequestContext context, HttpServletRequest request, HttpServletResponse response)
    {
        return bind(context, null, request, response);
    }
    
    /**
     * Bind.
     * 
     * @param context the context
     * @param object the object
     * @param request the request
     * @param response the response
     * 
     * @return the renderer context
     */
    public static RendererContext bind(RequestContext context, ModelObject object, HttpServletRequest request, HttpServletResponse response)
    {
        // the current context
        RendererContext currentRendererContext = peek(context);
        
        // create a new context
        RendererContext newRendererContext = null;
        if (currentRendererContext != null)
        {
            // clone the current render data
            newRendererContext = currentRendererContext.clone();
            newRendererContext.setRequestContext(context);
        }
        else
        {
            // create a new render data
            newRendererContext = new RendererContext(context);
        }
        newRendererContext.setRequest(request);
        newRendererContext.setResponse(response);
        
        // call generate function to populate from object, if available
        if (object != null)
        {
            newRendererContext.setObject(object);
            RendererContext generatedRenderData = generate(context, object);

            // populate into newRenderData
            newRendererContext.putAll(generatedRenderData);            
        }
        
        // push onto the stack
        push(context, newRendererContext);

        return newRendererContext;                
    }
    
    /**
     * Unbind.
     * 
     * @param requestContext the request context
     * 
     * @return the renderer context
     */
    public static RendererContext unbind(RequestContext requestContext)
    {
        // pop the current context
        return pop(requestContext);
    }
    
    /**
     * Current.
     * 
     * @param context the context
     * 
     * @return the renderer context
     */
    public static RendererContext current(RequestContext context)
    {
        return peek(context);
    }
    
    /**
     * Generate.
     * 
     * @param context the context
     * @param object the object
     * 
     * @return the renderer context
     */
    public static RendererContext generate(RequestContext context, ModelObject object)
    {
        RendererContext newData = null;

        // switch on object type
        if (object instanceof Component)
        {
            newData = generate(context, (Component) object);
        }
        else if (object instanceof Page)
        {
            newData = generate(context, (Page) object);
        }
        else if (object instanceof TemplateInstance)
        {
            newData = generate(context, (TemplateInstance) object);
        }
        
        if (newData != null)
        {
            // populate with custom properties settings
            Map<String, Serializable> properties = object.getCustomProperties();
            newData.putAll(properties);
        }
        
        return newData;
    }

    /**
     * Populates the configuration for a page.
     * 
     * @param context the context
     * @param page the page
     * 
     * @return the renderer context
     */
    protected static RendererContext generate(RequestContext context, Page page)
    {
        RendererContext rendererContext = new RendererContext(context, page);
                
        // properties about the page
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_PAGE_ID, page.getId());
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_PAGE_TYPE_ID, page.getPageTypeId());

        // properties about the html binding id
        String htmlBindingId = page.getId();
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID, htmlBindingId);
        
        return rendererContext;
    }
    
    
    /**
     * Populates the configuration for a template instance.
     * 
     * @param context the context
     * @param template the template
     * 
     * @return the renderer context
     */
    protected static RendererContext generate(RequestContext context, TemplateInstance template)
    {
        RendererContext rendererContext = new RendererContext(context, template);
        
        // properties about the template
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_TEMPLATE_ID, template.getId());
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_TEMPLATE_TYPE_ID, template.getTemplateType());
        
        // properties about the html binding id
        String htmlBindingId = template.getId();
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID, htmlBindingId);
        
        return rendererContext;
    }

    /**
     * Populates the configuration for a component.
     * 
     * @param context the context
     * @param component the component
     * 
     * @return the renderer context
     */
    protected static RendererContext generate(RequestContext context, Component component)
    {
        RendererContext rendererContext = new RendererContext(context, component);
        
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID, component.getId());
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_TYPE_ID, component.getComponentTypeId());
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_REGION_ID, component.getRegionId());
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_SOURCE_ID, component.getSourceId());
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_COMPONENT_SCOPE_ID, component.getScope());

        // properties about the html binding id
        String htmlBindingId = component.getScope() + "." + component.getRegionId();
        if (!component.getScope().equalsIgnoreCase(WebFrameworkConstants.REGION_SCOPE_GLOBAL))
        {
            htmlBindingId += "." + component.getSourceId();
        }
        rendererContext.put(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID, htmlBindingId);
        
        return rendererContext;
    }    
}
