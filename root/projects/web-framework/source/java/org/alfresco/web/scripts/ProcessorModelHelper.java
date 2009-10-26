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
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.config.ScriptConfigModel;
import org.alfresco.config.TemplateConfigModel;
import org.alfresco.i18n.I18NUtil;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderUtil;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * Helper to generate the model map for Script and Template execution.
 * <p>
 * The model consists of a number of context driven objects such as current
 * page and current template and a number of common helper objects such as the
 * URL and current user. 
 * 
 * @author muzquiano
 * @author kevinr
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
    public static final String MODEL_DESCRIPTION_ID = "descriptionId";
    public static final String MODEL_TITLE = "title";
    public static final String MODEL_TITLE_ID = "titleId";
    public static final String MODEL_ID = "id";
    public static final String MODEL_FORM = "form";
    public static final String MODEL_FORMDATA = "formdata";
    public static final String MODEL_APP = "app";
    public static final String PROP_HTMLID = "htmlid";
    public static final String MODEL_MESSAGE_METHOD = "msg";
    
    private static final FreemarkerI18NMessageMethod FREEMARKER_MESSAGE_METHOD_INSTANCE =
        new FreemarkerI18NMessageMethod();
    private static final ScriptConfigModel SCRIPT_CONFIG_MODEL_INSTANCE =
        new ScriptConfigModel(FrameworkHelper.getConfigService(), null);
    private static final TemplateConfigModel TEMPLATE_CONFIG_MODEL_INSTANCE =
        new TemplateConfigModel(FrameworkHelper.getConfigService(), null);
    
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
     */

    /**
     * Populates the common model for all processors
     * 
     * @param context   render context
     * @param model     to populate
     */
    private static void populateModel(RenderContext context, Map<String, Object> model)
    {
        // information about the current page being rendered
        if (context.getPage() != null)
        {
            // "page" model object
            Page page = context.getPage();
            Map<String, Object> pageModel = new HashMap<String, Object>(8, 1.0f);
            
            URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
            if (urlHelper != null)
            {
                pageModel.put(MODEL_URL, urlHelper);
            }
            
            pageModel.put(MODEL_ID, page.getId());
            pageModel.put(MODEL_TITLE, page.getTitle());
            pageModel.put(MODEL_TITLE_ID, page.getTitleId());
            pageModel.put(MODEL_DESCRIPTION, page.getDescription());
            pageModel.put(MODEL_DESCRIPTION_ID, page.getDescriptionId());
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
        else
        {
            // if we don't have a page, then we're processing a template directly
            // this can happen if we've arrived at the template via a content-based dispatch
            // for instance, we might be viewing the display template for an article in the default format
            if (context.getTemplate() != null)
            {            
                TemplateInstance template = context.getTemplate();
                
                // still make available a "page" model object
                Map<String, Object> pageModel = new HashMap<String, Object>(8, 1.0f);
                
                URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
                if (urlHelper != null)
                {
                    pageModel.put(MODEL_URL, urlHelper);
                }
    
                // stock the "page" model with attributes from the template
                pageModel.put(MODEL_ID, template.getId());
                pageModel.put(MODEL_TITLE, template.getTitle());
                pageModel.put(MODEL_TITLE_ID, template.getTitleId());
                pageModel.put(MODEL_DESCRIPTION, template.getDescription());
                pageModel.put(MODEL_DESCRIPTION_ID, template.getDescriptionId());
                pageModel.put(MODEL_THEME, context.getThemeId());
                
                pageModel.put(MODEL_PROPERTIES, Collections.EMPTY_MAP);
                
                model.put(MODEL_PAGE, pageModel);
            }            
        }
        
        // objects relevant to the current template
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
        
        // if we're rendering a component, then provide a "form" object
        if (context.getObject() instanceof Component)
        {
            // add form
            ScriptForm form = new ScriptForm(context);
            model.put(MODEL_FORM, form);
            
            if ("POST".equalsIgnoreCase(context.getRequestMethod()))
            {
                ScriptFormData formData = new ScriptFormData(context);
                model.put(MODEL_FORMDATA, formData);
            }
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
        
        ScriptRenderContext scriptRenderContext = new ScriptRenderContext(context);
        model.put(MODEL_CONTEXT, scriptRenderContext);
        
        if (context.getCurrentObject() != null)
        {
            ScriptContentObject scriptContent = new ScriptContentObject(context, context.getCurrentObject());
            model.put(MODEL_CONTENT, scriptContent);
        }
        
        ScriptRenderingInstance scriptRenderer = new ScriptRenderingInstance(context);
        model.put(MODEL_INSTANCE, scriptRenderer);
        
        // add in the web application reference
        ScriptWebApplication scriptWebApplication = new ScriptWebApplication(context);
        model.put(MODEL_APP, scriptWebApplication);
        
        // add in the current User
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
        String htmlBindingId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
        if (htmlBindingId != null && htmlBindingId.length() != 0)
        {
            model.put(PROP_HTMLID, htmlBindingId);
        }
    }
    
    /**
     * Populate the model for script processor.
     * 
     * @param context   render context
     * @param model     to populate
     */
    public static void populateScriptModel(RenderContext context, Map<String, Object> model)
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        
        // common population
        populateModel(context, model);
        
        if (context.getObject() instanceof TemplateInstance)
        {
            // add in the config service accessor
            model.put(MODEL_CONFIG, SCRIPT_CONFIG_MODEL_INSTANCE);
        }
    }
    
    /**
     * Populate the model for template processor.
     * 
     * @param context   render context
     * @param model     to populate
     */
    public static void populateTemplateModel(RenderContext context, Map<String, Object> model)
        throws RendererExecutionException, UnsupportedEncodingException
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        
        // common population
        populateModel(context, model);

        /**
         * We add in the "url" object if we're processing against a TemplateInstance
         * 
         * If we're processing against a Web Script, it will already be there
         */
        if (context.getObject() instanceof TemplateInstance)
        {
            // provide the URL helper for the template
            URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
            if (urlHelper != null)
            {
                model.put(MODEL_URL, urlHelper);
            }
            
            // add in the ${head} tag
            model.put(MODEL_HEAD, RenderUtil.renderTemplateHeaderAsString(context));
            
            // add in the config service accessor
            model.put(MODEL_CONFIG, TEMPLATE_CONFIG_MODEL_INSTANCE);
            
            // add in msg() method used for template I18N support - already provided by web script framework
            model.put(MODEL_MESSAGE_METHOD, FREEMARKER_MESSAGE_METHOD_INSTANCE);
        }
        
        // Components rendered in HEADER focus need to have access to URL
        if (context.getObject() instanceof Component)
        {
            if (model.get(MODEL_URL) == null)
            {
                // provide the URL helper for the template
                URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
                if (urlHelper != null)
                {
                    model.put(MODEL_URL, urlHelper);
                }                
            }
            
            // add in the config service accessor
            model.put(MODEL_CONFIG, TEMPLATE_CONFIG_MODEL_INSTANCE);
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
        
        // add in <@resource/> directive
        model.put("res", new ResourceFreemarkerTagDirective(context));
        
        addDirective(context, model, "componentInclude", "org.alfresco.web.site.taglib.ComponentIncludeTag");
        addDirective(context, model, "regionInclude", "org.alfresco.web.site.taglib.RegionIncludeTag");
        
        // content specific
        addDirective(context, model, "anchor", "org.alfresco.web.site.taglib.ObjectAnchorTag");
        addDirective(context, model, "edit", "org.alfresco.web.site.taglib.ObjectEditTag");
        addDirective(context, model, "print", "org.alfresco.web.site.taglib.ObjectPrintTag");
        addDirective(context, model, "pagelink", "org.alfresco.web.site.taglib.ObjectLinkTag");
        addDirective(context, model, "link", "org.alfresco.web.site.taglib.StylesheetTag");
    }
    
    private static void addDirective(RenderContext context, Map<String, Object> model, String name, String className)
    {
        GenericFreemarkerTagDirective directive = new GenericFreemarkerTagDirective(context, name, className);
        model.put(name, directive);
    }    
}
