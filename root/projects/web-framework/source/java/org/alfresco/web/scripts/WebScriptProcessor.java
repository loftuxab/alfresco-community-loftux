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

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import org.alfresco.tools.EncodingUtil;
import org.alfresco.web.config.ServerConfigElement;
import org.alfresco.web.config.ServerProperties;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.render.AbstractProcessor;
import org.alfresco.web.framework.render.ProcessorContext;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.uri.UriUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

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
public class WebScriptProcessor extends AbstractProcessor
{
    /** The WebScript service servlet path */
    public static final String WEBSCRIPT_SERVICE_SERVLET = "/service";
    
    /** The Constant PARAM_WEBSCRIPT_ID. */
    static final String PARAM_WEBSCRIPT_ID  = "_wsId";
    
    /** The Constant PARAM_WEBSCRIPT_URL. */
    static final String PARAM_WEBSCRIPT_URL = "_wsUrl";
    
    /** The logger. */
    private static Log logger = LogFactory.getLog(WebScriptProcessor.class);
    
    /** The template processor. */
    private TemplateProcessor templateProcessor;
    
    /** The web script container. */
    private LocalWebScriptRuntimeContainer webScriptContainer;
    
    /** The registry. */
    private Registry registry;
    
    /** The server properties. */
    private ServerProperties serverProperties;

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#init(org.springframework.context.ApplicationContext)
     */
    public void init(ApplicationContext applicationContext)
    {        
        // Setup the external access URL server properties - i.e. external hostname for absolute URLs
        ConfigService configService = (ConfigService)applicationContext.getBean("web.config");
        Config config = configService.getConfig("Server");
        serverProperties = (ServerConfigElement)config.getConfigElement(ServerConfigElement.CONFIG_ELEMENT_ID);
    }
    
    /**
     * Sets the web scripts registry.
     * 
     * @param registry the new registry
     */
    public void setRegistry(Registry registry)
    {
        this.registry = registry;
    }
    
    /**
     * Gets the web scripts registry.
     * 
     * @return the registry
     */
    public Registry getRegistry()
    {
        return this.registry;
    }
    
    /**
     * Sets the template processor.
     * 
     * @param templateProcessor the new template processor
     */
    public void setTemplateProcessor(TemplateProcessor templateProcessor)
    {
        this.templateProcessor = templateProcessor;
    }
    
    /**
     * Gets the template processor.
     * 
     * @return the template processor
     */
    public TemplateProcessor getTemplateProcessor()
    {
        return this.templateProcessor;
    }
    
    /**
     * Sets the container bean.
     * 
     * @param containerBean the new container bean
     */
    public void setContainerBean(LocalWebScriptRuntimeContainer containerBean)
    {
        this.webScriptContainer = containerBean;
    }
    
