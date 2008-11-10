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

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.config.WebFrameworkConfigElement.ErrorHandlerDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.SystemPageDescriptor;
import org.alfresco.web.framework.exception.ChromeRendererExecutionException;
import org.alfresco.web.framework.exception.JspRenderException;
import org.alfresco.web.framework.exception.PageRendererExecutionException;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.Chrome;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.Theme;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.Timer;
import org.alfresco.web.site.URLUtil;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * Utility methods for rendering various pages, regions and components.
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class RenderUtil
{
	public static final String NEWLINE = "\r\n";
	
	
    /**
     * Entry point for the rendering of a JSP page.
     *
     * @param parentContext the render context
     * @param dispatchPath
     * 
     * @throws JspRenderException
     */
    public static void renderJspPage(RenderContext parentContext,
            String dispatchPath) throws JspRenderException
    {    
    	// bind new render context
    	RenderContext context = RenderHelper.provideRenderContext(parentContext);
        try
        {
        	// start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "RenderJspPage-" + dispatchPath);
        	
        	RequestUtil.include(context.getRequest(), context.getResponse(), dispatchPath);
        }
        catch (Exception ex)
        {
            throw new JspRenderException("Unable to render JSP page", ex);
        }
        finally
        {
            // release the rendering context
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context.getRequest(), "RenderJspPage-" + dispatchPath);
        }
    }

    /**
     * Entry point for the rendering of the current page as provided
     * by the request context.
     *
     * @param parentContext the render context
     * 
     * @throws RendererExecutionException
     */
    public static void renderPage(RenderContext parentContext,
    		RenderFocus renderFocus)
            throws RendererExecutionException
    {
    	Page page = parentContext.getPage();
        if (page == null)
        {
            throw new PageRendererExecutionException(
                    "Unable to locate current page in request context");
        }
        
        startPageRenderer(parentContext, renderFocus, page);
    }
    
    /**
     * Processes the page renderer bean with the given page
     * 
     * @param parentContext render context
     * @param page page instance
     * 
     * @throws RendererExecutionException
     */
    protected static void startPageRenderer(RenderContext parentContext, 
    		RenderFocus renderFocus,
    		Page page)
    	throws RendererExecutionException
    {
        // provision a page-bound render context
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext, page);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "ProcessPageRenderer-" + page.getId());
        	
            // loads the "page renderer" bean and executes it
            Renderer renderer = RenderHelper.getRenderer(RendererType.PAGE);
            renderer.render(context, renderFocus);
        }
        finally
        {
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(parentContext, "ProcessPageRenderer-" + page.getId());
        }
    }
    
    /**
     * Entry point for the rendering of the current content item as
     * provided by the request context.
     *
     * @param parentContext the render context
     * 
     * @throws RendererExecutionException
     */
    public static void renderContent(RenderContext parentContext,
    		RenderFocus renderFocus)
            throws RendererExecutionException
    {
    	TemplateInstance template = parentContext.getTemplate();
    	
        // provision a template-bound render context
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext, template);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "RenderContent-" + template.getId());

            // loads the "template renderer" bean and executes it
            Renderer renderer = RenderHelper.getRenderer(RendererType.TEMPLATE);
            renderer.render(context, RenderFocus.BODY);
        }
        finally
        {
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "RenderContent-" + template.getId());
        }                
    }
    

    /**
     * Entry point for the rendering a region of a given template
     *
     * @param parentContext 
     * @param renderFocus
     * @param templateId
     * @param regionId
     * @param regionScopeId
     * @param overrideChromeId
     * 
     * @throws RendererExecutionException
     */
    public static void renderRegion(RenderContext parentContext,
    		RenderFocus renderFocus,
            String templateId, 
            String regionId, 
            String regionScopeId, 
            String overrideChromeId)
            throws RendererExecutionException
    {
    	TemplateInstance template = (TemplateInstance) parentContext.getModel().getTemplate(templateId);    	
    	startRegionRenderer(parentContext, renderFocus, template, regionId, regionScopeId, overrideChromeId);
    }
    
    /**
     * Processes the region renderer bean with the given region info
     *
     * @param parentContext
     * @param renderFocus 
     * @param template
     * @param regionId
     * @param regionScopeId
     * @param overrideChromeId
     * 
     * @throws RendererExecutionException
     */
    protected static void startRegionRenderer(RenderContext parentContext,
    		RenderFocus renderFocus,
            TemplateInstance template, 
            String regionId, 
            String regionScopeId,
            String overrideChromeId)
            throws RendererExecutionException
    {
        // provision a template-bound render context
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext, template);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "ProcessRegionRenderer-" + template.getId());
            
            // this must be bound in manually here
        	// set up region binding info onto render context
            String regionSourceId = determineSourceId(context, regionScopeId);            
            context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_ID, regionId);
            context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID, regionScopeId);
            context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_SOURCE_ID, regionSourceId);
            
            // figure out if a component is bound to this region
            // bind in the html id
            Component component = getComponentBoundToRegion(context, regionId, regionScopeId, regionSourceId);
            if(component != null)
            {
            	// bind in the region's htmlid from bound component
            	context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, RenderUtil.determineValidHtmlId(component.getId()));
            }
            else
            {
            	// bind in the region's htmlid by hand       
            	context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, "unbound-region-" + RenderUtil.determineValidHtmlId(regionId));
            }
            
            if(overrideChromeId != null)
            {
            	context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_CHROME_ID, overrideChromeId);
            }
        	
            // loads the "region renderer" bean and executes it
            Renderer renderer = RenderHelper.getRenderer(RendererType.REGION);
            renderer.render(context, renderFocus);
        }
        finally
        {
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(parentContext, "ProcessRegionRenderer-" + template.getId());
        }
    }
    
    /**
     * Renders the components of the region described by the render context
     * This method is generally called from the region include tag.
     * 
     * @param parentContext
     * @throws RendererExecutionException
     */
    public static void renderRegionComponents(RenderContext parentContext)
    	throws RendererExecutionException
	{
        // values from the render context
        String regionId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
        String regionScopeId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
        String regionSourceId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SOURCE_ID);
    	
    	// create a new render context (for the component)
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "RenderRegionComponents-" + regionId + "-" + regionScopeId);
                        
            // render in either one of two ways
            // if there is a component bound, then continue processing downstream
            // if not, then render a "no component" screen
            Component component = getComponentBoundToRegion(context, regionId, regionScopeId, regionSourceId);
            if (component != null)
            {
                // if we are in passive mode, then we won't bother to execute the renderer.
                // rather, we will notify the template that this component is bound to it
            	if(context.isPassiveMode())
                {
                    // don't render the component, just inform the current context what our component is
                    context.setRenderingComponent(component);
                }
                else
                {
                	// merge component into render context
                	RenderHelper.mergeRenderContext(context, component);
                	
                	// get the 'component' renderer bean
                	Renderer renderer = RenderHelper.getRenderer(RendererType.COMPONENT);
                	renderer.body(context);
                }
            }
            else
            {
            	// stamp an htmlid onto the renderer context
            	context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, "unbound-region-" + regionId);
            	
                // if we couldn't get a component, then redirect to a region "no-component" renderer
                RenderUtil.renderErrorHandlerPage(context,
                        WebFrameworkConstants.DISPATCHER_HANDLER_REGION_NO_COMPONENT);
            }
        }
        finally
        {
            // release the render context
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "RenderRegionComponents-" + regionId + "-" + regionScopeId);
        }    	
	}
    
    /**
     * Entry point for the rendering a single identified component
     * with the default chrome.
     *
     * @param parentContext
     * @param renderFocus
     * @param componentId
     * 
     * @throws RendererExecutionException
     */
    public static void renderComponent(RenderContext parentContext,
    		RenderFocus renderFocus,
            String componentId) throws RendererExecutionException
    {
    	renderComponent(parentContext, renderFocus, componentId, null);
    }

    /**
     * Entry point for the rendering a component with the given chrome.
     *
     * @param parentContext
     * @param renderFocus
     * @param componentId
     * @param overrideChromeId
     * 
     * @throws RendererExecutionException
     */
    public static void renderComponent(RenderContext parentContext,
    		RenderFocus renderFocus,
    		String componentId, 
    		String overrideChromeId)
		throws RendererExecutionException
    {
        Component component = parentContext.getModel().getComponent(componentId);
        if (component == null)
        {
            throw new ChromeRendererExecutionException(
                    "Unable to locate component: " + componentId);
        }
        
        startComponentRenderer(parentContext, renderFocus, component, overrideChromeId);
    }
    
    
    protected static void startComponentRenderer(RenderContext parentContext,
    		RenderFocus renderFocus, 
    		Component component, 
    		String overrideChromeId)
    	throws RendererExecutionException
    {
        // provision a template-bound render context
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext, component);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "ProcessComponentRenderer-" + component.getId());
            
            if(overrideChromeId != null)
            {
            	context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_CHROME_ID, overrideChromeId);
            }

            // loads the "component renderer" bean and executes it
            Renderer renderer = RenderHelper.getRenderer(RendererType.COMPONENT);
            renderer.render(context, renderFocus);
        }
        finally
        {
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(parentContext, "ProcessComponentRenderer-" + component.getId());
        }
    }
    	
    public static void renderRawComponent(RenderContext parentContext,
    		RenderFocus renderFocus,
    		String componentId)
    	throws RendererExecutionException
	{
    	Component component = parentContext.getModel().getComponent(componentId);
    	renderRawComponent(parentContext, renderFocus, component);	
	}

    public static void renderRawComponent(RenderContext parentContext,
    		RenderFocus renderFocus,
    		Component component)
    	throws RendererExecutionException
    {
        // provision a component-bound render context
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext, component);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "RenderRawComponent-" + component.getId());

            RenderHelper.processComponent(context, renderFocus, component);
        }
        finally
        {
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(parentContext, "RenderRawComponent-" + component.getId());
        }
    }    
    
    /**
     * Renders the fully formed URL string for a link to a given page
     *
     * @param context
     * @param request
     * @param response
     * @param objectId
     * @param formatId
     */
    public static void page(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, String pageId, String formatId, String objectId)
    {
        String url = context.getLinkBuilder().page(context, pageId, formatId);
        if (url != null)
        {
            try
            {
                response.getWriter().write(url);
            }
            catch (Exception ex)
            {
                FrameworkHelper.getLogger().error(ex);
            }
        }
    }

    /**
    /**
     * Renders the fully formed URL string for a link to a given content object
     *
     * @param context
     * @param request
     * @param response
     * @param objectId
     * @param formatId
     */
    public static void content(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String objectId, String formatId)
    {
        String pageId = context.getPage().getId();
        String url = context.getLinkBuilder().page(context, pageId, formatId);
        if (url != null)
        {
            try
            {
                response.getWriter().write(url);
            }
            catch (Exception ex)
            {
                FrameworkHelper.getLogger().error(ex);
            }
        }
    }

    /**
     * Renders the fully formed URL string for a link to a given page type
     *
     * @param context
     * @param request
     * @param response
     * @param objectId
     * @param formatId
     */
    public static void pageType(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, String pageTypeId, String formatId, String objectId)
    {
        String url = context.getLinkBuilder().pageType(context, pageTypeId, formatId, objectId);
        if (url != null)
        {
            try
            {
                response.getWriter().write(url);
            }
            catch (Exception ex)
            {
                FrameworkHelper.getLogger().error(ex);
            }
        }
    }

    public static String renderScriptImport(RequestContext context, String src)
    {
        return renderScriptImport(context.getRequest(), src);
    }

    public static String renderScriptImport(HttpServletRequest request,
            String src)
    {
        return renderScriptImport(request, src, true);
    }

    public static String renderScriptImport(HttpServletRequest request,
            String src, boolean includeQueryString)
    {
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 4)
            src = src + "?" + queryString;

        // make sure references resolve to the configured servlet
        src = URLUtil.browser(request, src);

        return "<script type=\"text/javascript\" src=\"" + src + "\"></script>";
    }

    public static String renderLinkImport(RequestContext context, String href)
    {
        return renderLinkImport(context.getRequest(), href, null, true);
    }

    public static String renderLinkImport(RequestContext context, String href,
            String id)
    {
        return renderLinkImport(context.getRequest(), href, id, true);
    }

    public static String renderLinkImport(HttpServletRequest request,
            String href)
    {
        return renderLinkImport(request, href, null, true);
    }

    public static String renderLinkImport(HttpServletRequest request,
            String href, String id, boolean includeQueryString)
    {
        if (includeQueryString)
        {
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 4)
                href = href + "?" + queryString;
        }

        // make sure references resolve to the configured servlet
        href = URLUtil.browser(request, href);

        String value = "<link ";
        if (id != null)
        {
            value += "id=\"" + id + "\" ";
        }
        value += "rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\"></link>";

        return value;
    }

    /**
     * Return the "source" ID for the given scope ID for the supplied context.
     * 
     * For 'global' scope this will simply return 'global',
     * for 'template' it will return the current template ID,
     * for 'page' it will return the current page ID,
     * for 'uri' it will return the current page URI,
     * for 'theme' it will return the current theme ID.
     * 
     * @param context   Current RequestContext
     * @param scopeId   {@link WebFrameworkConstants}
     * 
     * @return the source ID
     */
    public static String determineSourceId(RequestContext context, String scopeId)
    {
        String sourceId = null;
        
        if (WebFrameworkConstants.REGION_SCOPE_GLOBAL.equalsIgnoreCase(scopeId))
        {
            sourceId = WebFrameworkConstants.REGION_SCOPE_GLOBAL;
        }
        else if (WebFrameworkConstants.REGION_SCOPE_TEMPLATE.equalsIgnoreCase(scopeId))
        {
            sourceId = context.getTemplate().getId();
        }
        else if (WebFrameworkConstants.REGION_SCOPE_PAGE.equalsIgnoreCase(scopeId))
        {
            sourceId = context.getPage().getId();
        }
        else if (WebFrameworkConstants.REGION_SCOPE_URI.equalsIgnoreCase(scopeId))
        {
            sourceId = context.getUri();
        }
        else if (WebFrameworkConstants.REGION_SCOPE_THEME.equalsIgnoreCase(scopeId))
        {
            sourceId = context.getThemeId();
        }
        
        return sourceId;
    }
    
    /**
     * Returns the object to which this component is bound
     * This is the same as calling component.getSourceObject()
     * 
     * @param context
     * @param component
     * @return
     */
    public static Object determineComponentBindingSourceObject(RequestContext context, Component component)
    {
        Object obj = null;
        
        String scopeId = component.getScope();
        String sourceId = component.getSourceId();
        
        if (WebFrameworkConstants.REGION_SCOPE_GLOBAL.equalsIgnoreCase(scopeId))
        {
            obj = WebFrameworkConstants.REGION_SCOPE_GLOBAL;
        }
        else if (WebFrameworkConstants.REGION_SCOPE_TEMPLATE.equalsIgnoreCase(scopeId))
        {
            obj = context.getModel().getTemplate(sourceId);
        }
        else if (WebFrameworkConstants.REGION_SCOPE_PAGE.equalsIgnoreCase(scopeId))
        {
            obj = context.getModel().getPage(sourceId);
        }
        else if (WebFrameworkConstants.REGION_SCOPE_URI.equalsIgnoreCase(scopeId))
        {
            obj = context.getUri();
        }
        else if (WebFrameworkConstants.REGION_SCOPE_THEME.equalsIgnoreCase(scopeId))
        {
            obj = context.getModel().getTheme(sourceId);
        }
        
        return obj;
    }
    
    /**
     * Returns the Chrome instance to utilize while rendering the given
     * region on the given template.
     */
    public static Chrome determineRegionChrome(RenderContext context, TemplateInstance template, String regionId, String chromeId)
    {
    	Chrome chrome = null;
    	
        // if the chrome id is empty, see if there is an override
        // this allows the template to "override" the chrome on a per-region basis
        if (chromeId == null)
        {
            chromeId = template.getCustomProperty("region-" + regionId + "-chrome");
        }
        
        // see if a default chrome is specified in the framework configuration
        if (chromeId == null)
        {
            chromeId = context.getConfig().getDefaultRegionChrome();
        }
                
        if (chromeId != null && chromeId.length() != 0)
        {
        	chrome = context.getModel().getChrome(chromeId);
        }
        
        return chrome;
    }

    /**
     * Returns the Chrome instance to utilize while rendering the given
     * component.
     */
    public static Chrome determineComponentChrome(
            RenderContext context, Component component, String chromeId)
    {
    	Chrome chrome = null;
        
        // if the chrome id is empty, see if the component instance intself
        // the chrome that it would like to use
        if (chromeId == null)
        {
            chromeId = component.getChrome();
        }
        
        // see if a default chrome was specified
        if (chromeId == null)
        {
            chromeId = context.getConfig().getDefaultComponentChrome();
        }
        
        if (chromeId != null && chromeId.length() != 0)
        {
            chrome = context.getModel().getChrome(chromeId);
        }
        
        return chrome;
    }
    
    /**
     * Determines the component which is bound to the given region
     * If there is no component bound, then null is returned.
     * 
     * @param context
     * @param regionId
     * @param regionScopeId
     * @param regionSourceId
     * 
     * @return the component
     */
    public static Component getComponentBoundToRegion(RenderContext context,
    		String regionId,
    		String regionScopeId,
    		String regionSourceId)
    {
        Component component = context.getModel().getComponent(regionScopeId, regionId, regionSourceId);
        if (component == null || WebFrameworkConstants.REGION_SCOPE_THEME.equals(regionScopeId))
        {
            // check to see whether the current theme specifies a default component for this region id
            Theme theme = ThemeUtil.getCurrentTheme(context);
            if (theme != null)
            {
                component = theme.getDefaultComponent(context, regionId);
            }
        }
        
        return component;    	
    }
    
    /**
     * Renders a system page
     *
     * A system page is a "special page" designed to handle one of a few
     * exception cases such as when an error occurs or a page has not
     * yet been configured.  We want to show something rather than
     * have an exception purely occur.
     *
     * @param parentContext
     * @param errorHandlerPageId
     * @param defaultErrorHandlerPageRenderer
     */
    public static void renderErrorHandlerPage(RenderContext parentContext, String errorHandlerPageId)
    		throws RendererExecutionException
    {
        // bind the rendering to this page
        RenderContext context = RenderHelper.provideRenderContext(parentContext);

        try
        {
	    	// start a timer
	        if (Timer.isTimerEnabled())
	            Timer.start(parentContext, "RenderErrorHandlerPage-" + errorHandlerPageId);
	
	        // get the error handler descriptor from config
	        ErrorHandlerDescriptor descriptor = context.getConfig().getErrorHandlerDescriptor(errorHandlerPageId);
	        
	        // get descriptor properties and processor id
	        String processorId = descriptor.getProcessorId();
	        Map<String,String> descriptorProperties = descriptor.map();
	
	        // create the processor
	        Processor processor = RenderHelper.getProcessorById(processorId);
	        
	        // load processor context
	        ProcessorContext processorContext = new ProcessorContext(context);
	        processorContext.addDescriptor(RenderMode.VIEW.toString(), descriptorProperties);
	        
	        // execute processor
	        processor.executeBody(processorContext);
        }
        finally
        {
        	// release the render context
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "RenderErrorHandlerPage-" + errorHandlerPageId);
        }
    }

    /**
     * Renders a system container
     *
     * A system container is a page fragment that is rendered
     * as a container of other elements like components.
     *
     * @param context
     * @param systemPageId
     */
    public static void renderSystemPage(RenderContext parentContext,
            String systemPageId) throws RendererExecutionException
    {
        // bind the rendering to this page
        RenderContext context = RenderHelper.provideRenderContext(parentContext);

        try
        {
	    	// start a timer
	        if (Timer.isTimerEnabled())
	            Timer.start(parentContext, "RenderSystemPage-" + systemPageId);
	
	        // get the system page descriptor from config
	        SystemPageDescriptor descriptor = context.getConfig().getSystemPageDescriptor(systemPageId);
	        
	        // get descriptor properties and processor id
	        String processorId = descriptor.getProcessorId();
	        Map<String,String> descriptorProperties = descriptor.map();
	
	        // create the processor
	        Processor processor = RenderHelper.getProcessorById(processorId);
	        
	        // load processor context
	        ProcessorContext processorContext = new ProcessorContext(context);
	        processorContext.addDescriptor(RenderMode.VIEW.toString(), descriptorProperties);
	        
	        // execute processor
	        processor.executeBody(processorContext);
        }
        finally
        {
        	// release the render context
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "RenderSystemPage-" + systemPageId);
        }
    }

    /**
     * Generates text to be inserted into template markup head for a given
     * renderer context.  The renderer context must describe a template.
     *
     * @param rendererContext
     *
     * @return head tags render output
     */
    public static String renderTemplateHeaderAsString(RenderContext parentContext)
        throws RendererExecutionException, UnsupportedEncodingException
    {
        // if we're in passive mode, just return empty string
    	if(parentContext.isPassiveMode())
    	{
    		return "";
    	}

        String headTags = (String) parentContext.getValue(WebFrameworkConstants.PAGE_HEAD_DEPENDENCIES_STAMP, RenderContext.SCOPE_REQUEST);
        if (headTags == null)
        {
	        // produce a new render context
	        RenderContext context = RenderHelper.provideRenderContext(parentContext);
	        try
	        {
		    	// start a timer
		        if (Timer.isTimerEnabled())
		            Timer.start(parentContext, "RenderTemplateHeaderAsString");

		        // start building the buffer
	        	StringBuilder buf = new StringBuilder(2048);
	            buf.append(WebFrameworkConstants.WEB_FRAMEWORK_SIGNATURE);
	            buf.append(NEWLINE);
	        	
		        // wrap so that we can capture output
		        context = RenderHelper.wrapRenderContext(context);
		        
		        // get the 'template' renderer bean
		        // execute the renderer for 'header'
		        Renderer renderer = RenderHelper.getRenderer(RendererType.TEMPLATE);
		        renderer.header(context);
			        
		        // get head tags from captured output
		        headTags = ((WrappedRenderContext)context).getContentAsString();
			        
		        // build buffer
		        buf.append(headTags);
		        buf.append(NEWLINE);
		        buf.append(NEWLINE);
	        }
	        finally
	        {
	        	// release the render context
	        	context.release();
	
	            if (Timer.isTimerEnabled())
	                Timer.stop(context, "RenderTemplateHeaderAsString");
	        }
		      
	        if(headTags != null)
	        {
		        // store back		        
		        context.setValue(WebFrameworkConstants.PAGE_HEAD_DEPENDENCIES_STAMP, headTags);		        		        
        	}
        }
        
        if(headTags == null)
        {
        	headTags = "";
        }
        
        return headTags;
    }
    
    /** Mask for hex encoding. */
    private static final int MASK = (1 << 4) - 1;

    /** Digits used string encoding. */
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    
    /**
     * Helper to ensure only valid and acceptable characters are output as HTML element IDs.
     * 
     * @param id the id
     * 
     * @return the string
     */
    public static String determineValidHtmlId(String id)
    {
        int len = id.length();
        StringBuilder buf = new StringBuilder(len + (len>>1) + 8);
        for (int i = 0; i<len; i++)
        {
            char c = id.charAt(i);
            int ci = (int)c;
            if (i == 0)
            {
                if ((ci >= 65 && ci <= 90) ||   // A-Z
                    (ci >= 97 && ci <= 122))    // a-z
                {
                    buf.append(c);
                }
                else
                {
                    encodef(c, buf);
                }
            }
            else
            {
                if ((ci >= 65 && ci <= 90) ||   // A-Z
                    (ci >= 97 && ci <= 122) ||  // a-z
                    (ci >= 48 && ci <= 57) ||   // 0-9
                    ci == 45 || ci == 95)       // - and _
                {
                    buf.append(c);
                }
                else
                {
                    encode(c, buf);
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * Encode.
     * 
     * @param c the c
     * @param builder the builder
     */
    private static void encode(char c, StringBuilder builder)
    {
        char[] buf = new char[] { '_', 'x', '0', '0', '0', '0', '_' };
        int charPos = 6;
        do
        {
            buf[--charPos] = DIGITS[c & MASK];
            c >>>= 4;
        }
        while (c != 0);
        builder.append(buf);
    }
    
    /**
     * Encodef.
     * 
     * @param c the c
     * @param builder the builder
     */
    private static void encodef(char c, StringBuilder builder)
    {
        char[] buf = new char[] { 'x', '0', '0', '0', '0', '_' };
        int charPos = 5;
        do
        {
            buf[--charPos] = DIGITS[c & MASK];
            c >>>= 4;
        }
        while (c != 0);
        builder.append(buf);
    }
    
    
    /**
     * Attempts to retrieve the render context instance bound to the given
     * http servlet request
     * 
     * @param request
     * @return
     */
    public static RenderContext getContext(HttpServletRequest request)
    {
    	return (RenderContext) request.getAttribute(RenderContextRequest.ATTRIB_RENDER_CONTEXT);    	
    }
    
}            