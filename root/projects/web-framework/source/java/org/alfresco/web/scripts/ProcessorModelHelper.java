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
package org.alfresco.web.scripts;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RenderData;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.model.ModelObject;

/**
 * @author muzquiano
 */
public class ProcessorModelHelper
{
    public static final String PROP_HTMLID = "htmlid";
    
    public static void populateScriptModel(RequestContext context, RenderData renderData, Map<String, Object> model)
    {
        if(model == null || context == null)
        {
            return;
        }
        
        // populate url
        if(context instanceof HttpRequestContext)
        {
            HttpServletRequest request = ((HttpRequestContext)context).getRequest();
            
            Map<String, String> args = new HashMap<String, String>(request.getParameterMap().size(), 1.0f);
            Enumeration names = request.getParameterNames();
            while (names.hasMoreElements())
            {
                String name = (String)names.nextElement();
                args.put(name, request.getParameter(name));
            }
            
            URLHelper urlHelper = new URLHelper(request, args);
            model.put("url", urlHelper);
        }
    
        // page attributes
        if(context.getCurrentPage() != null)
        {
            model.put("description", context.getCurrentPage().getDescription());
            model.put("title", context.getCurrentPage().getName());
        }
        
        // copy in custom properties from the page
        if(context.getCurrentPage() != null)
        {
            Map<String, Object> pageProperties = context.getCurrentPage().getCustomProperties();
            copyToModel(model, pageProperties, null);
        }
        
        // copy in custom properties from the template
        if(context.getCurrentTemplate() != null)
        {
            Map<String, Object> templateProperties = context.getCurrentTemplate().getCustomProperties();
            copyToModel(model, templateProperties, null);
        }
        
        // we are also given the "rendering configuration" for the current
        // object.  usually, this is either a component or a template.
        // in either case, the configuration is set up ahead of time
        // our job here is to make sure that freemarker has everything
        // it needs for the component or template to process
        if(renderData != null)
        {
            ModelObject object = renderData.getObject();
            if(object != null)
            {
                Map<String, Object> objectProperties = object.getCustomProperties();
                copyToModel(model, objectProperties, null);
            }
            
            String htmlBindingId = (String) renderData.get(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID);
            if(htmlBindingId != null && htmlBindingId.length() > 0)
            {
                model.put(PROP_HTMLID, htmlBindingId);
            }
            
            // copy in render data settings
            copyToModel(model, renderData.map(), null);
        }
                
        model.put("theme", ThemeUtil.getCurrentThemeId(context));
        
        // add in the web framework script objects
        model.put("site", new ScriptSite(context));
        if(context.getUser() != null)
        {
            model.put("user", new ScriptUser(context, context.getUser()));
        }
    }

    public static void populateTemplateModel(RequestContext context, Map<String, Object> model)
    {
        populateTemplateModel(context, null, model);
    }
    
    public static void populateTemplateModel(RequestContext context, RenderData renderData, Map<String, Object> model)
    {
        if(model == null || context == null)
        {
            return;
        }

        // populate url
        if(context instanceof HttpRequestContext)
        {
            HttpServletRequest request = ((HttpRequestContext)context).getRequest();
            
            Map<String, String> args = new HashMap<String, String>(request.getParameterMap().size(), 1.0f);
            Enumeration names = request.getParameterNames();
            while (names.hasMoreElements())
            {
                String name = (String)names.nextElement();
                args.put(name, request.getParameter(name));
            }
            
            URLHelper urlHelper = new URLHelper(request, args);
            model.put("url", urlHelper);
        }
    
        // fixed page attributes
        if(context.getCurrentPage() != null)
        {
            model.put("title", context.getCurrentPage().getTitle());
            model.put("description", context.getCurrentPage().getDescription());
        }
        
        // copy in custom properties from the page
        if(context.getCurrentPage() != null)
        {
            Map<String, Object> pageProperties = context.getCurrentPage().getCustomProperties();
            copyToModel(model, pageProperties, null);
        }
        
        // copy in custom properties from the template
        if(context.getCurrentTemplate() != null)
        {
            Map<String, Object> templateProperties = context.getCurrentTemplate().getCustomProperties();
            copyToModel(model, templateProperties, null);
        }
        
        // we are also given the "rendering configuration" for the current
        // object.  usually, this is either a component or a template.
        // in either case, the configuration is set up ahead of time
        // our job here is to make sure that freemarker has everything
        // it needs for the component or template to process
        if(renderData != null)
        {
            ModelObject object = renderData.getObject();
            if(object != null)
            {
                Map<String, Object> objectProperties = object.getCustomProperties();
                copyToModel(model, objectProperties, null);
            }
            
            String htmlBindingId = (String) renderData.get(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID);
            if(htmlBindingId != null && htmlBindingId.length() > 0)
            {
                model.put(PROP_HTMLID, htmlBindingId);
            }
            
            // copy in render data settings
            copyToModel(model, renderData.map(), null);
        }
        

        // other fixed elements
        model.put("theme", ThemeUtil.getCurrentThemeId(context));
        model.put("head", WebFrameworkConstants.PAGE_HEAD_DEPENDENCIES_STAMP);
        
        // TODO: I would like this code to look at the tag libraries
        // registered with the configuration, parse the tag classes
        // and auto-instantiate directives for tags.
        
        // This would allow for developers to create their own tag libraries
        // that work in JSP, Freemarker and any parsed content using a
        // single, consistent approach
        
        model.put("region", new RegionFreemarkerTagDirective(context));
        model.put("component", new ComponentFreemarkerTagDirective(context));
        addDirective(context, model, "componentInclude", "org.alfresco.web.site.taglib.ComponentIncludeTag");
        addDirective(context, model, "pageTitle", "org.alfresco.web.site.taglib.PageTitleTag");
        addDirective(context, model, "require", "org.alfresco.web.site.taglib.RequireTag");
        
        // content specific
        addDirective(context, model, "anchor", "org.alfresco.web.site.taglib.ObjectAnchorTag");
        addDirective(context, model, "edit", "org.alfresco.web.site.taglib.ObjectEditTag");
        addDirective(context, model, "print", "org.alfresco.web.site.taglib.ObjectPrintTag");
        addDirective(context, model, "link", "org.alfresco.web.site.taglib.ObjectLinkTag");
        
        // temporary: add floating menu
        addDirective(context, model, "floatingMenu", "org.alfresco.web.site.taglib.FloatingMenuTag");        
        
        // add in the web framework script objects
        model.put("site", new ScriptSite(context));
        if(context.getUser() != null)
        {
            model.put("user", new ScriptUser(context, context.getUser()));
        }
    }    
    
    protected static void copyToModel(Map<String, Object> model, Map<String, Object> properties, String prefix)
    {
        Iterator it = properties.keySet().iterator();
        while(it.hasNext())
        {
            String key = (String) it.next();
            Object value = properties.get(key);
            
            if(prefix != null)
            {
                key = prefix + key;
            }
            
            model.put(key, value);
            
            // NOTE: Fix this
            // why do i have to do this?
            // Freemarker TemplateProcessor ObjectWrapper thinks of all values as non numerical...
            // Thus, have to force numerical... very strange.
            // Kevin's approach seems not to have to do this
            // Not sure why the disparity exists
            try {
                Integer a = Integer.valueOf((String)value);
                model.put(key, a);
            }catch(Exception ex) { }
        }
        //model.putAll(customProperties);        
    }
    
    protected static void addDirective(RequestContext context, Map<String, Object> model, String name, String className)
    {
        GenericFreemarkerTagDirective directive = new GenericFreemarkerTagDirective(context, name, className);
        model.put(name, directive);
    }
}
