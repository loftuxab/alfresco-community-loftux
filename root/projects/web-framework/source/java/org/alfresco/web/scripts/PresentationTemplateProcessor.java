/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import freemarker.cache.MruCacheStorage;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;


/**
 * Presentation (web tier) Template Processor
 *  
 * @author davidc
 */
public class PresentationTemplateProcessor
    implements TemplateProcessor, ApplicationListener, ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(PresentationTemplateProcessor.class);

    private ApplicationContext applicationContext;
    protected SearchPath searchPath;
    protected String defaultEncoding;
    protected Configuration templateConfig;
    protected Configuration stringConfig;
    private List<TemplateLoader> loaders = new ArrayList<TemplateLoader>();

    /**
     * @param searchPath
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }
    
    /**
     * @param defaultEncoding
     */
    public void setDefaultEncoding(String defaultEncoding)
    {
        this.defaultEncoding = defaultEncoding;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#getDefaultEncoding()
     */
    public String getDefaultEncoding()
    {
        return this.defaultEncoding;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#process(java.lang.String, java.lang.Object, java.io.Writer)
     */
    public void process(String template, Object model, Writer out)
    {
        if (template == null || template.length() == 0)
        {
            throw new IllegalArgumentException("Template name is mandatory.");
        }
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        if (out == null)
        {
            throw new IllegalArgumentException("Output Writer is mandatory.");
        }
        
        try
        {
            long startTime = 0;
            if (logger.isDebugEnabled())
            {
                logger.debug("Executing template: " + template);// + " on model: " + model);
                startTime = System.currentTimeMillis();
            }
            
            Template t = templateConfig.getTemplate(template);
            if (t != null)
            {
                try
                {
                    // perform the template processing against supplied data model
                    t.process(model, out);
                }
                catch (Throwable err)
                {
                    throw new WebScriptException("Failed to process template " + template, err);
                }
            }
            else
            {
                throw new WebScriptException("Cannot find template " + template);
            }
            
            if (logger.isDebugEnabled())
            {
                long endTime = System.currentTimeMillis();
                logger.debug("Time to execute template: " + (endTime - startTime) + "ms");
            }
        }
        catch (IOException ioerr)
        {
            throw new WebScriptException("Failed to process template " + template, ioerr);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#processString(java.lang.String, java.lang.Object, java.io.Writer)
     */
    public void processString(String template, Object model, Writer out)
    {
        if (template == null || template.length() == 0)
        {
            throw new IllegalArgumentException("Template is mandatory.");
        }
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        if (out == null)
        {
            throw new IllegalArgumentException("Output Writer is mandatory.");
        }
        
        long startTime = 0;
        if (logger.isDebugEnabled())
        {
            logger.debug("Executing template: " + template);// + " on model: " + model);
            startTime = System.currentTimeMillis();
        }
        
        try
        {
            Template t = new Template("name", new StringReader(template), stringConfig);
            t.process(model, out);
            
            if (logger.isDebugEnabled())
            {
                long endTime = System.currentTimeMillis();
                logger.debug("Time to execute template: " + (endTime - startTime) + "ms");
            }
        }
        catch (Throwable err)
        {
            throw new WebScriptException("Failed to process template " + template, err);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#reset()
     */
    public void reset()
    {
        if (templateConfig != null)
        {
            templateConfig.clearTemplateCache();
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#hasTemplate(java.lang.String)
     */
    public boolean hasTemplate(String templatePath)
    {
        boolean hasTemplate = false;
        try
        {
            Template template = templateConfig.getTemplate(templatePath);
            hasTemplate = (template != null);
        }
        catch(FileNotFoundException e)
        {
            // NOTE: return false as template is not found
        }
        catch(IOException e)
        {
            throw new WebScriptException("Failed to retrieve template " + templatePath, e);
        }
        return hasTemplate;
    }
    
    /**
     * Add a template loader to the list used when the config is initialised.
     * Must be called before the config is first initialised.
     * 
     * @param loader    TemplateLoader
     */
    public void addTemplateLoader(TemplateLoader loader)
    {
        loaders.add(loader);
    }
    
    /**
     * Initialise FreeMarker Configuration
     */
    public void initConfig()
    {
        // construct template config
        templateConfig = new Configuration();
        templateConfig.setCacheStorage(new MruCacheStorage(20, 100));
        templateConfig.setTemplateUpdateDelay(0);
        templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        templateConfig.setLocalizedLookup(false);
        templateConfig.setOutputEncoding("UTF-8");
        if (defaultEncoding != null)
        {
            templateConfig.setDefaultEncoding(defaultEncoding);
        }
        for (Store apiStore : searchPath.getStores())
        {
            TemplateLoader loader = apiStore.getTemplateLoader();
            if (loader == null)
            {
                throw new WebScriptException("Unable to retrieve template loader for Web Script store " + apiStore.getBasePath());
            }
            loaders.add(loader);
        }
        MultiTemplateLoader loader = new MultiTemplateLoader(loaders.toArray(new TemplateLoader[loaders.size()]));
        templateConfig.setTemplateLoader(loader);

        // construct string config
        stringConfig = new Configuration();
        stringConfig.setCacheStorage(new MruCacheStorage(2, 0));
        stringConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        stringConfig.setOutputEncoding("UTF-8");
        if (defaultEncoding != null)
        {
            stringConfig.setDefaultEncoding(defaultEncoding);
        }
    }
    
    /**
     * @return the current Configuration object for the template processor
     */
    public Configuration getConfig()
    {
        return templateConfig; 
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;
            ApplicationContext refreshContext = refreshEvent.getApplicationContext();
            if (refreshContext != null && refreshContext.equals(applicationContext))
            {
                initConfig();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
