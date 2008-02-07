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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.config.ScriptConfigModel;
import org.alfresco.config.TemplateConfigModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Abstract implementation of a Web Script
 *
 * @author davidc
 */
public abstract class AbstractWebScript implements WebScript 
{
    // Logger
    private static final Log logger = LogFactory.getLog(AbstractWebScript.class);

    // dependencies
    private Container container;
    private Description description;
    
    // script config
    private String xmlConfig;
    
    // Status Template cache
    private Map<String, StatusTemplate> statusTemplates = new HashMap<String, StatusTemplate>();    
    private ReentrantReadWriteLock statusTemplateLock = new ReentrantReadWriteLock(); 
    
    //
    // Initialisation
    //
    
    /**
     * Initialise Web Script
     *
     * @param scriptRegistry
     */
    public void init(Container container, Description description)
    {
        this.container = container;
        this.description = description;
        this.statusTemplateLock.writeLock().lock();
        try
        {
            this.statusTemplates.clear();
        }
        finally
        {
            this.statusTemplateLock.writeLock().unlock();
        }
        
        // setup the script's config
        setupScriptConfig();
    }

    /**
     * @return  web script container
     */
    final protected Container getContainer()
    {
        return container;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScript#getDescription()
     */
    final public Description getDescription()
    {
        return this.description;
    }

    
    //
    // Scripting Support
    //


    /**
     * Create a model for script usage
     *  
     * @param req  web script request
     * @param res  web script response
     * @param customModel  custom model entries
     * 
     * @return  script model
     */
    protected Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res, Map<String, Object> customParams)
    {
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        
        // add web script parameters
        params.put("webscript", req.getServiceMatch().getWebScript().getDescription());
        params.put("args", createArgs(req));
        params.put("argsM", createArgsM(req));
        params.put("headers", createHeaders(req));
        params.put("headersM", createHeadersM(req));
        params.put("guest", req.isGuest());
        params.put("url", new URLModel(req));

        // add context & runtime parameters
        params.putAll(req.getRuntime().getScriptParameters());
        params.putAll(container.getScriptParameters());
        
        // add configuration
        params.put("config", new ScriptConfigModel(this.container.getConfigService(), this.xmlConfig));
 
        // add custom parameters
        if (customParams != null)
        {
            params.putAll(customParams);
        }
        return params;
    }
    
    /**
     * Create a model for template usage
     * 
     * @param req  web script request
     * @param res  web script response
     * @param customModel  custom model entries
     *
     * @return  template model
     */
    protected Map<String, Object> createTemplateParameters(WebScriptRequest req, WebScriptResponse res, Map<String, Object> customParams)
    {
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        
        // add web script parameters
        params.put("webscript", req.getServiceMatch().getWebScript().getDescription());
        params.put("args", createArgs(req));
        params.put("argsM", createArgsM(req));
        params.put("headers", createHeaders(req));
        params.put("headersM", createHeadersM(req));
        params.put("guest", req.isGuest());
        params.put("url", new URLModel(req));

        // populate model with template methods
        params.put("absurl", new AbsoluteUrlMethod(req.getServerPath()));
        params.put("scripturl", new ScriptUrlMethod(req, res));
        params.put("clienturlfunction", new ClientUrlFunctionMethod(res));
        params.put("date", new Date());

        // add context & runtime parameters
        params.putAll(req.getRuntime().getTemplateParameters());
        params.putAll(container.getTemplateParameters());
        
        // add configuration
        params.put("config", new TemplateConfigModel(this.container.getConfigService(), this.xmlConfig));
        
        // add custom parameters
        if (customParams != null)
        {
            params.putAll(customParams);
        }
        return params;
    }

    /**
     * Create a map of arguments from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  argument map
     */
    final protected Map<String, String> createArgs(WebScriptRequest req)
    {
        Map<String, String> args = new HashMap<String, String>(8, 1.0f);
        String[] names = req.getParameterNames();
        for (String name : names)
        {
            args.put(name, req.getParameter(name));
        }
        return args;
    }

    /**
     * Create a map of (array) arguments from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  argument map
     */
    final protected Map<String, String[]> createArgsM(WebScriptRequest req)
    {
        Map<String, String[]> args = new HashMap<String, String[]>(8, 1.0f);
        String[] names = req.getParameterNames();
        for (String name : names)
        {
            args.put(name, req.getParameterValues(name));
        }
        return args;
    }

