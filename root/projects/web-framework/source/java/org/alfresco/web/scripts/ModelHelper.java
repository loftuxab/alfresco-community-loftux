package org.alfresco.web.scripts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;

public class ModelHelper
{
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
        model.put("description", context.getCurrentPage().getDescription());
        model.put("title", context.getCurrentPage().getName());
        model.put("theme", ThemeUtil.getCurrentThemeId(context));
    }

    public static void populateTemplateModel(RequestContext context, Map<String, Object> model)
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
        model.put("description", context.getCurrentPage().getDescription());
        model.put("title", context.getCurrentPage().getName());
        model.put("theme", ThemeUtil.getCurrentThemeId(context));
    
        // TODO: I would like this code to look at the tag libraries
        // registered with the configuration, parse the tag classes
        // and auto-instantiate directives for tags.
        
        // This would allow for developers to create their own tag libraries
        // that work in JSP, Freemarker and any parsed content using a
        // single, consistent approach
        
        addDirective(context, model, "region", "org.alfresco.web.site.taglib.RegionTag");
        addDirective(context, model, "head", "org.alfresco.web.site.taglib.HeadTag");
        addDirective(context, model, "pageTitle", "org.alfresco.web.site.taglib.PageTitleTag");
        addDirective(context, model, "require", "org.alfresco.web.site.taglib.RequireTag");
        
        addDirective(context, model, "anchor", "org.alfresco.web.site.taglib.ObjectAnchorTag");
        addDirective(context, model, "edit", "org.alfresco.web.site.taglib.ObjectEditTag");
        addDirective(context, model, "print", "org.alfresco.web.site.taglib.ObjectPrintTag");
        addDirective(context, model, "link", "org.alfresco.web.site.taglib.ObjectLinkTag");
        
        // temporary: add floating menu
        addDirective(context, model, "floatingMenu", "org.alfresco.web.site.taglib.FloatingMenuTag");
    }    
    
    protected static void addDirective(RequestContext context, Map<String, Object> model, String name, String className)
    {
        GenericFreemarkerTagDirective directive = new GenericFreemarkerTagDirective(context, name, className);
        model.put(name, directive);
    }
}
