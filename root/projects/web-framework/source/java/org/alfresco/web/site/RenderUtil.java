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

import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.Endpoint;
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
            String templateId) throws Exception
    {
        Template template = (Template) context.getModelManager().loadTemplate(
                context, templateId);

        // get the configuration for the template
        RuntimeConfig config = context.loadConfiguration(template);
        request.setAttribute("template-configuration", config);

        // get the renderer and execute it
        AbstractRenderer renderer = RendererFactory.newRenderer(context,
                template.getTemplateType(context));
        renderer.execute(context, request, response, config);
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
            String componentId) throws Exception
    {
        Component component = context.getModelManager().loadComponent(context,
                componentId);
        if (component != null)
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
            throws Exception
    {
        // get the template
        Template template = (Template) context.getModelManager().loadTemplate(
                context, templateId);

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

            // merge in the tempalte data
            config.merge(componentConfig);
        }

        // do the render
        request.setAttribute("region-configuration", config);
        RequestUtil.include(request, response, renderer);
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

    public static String toBrowserUrl(String rootRelativeUri)
    {
        if (rootRelativeUri == null)
            return "";

        // special case: "/"
        if (rootRelativeUri.equals("/"))
        {
            // the browser friendly url is just the preconfigured servlet
            // (i.e. /myapp/)
            String newUri = Framework.getConfig().getDefaultServletUri();
            return newUri;
        }

        // if it starts with "/", strip it off
        if (rootRelativeUri.startsWith("/"))
            rootRelativeUri = rootRelativeUri.substring(1,
                    rootRelativeUri.length());

        // now build the browser friendly ur
        String defaultUri = Framework.getConfig().getDefaultServletUri();
        String newUri = defaultUri + rootRelativeUri;
        return newUri;
    }

    public static String getContentEditURL(RequestContext context,
            String endpointId, String itemRelativePath)
    {
        // use default endpoint id if none specified
        if (endpointId == null)
            endpointId = DEFAULT_ALFRESCO_ENDPOINT_ID;

        // get the endpoint
        Endpoint endpoint = context.getModelManager().loadEndpoint(context,
                endpointId);

        // if the endpoint isn't found, just exit
        if (endpoint == null)
        {
            context.getLogger().debug("RenderUtil.getContentEditURL failed");
            context.getLogger().debug("Unable to find endpoint: " + endpointId);
            return "";
        }

        // endpoint settings
        String host = endpoint.getSetting("host");
        String port = endpoint.getSetting("port");
        String sandbox = context.getStoreId();
        String uri = "/alfresco/service/ads/redirect/incontext/" + sandbox + "/";

        // build the url
        String path = sandbox + ":/www/avm_webapps/ROOT" + itemRelativePath;
        String url = "http://" + host + ":" + port + uri + "?sandbox=" + sandbox + "&path=" + path + "&container=plain";

        return url;
    }

    public static String DEFAULT_ALFRESCO_ENDPOINT_ID = "alfresco";
}
