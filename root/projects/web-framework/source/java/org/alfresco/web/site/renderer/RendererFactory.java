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
import org.alfresco.web.site.model.Template;
import org.alfresco.web.site.model.TemplateType;

/**
 * @author muzquiano
 */
public class RendererFactory
{
    public static AbstractRenderer newRenderer(RequestContext context,
            Component component) throws RendererNotFoundException
    {
        /**
         * Special Case for Web Scripts as default
         * 
         * If there is a URI specified on the component, then assume
         * that this URI is the path to a web script.  This is basically
         * a short-hand way to express web script components.
         */
        String uri = component.getURL();
        if(uri == null)
        {
            uri = component.getProperty("uri");
        }
        if(uri == null)
        {
            uri = component.getProperty("url");
        }
        if(uri == null)
        {
            uri = component.getSetting("uri");
        }
        if(uri == null)
        {
            uri = component.getSetting("url");
        }
        if(uri != null && !"".equals(uri))
        {
            // execute as a web script
            // use a web script component
            ComponentType componentType = context.getModel().loadComponentType(
                    context, "webscript");
            return _newRenderer(context, componentType.getRendererType(), uri);
        }
        
        /**
         * Another special case for Web Scripts as default
         * 
         * If there is a component-type specified, check to see if the
         * component-type exists.  If it doesn't, then assume that the
         * value is the name/path to a web script.
         */
        String componentTypeId = component.getComponentTypeId();
        if(componentTypeId != null)
        {
            ComponentType testComponentType = context.getModel().loadComponentType(context, componentTypeId);
            if(testComponentType == null)
            {
                // use a web script component
                ComponentType componentType = context.getModel().loadComponentType(
                        context, "webscript");
                return _newRenderer(context, componentType.getRendererType(),
                        componentTypeId);                
            }
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
            Template template) throws RendererNotFoundException
    {                
        /**
         * Special case for Web Scripts as default
         * 
         * If there is a component-type specified, check to see if the
         * component-type exists.  If it doesn't, then assume that the
         * value is the name/path to a web script.
         */
        String templateTypeId = template.getTemplateType();
        if(templateTypeId != null)
        {
            TemplateType testTemplateType = context.getModel().loadTemplateType(context, templateTypeId);
            if(testTemplateType == null)
            {
                // execute as a freemarker template type
                TemplateType templateType = context.getModel().loadTemplateType(context, "freemarker");
                return _newRenderer(context, templateType.getRendererType(),
                        templateTypeId);                
            }
        }
        
        // Otherwise, proceed as before
        TemplateType templateType = template.getTemplateType(context);
        return newRenderer(context, templateType);
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
