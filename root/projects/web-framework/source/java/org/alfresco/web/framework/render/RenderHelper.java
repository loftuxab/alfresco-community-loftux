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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.ComponentType;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.TemplateType;
import org.alfresco.web.framework.render.ProcessorContext.ProcessorDescriptor;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * @author muzquiano
 * @author kevinr
 * @author mikeh
 */
public class RenderHelper 
{
    private static final String COMPONENTTYPE_WEBSCRIPT = "webscript";
    private static final String WEBFRAMEWORK_RENDERCONTEXT_PROVIDER = "webframework.rendercontext.provider";
    private static final String PREFIX_WEBFRAMEWORK_PROCESSOR = "webframework.processor.";
    private static final String PREFIX_WEBFRAMEWORK_RENDERER = "webframework.renderer.";
    

    /**
     * Gets a renderer for the given renderer type
     * (i.e. page, template, component, etc)
     * 
     * @param rendererType
     * @return renderer
     */
    public static Renderer getRenderer(RendererType rendererType)
    {
        return getRenderer(rendererType.toString());
    }
    
    /**
     * Gets a renderer for the given model object type
     * (i.e. page, template, component, etc)
     * 
     * @param modelObject
     * @return renderer
     */
    public static Renderer getRenderer(ModelObject modelObject)
    {
        Renderer renderer = null;
        if (modelObject instanceof TemplateInstance)
        {
            renderer = getRenderer(RendererType.TEMPLATE);
        }
        if (renderer == null)
        {
            renderer = getRenderer(modelObject.getTypeId());
        }
        
        return renderer;
    }
    
    /**
     * Gets a renderer for the given id
     * 
     * @param id
     * @return renderer
     */
    public static Renderer getRenderer(String id)
    {
        String rendererId = PREFIX_WEBFRAMEWORK_RENDERER + id;

        return (Renderer) FrameworkHelper.getApplicationContext().getBean(rendererId);
    }
    
    /**
     * Returns a processor for the given id (i.e. jsp, webscript, etc)
     * 
     * @param id
     * @return processor
     */
    public static Processor getProcessorById(String id)
    {
        String processorId = PREFIX_WEBFRAMEWORK_PROCESSOR + id;
        
        return (Processor) FrameworkHelper.getApplicationContext().getBean(processorId);
    }

    /**
     * Returns a processor for a renderable in the default VIEW render mode
     * 
     * @param renderable
     * @return
     */
    public static Processor getProcessor(Renderable renderable)
    {
        return getProcessor(renderable, RenderMode.VIEW);
    }
    
    /**
     * Returns a processor for a renderable in the given render mode
     * 
     * @param renderable
     * @param renderMode
     * @return
     */
    public static Processor getProcessor(Renderable renderable, RenderMode renderMode)
    {
        String processorId = renderable.getProcessorId(renderMode);
        return getProcessorById(processorId);
    }
    
    /**
     * Returns the render context provider instance
     * 
     * @return render context provider
     */
    public static RenderContextProvider getRenderContextProvider()
    {
        return (RenderContextProvider) FrameworkHelper.getApplicationContext().getBean(WEBFRAMEWORK_RENDERCONTEXT_PROVIDER);
    }
    
    /**
     * Provides a new render context bound to the given model object
     * This will use the VIEW render mode
     * 
     * @param context
     * @param request
     * @param response
     * @return render context
     */
    public static RenderContext provideRenderContext(RequestContext context, HttpServletRequest request, HttpServletResponse response)
    {
        return getRenderContextProvider().provide(context, request, response, RenderMode.VIEW);
    }
        
    /**
     * Provides a new render context bound to the given model object
     * This will use the VIEW render mode
     * 
     * @param context
     * @param request
     * @param response
     * @param renderMode
     * @return render context
     */
    public static RenderContext provideRenderContext(RequestContext context, HttpServletRequest request, HttpServletResponse response, RenderMode renderMode)
    {
        return getRenderContextProvider().provide(context, request, response, renderMode);
    }
    
    /**
     * Provides a new render context instance which inherits properties
     * from the given render context instance
     * 
     * @param renderContext
     * @return
     */
    public static RenderContext provideRenderContext(RenderContext renderContext)
    {
        return getRenderContextProvider().provide(renderContext);
    }

    /**
     * Provides a new render context instance which inherits properties
     * from the given render context instance
     * 
     * The given model object will be bound to the render context.
     * 
     * @param renderContext
     * @param modelObject
     * @return
     */
    public static RenderContext provideRenderContext(RenderContext renderContext, ModelObject modelObject)
    {
        return getRenderContextProvider().provide(renderContext, modelObject);
    }
    
    /**
     * Merges a model object into the given render context.
     * 
     * @param renderContext
     * @param modelObject
     */
    public static void mergeRenderContext(RenderContext renderContext, ModelObject modelObject)
    {
        getRenderContextProvider().merge(renderContext, modelObject);
    }
    
    /**
     * Releases the given render context
     * 
     * @param renderContext
     */
    public static void releaseRenderContext(RenderContext renderContext)
    {
        getRenderContextProvider().release(renderContext);
    }
    
