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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.config.ScriptConfigModel;
import org.alfresco.config.TemplateConfigModel;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.alfresco.web.scripts.json.JSONWriter;
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
    
    // Constants
    private static final String DOT_PROPS = ".properties";
    
    // Dependencies
    private Container container;
    private Description description;
    
    // Script config
    private String xmlConfig;
    
    // Service resources
    private Map<Locale, ResourceBundle> resources = new HashMap<Locale, ResourceBundle>(4);
    private Map<Locale, String> jsonResources = new HashMap<Locale, String>(4);
    
    // Status Template cache
    private Map<String, StatusTemplate> statusTemplates = new HashMap<String, StatusTemplate>(4);    
    private ReentrantReadWriteLock statusTemplateLock = new ReentrantReadWriteLock(); 
    
    // Script Context
    private String basePath;
    private Map<String, ScriptDetails> scripts = new HashMap<String, ScriptDetails>(4);
    private ReentrantReadWriteLock scriptLock = new ReentrantReadWriteLock(); 
    
    // The entry we use to 'remember' nulls in the cache
    private static final ScriptDetails NULLSENTINEL = new ScriptDetails(null, null);    

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
    	// sanity check to ensure a web script is only registered with a single web script container
    	if (this.container != null && (!this.container.equals(container)))
    	{
    		throw new WebScriptException("Web Script " + description.getId() + " already associated with the '" + this.container.getName() + "' container");
    	}
    	
        this.container = container;
        this.description = description;
        this.basePath = description.getId();
        
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
        
        // init the resources for the default locale
        getResources();
        
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
    }

    /**
     * @return  web script container
     */
    final protected Container getContainer()
    {
        return container;
    }
    
    /**
     * @return the service description
     */
    final public Description getDescription()
    {
        return this.description;
    }
    
    /**
     * @return the services resources or null if none present
     */
    final public ResourceBundle getResources()
    {
        ResourceBundle result = null;
        Locale locale = I18NUtil.getLocale();
        
        synchronized (this.resources)
        {
            result = this.resources.get(locale);
            if (result == null && this.resources.containsKey(locale) == false)
            {
                // The following locale based lookup sequence is performed using the supplied locale
                //  1. lookup <descid><language_country_variant>.properties
                //  2. lookup <descid><language_country>.properties
                //  3. lookup <descid><language>.properties
                // If nothing is found, then the sequence is repeated using the default server locale
                // Finally the following is attempted:
                //  lookup <descid>.properties
                // And the result cached - a null result is acceptable to be cached.
                try
                {
                    String webscriptId = getDescription().getId();
                    String localePath = webscriptId + '_' + locale.toString() + DOT_PROPS;
                    result = getBundleFromPath(localePath);
                    if (result == null)
                    {
                        // it's quite likely that the first contructed path will be the same as locale.toString()
                        String resourcePath = webscriptId + '_' + locale.getLanguage() + '_' + locale.getCountry() + DOT_PROPS;
                        if (!resourcePath.equals(localePath))
                        {
                            result = getBundleFromPath(resourcePath);
                        }
                        if (result == null)
                        {
                            result = getBundleFromPath(webscriptId + '_' + locale.getLanguage() + DOT_PROPS);
                        }
                    }
                    
                    // did we find anything? if not, we should try using the default locale lookup sequence
                    if (result == null)
                    {
                        Locale defaultLocale = Locale.getDefault();
                        if (!defaultLocale.equals(locale))
                        {
                            localePath = webscriptId + '_' + defaultLocale.toString() + DOT_PROPS;
                            result = getBundleFromPath(localePath);
                            if (result == null)
                            {
                                // it's quite likely that the first contructed path will be the same as defaultLocale.toString()
                                String resourcePath = webscriptId + '_' + defaultLocale.getLanguage() + '_' + defaultLocale.getCountry() + DOT_PROPS;
                                if (!resourcePath.equals(localePath))
                                {
                                    result = getBundleFromPath(resourcePath);
                                }
                                if (result == null)
                                {
                                    result = getBundleFromPath(webscriptId + '_' + defaultLocale.getLanguage() + DOT_PROPS);
                                }
                            }
                        }
                    }
                    
                    // finally if still no result, get the default bundle name with no locale whatsoever
                    if (result == null)
                    {
                        result = getBundleFromPath(webscriptId + DOT_PROPS);
                    }
                }
                catch (IOException resErr)
                {
                    // no resources available if this occurs
                    logger.error(resErr);
                }
                
                // push the resources into the cache - null value is acceptable if none found
                this.resources.put(locale, result);
            }
        }
        
        return result;
    }
    
    private ResourceBundle getBundleFromPath(String path) throws IOException
    {
        ResourceBundle result = null;
        if (container.getSearchPath().hasDocument(path))
        {
            result = new PropertyResourceBundle(container.getSearchPath().getDocument(path));
        }
        return result;
    }

    
    //
    // Scripting Support
    //

    /**
	 * Find execute script for given request format
	 * 
	 * Note: This method caches the script to request format mapping
	 * 
	 * @param mimetype
	 * @return  execute script
	 */
	protected ScriptDetails getExecuteScript(String mimetype)
    {
		ScriptDetails script = null;
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
                        FormatRegistry formatRegistry = getContainer().getFormatRegistry();
	                	
	                    // Locate script in web script store
                        ScriptContent scriptContent = null;
	                	String generalizedMimetype = mimetype;
	                	while (generalizedMimetype != null)
	                	{
	                        String format = formatRegistry.getFormat(null, generalizedMimetype);
	                        if (format != null)
	                        {
	                            scriptContent = getContainer().getScriptProcessor().findScript(basePath + "." + format + ".js");
	                            if (scriptContent != null)
	                            {
	                                break;
	                            }
	                        }
                            generalizedMimetype = formatRegistry.generalizeMimetype(generalizedMimetype);
	                	}
	                    
	                    // fall-back to default
						if (scriptContent == null)
						{
							scriptContent = getContainer().getScriptProcessor().findScript(basePath + ".js");

							// TODO: Special case. Because multipart form data
                            // is parsed for free, we still allow non type
                            // specific scripts to see the parsed form data
							generalizedMimetype = Format.FORMDATA.mimetype().equals(mimetype) ? mimetype : null;
						}
	                    
	                    if (scriptContent != null)
						{
	                    	// Validate that there is actually a reader registered to handle this format
	                    	if (formatRegistry.getReader(generalizedMimetype) == null)
	                    	{
	                    		throw new WebScriptException("No reader registered for \"" + generalizedMimetype + "\"");
	                    	}
							script = new ScriptDetails(scriptContent, generalizedMimetype);
						}
	                    
	                    if (logger.isDebugEnabled())
                            logger.debug("Caching script " + ((script == null) ? "null" : script.getContent().getPathDescription()) + " for web script " + basePath + " and request mimetype " + ((mimetype == null) ? "null" : mimetype));
                        
                        scripts.put(key, script != null ? script : NULLSENTINEL);	                    
	                }
	            }
	            finally
	            {
	                // Downgrade lock to read
	                scriptLock.readLock().lock();
	                scriptLock.writeLock().unlock();
	            }
	        }
	        return script != NULLSENTINEL ? script : null;
	    }
	    finally
	    {
	        scriptLock.readLock().unlock();
	    }
	}

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
        Map<String, Object> params = new HashMap<String, Object>(32, 1.0f);
        
        // add web script parameters
        params.put("webscript", req.getServiceMatch().getWebScript().getDescription());
        params.put("format", new FormatModel(container.getFormatRegistry(), req.getFormat()));
        params.put("args", createArgs(req));
        params.put("argsM", createArgsM(req));
        params.put("headers", createHeaders(req));
        params.put("headersM", createHeadersM(req));
        params.put("guest", req.isGuest());
        params.put("url", new URLModel(req));
        ScriptMessage message = new ScriptMessage(this);
        params.put("msg", message);
        
        // If there is a request type specific script (e.g. *.json.js), parse
		// the request according to its MIME type and add request specific
		// parameters. Use the FormatReader for the generalised mime type
		// corresponding to the script - not necessarily the request mime type
		ScriptDetails script = getExecuteScript(req.getContentType());
		if (script != null)
		{
			FormatReader<Object> reader = container.getFormatRegistry().getReader(script.getRequestType());
			params.putAll(reader.createScriptParameters(req, res));
		}
        
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
        Map<String, Object> params = new HashMap<String, Object>(64, 1.0f);
        
        // add context & runtime parameters
        params.putAll(req.getRuntime().getTemplateParameters());
        params.putAll(container.getTemplateParameters());
        
        // add web script parameters
        params.put("webscript", req.getServiceMatch().getWebScript().getDescription());
        params.put("format", new FormatModel(container.getFormatRegistry(), req.getFormat()));
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
        params.put("formatwrite", new FormatWriterMethod(container.getFormatRegistry(), req.getFormat()));
        MessageMethod message = new MessageMethod(this);
        params.put("message", message);     // for compatibility with repo templates
        params.put("msg", message);         // short form for presentation webscripts
        
        // add the webscript I18N resources as a JSON object
        params.put("messages", renderJSONResources(getResources()));

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
        String[] names = req.getParameterNames();
        Map<String, String> args = new HashMap<String, String>(names.length, 1.0f);
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
        String[] names = req.getParameterNames();
        Map<String, String[]> args = new HashMap<String, String[]>(names.length, 1.0f);
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
        // NOTE: headers names are case-insensitive according to HTTP Spec
        Map<String, String> headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
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
        // NOTE: headers names are case-insensitive according to HTTP Spec
        Map<String, String[]> headers = new TreeMap<String, String[]>(String.CASE_INSENSITIVE_ORDER);
        String[] names = req.getHeaderNames();
        for (String name : names)
        {
            headers.put(name, req.getHeaderValues(name));
        }
        return headers;
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
        long start = System.nanoTime();
        container.getTemplateProcessor().process(templatePath, model, writer);
        if (logger.isDebugEnabled())
            logger.debug("Rendered template " + templatePath + " in " + (System.nanoTime() - start)/1000000f + "ms");
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
        String mimetype = container.getFormatRegistry().getMimeType(req.getAgent(), template.getFormat());
        if (mimetype == null)
        {
            throw new WebScriptException("Web Script format '" + template.getFormat() + "' is not registered");
        }
    
        if (logger.isDebugEnabled())
        {
            logger.debug("Force success status header in response: " + req.forceSuccessStatus());
            logger.debug("Sending status " + statusCode + " (Template: " + template.getPath() + ")");
            logger.debug("Rendering response: content type=" + mimetype);
        }
    
        res.reset();
        res.setCache(cache);
        res.setStatus(req.forceSuccessStatus() ? HttpServletResponse.SC_OK : statusCode);
        String location = status.getLocation();
        if (location != null && location.length() > 0)
        {
            if (logger.isDebugEnabled())
                logger.debug("Setting location to " + location);
            res.setHeader(WebScriptResponse.HEADER_LOCATION, location);
        }
        res.setContentType(mimetype + ";charset=UTF-8");
        renderTemplate(template.getPath(), model, res.getWriter());
    }

    /**
     * Create an exception whose associated message is driven from a status template and model
     * 
     * @param e  exception
     * @param req  web script request
     * @param res  web script response
     * @return  web script exception with associated template message and model
     */
    final protected WebScriptException createStatusException(Throwable e, final WebScriptRequest req, final WebScriptResponse res)
    {
        // decorate exception with template message
        final WebScriptException we;
        if (e instanceof WebScriptException)
        {
            we = (WebScriptException)e;
        }
        else
        {
            we = new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Wrapped Exception (with status template): " + e.getMessage(), e);
        }       
        
        // find status template and construct model for it
        we.setStatusTemplateFactory(new StatusTemplateFactory()
        {
            public Map<String, Object> getStatusModel()
            {
                return createTemplateParameters(req, res, null);
            }

            public StatusTemplate getStatusTemplate()
            {
                int statusCode = we.getStatus();
                String format = req.getFormat();
                String scriptId = getDescription().getId();
                return AbstractWebScript.this.getStatusTemplate(scriptId, statusCode, (format == null) ? "" : format);
            }
        });
        
        return we;
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
    protected StatusTemplate getStatusTemplate(String scriptId, int statusCode, String format)
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
                            logger.debug("Caching template " + statusTemplate.getPath() + " for web script " + scriptId +
                                         " and status " +statusCode + " (format: " + format + ")");
                        
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
                StringBuilder fileContents = new StringBuilder(1024);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"), 1024);
                char[] buf = new char[1024];
                int read;
                while ((read=reader.read(buf)) != -1)
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
    protected void executeScript(ScriptContent location, Map<String, Object> model)
    {
        long start = System.nanoTime();
        container.getScriptProcessor().executeScript(location, model);
        if (logger.isDebugEnabled())
            logger.debug("Executed script " + location.getPathDescription() + " in " + (System.nanoTime() - start)/1000000f + "ms");
    }
    
    /**
     * Helper to render a bundle of webscript I18N resources as a JSON object
     * 
     * @param resources     To render - can be null if no resources present,
     *                      in which case an empty JSON object will be output.
     * 
     * @return JSON object string
     */
    private String renderJSONResources(ResourceBundle resources)
    {
        String result = "{}";
        
        if (resources != null)
        {
            synchronized (jsonResources)
            {
                Locale locale = I18NUtil.getLocale();
                result = jsonResources.get(locale);
                if (result == null)
                {
                    StringBuilderWriter buf = new StringBuilderWriter(256);
                    JSONWriter out = new JSONWriter(buf);
                    try
                    {
                        out.startObject();
                        Enumeration<String> keys = resources.getKeys();
                        while (keys.hasMoreElements())
                        {
                            String key = keys.nextElement();
                            out.writeValue(key, resources.getString(key));
                        }
                        out.endObject();
                    }
                    catch (IOException jsonErr)
                    {
                        throw new WebScriptException("Error rendering I18N resources.", jsonErr);
                    }
                    result = buf.toString();
                    jsonResources.put(locale, result);
                }
            }
        }
        
        return result;
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
	 * The combination of a ScriptContent and a request MIME type. Records the
	 * most specific request MIME type expected by a script (according to its
	 * naming convention, e.g. *.json.js or *.js). Used to determine what kind
	 * of parsing should be done on the request (i.e. what kind of FormatReader
	 * should be invoked to get extra script parameters).
	 */    
    protected static class ScriptDetails
    {
		private final ScriptContent content;
		private final String requestType;

		private ScriptDetails(ScriptContent content, String requestType)
        {
			this.content = content;
			this.requestType = requestType;
		}

		/**
		 * @return the content
		 */
		public ScriptContent getContent()
        {
			return content;
		}

		/**
		 * @return the requestType
		 */
		public String getRequestType()
        {
			return requestType;
		}
	}

}