    /**
     * Create a map of headers from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  header map
     */
    final protected Map<String, String> createHeaders(WebScriptRequest req)
    {
        Map<String, String> headers = new HashMap<String, String>(8, 1.0f);
        String[] names = req.getHeaderNames();
        for (String name : names)
        {
            headers.put(name, req.getHeader(name));
        }
        return headers;
    }

    /**
     * Create a map of (array) headers from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  argument map
     */
    final protected Map<String, String[]> createHeadersM(WebScriptRequest req)
    {
        Map<String, String[]> args = new HashMap<String, String[]>(8, 1.0f);
        String[] names = req.getHeaderNames();
        for (String name : names)
        {
            args.put(name, req.getHeaderValues(name));
        }
        return args;
    }

    /**
     * Render a template (identified by path)
     * 
     * @param templatePath  template path
     * @param model  model
     * @param writer  output writer
     */
    final protected void renderTemplate(String templatePath, Map<String, Object> model, Writer writer)
    {
        long start = System.currentTimeMillis();
        container.getTemplateProcessor().process(templatePath, model, writer);
        if (logger.isDebugEnabled())
            logger.debug("Rendered template " + templatePath + " in " + (System.currentTimeMillis() - start) + "ms");
    }
    
    /**
     * Render a template (contents as string)
     * @param template  the template
     * @param model  model
     * @param writer  output writer
     */
    final protected void renderString(String template, Map<String, Object> model, Writer writer)
    {
        container.getTemplateProcessor().processString(template, model, writer);
    }
     
    /**
     * Render an explicit response status template
     * 
     * @param req  web script request
     * @param res  web script response
     * @param status  web script status
     * @param format  format
     * @param model  model
     * @throws IOException
     */
    final protected void sendStatus(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache, String format, Map<String, Object> model)
        throws IOException
    {
        // locate status template
        // NOTE: search order...
        // NOTE: package path is recursed to root package
        //   1) script located <scriptid>.<format>.<status>.ftl
        //   2) script located <scriptid>.<format>.status.ftl
        //   3) package located <scriptpath>/<format>.<status>.ftl
        //   4) package located <scriptpath>/<format>.status.ftl
        //   5) default <status>.ftl
        //   6) default status.ftl

        int statusCode = status.getCode();
        String statusFormat = (format == null) ? "" : format;
        String scriptId = getDescription().getId();
        StatusTemplate template = getStatusTemplate(scriptId, statusCode, statusFormat);

        // render output
        String mimetype = container.getFormatRegistry().getMimeType(req.getAgent(), template.format);
        if (mimetype == null)
        {
            throw new WebScriptException("Web Script format '" + template.format + "' is not registered");
        }
    
        if (logger.isDebugEnabled())
        {
            logger.debug("Force success status header in response: " + req.forceSuccessStatus());
            logger.debug("Sending status " + statusCode + " (Template: " + template.path + ")");
            logger.debug("Rendering response: content type=" + mimetype);
        }
    
        res.reset();
        res.setCache(cache);
        res.setStatus(req.forceSuccessStatus() ? HttpServletResponse.SC_OK : statusCode);
        res.setContentType(mimetype + ";charset=UTF-8");
        renderTemplate(template.path, model, res.getWriter());
    }

    /**
     * Find status template
     * 
     * Note: This method caches template search results
     * 
     * @param scriptId
     * @param statusCode
     * @param format
     * @return  status template (or null if not found)
     */
    private StatusTemplate getStatusTemplate(String scriptId, int statusCode, String format)
    {
        StatusTemplate statusTemplate = null;
        statusTemplateLock.readLock().lock();

        try
        {
            String key = statusCode + "." + format;
            statusTemplate = statusTemplates.get(key);
            if (statusTemplate == null)
            {
                // Upgrade read lock to write lock
                statusTemplateLock.readLock().unlock();
                statusTemplateLock.writeLock().lock();

                try
                {
                    // Check again
                    statusTemplate = statusTemplates.get(key);
                    if (statusTemplate == null)
                    {
                        // Locate template in web script store
                        statusTemplate = getScriptStatusTemplate(scriptId, statusCode, format);
                        if (statusTemplate == null)
                        {
                            Path path = container.getRegistry().getPackage(PathImpl.concatPath("/", getDescription().getScriptPath()));
                            statusTemplate = getPackageStatusTemplate(path, statusCode, format);
                            if (statusTemplate == null)
                            {
                                statusTemplate = getDefaultStatusTemplate(statusCode);
                            }
                        }
                        
                        if (logger.isDebugEnabled())
                            logger.debug("Caching template " + statusTemplate.path + " for web script " + scriptId + " and status " + statusCode + " (format: " + format + ")");
                        
                        statusTemplates.put(key, statusTemplate);
                    }
                }
                finally
                {
                    // Downgrade lock to read
                    statusTemplateLock.readLock().lock();
                    statusTemplateLock.writeLock().unlock();
                }
            }
            return statusTemplate;
        }
        finally
        {
            statusTemplateLock.readLock().unlock();
        }
    }
    