    /**
     * Gets the container bean.
     * 
     * @return the container bean
     */
    public LocalWebScriptRuntimeContainer getContainerBean()
    {
        return this.webScriptContainer;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeHeader(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeHeader(ProcessorContext pc)
        throws RendererExecutionException
    {
        // get render context and processor properties
        RenderContext context = pc.getRenderContext();
        String url = this.getProperty(pc, "uri");
        
        /**
         * If the ModelObject being rendered is a component, then we will
         * allow for the execution of .head template files ahead of the
         * actual WebScript execution.
         */
        if (context.getObject() instanceof Component)
        {
            // Pull a few necessary things from the renderer context
            HttpServletRequest request = context.getRequest();
            
            // Copy in request parameters into a HashMap
            // This is so as to be compatible with UriUtils (and Token substitution)
            Map<String, String> args = buildArgs(request);
            
            // Get the component and its URL.  Do a token replacement
            // on the URL right away and remove the query string
            Component component = (Component) context.getObject();
            
            url = UriUtils.replaceUriTokens(url, args);
            if (url.lastIndexOf('?') != -1)
            {
                url = url.substring(0, url.lastIndexOf('?'));
            }
            
            // Find the web script
            Match match = registry.findWebScript(request.getMethod(), url);
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
                     * request context "tags" buffer for output later.
                     */
                    if (templateProcessor.hasTemplate(path))
                    {
                        try
                        {
                            Map<String, Object> model = new HashMap<String, Object>(32);
                            ProcessorModelHelper.populateTemplateModel(context, model);
                            
                            // commit to output stream
                            templateProcessor.process(path, model, context.getResponse().getWriter());                                                    
                        }
                        catch (UnsupportedEncodingException uee)
                        {
                            throw new RendererExecutionException(uee);
                        }
                        catch (IOException ioe)
                        {
                            throw new RendererExecutionException(ioe);
                        }
                    }
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#doExecuteBody()
     */
    public void executeBody(ProcessorContext pc)
        throws RendererExecutionException    
    {
        // get render context and processor properties
        RenderContext context = pc.getRenderContext();
        String uri = this.getProperty(pc, "uri");
        
        // Construct a "context" object that the Web Script engine will utilise
        LocalWebScriptContext webScriptContext = new LocalWebScriptContext();
        
        // Copy in request parameters into a HashMap
        // This is so as to be compatible with UriUtils (and Token substitution)
        webScriptContext.Tokens = buildArgs(context.getRequest());
        
        // Begin to process the actual web script.
        
        // Get the web script url, perform token substitution and remove query string
        String url = UriUtils.replaceUriTokens(uri, webScriptContext.Tokens);
        webScriptContext.ScriptUrl = (url.indexOf('?') == -1 ? url : url.substring(0, url.indexOf('?')));
        
        // Get up the request path.
        // If none is supplied, assume the servlet path.
        String requestPath = (String) context.getValue("requestPath");
        if (requestPath == null)
        {
            requestPath = context.getRequest().getContextPath();
        }
        
        // if this webscript has been clicked, update the script URL - 
        if (context.getObject().getId().equals(context.getRequest().getParameter(PARAM_WEBSCRIPT_ID)))
        {
            webScriptContext.ExecuteUrl = context.getRequest().getParameter(PARAM_WEBSCRIPT_URL);
        }
        else
        {
            // else use the webscript default url
            webScriptContext.ExecuteUrl = requestPath + WEBSCRIPT_SERVICE_SERVLET + url;
        }
        
        // Set up character encoding.  If none, set the default.
        String encoding = context.getRequest().getCharacterEncoding();
        if (encoding == null)
        {
            try
            {
                context.getRequest().setCharacterEncoding(EncodingUtil.DEFAULT_ENCODING);
            }
            catch (UnsupportedEncodingException uee)
            {
                throw new RendererExecutionException("Unable to set encoding to default: " + EncodingUtil.DEFAULT_ENCODING, uee);
            }
        }
        
        // Set up state onto the web script context
        webScriptContext.RuntimeContainer = webScriptContainer;
        webScriptContext.RenderContext = context;
        webScriptContext.Object = context.getObject();
        webScriptContext.RequestContext = context;
        
        try
        {
            // Construct the Web Script Runtime
            // This bundles the container, the context and the encoding
            LocalWebScriptRuntime runtime = new LocalWebScriptRuntime(
                    new BufferedWriter(context.getResponse().getWriter(), 4096),
                    webScriptContainer, serverProperties, webScriptContext);
            
            // set the method onto the runtime
            if (context.getRequest().getMethod() != null)
            {
                runtime.setScriptMethod(context.getRequest().getMethod());
            }
            
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
            webScriptContainer.bindRenderContext(context);
            
            // Execute the script
            runtime.executeScript();
            
            // Be sure to unbind the request context thread local variable
            webScriptContainer.unbindRenderContext();
        }
        catch (IOException exc)
        {
            throw new RendererExecutionException("Unable to read back response from Web Script Runtime buffer", exc);
        }
    }
    
    /**
     * Helper to build argument map from the servlet request parameters.
     * 
     * @param request the request
     * 
     * @return the map< string, string>
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
