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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Script/template driven based implementation of an Web Script
 *
 * @author davidc
 */
public class DeclarativeWebScript extends AbstractWebScript 
{
    // Logger
    private static final Log logger = LogFactory.getLog(DeclarativeWebScript.class);

    // Script Context
    private String basePath;
    private Map<String, ScriptContent> scripts = new HashMap<String, ScriptContent>();
    private ReentrantReadWriteLock scriptLock = new ReentrantReadWriteLock(); 
    

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractWebScript#init(org.alfresco.web.scripts.WebScriptRegistry)
     */
    @Override
    public void init(Container container, Description description)
    {
        super.init(container, description);

        // clear scripts to format map
        this.scriptLock.writeLock().lock();
        try
        {
            this.scripts.clear();
        }
        finally
        {
            this.scriptLock.writeLock().unlock();
        }
        
        basePath = getDescription().getId();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScript#execute(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    final public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        // retrieve requested format
        String format = req.getFormat();

        try
        {
            // establish mimetype from format
            String mimetype = getContainer().getFormatRegistry().getMimeType(req.getAgent(), format);
            if (mimetype == null)
            {
                throw new WebScriptException("Web Script format '" + format + "' is not registered");
            }
            
            // construct model for script / template
            Status status = new Status();
            Cache cache = new Cache(getDescription().getRequiredCache());
            Map<String, Object> model = executeImpl(req, status, cache);
            if (model == null)
            {
                model = new HashMap<String, Object>(8, 1.0f);
            }
            model.put("status", status);
            model.put("cache", cache);
            
            // execute script if it exists
            ScriptContent script = getExecuteScript(req.getContentType());
            if (script != null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Executing script " + script.getPathDescription());
                
                Map<String, Object> scriptModel = createScriptParameters(req, res, model);
                // add return model allowing script to add items to template model
                Map<String, Object> returnModel = new HashMap<String, Object>(8, 1.0f);
                scriptModel.put("model", returnModel);
                executeScript(script, scriptModel);
                mergeScriptModelIntoTemplateModel(returnModel, model);
            }
    
            // create model for template rendering
            Map<String, Object> templateModel = createTemplateParameters(req, res, model);
            
            // is a redirect to a status specific template required?
            if (status.getRedirect())
            {
                sendStatus(req, res, status, cache, format, templateModel);
            }
            else
            {
                // render output
                int statusCode = status.getCode();
                if (statusCode != HttpServletResponse.SC_OK && !req.forceSuccessStatus())
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Force success status header in response: " + req.forceSuccessStatus());
                        logger.debug("Setting status " + statusCode);
                    }
                    res.setStatus(statusCode);
                }
                
                // apply location
                String location = status.getLocation();
                if (location != null && location.length() > 0)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Setting location to " + location);
                    res.setHeader(WebScriptResponse.HEADER_LOCATION, location);
                }

                // apply cache
                res.setCache(cache);
                
                String callback = req.getJSONCallback();
                if (format.equals(WebScriptResponse.JSON_FORMAT) && callback != null)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Rendering JSON callback response: content type=" + Format.JAVASCRIPT.mimetype() + ", status=" + statusCode + ", callback=" + callback);
                    
                    // NOTE: special case for wrapping JSON results in a javascript function callback
                    res.setContentType(Format.JAVASCRIPT.mimetype() + ";charset=UTF-8");
                    res.getWriter().write((callback + "("));
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Rendering response: content type=" + mimetype + ", status=" + statusCode);

