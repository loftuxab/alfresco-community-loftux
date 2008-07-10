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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.config.ScriptConfigModel;
import org.alfresco.config.TemplateConfigModel;
import org.alfresco.i18n.I18NUtil;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.renderer.RendererContext;

/**
 * @author muzquiano
 */
public final class ProcessorModelHelper
{
    public static final String MODEL_CONFIG = "config";
    public static final String MODEL_HEAD = "head";
    public static final String MODEL_URL = "url";
    public static final String MODEL_USER = "user";
    public static final String MODEL_INSTANCE = "instance";
    public static final String MODEL_CONTENT = "content";
    public static final String MODEL_CONTEXT = "context";
    public static final String MODEL_SITEDATA = "sitedata";
    public static final String MODEL_LOCALE = "locale";
    public static final String MODEL_TEMPLATE = "template";
    public static final String MODEL_PAGE = "page";
    public static final String MODEL_PROPERTIES = "properties";
    public static final String MODEL_THEME = "theme";
    public static final String MODEL_DESCRIPTION = "description";
    public static final String MODEL_TITLE = "title";
    public static final String MODEL_ID = "id";
    public static final String PROP_HTMLID = "htmlid";
    
    /**
     * Templates have the following:
     * 
     * sitedata
     * context
     * content
     * user
     * instance (current object being rendered)
     * page
     * theme
     * htmlid
     * 
     * url
     * head
     * 
     * 
     * Components have the following
     * 
     * sitedata
     * context
     * content
     * user
     * instance (current object being rendered)
     * page
     * theme
     * htmlid
     * 
     */

    /**
     * Populates the common model for all processors
     * 
     * @param rendererContext   current context
     * @param model     to populate
     */
    private static void populateModel(RendererContext rendererContext, Map<String, Object> model)
    {
        RequestContext context = rendererContext.getRequestContext();
        
        // information about the current page being rendererd
        if (context.getPage() != null)
        {
            // "page" model object
            Page page = context.getPage();
            Map<String, Object> pageModel = new HashMap<String, Object>(8, 1.0f);
            if (context instanceof HttpRequestContext)
            {
                URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
                pageModel.put(MODEL_URL, urlHelper);
            }
            pageModel.put(MODEL_ID, page.getId());
            pageModel.put(MODEL_TITLE, page.getTitle());
            pageModel.put(MODEL_DESCRIPTION, page.getDescription());
            pageModel.put(MODEL_THEME, context.getThemeId());
            
            // custom page properties - add to model
            // use ${page.properties["abc"]}
            if (page.getCustomProperties().size() != 0)
            {
                Map<String, Serializable> customProps = new HashMap<String, Serializable>(
                        page.getCustomProperties().size());
                customProps.putAll(page.getCustomProperties());
                pageModel.put(MODEL_PROPERTIES, customProps);
            }
            else
            {
                pageModel.put(MODEL_PROPERTIES, Collections.EMPTY_MAP);
            }
            
            model.put(MODEL_PAGE, pageModel);
        }
        
        // things from the current template
        // use ${template.properties["abc"]}
        if (context.getTemplate() != null)
        {
            Map<String, Object> templateModel = new HashMap<String, Object>(1, 1.0f);
            if (context.getTemplate().getCustomProperties().size() != 0)
            {
                Map<String, Serializable> customProps = new HashMap<String, Serializable>(
                            context.getTemplate().getCustomProperties());
                customProps.putAll(context.getTemplate().getCustomProperties());
                templateModel.put(MODEL_PROPERTIES, customProps);
            }
            else
            {
                templateModel.put(MODEL_PROPERTIES, Collections.EMPTY_MAP);
            }
            
            model.put(MODEL_TEMPLATE, templateModel);
        }
        
        // the global app theme
        model.put(MODEL_THEME, context.getThemeId());
        
        // locale for the current thread
        model.put(MODEL_LOCALE, I18NUtil.getLocale().toString());
        
        //
        // add in the root-scoped web framework script objects
        //
        ScriptSiteData scriptSiteData = new ScriptSiteData(context); 
        model.put(MODEL_SITEDATA, scriptSiteData);
        
        ScriptRequestContext scriptRequestContext = new ScriptRequestContext(context);
        model.put(MODEL_CONTEXT, scriptRequestContext);
        
        if (context.getCurrentObject() != null)
        {
            ScriptContentObject scriptContent = new ScriptContentObject(context, context.getCurrentObject());
            model.put(MODEL_CONTENT, scriptContent);
        }
        
        ScriptRenderingInstance scriptRenderer = new ScriptRenderingInstance(rendererContext);
        model.put(MODEL_INSTANCE, scriptRenderer);
        
        if (context.getUser() != null)
        {
            ScriptUser scriptUser = new ScriptUser(context, context.getUser());
            model.put(MODEL_USER, scriptUser);
        }                        

        // we are also given the "rendering configuration" for the current
        // object.  usually, this is either a component or a template.
        // in either case, the configuration is set up ahead of time
        // our job here is to make sure that freemarker has everything
        // it needs for the component or template to process
        if (rendererContext != null)
        {
            String htmlBindingId = (String) rendererContext.get(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID);
            if (htmlBindingId != null && htmlBindingId.length() != 0)
            {
                model.put(PROP_HTMLID, htmlBindingId);
            }
        }
    }
    
