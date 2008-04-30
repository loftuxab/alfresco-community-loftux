/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.RenderData;
import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.renderer.AbstractRenderer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author muzquiano
 */
public class WebScriptRenderer extends AbstractRenderer
{
    public void execute(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, RenderData renderData)
            throws RendererExecutionException
    {
        // get the application context and context objects
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
        TemplateProcessor templateProcessor = (TemplateProcessor) appContext.getBean("site.templateprocessor");
        Registry registry = (Registry) appContext.getBean("site.webscripts.registry");

        //
        //
        //
        // If the ModelObject being rendered is a component, then we allow
        // for the execution of .head templates.
        //
        //
        if (renderData.getObject() instanceof Component)
        {
            Component component = (Component) renderData.getObject();
            String url = component.getURL();
            if (url.lastIndexOf('?') != -1)
            {
                url = url.substring(0, url.lastIndexOf('?'));
            }
            Match match = registry.findWebScript("GET", url);
            if (match != null)
            {
                WebScript webScript = match.getWebScript();
                if (webScript != null)
                {
                    // found a webscript, build the path to the .head.ftl template
                    String path = webScript.getDescription().getId() + ".head.ftl";

                    Map<String, Object> model = new HashMap<String, Object>(8);
                    ProcessorModelHelper.populateTemplateModel(context, renderData, model);
                    
                    // get the template processor
                    if (templateProcessor.hasTemplate(path))
                    {
                        StringWriter out = new StringWriter();
                        templateProcessor.process(path, model, out);

                        String tags = out.toString();
                        RenderUtil.appendHeadTags(context, tags);
                    }
                }
            }
        }
        //
        //
        //

        // now process the webscript

        // get the webscript destination property
        String requestUri = this.getRenderer();

        // request path
        String requestPath = (String) renderData.get("requestPath"); // i.e.
        // /test/component1
        if (requestPath == null)
            requestPath = "/service";

        // TODO: Other args
        Map args = new HashMap();

        // character encoding
        String encoding = request.getCharacterEncoding();
        if (encoding == null)
        {
            try
            {
                request.setCharacterEncoding("UTF-8");
                encoding = request.getCharacterEncoding();
            }
            catch (UnsupportedEncodingException uee)
            {
            }
        }

        // get the web script runtime container
        String containerId = context.getConfig().getRendererProperty(
                getRendererType(), "container-bean");
        if (containerId == null || containerId.length() == 0)
        {
            containerId = "webscripts.container";
        }
        LocalWebScriptRuntimeContainer webScriptContainer = (LocalWebScriptRuntimeContainer) appContext.getBean(containerId);

        // build the web script context object
        LocalWebScriptContext webScriptContext = new LocalWebScriptContext();
        webScriptContext.RequestURI = requestUri;
        webScriptContext.RequestPath = requestPath;
        webScriptContext.renderData = renderData;
        webScriptContext.object = renderData.getObject();
        webScriptContext.Tokens = args;
        webScriptContext.scriptUrl = requestPath + requestUri;
        webScriptContext.requestContext = context;

        // build the runtime
        LocalWebScriptRuntime runtime = new LocalWebScriptRuntime(
                webScriptContainer, webScriptContext, encoding);

        // bind the request context with a threadlocal variable
        webScriptContainer.bindRequestContext(context);

        // Note: The model is created later on and will use
        // getScriptParameters and getTemplateParameters
        // from the Container. The container looks up the
        // thread local variable and does its thing.

        // execute the script
        runtime.executeScript();

        // unbind the request context
        webScriptContainer.unbindRequestContext();

        // read back the results
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
            exc.printStackTrace();
        }

        // commit back to output stream
        try
        {
            response.getWriter().write(buffer.toString());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
