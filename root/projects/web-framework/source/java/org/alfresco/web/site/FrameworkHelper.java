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
package org.alfresco.web.site;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.web.site.filesystem.FileSystemManager;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.servlet.DispatcherServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * Static utility functions for starting up the Framework
 * 
 * @author muzquiano
 */
public class FrameworkHelper
{
    private static Log logger = LogFactory.getLog(FrameworkHelper.class);
    
    public static void initFramework(ServletContext servletContext,
            ApplicationContext context)
    {
        synchronized (DispatcherServlet.class)
        {
            if (!Framework.isInitialized())
            {
                // get the config service
                ConfigService configService = (ConfigService) context.getBean("site.config");
                Config config = configService.getConfig("WebFramework");

                // set the config onto the framework
                DefaultFrameworkConfig webFrameworkConfig = new DefaultFrameworkConfig(config);
                Framework.setConfig(webFrameworkConfig);

                // set the model onto the framework
                String modelRootPath = webFrameworkConfig.getModelRootPath();
                IFileSystem modelFileSystem = FileSystemManager.getLocalFileSystem(
                        servletContext, modelRootPath);
                IModel model = new DefaultModel(modelFileSystem);
                Framework.setModel(model);

                logger.info("Successfully Initialized Web Framework");
            }
        }
    }
    
    public static void initRequestContext(ServletRequest request)
        throws Exception
    {
        // get whatever factory builder we're configured to use
        RequestContextFactory factory = RequestContextFactoryBuilder.sharedFactory();
        if (factory instanceof HttpRequestContextFactory)
        {
            if(request instanceof HttpServletRequest)
            {
                // this is what we expect
                RequestContext context = ((HttpRequestContextFactory)factory).newInstance((HttpServletRequest)request);
                RequestUtil.setRequestContext((HttpServletRequest)request, context);
            }
        }
        else
        {
            throw new Exception(
                    "The configured request context factory does not extend from HttpRequestContextFactory");
        }        
    }
}