    /**
     * Populate the model for script processor.
     * 
     * @param rendererContext   current context
     * @param model     to populate
     */
    public static void populateScriptModel(RendererContext rendererContext, Map<String, Object> model)
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        
        // common population
        populateModel(rendererContext, model);
        
        if (rendererContext.getObject() instanceof TemplateInstance)
        {
            // add in the config service accessor
            model.put(MODEL_CONFIG, new ScriptConfigModel(FrameworkHelper.getConfigService(), null));
        }
    }
    
    /**
     * Populate the model for template processor.
     * 
     * @param rendererContext   current context
     * @param model     to populate
     */
    public static void populateTemplateModel(RendererContext rendererContext, Map<String, Object> model)
        throws RendererExecutionException
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        
        RequestContext context = rendererContext.getRequestContext();

        // common population
        populateModel(rendererContext, model);

        /**
         * We add in the "url" object if we're processing against a TemplateInstance
         * 
         * If we're processing against a Web Script, it will already be there
         */
        if (rendererContext.getObject() instanceof TemplateInstance)
        {
            if (context instanceof HttpRequestContext)
            {
                // provide the URL helper for the template
                URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
                model.put(MODEL_URL, urlHelper);
            }
            
            // add in the ${head} tag
            model.put(MODEL_HEAD, RenderUtil.processHeader(rendererContext));
            
            // add in the config service accessor
            model.put(MODEL_CONFIG, new TemplateConfigModel(FrameworkHelper.getConfigService(), null));
        }
        
        /**
         * TAGS
         */
        
        // TODO: I would like this code to look at the tag libraries
        // registered with the configuration, parse the tag classes
        // and auto-instantiate directives for tags.
        
        // This would allow for developers to create their own tag libraries
        // that work in JSP, Freemarker and any parsed content using a
        // single, consistent approach
        
        model.put("region", new RegionFreemarkerTagDirective(context));
        model.put("component", new ComponentFreemarkerTagDirective(context));
        
        addDirective(context, model, "componentInclude", "org.alfresco.web.site.taglib.ComponentIncludeTag");
        
        // content specific
        addDirective(context, model, "anchor", "org.alfresco.web.site.taglib.ObjectAnchorTag");
        addDirective(context, model, "edit", "org.alfresco.web.site.taglib.ObjectEditTag");
        addDirective(context, model, "print", "org.alfresco.web.site.taglib.ObjectPrintTag");
        addDirective(context, model, "link", "org.alfresco.web.site.taglib.ObjectLinkTag");
        
        // temporary: add floating menu
        //addDirective(context, model, "floatingMenu", "org.alfresco.web.site.taglib.FloatingMenuTag");        
    }
    
    private static void addDirective(RequestContext context, Map<String, Object> model, String name, String className)
    {
        GenericFreemarkerTagDirective directive = new GenericFreemarkerTagDirective(context, name, className);
        model.put(name, directive);
    }    
}
