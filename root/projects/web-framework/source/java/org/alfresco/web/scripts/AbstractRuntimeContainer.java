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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigModel;
import org.alfresco.config.ConfigService;
import org.alfresco.web.config.ServerConfigElement;
import org.alfresco.web.config.ServerProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * Encapsulates a Container within which the Web Script Runtime executes.
 * 
 * Container examples - presentation (web tier), repository (server tier)
 * 
 * @author dcaruana
 */
public abstract class AbstractRuntimeContainer
    implements RuntimeContainer, ApplicationListener, ApplicationContextAware
{
    // Logger
    private static final Log logger = LogFactory.getLog(AbstractRuntimeContainer.class);
    
    private ApplicationContext applicationContext = null;
    private String name = "<undefined>";
    private Registry registry;
    private FormatRegistry formatRegistry;
    private ScriptProcessor scriptProcessor;
    private TemplateProcessor templateProcessor;
    private ConfigService configService;
    private ConfigModel configModel;

    /**
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param formatRegistry
     */
    public void setFormatRegistry(FormatRegistry formatRegistry)
    {
        this.formatRegistry = formatRegistry;
    }

    /**
     * @param registry
     */
    public void setRegistry(Registry registry)
    {
        this.registry = registry;
    }
    
    /**
     * @param scriptProcessor
     */
    public void setScriptProcessor(ScriptProcessor scriptProcessor)
    {
        this.scriptProcessor = scriptProcessor;
    }
    
    /**
     * @param templateProcessor
     */
    public void setTemplateProcessor(TemplateProcessor templateProcessor)
    {
        this.templateProcessor = templateProcessor;
    }
    
    /**
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.RuntimeContainer#getName()
     */
    public String getName()
    {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getScriptParameters()
     */
    public Map<String, Object> getScriptParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.put("server", getDescription());
        params.put("config", getConfigModel());
        params.put("logger", new ScriptLogger());
        
        // retrieve remote server configuration 
        Config config = configService.getConfig("Remote");
        ConfigElement remoteConfig = (ConfigElement)config.getConfigElement("remote");
        String endpoint = remoteConfig.getChild("endpoint").getValue();
        ScriptRemote remote = new ScriptRemote(endpoint + "/service", "UTF-8");
        //
        // TODO: use appropriate webscript servlet here - one that supports TICKET auth etc!
        //
        // TODO: remove this block - for testing only!
        if (remoteConfig.getChild("username") != null && remoteConfig.getChild("password") != null)
        {
           remote.setUsernamePassword(
                 remoteConfig.getChild("username").getValue(),
                 remoteConfig.getChild("password").getValue());
        }
        params.put("remote", remote);
        
        return Collections.unmodifiableMap(params);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getTemplateParameters()
     */
    public Map<String, Object> getTemplateParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.put("server", getDescription());
        params.put("config", getConfigModel());
        
        return Collections.unmodifiableMap(params);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getFormatRegistry()
     */
    public FormatRegistry getFormatRegistry()
    {
        return formatRegistry;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getRegistry()
     */
    public Registry getRegistry()
    {
        return registry;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getScriptProcessor()
     */
    public ScriptProcessor getScriptProcessor()
    {
        return scriptProcessor;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getTemplateProcessor()
     */
    public TemplateProcessor getTemplateProcessor()
    {
        return templateProcessor;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#reset()
     */
    public void reset() 
    {
        long startTime = System.currentTimeMillis();
        try
        {
            scriptProcessor.reset();
            templateProcessor.reset();
            registry.reset();
        }
        finally
        {
            if (logger.isInfoEnabled())
                logger.info("Initialised " + getName() + " Web Script Container (in " + (System.currentTimeMillis() - startTime) + "ms)");
        }        
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
                reset();
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

    /**
     * Gets the Application Context
     * 
     * @return  application context
     */
    protected ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }
    
    /**
     * Gets the ConfigModel object representing the application configuration
     * 
     * @return
     */
    protected ConfigModel getConfigModel()
    {
       if (this.configModel == null)
       {
          this.configModel = new ConfigModel(this.configService);
       }
       
       return this.configModel;
    }
}
