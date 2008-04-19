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

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.model.ModelObject;

/**
 * @author muzquiano
 */
public class ModelHelper
{
    public static final String PROP_HTMLID = "htmlid";
    
    public static void populateScriptModel(RequestContext context, Map<String, Object> model)
    {
        if(model == null || context == null)
        {
            return;
        }
        
        // populate url
        if(context instanceof HttpRequestContext)
        {
            HttpServletRequest request = ((HttpRequestContext)context).getRequest();
            
            URLHelper urlHelper = new URLHelper(request);
            model.put("url", urlHelper);
        }
    
        // page attributes
        if(context.getCurrentPage() != null)
        {
            model.put("description", context.getCurrentPage().getDescription());
            model.put("title", context.getCurrentPage().getName());
        }
        
        model.put("theme", ThemeUtil.getCurrentThemeId(context));
        
        // add in the web framework script objects
        model.put("site", new ScriptSite(context));
        if(context.getUser() != null)
        {
            model.put("user", new ScriptUser(context, context.getUser()));
        }
        //model.put("wizard", new ScriptWizard(context));
    }

    public static void populateTemplateModel(RequestContext context, Map<String, Object> model)
    {
        populateTemplateModel(context, null, model);
    }
    
    public static void populateTemplateModel(RequestContext context, ModelObject object, Map<String, Object> model)
    {
        if(model == null || context == null)
        {
            return;
        }

        // populate url
        if(context instanceof HttpRequestContext)
        {
            HttpServletRequest request = ((HttpRequestContext)context).getRequest();
            
            URLHelper urlHelper = new URLHelper(request);
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
            copyToModel(model, pageProperties);
        }
        
        // copy in custom properties from the template
        if(context.getCurrentTemplate() != null)
        {
            Map<String, Object> templateProperties = context.getCurrentTemplate().getCustomProperties();
            copyToModel(model, templateProperties);
        }
        
        // copy in custom properties from the passed in model object
        // this is usually a component
        if(object != null)
        {
            Map<String, Object> objectProperties = object.getCustomProperties();
            copyToModel(model, objectProperties);
            model.put(PROP_HTMLID, object.getId());
        }
        
        

        // other fixed elements
        model.put("theme", ThemeUtil.getCurrentThemeId(context));
        model.put("head", RenderUtil.PAGE_HEAD_DEPENDENCIES_STAMP);
        
        // TODO: I would like this code to look at the tag libraries
        // registered with the configuration, parse the tag classes
        // and auto-instantiate directives for tags.
        
        // This would allow for developers to create their own tag libraries
        // that work in JSP, Freemarker and any parsed content using a
        // single, consistent approach
        
        model.put("region", new RegionFreemarkerTagDirective(context));
        //addDirective(context, model, "region", "org.alfresco.web.site.taglib.RegionTag");
        //addDirective(context, model, "head", "org.alfresco.web.site.taglib.HeadTag");
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
    
    protected static void copyToModel(Map<String, Object> model, Map<String, Object> properties)
    {
        Iterator it = properties.keySet().iterator();
        while(it.hasNext())
        {
            String key = (String) it.next();
            Object value = properties.get(key);
            
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
