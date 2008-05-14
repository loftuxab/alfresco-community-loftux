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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.exception.ComponentChromeRenderException;
import org.alfresco.web.site.exception.JspRenderException;
import org.alfresco.web.site.exception.PageRenderException;
import org.alfresco.web.site.exception.RegionRenderException;
import org.alfresco.web.site.exception.RequestDispatchException;
import org.alfresco.web.site.exception.TemplateRenderException;

/**
 * This class basically delegates through to the RenderUtil
 * functions but provides for a 'pretty' user experience.
 * 
 * Exceptions that trickle back are trapped and presented nicely
 * to the end user.  The exceptions are also logged so that
 * administrators can track down the issues at hand.
 * 
 * @author Uzquiano
 *
 */
public class PresentationUtil
{
    /**
     * Renders a JSP page
     * 
     * @param context
     * @param request
     * @param response
     * @param dispatchPath
     */
    public static void renderJspPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String dispatchPath) throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderJspPage(context, request, response, dispatchPath);
        }
        catch (JspRenderException ex)
        {
            handlePageRenderProblem(context, request, response, ex,
                    dispatchPath);
        }
    }

    /**
     * Renders the given page
     * 
     * @param context
     * @param request
     * @param response
     * @param pageId
     */
    public static void renderPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response, String pageId) 
            throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderPage(context, request, response, pageId);
        }
        catch (PageRenderException ex)
        {
            handlePageRenderProblem(context, request, response, ex,
                    context.getCurrentPage().getId());
        }
    }
    
    /**
     * Renders the current page
     * 
     * @param context
     * @param request
     * @param response
     */
    public static void renderPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response) 
            throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderPage(context, request, response);
        }
        catch (PageRenderException ex)
        {
            handlePageRenderProblem(context, request, response, ex,
                    context.getCurrentPage().getId());
        }
    }

    /**
     * Renders a given template
     * 
     * @param context
     * @param request
     * @param response
     * @param templateId
     */
    public static void renderTemplate(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String templateId) throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderTemplate(context, request, response, templateId);
        }
        catch (TemplateRenderException ex)
        {
            handlePageRenderProblem(context, request, response, ex, templateId);
        }
    }

    public static void renderChromelessRegion(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String templateId, String regionId, String regionScopeId) 
            throws RequestDispatchException 
    {
        renderRegion(context, request, response, templateId, regionId, regionScopeId, WebFrameworkConstants.CHROMELESS_REGION_CHROME_ID);
    }

    public static void renderRegion(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String templateId, String regionId, String regionScopeId) 
            throws RequestDispatchException 
    {
        renderRegion(context, request, response, templateId, regionId, regionScopeId, null);
    }
    
    public static void renderRegion(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String templateId, String regionId, String regionScopeId, String overrideChromeId) 
            throws RequestDispatchException 
    {
        try
        {
            RenderUtil.renderRegion(context, request, response, templateId,
                    regionId, regionScopeId, overrideChromeId);
        }
        catch (RegionRenderException ex)
        {
            handleRegionRenderProblem(context, request, response, ex,
                    templateId, regionId, regionScopeId);
        }
    }

    public static void renderChromelessComponent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String componentId) throws RequestDispatchException
    {
        renderComponent(context, request, response, componentId, WebFrameworkConstants.CHROMELESS_COMPONENT_CHROME_ID);
    }
    
    public static void renderComponent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String componentId) throws RequestDispatchException
    {
        renderComponent(context, request, response, componentId, null);
    }

    public static void renderComponent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String componentId, String chromeId) throws RequestDispatchException
    {
        try
        {
            RenderUtil.renderComponent(context, request, response, componentId, chromeId);
        }
        catch (ComponentChromeRenderException ex)
        {
            handleComponentRenderProblem(context, request, response, ex,
                    componentId);
        }
    }
    
    // pretty methods

    protected static void handlePageRenderProblem(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            Throwable t, String pageId) throws RequestDispatchException
    {
        // log the error
        context.getLogger().error("A Page Rendering problem was handled");
        context.getLogger().error("Page Id: " + pageId);
        context.getLogger().error("Trace", t);
        try
        {
            request.setAttribute("error", t);
            request.setAttribute("error-pageId", pageId);
            request.setAttribute("error-objectId", pageId);

            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, request, response, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_PAGE_ERROR,
                    WebFrameworkConstants.DEFAULT_DISPATCHER_HANDLER_PAGE_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A page rendering problem was not able to be handled by the framework", ex);
        }
    }

    protected static void handleTemplateRenderProblem(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            Throwable t, String templateId) throws RequestDispatchException
    {
        // log the error
        context.getLogger().error("A Template Rendering problem was handled");
        context.getLogger().error("Template Id: " + templateId);
        context.getLogger().error(t);
        try
        {
            request.setAttribute("error", t);
            request.setAttribute("error-templateId", templateId);
            request.setAttribute("error-objectId", templateId);

            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, request, response, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_TEMPLATE_ERROR,
                    WebFrameworkConstants.DEFAULT_DISPATCHER_HANDLER_TEMPLATE_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A template rendering problem was not able to be handled by the framework", ex);
        }
    }

    protected static void handleRegionRenderProblem(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
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
            request.setAttribute("error", t);
            request.setAttribute("error-templateId", templateId);
            request.setAttribute("error-regionId", regionId);
            request.setAttribute("error-regionScopeId", regionScopeId);
            request.setAttribute("error-objectId", regionId);

            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, request, response, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_REGION_ERROR,
                    WebFrameworkConstants.DEFAULT_DISPATCHER_HANDLER_REGION_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A region rendering problem was not able to be handled by the framework", ex);
        }
    }

    protected static void handleComponentRenderProblem(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            Throwable t, String componentId) throws RequestDispatchException
    {
        // log the error
        context.getLogger().error("A Component Rendering problem was handled");
        context.getLogger().error("Component Id: " + componentId);
        context.getLogger().error(t);
        try
        {
            request.setAttribute("error", t);
            request.setAttribute("error-componentId", componentId);
            request.setAttribute("error-objectId", componentId);

            // allow the framework to handle things gracefully
            RenderUtil.renderErrorHandlerPage(context, request, response, 
                    WebFrameworkConstants.DISPATCHER_HANDLER_COMPONENT_ERROR,
                    WebFrameworkConstants.DEFAULT_DISPATCHER_HANDLER_COMPONENT_ERROR);
        }
        catch (Exception ex)
        {
            throw new RequestDispatchException("A component rendering problem was not able to be handled by the framework", ex);
        }
    }
}
