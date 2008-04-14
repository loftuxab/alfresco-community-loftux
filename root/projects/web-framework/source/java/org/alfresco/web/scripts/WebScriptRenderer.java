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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.renderer.AbstractRenderer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author muzquiano
 */
public class WebScriptRenderer extends AbstractRenderer
{
    public void executeImpl(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, RuntimeConfig modelConfig)
            throws RendererExecutionException
    {
        // pull values from the config
        // these are values that are stored on the component instance
        // or on the template instance
        String requestUri = (String) modelConfig.get("uri"); // i.e. /test/component1
        String requestPath = (String) modelConfig.get("requestPath"); // i.e. /test/component1
        if (requestPath == null)
            requestPath = "/service";

        // TODO: Other args
        Map args = new HashMap();

        // build the web script context object
        LocalWebScriptContext webScriptContext = new LocalWebScriptContext();
        webScriptContext.RequestURI = requestUri;
        webScriptContext.RequestPath = requestPath;
        webScriptContext.modelConfig = modelConfig;
        webScriptContext.modelObject = modelConfig.getObject();
        webScriptContext.Tokens = args;

        System.out.println(" -> requesturi: " + webScriptContext.RequestURI);
        System.out.println(" -> requestpath: " + webScriptContext.RequestPath);

        // get the application context
        // get the web script runtime container
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
        RuntimeContainer webScriptContainer = (RuntimeContainer) appContext.getBean("webscripts.container");

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

        // build the runtime
        LocalWebScriptRuntime runtime = new LocalWebScriptRuntime(
                webScriptContainer, webScriptContext, encoding);

        // execute the script
        runtime.executeScript();

        // read back the results
        Reader reader = (Reader) runtime.getResponseReader();
        BufferedReader in = new BufferedReader(reader);
        char[] cbuf = new char[65536];
        StringBuffer buffer = new StringBuffer();
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
