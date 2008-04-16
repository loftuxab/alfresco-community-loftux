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
package org.alfresco.web.site.renderer;

import java.util.HashMap;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.exception.RendererNotFoundException;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ComponentType;
import org.alfresco.web.site.model.TemplateType;

/**
 * @author muzquiano
 */
public class RendererFactory
{
    public static AbstractRenderer newRenderer(RequestContext context,
            Component component) throws RendererNotFoundException
    {
        // Special case for URIs
        // If the component has a URI property, then lets assume it is a local
        // web script
        if (component.getSetting("uri") != null && component.getSetting("uri").length() > 0)
        {
            ComponentType componentType = context.getModel().loadComponentType(
                    context, "ct-webscriptComponent");
            return _newRenderer(context, componentType.getRendererType(),
                    componentType.getRenderer());
        }
        
        // Another special case - if the component type id starts
        // with / then it is a URI - a web script
        String componentTypeId = component.getComponentTypeId();
        if(componentTypeId != null && componentTypeId.startsWith("/"))
        {
            // this is either a web script or a freemarker template
            // lets assume web script...
            ComponentType componentType = context.getModel().loadComponentType(
                    context, "ct-webscriptComponent");
            return _newRenderer(context, componentType.getRendererType(),
                    componentType.getRenderer());            
        }

        // Otherwise, proceed as before
        ComponentType componentType = component.getComponentType(context);
        return newRenderer(context, componentType);
    }

    public static AbstractRenderer newRenderer(RequestContext context,
            ComponentType componentType) throws RendererNotFoundException
    {
        return _newRenderer(context, componentType.getRendererType(), componentType.getRenderer());
    }

    public static AbstractRenderer newRenderer(RequestContext context,
            TemplateType templateType) throws RendererNotFoundException
    {
        return _newRenderer(context, templateType.getRendererType(), templateType.getRenderer());
    }
    
    public static AbstractRenderer newRenderer(RequestContext context, String rendererType, String renderer)
        throws RendererNotFoundException
    {
        return _newRenderer(context, rendererType, renderer);
    }

    protected static AbstractRenderer _newRenderer(RequestContext context,
            String rendererType, String renderer)
            throws RendererNotFoundException
    {
        // JSP is the default case
        if (rendererType == null || "".equals(rendererType))
        {
            rendererType = "jsp";
        }

        // look up the class implementation
        String className = context.getConfig().getRendererClass(rendererType);
        if (className == null || "".equals(className))
        {
            // JSP is the default case
            className = "org.alfresco.web.site.renderer.JSPRenderer";
        }

        // cache the renderers for performance
        if (renderers == null)
        {
            renderers = new HashMap<String, AbstractRenderer>();
        }

        // look up
        String cacheKey = className + "_" + renderer;
        AbstractRenderer r = (AbstractRenderer) renderers.get(cacheKey);
        if (r == null)
        {
            try
            {
                System.out.println("Instantiating renderer: " + className);
                if(renderer != null)
                    System.out.println(" -> renderer: " + renderer);

                r = (AbstractRenderer) Class.forName(className).newInstance();
                r.setRenderer(renderer);
                r.setRendererType(rendererType);
                renderers.put(cacheKey, r);
            }
            catch (Exception ex)
            {
                // unable to find the renderer implementation class
                ex.printStackTrace();
                throw new RendererNotFoundException(ex);
            }
        }
        return r;
    }

    protected static HashMap<String, AbstractRenderer> renderers = null;

}
