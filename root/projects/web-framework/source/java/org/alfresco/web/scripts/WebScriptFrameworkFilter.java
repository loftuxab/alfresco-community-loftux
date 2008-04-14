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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.web.site.DefaultModel;
import org.alfresco.web.site.Framework;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.HttpRequestContextFactory;
import org.alfresco.web.site.IModel;
import org.alfresco.web.site.RequestContextFactory;
import org.alfresco.web.site.RequestContextFactoryBuilder;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.filesystem.FileSystemManager;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author muzquiano
 */
public class WebScriptFrameworkFilter implements Filter
{
    public WebScriptFrameworkFilter()
    {
        super();
    }

    public static void initFramework(ServletContext servletContext, ApplicationContext context)
    {
        synchronized (WebScriptFrameworkFilter.class)
        {
            if (!Framework.isInitialized())
            {
                // get the config service
                ConfigService configService = (ConfigService) context.getBean("site.config");
                Config config = configService.getConfig("WebFramework");

                // set the config onto the framework
                WebScriptFrameworkConfig webFrameworkConfig = new WebScriptFrameworkConfig(config);
                Framework.setConfig(webFrameworkConfig);
                
                // set the model onto the framework
                String modelRootPath = webFrameworkConfig.getModelRootPath();
                IFileSystem modelFileSystem = FileSystemManager.getLocalFileSystem(servletContext, modelRootPath);
                IModel model = new DefaultModel(modelFileSystem);
                Framework.setModel(model);
                
                System.out.println("WebScriptFrameworkFilter - Initialized WebScript Framework");
            }
        }
    }

    public void init(FilterConfig config) throws ServletException
    {
        // make sure the default framework is loaded
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        ServletContext servletContext = config.getServletContext();
        initFramework(servletContext, context);
    }

    public static void initRequestContext(HttpServletRequest request)
            throws Exception
    {
        // get whatever factory builder we're configured to use
        RequestContextFactory factory = RequestContextFactoryBuilder.newFactory();
        if (factory instanceof HttpRequestContextFactory)
        {
            // this is what we expect
            HttpRequestContext context = ((HttpRequestContextFactory) factory).newInstance(request);
            RequestUtil.setRequestContext(request, context);
        }
        else
        {
            throw new Exception(
                    "The configured request context factory does not extend from HttpRequestContextFactory");
        }
    }

    public void doFilter(ServletRequest sreq, ServletResponse sresp,
            FilterChain chain) throws IOException, ServletException
    {
        try
        {
            initRequestContext((HttpServletRequest) sreq);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }

        // chain to other filters        
        chain.doFilter(sreq, sresp);
        return;
    }

    public void destroy()
    {
        // Nothing to do
    }

}
