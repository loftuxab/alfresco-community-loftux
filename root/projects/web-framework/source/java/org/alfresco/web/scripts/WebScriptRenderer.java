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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.tools.EncodingUtil;
import org.alfresco.util.StringBuilderWriter;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.renderer.AbstractRenderer;
import org.alfresco.web.site.renderer.RendererContext;
import org.alfresco.web.uri.UriUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The WebScriptRenderer is an implementation of Renderable which describes
 * a rendering engine that the Web Framework can use to execute a web script.
 * <p>
 * A WebScriptRenderer can be used to execute a web script for any purpose
 * so long as an appropriate RendererContext instance is passed to it.
 * <p>
 * Most commonly, the RendererContext passed in will describe a Component.
 * <p>
 * The renderer supports "full page refresh" link backs to a webscript. The
 * LocalWebScriptResponse object is responsable for encoding compatible links
 * via the scripturl() template method.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class WebScriptRenderer extends AbstractRenderer
{
    static final String PARAM_WEBSCRIPT_ID  = "_wsId";
    static final String PARAM_WEBSCRIPT_URL = "_wsUrl";
    
    private static Log logger = LogFactory.getLog(WebScriptRenderer.class);
    
    private TemplateProcessor templateProcessor;
    private LocalWebScriptRuntimeContainer webScriptContainer;
    private Registry registry;
    
    
    public void init(RendererContext rendererContext)
    {
        // Get the application context and relevant context objects
        ServletContext servletContext = rendererContext.getRequest().getSession().getServletContext();
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        RequestContext context = (RequestContext) rendererContext.getRequestContext();
        
        templateProcessor = (TemplateProcessor)appContext.getBean("webframework.templateprocessor");
        registry = (Registry) appContext.getBean("webframework.webscripts.registry");
        
        // Figure out which web script runtime container to use.
        // Most of the time, this will be the default one.
        String containerId = context.getConfig().getRendererDescriptor(getRendererType()).getStringProperty("container-bean");
        if (containerId == null || containerId.length() == 0)
        {
            containerId = "webframework.webscripts.container";
        }
        webScriptContainer = (LocalWebScriptRuntimeContainer)appContext.getBean(containerId);
        if (webScriptContainer == null)
        {
            throw new AlfrescoRuntimeException("Unable to find web script container: " + containerId); 
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.AbstractRenderer#head(org.alfresco.web.site.renderer.RendererContext)
     */
    public String head(RendererContext rendererContext)
        throws RendererExecutionException
    {
        String head = null;
        
        /**
         * If the ModelObject being rendered is a component, then we will
         * allow for the execution of .head template files ahead of the
         * actual WebScript execution.
         */
        if (rendererContext.getObject() instanceof Component)
        {
            // Pull a few necessary things from the renderer context
            HttpServletRequest request = rendererContext.getRequest();
            
            // Copy in request parameters into a HashMap
            // This is so as to be compatible with UriUtils (and Token substitution)
            Map<String, String> args = buildArgs(request);
            
            // Get the component and its URL.  Do a token replacement
            // on the URL right away and remove the query string
            Component component = (Component) rendererContext.getObject();
            String url = component.getURL();
            url = UriUtils.replaceUriTokens(url, args);
            if (url.lastIndexOf('?') != -1)
            {
                url = url.substring(0, url.lastIndexOf('?'));
            }
            
            // Find the web script
            Match match = registry.findWebScript("GET", url);
            if (match != null)
            {
                WebScript webScript = match.getWebScript();
                if (webScript != null)
                {
                    // Modify the path to resolve the .head.ftl file
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
                        Map<String, Object> model = new HashMap<String, Object>(32);
                        ProcessorModelHelper.populateTemplateModel(rendererContext, model);
                        
                        StringBuilderWriter out = new StringBuilderWriter(512);
                        templateProcessor.process(path, model, out);
                        
                        String result = out.toString();
                        if (result != null && result.length() > 0)
                        {
                            head = out.toString();
                            
                            // output wrapping comment blocks if debug enabled
                            if (logger.isDebugEnabled())
                                head = super.head(rendererContext) + head;
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
        HttpServletRequest request = rendererContext.getRequest();
        RequestContext context = rendererContext.getRequestContext();

        // Construct a "context" object that the Web Script engine will utilise
        LocalWebScriptContext webScriptContext = new LocalWebScriptContext();
        
        // Copy in request parameters into a HashMap
        // This is so as to be compatible with UriUtils (and Token substitution)
        webScriptContext.Tokens = buildArgs(request);
        
        // Begin to process the actual web script.
        
        // Get the web script url, perform token substitution and remove query string
        String url = UriUtils.replaceUriTokens(this.getRenderer(), webScriptContext.Tokens);
        webScriptContext.ScriptUrl = (url.indexOf('?') == -1 ? url : url.substring(0, url.indexOf('?')));
        
        // Get up the request path.
        // If none is supplied, assume the servlet path.
        String requestPath = (String)rendererContext.get("requestPath");
        if (requestPath == null)
        {
            requestPath = request.getContextPath();
        }
        
        // if this webscript has been clicked, update the script URL - 
        if (rendererContext.getObject().getId().equals(request.getParameter(PARAM_WEBSCRIPT_ID)))
        {
            webScriptContext.ExecuteUrl = request.getParameter(PARAM_WEBSCRIPT_URL);
        }
        else
        {
            // else use the webscript default url
            webScriptContext.ExecuteUrl = requestPath + "/service" + url;
        }
        
        // Set up character encoding.  If none, set the default.
        String encoding = request.getCharacterEncoding();
        if (encoding == null)
        {
            try
            {
                request.setCharacterEncoding(EncodingUtil.DEFAULT_ENCODING);
            }
            catch (UnsupportedEncodingException uee)
            {
                throw new RendererExecutionException("Unable to set encoding to default: " + EncodingUtil.DEFAULT_ENCODING, uee);
            }
        }
        
        // Set up state onto the web script context
        webScriptContext.RuntimeContainer = webScriptContainer;
        webScriptContext.RendererContext = rendererContext;
        webScriptContext.Object = rendererContext.getObject();
        webScriptContext.RequestContext = context;
        
        try
        {
            // Construct the Web Script Runtime
            // This bundles the container, the context and the encoding
            LocalWebScriptRuntime runtime = new LocalWebScriptRuntime(
                    new BufferedWriter(rendererContext.getResponse().getWriter(), 4096),
                    webScriptContainer, webScriptContext);
            
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
    
            // Execute the script
            runtime.executeScript();
    
            // Be sure to unbind the request context thread local variable
            webScriptContainer.unbindRendererContext();
        }
        catch (IOException exc)
        {
            throw new RendererExecutionException("Unable to read back response from Web Script Runtime buffer", exc);
        }
    }
    
    /**
     * Helper to build argument map from the servlet request parameters
     */
    private static Map<String, String> buildArgs(HttpServletRequest request)
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
}