                    res.setContentType(mimetype + ";charset=UTF-8");
                }
            
                // render response according to requested format
                renderFormatTemplate(format, templateModel, res.getWriter());
                
                if (format.equals(WebScriptResponse.JSON_FORMAT) && callback != null)
                {
                    // NOTE: special case for wrapping JSON results in a javascript function callback
                    res.getWriter().write(")");
                }
            }
        }
        catch(Throwable e)
        {
            if (logger.isInfoEnabled())
                logger.info("Caught exception & redirecting to status template: " + e.getMessage());
                
            // extract status code, if specified
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            if (e instanceof WebScriptException)
            {
                statusCode = ((WebScriptException)e).getStatus();
            }

            // send status
            Status status = new Status();
            status.setCode(statusCode);
            status.setMessage(e.getMessage());
            status.setException(e);
            Cache cache = new Cache();
            cache.setNeverCache(true);
            Map<String, Object> customModel = new HashMap<String, Object>(8, 1.0f);
            customModel.put("status", status);
            Map<String, Object> templateModel = createTemplateParameters(req, res, customModel);
            sendStatus(req, res, status, cache, format, templateModel);
        }
    }
    
    /**
     * Merge script generated model into template-ready model
     * 
     * @param scriptModel  script model
     * @param templateModel  template model
     */
    final private void mergeScriptModelIntoTemplateModel(Map<String, Object> scriptModel, Map<String, Object> templateModel)
    {
        for (Map.Entry<String, Object> entry : scriptModel.entrySet())
        {
            // retrieve script model value
            Object value = entry.getValue();
            Object templateValue = getContainer().getScriptProcessor().unwrapValue(value);
            templateModel.put(entry.getKey(), templateValue);
        }
    }

    /**
     * Execute custom Java logic
     * 
     * @param req  Web Script request
     * @param status Web Script status
     * @return  custom service model
     * @deprecated
     */
    protected Map<String, Object> executeImpl(WebScriptRequest req, WebScriptStatus status)
    {
        return null;
    }

    /**
     * Execute custom Java logic
     * 
     * @param req  Web Script request
     * @param status Web Script status
     * @return  custom service model
     * @deprecated
     */
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        return executeImpl(req, new WebScriptStatus(status));
    }

    /**
     * Execute custom Java logic
     * 
     * @param  req  Web Script request
     * @param  status Web Script status
     * @param  cache  Web Script cache
     * @return  custom service model
     */
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // NOTE: Redirect to those web scripts implemented before cache support and v2.9
        return executeImpl(req, status);
    }
    
    /**
     * Render a template (of given format) to the Web Script Response
     * 
     * @param format  template format (null, default format)  
     * @param model  data model to render
     * @param writer  where to output
     */
    final protected void renderFormatTemplate(String format, Map<String, Object> model, Writer writer)
    {
        format = (format == null) ? "" : format;
        String templatePath = basePath + "." + format + ".ftl";

        if (logger.isDebugEnabled())
            logger.debug("Rendering template '" + templatePath + "'");

        renderTemplate(templatePath, model, writer);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.basePath;
    }

    /**
     * Find execute script for given request format
     * 
     * Note: This method caches the script to request format mapping
     * 
     * @param mimetype
     * @return  execute script
     */
    private ScriptContent getExecuteScript(String mimetype)
    {
        ScriptContent script = null;
        scriptLock.readLock().lock();

        try
        {
            String key = (mimetype == null) ? "<UNKNOWN>" : mimetype;
            script = scripts.get(key);
            if (script == null)
            {
                // Upgrade read lock to write lock
                scriptLock.readLock().unlock();
                scriptLock.writeLock().lock();

                try
                {
                    // Check again
                    script = scripts.get(key);
                    if (script == null)
                    {
                        // Locate script in web script store
                        String format = getContainer().getFormatRegistry().getFormat(null, mimetype);
                        if (format != null)
                        {
                            script = getContainer().getScriptProcessor().findScript(basePath + "." + format + ".js");
                            if (script == null)
                            {
                                // generalize mimetype if possible
                                String generalizedMimetype = getContainer().getFormatRegistry().generalizeMimetype(mimetype);
                                if (generalizedMimetype != null)
                                {
                                    format = getContainer().getFormatRegistry().getFormat(null, generalizedMimetype);
                                    if (format != null)
                                    {
                                        script = getContainer().getScriptProcessor().findScript(basePath + "." + format + ".js");
                                    }
                                }
                            }
                        }
                        
                        // fall-back to default
                        if (script == null)
                        {
                            script = getContainer().getScriptProcessor().findScript(basePath + ".js");
                        }
                        
                        if (logger.isDebugEnabled())
                            logger.debug("Caching script " + ((script == null) ? "null" : script.getPathDescription()) + " for web script " + basePath + " and request mimetype " + ((mimetype == null) ? "null" : mimetype));
                        
                        scripts.put(key, script);
                    }
                }
                finally
                {
                    // Downgrade lock to read
                    scriptLock.readLock().lock();
                    scriptLock.writeLock().unlock();
                }
            }
            return script;
        }
        finally
        {
            scriptLock.readLock().unlock();
        }
    }
    
}