    /**
     * Renders the specific focus for the given model object
     * 
     * @param renderContext
     * @param renderFocus
     * @throws RendererExecutionException
     */
    public static void renderModelObject(RenderContext renderContext, RenderFocus renderFocus)
        throws RendererExecutionException
    {
        ModelObject modelObject = renderContext.getObject();
        if (modelObject != null)
        {
            Renderer renderer = (Renderer) RenderHelper.getRenderer(modelObject);
            renderer.render(renderContext, renderFocus);
        }
    }
    
    public static String renderModelObjectAsString(RenderContext renderContext)
        throws RendererExecutionException, UnsupportedEncodingException
    {
        return renderModelObjectAsString(renderContext, RenderFocus.BODY);
    }
    
    public static String renderModelObjectAsString(RenderContext renderContext, RenderFocus renderFocus)
        throws RendererExecutionException, UnsupportedEncodingException
    {
        renderContext = wrapRenderContext(renderContext);
        
        renderModelObject(renderContext, renderFocus);
        
        return ((WrappedRenderContext)renderContext).getContentAsString();
    }
    
    public static RenderContext wrapRenderContext(RenderContext renderContext)
    {
        return new WrappedRenderContext(renderContext);
    }
        
    public static void processRenderable(RenderContext context, RenderFocus renderFocus, Renderable renderable)
        throws RendererExecutionException
    {
        // get the processor
        Processor processor = RenderHelper.getProcessor(renderable);
        if (processor != null)
        {
            // build a processor context
            ProcessorContext processorContext = new ProcessorContext(context);
            
            // load from renderable data
            processorContext.load(renderable);
            
            // execute the processor
            processor.execute(processorContext, renderFocus);
        }        
    }
    
    @SuppressWarnings("unchecked")
    public static void processComponent(RenderContext context, RenderFocus renderFocus, Component component)
        throws RendererExecutionException
    {
        ComponentType renderable = null;
        
        // special case for web scripts
        String uri = component.getURL();
        if (uri == null)
        {
            uri = component.getProperty("uri");
        }
        if (uri == null)
        {
            uri = component.getProperty("url");
        }
        if (uri != null && uri.length() != 0)
        {
            renderable = context.getModel().getComponentType(COMPONENTTYPE_WEBSCRIPT);
        }
        String componentTypeId = component.getComponentTypeId();
        if (componentTypeId != null)
        {
            ComponentType testComponentType = context.getModel().getComponentType(componentTypeId);
            if (testComponentType == null)
            {
                renderable = context.getModel().getComponentType(COMPONENTTYPE_WEBSCRIPT);
                uri = componentTypeId;
            }
        }
        if (renderable == null)
        {
            renderable = component.getComponentType(context);
        }
        
        // catch issues where the URL etc. have not been defined
        if (renderable == null)
        {
            throw new RendererExecutionException("Cannot resolve component URL - may be missing from the definition: " +
                    component.toString());
        }
        
        // get the processor
        Processor processor = RenderHelper.getProcessor(renderable);
        if (processor != null)
        {
            // build a processor context
            ProcessorContext processorContext = new ProcessorContext(context);
            
            // load from renderable data
            processorContext.load(renderable);
            
            // apply any overrides from special cases
            if (uri != null)
            {
                ProcessorDescriptor viewDescriptor = processorContext.getDescriptor(RenderMode.VIEW);
                viewDescriptor.put("uri", uri);
            }
            
            // execute the processor
            processor.execute(processorContext, renderFocus);
            
            /**
             * This is a workaround for the Internet Explorer bug detailed in KB262161
             * "All style tags after the first 30 style tags on an HTML page are not applied in Internet Explorer"
             * http://support.microsoft.com/kb/262161
             */
            if (context.hasValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME))
            {
                // stylesheets to consolidate
                LinkedList<String> css = (LinkedList<String>)context.getValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME);
                if (css != null)
                {
                    try
                    {
                        Iterator iter = css.iterator();
                        Writer writer = context.getResponse().getWriter();
                        writer.write("   <style type=\"text/css\" media=\"screen\">\n");
                        while (iter.hasNext())
                        {
                            writer.write("      @import \"" + iter.next() + "\";\n");
                        }
                        writer.write("   </style>");
                    }
                    catch (IOException ioe)
                    {
                        throw new RendererExecutionException(ioe);
                    }
                }
                context.removeValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME);
            }

        }        
    }

    public static void processTemplate(RenderContext context, RenderFocus renderFocus, TemplateInstance template)
        throws RendererExecutionException
    {
        TemplateType renderable = null;
        
        // special case for web scripts
        String uri = null;
        String templateTypeId = template.getTemplateType();
        if (templateTypeId != null)
        {
            TemplateType testTemplateType = context.getModel().getTemplateType(templateTypeId);
            if (testTemplateType == null)
            {
                renderable = context.getModel().getTemplateType("freemarker");
                uri = templateTypeId;
            }
        }
        if (renderable == null)
        {
            renderable = template.getTemplateType(context);
        }
        
        // get the processor
        Processor processor = RenderHelper.getProcessor(renderable);
        if (processor != null)
        {
            // build a processor context
            ProcessorContext processorContext = new ProcessorContext(context);
            
            // load from renderable data
            processorContext.load(renderable);
            
            // apply any overrides from special cases
            if (uri != null)
            {
                ProcessorDescriptor viewDescriptor = processorContext.getDescriptor(RenderMode.VIEW);
                viewDescriptor.put("uri", uri);
            }
            
            // execute the processor
            processor.execute(processorContext, renderFocus);
        }        
    }
}
