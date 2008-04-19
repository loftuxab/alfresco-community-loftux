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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.tools.FakeHttpServletResponse;
import org.alfresco.tools.WrappedHttpServletRequest;
import org.alfresco.tools.WrappedHttpServletResponse;
import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.config.RuntimeConfigManager;
import org.alfresco.web.site.exception.ComponentRenderException;
import org.alfresco.web.site.exception.JspRenderException;
import org.alfresco.web.site.exception.PageRenderException;
import org.alfresco.web.site.exception.RegionRenderException;
import org.alfresco.web.site.exception.TemplateRenderException;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.TemplateInstance;
import org.alfresco.web.site.renderer.AbstractRenderer;
import org.alfresco.web.site.renderer.RendererFactory;

/**
 * @author muzquiano
 */
public class RenderUtil
{
    /**
     * Renders a given JSP page.
     * 
     * This wraps the JSP rendering in servlet wrappers and will
     * do variable substitution on HEAD tags.
     * 
     * This method should really only be used for top-level page
     * elements (i.e. the first dispatch to a JSP page).
     * 
     * If you use it for downstream JSP includes, it will work fine,
     * but it will be less efficient.  For each call to this method,
     * there exists some extra overhead for the wrapping/unwrapping
     * of servlet objects and substitution within response text.
     * 
     * @param context
     * @param request
     * @param response
     * @param dispatchPath
     * @throws Exception
     */
    public static void renderJspPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String dispatchPath) throws JspRenderException
    {
        // start a timer
        Timer.start(request, "RenderJspPage-" + dispatchPath);
        
        try
        {
            // wrap the request and response
            WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(
                    request);
            WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(
                    response);

            // do the include
            RequestUtil.include(wrappedRequest, wrappedResponse, dispatchPath);

            // generate the HEAD tag
            String headTags = generateHeader(context, request, response);

            // Now do a replace on all of the stamp placeholders
            //String responseBody = wrappedResponse.getOutput();
            String responseBody = wrappedResponse.getOutput();
            int i = -1;
            do
            {
                i = responseBody.indexOf(PAGE_HEAD_DEPENDENCIES_STAMP);
                if (i > -1)
                {
                    responseBody = responseBody.substring(0, i) + headTags + responseBody.substring(
                            i + PAGE_HEAD_DEPENDENCIES_STAMP.length(),
                            responseBody.length());
                }
            }
            while (i > -1);

            // Finally, commit the entire thing to the output stream
            response.getWriter().print(responseBody);
        }
        catch (Exception ex)
        {
            throw new JspRenderException("Unable to render JSP page", ex);
        }
        finally
        {
            Timer.stop(request, "RenderJspPage-" + dispatchPath);
        }
    }

    /**
     * Renders the current page instance.
     */
    public static void renderPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response)
            throws PageRenderException
    {
        Page page = context.getCurrentPage();
        if (page == null)
        {
            throw new PageRenderException(
                    "Unable to locate current page in request context");
        }
        
        renderPage(context, request, response, page.getId());
    }
    
    /**
     * Renders a given page instance
     */
    public static void renderPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String pageId) throws PageRenderException
    {
        // start a timer
        Timer.start(request, "RenderPage-" + pageId);
        
        // look up the page
        Page page = (Page) context.getModel().loadPage(context, pageId);
        if (page == null)
            throw new PageRenderException(
                    "Unable to locate page: " + pageId);

        // look up the page template
        TemplateInstance currentTemplate = page.getTemplate(context);
        if (currentTemplate == null)
        {
            throw new PageRenderException(
                    "Unable to locate template for page: " + pageId);
        }

        // do our thing
        try
        {
            // Wrap the Request and Response
            WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(
                    request);
            WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(
                    response);

            // Execute the template        
            RenderUtil.renderTemplate(context, wrappedRequest, wrappedResponse,
                    currentTemplate.getId());

            // At this point, the template and all of the components
            // have executed.  We must now stamp the <!--${head}-->
            // onto the output.  To do so, we must first generate
            // the stamp.        
            String headTags = generateHeader(context, request, response);

            // Now do a replace on all of the stamp placeholders
            String responseBody = wrappedResponse.getOutput();
            int i = -1;
            do
            {
                i = responseBody.indexOf(PAGE_HEAD_DEPENDENCIES_STAMP);
                if (i > -1)
                {
                    responseBody = responseBody.substring(0, i) + headTags + responseBody.substring(
                            i + PAGE_HEAD_DEPENDENCIES_STAMP.length(),
                            responseBody.length());
                }
            }
            while (i > -1);

            // Finally, commit the entire thing to the output stream
            response.getWriter().print(responseBody);
        }
        catch (Exception ex)
        {
            throw new PageRenderException(
                    "An exception occurred while rendering page: " + page.getId(),
                    ex);
        }
        finally
        {
            Timer.stop(request, "RenderPage-" + page.getId());
        }
    }

    /**
     * Renders a given template instance.  This fetches the abstract renderer
     * instance for the given template's type and then binds configuration data
     * to the rendering engine.  It then executes the template.
     * 
     * @param context
     * @param request
     * @param response
     * @param templateId
     * @throws Exception
     */
    public static void renderTemplate(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String templateId) throws TemplateRenderException
    {
        // start a timer
        Timer.start(request, "RenderTemplate-" + templateId);

        TemplateInstance template = (TemplateInstance) context.getModel().loadTemplate(context,
                templateId);
        if (template == null)
            throw new TemplateRenderException(
                    "Unable to locate template: " + templateId);
        try
        {
            // get the configuration for the template
            RuntimeConfig config = context.loadConfiguration(template);
            request.setAttribute("template-configuration", config);

            // get the renderer and execute it
            AbstractRenderer renderer = RendererFactory.newRenderer(context, template);
            renderer.execute(context, request, response, config);
        }
        catch (Exception ex)
        {
            throw new TemplateRenderException(
                    "An exception occurred while rendering template: " + templateId,
                    ex);
        }
        finally
        {
            Timer.stop(request, "RenderTemplate-" + templateId);            
        }
    }

    /**
     * Renders a region for a given template.
     *  
     * @param context
     * @param request
     * @param response
     * @param templateId
     * @param regionId
     * @param regionScopeId
     * @throws Exception
     */
    public static void renderRegion(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String templateId, String regionId, String regionScopeId)
            throws RegionRenderException
    {
        // start a timer
        Timer.start(request, "RenderRegion-" + templateId+"-"+regionId+"-"+regionScopeId);
        
        // get the template
        TemplateInstance template = (TemplateInstance) context.getModel().loadTemplate(context,
                templateId);
        if (template == null)
            throw new RegionRenderException(
                    "Unable to locate template: " + templateId);

        try
        {
            // load the baseline configuration for this template
            RuntimeConfig config = context.loadConfiguration(template);
            request.setAttribute("template-configuration", config);

            // build the configuration for this region
            config.put("region-id", regionId);
            config.put("region-scope-id", regionScopeId);
            String sourceId = getSourceId(context, regionScopeId);
            config.put("region-source-id", sourceId);

            // determine the region renderer
            // this is set in the configuration
            String renderer = context.getConfig().getPresentationContainerURI(WebFrameworkConstants.PRESENTATION_CONTAINER_REGION);
            if(renderer == null || "".equals(renderer))
            {
                renderer = WebFrameworkConstants.DEFAULT_CONTAINER_URI_REGION;
            }
            // Allow this to be overridden by the template
            // store the setting "region-regionName-renderer" with value "/my/jsppage.jsp"
            String rendererOverride = template.getSetting("region-" + regionId + "-renderer");
            if(rendererOverride != null && !"".equals(rendererOverride))
            {
                renderer = rendererOverride;
            }

            // if there is already a component associated for this region,
            // we must let the region know
//            System.out.println("For region: " + regionId);
//            System.out.println(" -> looking scopeId: " + regionScopeId);
//            System.out.println(" -> looking sourceId: " + sourceId);
            Component[] components = ModelUtil.findComponents(context,
                    regionScopeId, sourceId, regionId, null);
//            System.out.println("FOUND " + components.length);
            if (components.length > 0)
            {
                Component component = components[0];

                // load config for this component
                RuntimeConfig componentConfig = context.loadConfiguration(component);
                request.setAttribute("component-configuration", componentConfig);

                // merge in the template data
                config.merge(componentConfig);
            }
            else
            {
                // if we couldn't find a component, then redirect to a
                // region "no-component" page
                renderer = context.getConfig().getPresentationContainerURI(WebFrameworkConstants.PRESENTATION_CONTAINER_REGION_NO_COMPONENT);
                if(renderer == null)
                {
                    renderer = WebFrameworkConstants.DEFAULT_CONTAINER_URI_REGION_NO_COMPONENT;
                }
            }

            // do the render
            request.setAttribute("region-configuration", config);
            RequestUtil.include(request, response, renderer);
        }
        catch (Exception ex)
        {
            throw new RegionRenderException(
                    "Unable to render region: " + regionId, ex);
        }
        finally
        {
            Timer.stop(request, "RenderRegion-" + templateId+"-"+regionId+"-"+regionScopeId);
        }
    }

    /**
     * Renders a given component instance.  This fetches the abstract renderer
     * instance for the given component's type and then binds configuration data
     * to the rendering engine.  It then executes the component.
     * 
     * @param context
     * @param request
     * @param response
     * @param componentId
     * @throws Exception
     */
    public static void renderComponent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String componentId) throws ComponentRenderException
    {
        // start a timer
        Timer.start(request, "RenderComponent-" + componentId);
        
        Component component = context.getModel().loadComponent(context,
                componentId);
        if (component == null)
            throw new ComponentRenderException(
                    "Unable to locate component: " + componentId);

        try
        {
            // get the configuration for the component
            RuntimeConfig config = context.loadConfiguration(component);
            request.setAttribute("component-configuration", config);

            // build a renderer for this component
            AbstractRenderer renderer = RendererFactory.newRenderer(context,
                    component);
            renderer.execute(context, request, response, config);
        }
        catch (Exception ex)
        {
            throw new ComponentRenderException(
                    "An exception occurred while rendering component: " + componentId,
                    ex);

        }
        finally
        {
            Timer.stop(request, "RenderComponent-" + componentId);
        }
    }

    /**
     * Renders the fully formed URL string fo
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
                ex.printStackTrace();
            }
        }
    }

    public static void content(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String objectId, String formatId)
    {
        String pageId = context.getCurrentPage().getId();
        String url = context.getLinkBuilder().page(context, pageId, formatId);
        if (url != null)
        {
            try
            {
                response.getWriter().write(url);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static void appendHeadTags(RequestContext context, String tags)
    {
        getHeadTags(context).add(tags);
    }

    public static List getHeadTags(RequestContext context)
    {
        List list = (List) context.getValue(RequestContext.VALUE_HEAD_TAGS);
        if (list == null)
        {
            list = new ArrayList();
            context.setValue(RequestContext.VALUE_HEAD_TAGS, list);
        }
        return list;
    }

    public static String renderScriptImport(RequestContext context, String src)
    {
        if (context instanceof HttpRequestContext)
        {
            HttpServletRequest request = (HttpServletRequest) ((HttpRequestContext) context).getRequest();
            return renderScriptImport(request, src);
        }
        return null;
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
        if (context instanceof HttpRequestContext)
        {
            HttpServletRequest request = (HttpServletRequest) ((HttpRequestContext) context).getRequest();
            return renderLinkImport(request, href, null, true);
        }
        return null;
    }

    public static String renderLinkImport(RequestContext context, String href,
            String id)
    {
        if (context instanceof HttpRequestContext)
        {
            HttpServletRequest request = (HttpServletRequest) ((HttpRequestContext) context).getRequest();
            return renderLinkImport(request, href, id, true);
        }
        return null;
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

    protected static String getSourceId(RequestContext context, String scopeId)
    {
        // rendering objects
        Page page = context.getCurrentPage();
        TemplateInstance template = context.getCurrentTemplate();

        // get the component association in that scope
        String sourceId = null;
        if (REGION_SCOPE_GLOBAL.equalsIgnoreCase(scopeId))
            sourceId = REGION_SCOPE_GLOBAL;
        if (REGION_SCOPE_TEMPLATE.equalsIgnoreCase(scopeId))
            sourceId = template.getId();
        if (REGION_SCOPE_PAGE.equalsIgnoreCase(scopeId))
            sourceId = page.getId();

        return sourceId;
    }
    
    public static final String REGION_SCOPE_GLOBAL   = "global";
    public static final String REGION_SCOPE_TEMPLATE = "template";
    public static final String REGION_SCOPE_PAGE     = "page";
 

    protected static void appendBuffer(StringBuffer buffer, String toAppend)
    {
        buffer.append(toAppend);
        buffer.append("\r\n"); // cosmetic
    }

    // TODO: Introduce some caching for this
    protected static String generateHeader(RequestContext context, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        StringBuffer buffer = new StringBuffer();
        appendBuffer(buffer, "");

        /**
         * This is a work in progress.  Still not sure what the best
         * way is to define a "global" include.
         * 
         * With this approach, allow a global.head.renderer.xml file
         * to live as a Configuration object.
         * 
         * If this file is available, it is automatically read
         * and the renderer described therein is executed.
         */
        Configuration config = context.getModel().loadConfiguration(context, "global.head.renderer");
        if(config != null)
        {
            // renderer properties
            String rendererType = config.getProperty("renderer-type");
            String renderer = config.getProperty("renderer");
            
            // execute renderer
            String tags = processRenderer(context, request, response, rendererType, renderer);
            appendBuffer(buffer, tags);
        }
        
        // Now import the stuff that the components on the page needed us to import
        List tagsList = RenderUtil.getHeadTags(context);
        for (int i = 0; i < tagsList.size(); i++)
        {
            String tags = (String) tagsList.get(i);
            appendBuffer(buffer, tags);
        }

        return buffer.toString();
    }

    protected static void print(HttpServletResponse response, String str)
            throws IOException
    {
        response.getWriter().print(str);
    }
    
    protected static String processRenderer(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String rendererType, String renderer) throws Exception
    {
        // wrap the request and response
        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(
                request);
        FakeHttpServletResponse fakeResponse = new FakeHttpServletResponse();
        
        // build a configuration instance
        RuntimeConfig config = RuntimeConfigManager.newConfiguration(context);

        // build a renderer for this component
        AbstractRenderer rendererInstance = RendererFactory.newRenderer(context, rendererType, renderer); 
        rendererInstance.execute(context, wrappedRequest, fakeResponse, config);
        
        // return the result
        return fakeResponse.getContentAsString();        
    }

    public static String DEFAULT_ALFRESCO_ENDPOINT_ID = "alfresco";
    public static String PAGE_HEAD_DEPENDENCIES_STAMP = "<!--${head}-->";
}
