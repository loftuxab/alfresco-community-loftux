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

import org.alfresco.tools.WrappedHttpServletRequest;
import org.alfresco.tools.WrappedHttpServletResponse;
import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.exception.ComponentRenderException;
import org.alfresco.web.site.exception.JspRenderException;
import org.alfresco.web.site.exception.PageRenderException;
import org.alfresco.web.site.exception.RegionRenderException;
import org.alfresco.web.site.exception.TemplateRenderException;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.Template;
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
    public static void renderJspPage(RequestContext context, HttpServletRequest request, HttpServletResponse response, String dispatchPath) throws JspRenderException
    {
        try
        {
            // wrap the request and response
            WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
            WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);
            
            // do the include
            RequestUtil.include(wrappedRequest, wrappedResponse, dispatchPath);
            
            // generate the HEAD tag
            String headTags = generateHeader(context);
            
            // Now do a replace on all of the stamp placeholders
            String responseBody = wrappedResponse.getOutput();
            int i = -1;
            do {
                i = responseBody.indexOf(PAGE_HEAD_DEPENDENCIES_STAMP);
                if(i > -1)
                {
                    responseBody = responseBody.substring(0, i) + headTags + responseBody.substring(i+PAGE_HEAD_DEPENDENCIES_STAMP.length(), responseBody.length());
                }
            }
            while(i > -1);
            
            // Finally, commit the entire thing to the output stream
            response.getWriter().print(responseBody);
        }
        catch(Exception ex)
        {
            throw new JspRenderException("Unable to render JSP page", ex);
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
        if(page == null)
        {
            throw new PageRenderException("Unable to locate current page in request context");
        }
        Template currentTemplate = page.getTemplate(context);
        if(currentTemplate == null)
        {
            throw new PageRenderException("Unable to locate template for page: " + page.getId());
        }
        
        try
        {
            // Wrap the Request and Response
            WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(request);
            WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(response);
    
            // Execute the template        
            RenderUtil.renderTemplate(context, wrappedRequest, wrappedResponse,
                    currentTemplate.getId());
    
            // At this point, the template and all of the components
            // have executed.  We must now stamp the <!--${head}-->
            // onto the output.  To do so, we must first generate
            // the stamp.        
            String headTags = generateHeader(context);
            
            // Now do a replace on all of the stamp placeholders
            String responseBody = wrappedResponse.getOutput();
            int i = -1;
            do {
                i = responseBody.indexOf(PAGE_HEAD_DEPENDENCIES_STAMP);
                if(i > -1)
                {
                    responseBody = responseBody.substring(0, i) + headTags + responseBody.substring(i+PAGE_HEAD_DEPENDENCIES_STAMP.length(), responseBody.length());
                }
            }
            while(i > -1);
            
            // Finally, commit the entire thing to the output stream
            response.getWriter().print(responseBody);
        }
        catch(Exception ex)
        {
            throw new PageRenderException("An exception occurred while rendering page: " + page.getId(), ex);
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
        Template template = (Template) context.getModel().loadTemplate(
                context, templateId);
        if(template == null)
            throw new TemplateRenderException("Unable to locate template: " + templateId);

        try
        {
            // get the configuration for the template
            RuntimeConfig config = context.loadConfiguration(template);
            request.setAttribute("template-configuration", config);
    
            // get the renderer and execute it
            AbstractRenderer renderer = RendererFactory.newRenderer(context,
                    template.getTemplateType(context));
            renderer.execute(context, request, response, config);
        }
        catch(Exception ex)
        {
            throw new TemplateRenderException("An exception occurred while rendering template: " + templateId, ex);
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
        // get the template
        Template template = (Template) context.getModel().loadTemplate(
                context, templateId);
        if(template == null)
            throw new RegionRenderException("Unable to locate template: " + templateId);

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
    
            // determine the renderer
            // this can be overridden by a setting on either the layout
            // or the layout type instances
            // TODO: Do we want to keep this?
            String renderer = "/ui/core/region.jsp";
    
            // if there is already a component associated for this region,
            // we must let the region know      
            Component[] components = ModelUtil.findComponents(context,
                    regionScopeId, sourceId, regionId, null);
            if (components.length > 0)
            {
                Component component = components[0];
    
                // load config for this component
                RuntimeConfig componentConfig = context.loadConfiguration(component);
                request.setAttribute("component-configuration", componentConfig);
    
                // merge in the template data
                config.merge(componentConfig);
            }
    
            // do the render
            request.setAttribute("region-configuration", config);
            RequestUtil.include(request, response, renderer);
        }
        catch(Exception ex)
        {
            throw new RegionRenderException("Unable to render region: " + regionId, ex);
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
        Component component = context.getModel().loadComponent(context,
                componentId);
        if(component == null)
            throw new ComponentRenderException("Unable to locate component: " + componentId);
        
        try
        {
            // get the configuration for the component
            RuntimeConfig config = context.loadConfiguration(component);
            request.setAttribute("component-configuration", config);

            // build a renderer for this component type
            //ComponentType componentType = component.getComponentType(context);
            AbstractRenderer renderer = RendererFactory.newRenderer(context,
                    component);
            renderer.execute(context, request, response, config);
        }
        catch(Exception ex)
        {
            throw new ComponentRenderException("An exception occurred while rendering component: " + componentId, ex);
            
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
            HttpServletResponse response, String pageId, String formatId)
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
        if(list == null)
        {
            list = new ArrayList();
            context.setValue(RequestContext.VALUE_HEAD_TAGS, list);
        }
        return list;
    }
    
    
    
    
    
       

    public static String renderScriptImport(HttpServletRequest request, String src)
    {
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 4)
            src = src + "?" + queryString;
        
        return renderScriptImport(src);
    }

    public static String renderScriptImport(String src)
    {
        // make sure references resolve to the configured servlet
        src = URLUtil.toBrowserUrl(src);
        
        return "<script type=\"text/javascript\" src=\"" + src + "\"></script>";
    }

    public static String renderLinkImport(String href)
    {
        return renderLinkImport(href, null);
    }

    public static String renderLinkImport(String href, String id)
    {
        // make sure references resolve to the configured servlet
        href = URLUtil.toBrowserUrl(href);

        String value = "<link ";
        if (id != null)
            value += "id=\"" + id + "\" ";
        value += "rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\"></link>";
        
        return value;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    protected static String getSourceId(RequestContext context, String scopeId)
    {
        // rendering objects
        Page page = context.getCurrentPage();
        Template template = context.getCurrentTemplate();

        // get the component association in that scope
        String sourceId = null;
        if ("site".equalsIgnoreCase(scopeId))
            sourceId = "site";
        if ("template".equalsIgnoreCase(scopeId))
            sourceId = template.getId();
        if ("page".equalsIgnoreCase(scopeId))
            sourceId = page.getId();

        return sourceId;
    }

    protected static void appendBuffer(StringBuffer buffer, String toAppend)
    {
        buffer.append(toAppend);
        buffer.append("\r\n"); // cosmetic
    }
    
    // TODO: Introduce some caching for this
    protected static String generateHeader(RequestContext context)
    {
        StringBuffer buffer = new StringBuffer();
        appendBuffer(buffer, "");

        // CSS
        appendBuffer(buffer, renderLinkImport("/extjs/resources/css/ext-all.css"));
        appendBuffer(buffer, renderLinkImport("/ui/themes/builder/css/builder-default.css"));

        // Theme CSS
        String currentThemeId = ThemeUtil.getCurrentThemeId(context);
        appendBuffer(buffer, renderLinkImport("/ui/themes/extjs/css/xtheme-" + currentThemeId + ".css", "extjs-theme-link"));
        appendBuffer(buffer, renderLinkImport("/ui/themes/builder/css/builder-" + currentThemeId + ".css", "builder-theme-link"));

        // ExtJS things
        appendBuffer(buffer, renderScriptImport("/extjs/adapter/ext/ext-base.js"));
        appendBuffer(buffer, renderScriptImport("/extjs/ext-all.js"));

        // Custom JS things
        appendBuffer(buffer, renderScriptImport("/ui/builder/utils/miframe-min.js"));
        appendBuffer(buffer, renderScriptImport("/ui/builder/utils/json.js"));
        appendBuffer(buffer, renderScriptImport("/ui/builder/dynamic.js.jsp"));
        appendBuffer(buffer, renderScriptImport("/ui/builder/incontext.js.jsp"));
        
        // Web Components (in progress)
        appendBuffer(buffer, renderScriptImport("/ui/builder/wizard-core.js"));
        appendBuffer(buffer, renderScriptImport("/ui/builder/wizard-adapter-extjs.js"));
        appendBuffer(buffer, renderScriptImport("/ui/builder/application.js"));
        appendBuffer(buffer, renderScriptImport("/ui/builder/builder.js"));

        // YUI things (for menus)
        // This will be replaced by imports required specifically by
        // the nav component
        // and of course, the nav component will be rewritten
        // to do away with YUI altogether!
        //
        //importScript("/yui/build/yahoo-dom-event/yahoo-dom-event.js", false);
        //importScript("/yui/build/animation/animation-min.js", false);
        //importScript("/yui/build/container/container-min.js", false);
        //importScript("/yui/build/menu/menu.js", false);

        // Now import the stuff that the components on the page needed us to import
        List tagsList = RenderUtil.getHeadTags(context);
        for(int i = 0; i < tagsList.size(); i++)
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
    

    public static String DEFAULT_ALFRESCO_ENDPOINT_ID = "alfresco";
    public static String PAGE_HEAD_DEPENDENCIES_STAMP = "<!--${head}-->";
}
