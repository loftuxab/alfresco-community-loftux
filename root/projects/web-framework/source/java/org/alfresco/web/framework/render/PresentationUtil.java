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

import org.alfresco.web.framework.exception.JspRenderException;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.exception.RequestDispatchException;

/**
 * This class basically delegates through to the RenderUtil
 * functions but provides for a 'pretty' user experience.
 * 
 * Exceptions that trickle back are trapped and presented nicely
 * to the end user.  The exceptions are also logged so that
 * administrators can track down the issues at hand.
 * 
 * @author muzquiano
 */
public class PresentationUtil
{
    /**
     * Renders a JSP page
     * 
     * @param context
     * @param dispatchPath
     */
    public static void renderJspPage(RenderContext context, String dispatchPath) throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderJspPage(context, dispatchPath);
        }
        catch (JspRenderException ex)
        {
            handlePageRenderProblem(context, ex, dispatchPath);
        }
    }

    /**
     * Renders the current page using the BODY focus
     * 
     * @param context
     */
    public static void renderPage(RenderContext context) 
            throws RequestDispatchException
    {
    	renderPage(context, RenderFocus.BODY);
    }

    /**
     * Renders the current page using the BODY focus
     * 
     * @param context
     * @param renderFocus
     */
    public static void renderPage(RenderContext context, 
    		RenderFocus renderFocus) 
            throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderPage(context, renderFocus);
        }
        catch (RendererExecutionException ex)
        {
            handlePageRenderProblem(context, ex,
                    context.getPage().getId());
        }
    }
    
    public static void renderChromelessRegion(RenderContext context,
    		RenderFocus renderFocus,
            String templateId, 
            String regionId, 
            String regionScopeId) 
            throws RequestDispatchException 
    {
        renderRegion(context, renderFocus, templateId, regionId, regionScopeId, WebFrameworkConstants.CHROMELESS_REGION_CHROME_ID);
    }

    public static void renderRegion(RenderContext context,
    		RenderFocus renderFocus,
            String templateId,
            String regionId,
            String regionScopeId) 
            throws RequestDispatchException 
    {
        renderRegion(context, renderFocus, templateId, regionId, regionScopeId, null);
    }
    
    public static void renderRegion(RenderContext context,
    		RenderFocus renderFocus,
    		String templateId, 
    		String regionId, 
    		String regionScopeId, 
    		String overrideChromeId) 
            throws RequestDispatchException 
    {
        try
        {
            RenderUtil.renderRegion(context, renderFocus, templateId,
                    regionId, regionScopeId, overrideChromeId);
        }
        catch (RendererExecutionException ex)
        {
            handleRegionRenderProblem(context, ex,
                    templateId, regionId, regionScopeId);
        }
    }    

    public static void renderChromelessComponent(RenderContext context,
    		RenderFocus renderFocus,
            String componentId) throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderRawComponent(context, renderFocus, componentId);
        }
        catch (RendererExecutionException ex)
        {
            handleComponentRenderProblem(context, ex,
                    componentId);
        }
    }
    

    public static void renderComponent(RenderContext context,
    		RenderFocus renderFocus,
            String componentId) throws RequestDispatchException
    {
        renderComponent(context, renderFocus, componentId, null);
    }

    public static void renderComponent(RenderContext context,
    		RenderFocus renderFocus,
            String componentId,
            String chromeId) throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderComponent(context, renderFocus, componentId, chromeId);
        }
        catch (RendererExecutionException ex)
        {
            handleComponentRenderProblem(context, ex, componentId);
        }
    }   
    
    /**
     * Renders the current content object using the BODY focus
     * 
     * @param context
     */
    public static void renderContent(RenderContext context) 
            throws RequestDispatchException
    {
    	renderContent(context, RenderFocus.BODY);
    }
    
    /**
     * Renders the current content object using its associated
     * presentation template in the given focus.
     * 
     * @param context
     * @param renderFocus
     */
    public static void renderContent(RenderContext context, 
    		RenderFocus renderFocus) 
            throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderContent(context, renderFocus);
        }
        catch (RendererExecutionException ex)
        {
            handlePageRenderProblem(context, ex,
                    context.getPage().getId());
        }
    }
    

    // pretty methods

    protected static void handlePageRenderProblem(RenderContext context,
            Throwable t, String pageId) throws RequestDispatchException
    {
        // log the error
        context.getLogger().error("A Page Rendering problem was handled");
        context.getLogger().error("Page Id: " + pageId);
        context.getLogger().error("Trace", t);
        try
        {
            context.getRequest().setAttribute("error", t);
            context.getRequest().setAttribute("error-pageId", pageId);
            context.getRequest().setAttribute("error-objectId", pageId);

            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_PAGE_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A page rendering problem was not able to be handled by the framework", ex);
        }
    }

    protected static void handleTemplateRenderProblem(RenderContext context,
            Throwable t, String templateId) throws RequestDispatchException
    {
        // log the error
        context.getLogger().error("A Template Rendering problem was handled");
        context.getLogger().error("Template Id: " + templateId);
        context.getLogger().error(t);
        try
        {
            context.getRequest().setAttribute("error", t);
            context.getRequest().setAttribute("error-templateId", templateId);
            context.getRequest().setAttribute("error-objectId", templateId);

            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_TEMPLATE_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A template rendering problem was not able to be handled by the framework", ex);
        }
    }

    protected static void handleRegionRenderProblem(RenderContext context,
            Throwable t, String templateId, String regionId,
            String regionScopeId) throws RequestDispatchException
    {
        // log the error
        context.getLogger().error("A Region Rendering problem was handled");
        context.getLogger().error("Template Id: " + templateId);
        context.getLogger().error("Region Id: " + regionId);
        context.getLogger().error("Region Scope Id: " + regionScopeId);
        context.getLogger().error(t);
        try
        {
        	String regionSourceId = RenderUtil.determineSourceId(context, regionScopeId);
        	
            context.getRequest().setAttribute("error", t);
            context.getRequest().setAttribute("error-templateId", templateId);
            context.getRequest().setAttribute("error-regionId", regionId);
            context.getRequest().setAttribute("error-regionScopeId", regionScopeId);
            context.getRequest().setAttribute("error-regionSourceId", regionSourceId);
            context.getRequest().setAttribute("error-objectId", regionId);

            // force into "VIEW" mode
            context.setRenderMode(RenderMode.VIEW);
            
            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_REGION_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A region rendering problem was not able to be handled by the framework", ex);
        }
    }

    protected static void handleComponentRenderProblem(RenderContext context,
            Throwable t, String componentId) throws RequestDispatchException
    {
        // log the error
        context.getLogger().error("A Component Rendering problem was handled");
        context.getLogger().error("Component Id: " + componentId);
        context.getLogger().error(t);
        try
        {
            context.getRequest().setAttribute("error", t);
            context.getRequest().setAttribute("error-componentId", componentId);
            context.getRequest().setAttribute("error-objectId", componentId);

            // force into "VIEW" mode
            context.setRenderMode(RenderMode.VIEW);
            
            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_COMPONENT_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A component rendering problem was not able to be handled by the framework", ex);
        }
    }
}
