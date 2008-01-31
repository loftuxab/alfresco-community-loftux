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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Encapsulates the execution of a single Web Script.
 *
 * Sub-classes of WebScriptRuntime maintain the execution environment e.g. servlet
 * request & response.
 * 
 * A new instance of WebScriptRuntime is required for each invocation.
 * 
 * @author davidc
 */
public abstract class AbstractRuntime implements Runtime
{
    // Logger
    protected static final Log logger = LogFactory.getLog(AbstractRuntime.class);

    /** Component Dependencies */
    protected RuntimeContainer container;

    /**
     * Construct
     * 
     * @param container  web script context
     */
    public AbstractRuntime(RuntimeContainer container)
    {
        this.container = container;
    }
    
    /**
     * Execute the Web Script encapsulated by this Web Script Runtime
     */
    final public void executeScript()
    {
        long startRuntime = System.currentTimeMillis();

        String method = getScriptMethod();
        String scriptUrl = null;

        try
        {
            // extract script url
            scriptUrl = getScriptUrl();
            if (scriptUrl == null || scriptUrl.length() == 0)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Script URL not specified");
            }

            if (logger.isDebugEnabled())
                logger.debug("(Runtime=" + getName() + ", Container=" + container.getName() + ") Processing script url ("  + method + ") " + scriptUrl);

            Match match = container.getRegistry().findWebScript(method, scriptUrl);
            if (match == null || match.getKind() == Match.Kind.URI)
            {
                if (match == null)
                {
                    String msg = "Script url " + scriptUrl + " does not map to a Web Script.";
                    if (logger.isDebugEnabled())
                        logger.debug(msg);
                    throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, msg);
                }
                else
                {
                    String msg = "Script url " + scriptUrl + " does not support the method " + method;
                    if (logger.isDebugEnabled())
                        logger.debug(msg);
                    throw new WebScriptException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
                }
            }

            // create web script request & response
            final WebScriptRequest scriptReq = createRequest(match);
            final WebScriptResponse scriptRes = createResponse();
            final Authenticator auth = createAuthenticator();
            
            if (logger.isDebugEnabled())
                logger.debug("Agent: " + scriptReq.getAgent());

            long startScript = System.currentTimeMillis();
            final WebScript script = match.getWebScript();
            final Description description = script.getDescription();
            
            try
            {
                if (logger.isDebugEnabled())
                {
                    String reqFormat = scriptReq.getFormat();
                    String format = (reqFormat == null || reqFormat.length() == 0) ? "default" : reqFormat;
                    Description desc = scriptReq.getServiceMatch().getWebScript().getDescription();
                    logger.debug("Invoking Web Script " + description.getId() + " (format " + format + ", style: " + desc.getFormatStyle() + ", default: " + desc.getDefaultFormat() + ")");
                }

                executeScript(scriptReq, scriptRes, auth);
            }
            finally
            {
                if (logger.isDebugEnabled())
                {
                    long endScript = System.currentTimeMillis();
                    logger.debug("Web Script " + description.getId() + " executed in " + (endScript - startScript) + "ms");
                }
            }
        }
        catch(Throwable e)
        {
            if (logger.isInfoEnabled())
                logger.info("Caught exception & redirecting to status template: " + e.getMessage());
            if (logger.isDebugEnabled())
                logger.debug("Caught exception: " + e.toString());
            
            // extract status code, if specified
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            if (e instanceof WebScriptException)
            {
                statusCode = ((WebScriptException)e).getStatus();
            }

            // create web script status for status template rendering
            Status status = new Status();
            status.setCode(statusCode);
            status.setMessage(e.getMessage());
            status.setException(e);
            
            // create basic model for status template rendering
            WebScriptRequest req = createRequest(null);
            WebScriptResponse res = createResponse();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("status", status);
            model.put("url", new URLModel(req));
            model.put("server", container.getDescription());
            model.put("date", new Date());
            
            // locate status template
            // NOTE: search order...
            //   1) root located <status>.ftl
            //   2) root located status.ftl
            String templatePath = getStatusCodeTemplate(statusCode);
            if (!container.getTemplateProcessor().hasTemplate(templatePath))
            {
                templatePath = getStatusTemplate();
                if (!container.getTemplateProcessor().hasTemplate(templatePath))
                {
                    throw new WebScriptException("Failed to find status template " + templatePath + " (format: " + WebScriptResponse.HTML_FORMAT + ")");
                }
            }

            // render output
            if (logger.isDebugEnabled())
            {
                logger.debug("Force success status header in response: " + req.forceSuccessStatus());
                logger.debug("Sending status " + statusCode + " (Template: " + templatePath + ")");
                logger.debug("Rendering response: content type=" + Format.HTML.mimetype());
            }

            res.reset();
            Cache cache = new Cache();
            cache.setNeverCache(true);
            res.setCache(cache);
            res.setStatus(req.forceSuccessStatus() ? HttpServletResponse.SC_OK : statusCode);
            res.setContentType(Format.HTML.mimetype() + ";charset=UTF-8");
            try
            {
                container.getTemplateProcessor().process(templatePath, model, res.getWriter());
            }
            catch (IOException e1)
            {
                throw new WebScriptException("Internal error", e1);
            }
        }
        finally
        {
            long endRuntime = System.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Processed script url ("  + method + ") " + scriptUrl + " in " + (endRuntime - startRuntime) + "ms");
        }
    }

    /**
     * Execute script given the specified context
     * 
     * @param scriptReq
     * @param scriptRes
     * @param auth
     * 
     * @throws IOException
     */
    protected void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException
    {
        container.executeScript(scriptReq, scriptRes, auth);
    }

    /**
     * Get code specific Status Template path
     * 
     * @param statusCode
     * @return  path
     */
    protected String getStatusCodeTemplate(int statusCode)
    {
        return "/" + statusCode + ".ftl";
    }
    
    /**
     * Get Status Template path
     * 
     * @return  path
     */
    protected String getStatusTemplate()
    {
        return "/status.ftl";
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Runtime#getScriptParameters()
     */
    public Map<String, Object> getScriptParameters()
    {
        return Collections.emptyMap();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Runtime#getTemplateParameters()
     */
    public Map<String, Object> getTemplateParameters()
    {
        return Collections.emptyMap();
    }
    
    /**
     * Get the Web Script Method  e.g. get, post
     * 
     * @return  web script method
     */
    protected abstract String getScriptMethod();

    /**
     * Get the Web Script Url
     * 
     * @return  web script url
     */
    protected abstract String getScriptUrl();
    
    /**
     * Create a Web Script Request
     * 
     * @param match  web script matching the script method and url
     * @return  web script request
     */
    protected abstract WebScriptRequest createRequest(Match match);
    
    /**
     * Create a Web Script Response
     * 
     * @return  web script response
     */
    protected abstract WebScriptResponse createResponse();
    
    /**
     * Create a Web Script Authenticator
     * 
     * @return  web script authenticator
     */
    protected abstract Authenticator createAuthenticator();

}