    /**
     * Find a script specific status template
     * 
     * @param scriptId
     * @param statusCode
     * @param format
     * @return  status template (or null, if not found)
     */
    private StatusTemplate getScriptStatusTemplate(String scriptId, int statusCode, String format)
    {
        String path = scriptId + "." + format + "." + statusCode + ".ftl";
        if (container.getTemplateProcessor().hasTemplate(path))
        {
            return new StatusTemplate(path, format);
        }
        path = scriptId + "." + format + ".status.ftl";
        if (container.getTemplateProcessor().hasTemplate(path))
        {
            return new StatusTemplate(path, format);
        }
        return null;
    }

    /**
     * Find a package specific status template
     * 
     * @param scriptPath
     * @param statusCode
     * @param format
     * @return  status template (or null, if not found)
     */
    private StatusTemplate getPackageStatusTemplate(Path scriptPath, int statusCode, String format)
    {
        while(scriptPath != null)
        {
            String path = PathImpl.concatPath(scriptPath.getPath(), format + "." + statusCode + ".ftl");
            if (container.getTemplateProcessor().hasTemplate(path))
            {
                return new StatusTemplate(path, format);
            }
            path = PathImpl.concatPath(scriptPath.getPath(), format + ".status.ftl");
            if (container.getTemplateProcessor().hasTemplate(path))
            {
                return new StatusTemplate(path, format);
            }
            scriptPath = scriptPath.getParent();
        }
        return null;
    }
    
    /**
     * Find default status template
     * 
     * @param statusCode
     * @return  status template
     */
    private StatusTemplate getDefaultStatusTemplate(int statusCode)
    {
        String path = statusCode + ".ftl";
        if (container.getTemplateProcessor().hasTemplate(path))
        {
            return new StatusTemplate(path, WebScriptResponse.HTML_FORMAT);
        }
        path = "status.ftl";
        if (container.getTemplateProcessor().hasTemplate(path))
        {
            return new StatusTemplate(path, WebScriptResponse.HTML_FORMAT);
        }
        throw new WebScriptException("Default status template /status.ftl could not be found");
    }
    
    /**
     * Looks for the script's config file and reads it's contents
     * if present. The result is the XML config stored in the
     * <code>xmlConfig</code> member variable.
     */
    private void setupScriptConfig()
    {
        InputStream input = null;
        try
        {
            // Look for script's config file
            String configPath = getDescription().getId() + ".config.xml";
            input = this.container.getSearchPath().getDocument(configPath);
            if (input != null)
            {
                // if config file found, read contents into buffer
                StringBuffer fileContents = new StringBuffer(1024);
                InputStreamReader isr = new InputStreamReader(input);
                BufferedReader reader = new BufferedReader(isr);
                char[] buf = new char[1024];
                int read=0;
                while((read=reader.read(buf)) != -1)
                {
                    fileContents.append(buf, 0, read);
                }
                
                this.xmlConfig = fileContents.toString();
            }
        }
        catch (IOException ioe)
        {
            throw new WebScriptException("Failed to read script configuration file", ioe);
        }
        finally
        {
            if (input != null) try { input.close(); } catch (IOException e) {}
        }
    }
        
    /**
     * Execute a script
     * 
     * @param location  script location
     * @param model  model
     */
    final protected void executeScript(ScriptContent location, Map<String, Object> model)
    {
        long start = System.currentTimeMillis();
        container.getScriptProcessor().executeScript(location, model);
        if (logger.isDebugEnabled())
            logger.debug("Executed script " + location.getPathDescription() + " in " + (System.currentTimeMillis() - start) + "ms");
    }
    
    
    /**
     * Status Template
     */
    private class StatusTemplate
    {
        /**
         * Construct
         * 
         * @param path
         * @param format
         */
        private StatusTemplate(String path, String format)
        {
            this.path = path;
            this.format = format;
        }
        
        private String path;
        private String format;
    }
    
}
