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

import java.util.HashMap;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.exception.RendererNotFoundException;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ComponentType;
import org.alfresco.web.site.model.TemplateInstance;
import org.alfresco.web.site.model.TemplateType;

/**
 * Produces the appropriate renderer implementations for a given
 * model object.  The methods provided here look at the model and walk the
 * object tree to determine its type and type of renderer to use.
 * 
 * In general, renderers are instantiated for Components and Templates.
 * 
 * @author muzquiano
 */
public class RendererFactory
{
    /**
     * Constructs a renderer for a given component instance.
     * 
     * @param context the context
     * @param component the components
     * 
     * @return the render instance
     * 
     * @throws RendererNotFoundException 
     */
    public static Renderable newRenderer(RequestContext context,
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
        if(uri != null && uri.length() != 0)
        {
            /**
             * If it had a URI property, then we're assuming it is a
             * webscript component type.
             */
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

        /**
         * Otherwise, just look at the component type and instantiate
         * a renderer for that component type
         */
        ComponentType componentType = component.getComponentType(context);
        return newRenderer(context, componentType);
    }

    /**
     * Constructs a renderer for a given component type
     * 
     * @param context the context
     * @param componentType the component type
     * 
     * @return the renderer instance
     * 
     * @throws RendererNotFoundException
     */
    public static Renderable newRenderer(RequestContext context,
            ComponentType componentType) throws RendererNotFoundException
    {
        return _newRenderer(context, componentType.getRendererType(), componentType.getRenderer());
    }

    /**
     * Constructs a renderer for a given template instance
     * 
     * @param context the context
     * @param template the template
     * 
     * @return the renderer instance
     * 
     * @throws RendererNotFoundException
     */
    public static Renderable newRenderer(RequestContext context,
            TemplateInstance template) throws RendererNotFoundException
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
    
    /**
     * Constructs a renderer for a given template type.
     * 
     * @param context the context
     * @param templateType the template type
     * 
     * @return the renderer isntance
     * 
     * @throws RendererNotFoundException
     */
    public static Renderable newRenderer(RequestContext context,
            TemplateType templateType) throws RendererNotFoundException
    {        
        return _newRenderer(context, templateType.getRendererType(), templateType.getRenderer());
    }
    
    /**
     * Constructs a renderer for a given renderer path and type
     * 
     * @param context the context
     * @param rendererType the renderer type
     * @param renderer the renderer
     * 
     * @return the renderer instance
     * 
     * @throws RendererNotFoundException
     */
    public static Renderable newRenderer(RequestContext context, String rendererType, String renderer)
        throws RendererNotFoundException
    {
        return _newRenderer(context, rendererType, renderer);
    }

    /**
     * Internal workhorse method for building renderers
     * 
     * @param context the context
     * @param rendererType the renderer type
     * @param renderer the renderer
     * 
     * @return the renderer instance
     * 
     * @throws RendererNotFoundException
     */
    protected static Renderable _newRenderer(RequestContext context,
            String rendererType, String renderer)
            throws RendererNotFoundException
    {
        /**
         * If a renderer type is not specified, assume JSP.
         */
    	String className = null;
        if (rendererType == null || rendererType.length() == 0)
        {
            rendererType = WebFrameworkConstants.RENDERER_TYPE_JSP;
            className = "org.alfresco.web.site.renderer.JSPRenderer";
        }
        else
        {
	        /**
	         * Look up the implementation class name for this renderer type
	         */
        	className = context.getConfig().getRendererDescriptor(rendererType).getImplementationClass();
        }

        /**
         * We only want to instantiate renderers once, so make sure
         * that renderer cache is instantiated
         */
        if (renderers == null)
        {
            renderers = new HashMap<String, Renderable>();
        }

        /**
         * Check the cache to see if this renderer has already been
         * instantiated.  If not, then create a new one
         */
        String cacheKey = className + "_" + renderer;
        Renderable r = (Renderable) renderers.get(cacheKey);
        if (r == null)
        {
            try
            {
                r = (Renderable)Class.forName(className).newInstance();
                
                // setup and single time initialisation
                r.setRenderer(renderer);
                r.setRendererType(rendererType);
                r.init(context.getRenderContext());
                
                renderers.put(cacheKey, r);
            }
            catch (Exception ex)
            {
                /**
                 * If for whatever reason we could not instantiate, throw
                 * back with the RendererNotFoundException
                 */
                throw new RendererNotFoundException(ex);
            }
        }
        return r;
    }

    /** The renderers */
    protected static HashMap<String, Renderable> renderers = null;
}
