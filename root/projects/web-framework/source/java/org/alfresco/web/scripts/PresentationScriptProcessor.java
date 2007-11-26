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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.FileCopyUtils;


/**
 * Script Processor for use in Web Script
 * 
 * @author davidc
 */
public class PresentationScriptProcessor implements ScriptProcessor, ApplicationListener, ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(PresentationTemplateProcessor.class);
    private static WrapFactory wrapFactory = new PresentationWrapFactory(); 
    
    private ApplicationContext applicationContext;
    protected SearchPath searchPath;
    protected ScriptLoader scriptLoader;

    
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }
    
    
    /**
     * Find a script at the specified path (within registered Web Script stores)
     * 
     * @param path   script path
     * @return  script location (or null, if not found)
     */
    public ScriptContent findScript(String path)
    {
        return scriptLoader.getScript(path);
    }
    
    /**
     * Execute script
     * 
     * @param path  script path
     * @param model  model
     * @return  script result
     * @throws ScriptException
     */
    public Object executeScript(String path, Map<String, Object> model)
    {
        // locate script within web script stores
        ScriptContent scriptLocation = findScript(path);
        if (scriptLocation == null)
        {
            throw new WebScriptException("Unable to locate script " + path);
        }
        // execute script
        return executeScript(scriptLocation, model);
    }

    /**
     * Execute script
     *  
     * @param location  script location
     * @param model  model
     * @return  script result
     */
    public Object executeScript(ScriptContent location, Map<String, Object> model)
    {
        String script = null;
        
        // TODO: script caching (compiled version)
        
        // TODO: script imports (as RhinoScriptProcessor does)
        
        // read script from location
        try
        {   
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileCopyUtils.copy(location.getInputStream(), os);  // both streams are closed
            byte[] bytes = os.toByteArray();
            script = new String(bytes);
        }
        catch (Throwable e)
        {
            throw new WebScriptException("Failed to load script '" + location.toString() + "': " + e.getMessage(), e);
        }
        
        // execute script
        long startTime = 0;
        if (logger.isDebugEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        Context cx = Context.enter();
        try
        {
            cx.setWrapFactory(wrapFactory);
            Scriptable scope = cx.initStandardObjects();

            // insert supplied object model into root of the default scope
            if (model != null)
            {
                for (String key : model.keySet())
                {
                    Object obj = model.get(key);
                    ScriptableObject.putProperty(scope, key, obj);
                }
            }
            
            // execute the script
            Object result = cx.evaluateString(scope, script, "AlfrescoScript", 1, null);
            return result;
        }
        catch (Throwable e)
        {
            throw new WebScriptException(e.getMessage(), e);
        }
        finally
        {
            Context.exit();
            
            if (logger.isDebugEnabled())
            {
                long endTime = System.currentTimeMillis();
                logger.debug("Time to execute script: " + (endTime - startTime) + "ms");
            }
        }
    }

    /**
     * Reset script cache
     */
    public void reset()
    {
        // NOOP
    }
    

    /**
     * Register script loader from each Web Script Store with Script Processor
     */
    protected void init()
    {
        List<ScriptLoader> loaders = new ArrayList<ScriptLoader>();
        for (Store apiStore : searchPath.getStores())
        {
            ScriptLoader loader = apiStore.getScriptLoader();
            if (loader == null)
            {
                throw new WebScriptException("Unable to retrieve script loader for Web Script store " + apiStore.getBasePath());
            }
            loaders.add(loader);
        }
        scriptLoader = new MultiScriptLoader(loaders.toArray(new ScriptLoader[loaders.size()]));
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
                init();
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
