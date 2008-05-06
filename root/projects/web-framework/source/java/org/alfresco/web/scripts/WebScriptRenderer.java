/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.tools.EncodingUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.renderer.AbstractRenderer;
import org.alfresco.web.site.renderer.RendererContext;
import org.alfresco.web.uri.UriUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The WebScriptRenderer is an implementation of Renderable which describes
 * a rendering engine that the Web Framework can use to execute a web script.
 * 
 * A WebScriptRenderer can be used to execute a web script for any purpose
 * so long as an appropriate RendererContext instance is passed to it.
 * 
 * Most commonly, the RendererContext passed in will describe a Component.
 * 
 * The WebScriptRenderer uses a buffering pattern internally as this is the
 * nature of web scripts.  The buffer is managed by the Web Script engine
 * and is purely read back and committed to the output stream at the end of
 * the WebScriptRenderer's execution.
 * 
 * @author muzquiano
 */
public class WebScriptRenderer extends AbstractRenderer
{
    protected Map<String, String> getArgs(HttpServletRequest request)
    {
        Map<String, String> args = new HashMap<String, String>(request.getParameterMap().size(), 1.0f);
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements())
        {
           String name = (String)names.nextElement();
           args.put(name, request.getParameter(name));
        }
        return args;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.AbstractRenderer#head(org.alfresco.web.site.renderer.RendererContext)
     */
    public String head(RendererContext rendererContext)
        throws RendererExecutionException
    {
        String head = null;
        
        /**
         * Pull a few necessary things from the renderer context
         */
        HttpServletRequest request = rendererContext.getRequest();

        /**
         * Get the application context and relevant context objects
         */
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
        TemplateProcessor templateProcessor = (TemplateProcessor) appContext.getBean("site.templateprocessor");
        Registry registry = (Registry) appContext.getBean("site.webscripts.registry");

        /**
         * Copy in request parameters into a HashMap
         * This is so as to be compatible with UriUtils (and Token substitution)
         */
        Map<String, String> args = getArgs(request);

        /**
         * If the ModelObject being rendered is a component, then we will
         * allow for the execution of .head template files ahead of the
         * actual WebScript execution.
         * 
         * .head template files are committed to a wrapped output stream
         * that is then committed into the completed output stream
         * at the end.
         * 
         */
        if (rendererContext.getObject() instanceof Component)
        {
            /**
             * Get the component and its URL.  Do a token replacement
             * on the URL right away and remove the query string
             */
            Component component = (Component) rendererContext.getObject();
            String url = component.getURL();
            url = UriUtils.replaceUriTokens(url, args);
            if (url.lastIndexOf('?') != -1)
            {
                url = url.substring(0, url.lastIndexOf('?'));
            }
            
            /**
             * Find the web script
             */
            Match match = registry.findWebScript("GET", url);
            if (match != null)
            {
                WebScript webScript = match.getWebScript();
                if (webScript != null)
                {
                    /**
                     * Twiddle with the path so as to resolve the .head.ftl file
                     */
                    String path = webScript.getDescription().getId() + ".head.ftl";

                    /**
                     * If the .head template file exists, we can execute
                     * it against a template model.
                     * 
                     * We then trap the results and append them into the
                     * request context "tags" buffer for later processing.
                     */
                    if (templateProcessor.hasTemplate(path))
                    {
                        Map<String, Object> model = new HashMap<String, Object>(8);
                        ProcessorModelHelper.populateTemplateModel(rendererContext, model);
                        
                        /**
                         * TODO
                         * This is pretty bad form.  It seems that the Slingshot component .head files
                         * reference ${url}.  That should not be valid since url is a variable
                         * on page.  The correct reference should be ${page.url}.
                         * 
                         * At any rate, we can add this in for the time being.
                         */
                        Map pageModel = (Map) model.get("page");
                        if(pageModel != null && model.get("url") == null)
                        {
                            URLHelper helper = (URLHelper) pageModel.get("url");
                            if(helper != null)
                            {
                                model.put("url", helper);
                            }
                        }

                        StringWriter out = new StringWriter();
                        templateProcessor.process(path, model, out);
                        
                        String result = out.toString();
                        if(result != null && result.length() > 0)
                        {
                            head = super.head(rendererContext) + out.toString();
                        }
                    }
                }
            }
        }
        
        return head;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#execute(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.alfresco.web.site.RenderData)
     */
    public void execute(RendererContext rendererContext)
            throws RendererExecutionException
    {
        /**
         * Pull a few necessary things from the renderer context
         */
        HttpServletRequest request = rendererContext.getRequest();
        RequestContext context = (RequestContext) rendererContext.getRequestContext();

        /**
         * Get the application context and relevant context objects
         */
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());

        /**
         * Copy in request parameters into a HashMap
         * This is so as to be compatible with UriUtils (and Token substitution)
         */
        Map<String, String> args = getArgs(request);

        /**
         * Begin to process the actual web script.
         * 
         * Construct a "context" object that the Web Script Engine will use
         * to figure out what we want it to do.
         */
        LocalWebScriptContext webScriptContext = new LocalWebScriptContext();
        
        /**
         * Get the web script url, do token substitution and lop off query string
         * Set onto the context
         */
        String url = this.getRenderer();
        url = UriUtils.replaceUriTokens(url, args);
        if (url.lastIndexOf('?') != -1)
        {
            url = url.substring(0, url.lastIndexOf('?'));
        }
        webScriptContext.RequestURI = url;

        /**
         * Set up the request path.
         * If none is supplied, assume the servlet path.
         */
        String requestPath = (String) rendererContext.get("requestPath"); // i.e.
        if (requestPath == null)
        {
            requestPath = request.getContextPath();
            //requestPath = "/service";
        }
        webScriptContext.RequestPath = requestPath;
        webScriptContext.scriptUrl = requestPath + webScriptContext.RequestURI;

        /**
         * Set up character encoding.  If none, set the default.
         */
        // character encoding
        String encoding = request.getCharacterEncoding();
        if (encoding == null)
        {
            try
            {
                request.setCharacterEncoding(EncodingUtil.DEFAULT_ENCODING);
                encoding = request.getCharacterEncoding();
            }
            catch (UnsupportedEncodingException uee)
            {
                throw new RendererExecutionException("Unable to set encoding to default: " + EncodingUtil.DEFAULT_ENCODING, uee);
            }
        }

        /**
         * Figure out which web script runtime container to use.
         * Most of the time, this will be the default one.
         */
        String containerId = context.getConfig().getRendererProperty(
                getRendererType(), "container-bean");
        if (containerId == null || containerId.length() == 0)
        {
            containerId = "site.webscripts.container";
        }
        LocalWebScriptRuntimeContainer webScriptContainer = (LocalWebScriptRuntimeContainer) appContext.getBean(containerId);
        if(webScriptContainer == null)
        {
            throw new RendererExecutionException("Unable to find web script container: " + containerId); 
        }
        webScriptContext.RuntimeContainer = webScriptContainer;
        
        /**
         * Set up additional state onto the web script context
         */
        webScriptContext.rendererContext = rendererContext;
        webScriptContext.object = rendererContext.getObject();
        webScriptContext.Tokens = args;
        webScriptContext.requestContext = context;

        /**
         * Construct the Web Script Runtime
         * This bundles the container, the context and the encoding
         */
        LocalWebScriptRuntime runtime = new LocalWebScriptRuntime(
                webScriptContainer, webScriptContext, encoding);

        /**
         * Bind the RequestContext to the Web Script Container using a
         * thread local variable.  The Web Script Container methods for
         * setting model properties are not request scoped, so this is the
         * only way to do this (it seems)
         * 
         * Note: The models for script and template processing are created
         * later on and will use getScriptParameters and
         * getTemplateParameters from the container.  The container looks up
         * the thread local variable and does its thing. 
         */
        webScriptContainer.bindRendererContext(rendererContext);

        /**
         * Execute the script
         */
        runtime.executeScript();

        /**
         * Be sure to unbind the request context thread local variable
         */
        webScriptContainer.unbindRendererContext();

        /**
         * Pull back the results from the runtime buffer
         */
        Reader reader = (Reader) runtime.getResponseReader();
        BufferedReader in = new BufferedReader(reader);
        char[] cbuf = new char[65536];
        StringBuilder buffer = new StringBuilder();
        int read_this_time = 0;
        try
        {
            do
            {
                read_this_time = in.read(cbuf, 0, 65536);
                if (read_this_time > 0)
                    buffer.append(cbuf, 0, read_this_time);
            }
            while (read_this_time > 0);
        }
        catch (IOException exc)
        {
            throw new RendererExecutionException("Unable to read back response from Web Script Runtime buffer", exc);
        }

        /**
         * Commit the buffer back to the output stream
         */
        try
        {
            rendererContext.getResponse().getWriter().write(buffer.toString());
        }
        catch (IOException ioe)
        {
            throw new RendererExecutionException("Unable to commit Web Script results buffer to output stream", ioe);
        }
    }
}
