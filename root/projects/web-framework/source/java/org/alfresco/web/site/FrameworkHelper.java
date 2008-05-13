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

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.connector.ConnectorFactory;
import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.CredentialVaultFactory;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.WebFrameworkConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.alfresco.web.site.exception.FrameworkInitializationException;
import org.alfresco.web.site.exception.RequestContextException;
import org.alfresco.web.site.filesystem.FileSystemManager;
import org.alfresco.web.site.filesystem.IFileSystem;
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
    
    /**
     * Initializes the Web Framework.  This method must be called once
     * in order to instruct the framework to walk through its appropriate
     * startup steps.  This involves locating the configuration service,
     * connecting the appropriate model stores and the like.
     * 
     * This should be very quick to do.  The objects loaded here are
     * classloader scoped and should then be available to all future
     * requests.
     * 
     * @param servletContext
     * @param context
     */
    public synchronized static void initFramework(ServletContext servletContext,
            ApplicationContext context)
    	throws FrameworkInitializationException
    {
        if (!isInitialized())
        {
        	// Store the application context
        	applicationContext = context;
        	
            
            // init config cache
            ConfigService configService = (ConfigService) applicationContext.getBean("web.config");
            Config config = configService.getConfig("Remote");
            remoteConfig = (RemoteConfigElement)config.getConfigElement("remote");
            config = getConfigService().getConfig("WebFramework");
            webFrameworkConfig = (WebFrameworkConfigElement)config.getConfigElement("web-framework");
            
            
            /**
             * Loads the model implementation onto the framework.
             * 
             * A model implementation is the persister layer between the
             * model objects and the XML on disk.
             * 
             * TODO:  At present, this mounts against a FileSystem
             * implementation which is pointed at the model root directory.
             * We would like to change this to use the Store abstraction
             * (which is underway but not yet complete).
             */
            String modelRootPath = getConfig().getRootPath();
            IFileSystem modelFileSystem = FileSystemManager.getLocalFileSystem(
                    servletContext, modelRootPath);
            Model model = new DefaultModel(modelFileSystem);
            setModel(model);
            
            
            // Fetches the credential vault and makes it available
            CredentialVaultFactory vaultFactory = CredentialVaultFactory.getInstance(getConfigService());
            try 
            {
            	// grab the default vault
            	credentialVault = (CredentialVault) vaultFactory.vault();
            }
            catch(RemoteConfigException rce)
            {
            	throw new FrameworkInitializationException("Unable to load the default credential vault", rce);
            }
            
            
            logger.info("Successfully Initialized Web Framework");
        }
    }
    
    /**
     * Creates and initializes a single request context instance.
     * 
     * This method is called once at the top of the request processing chain.
     * It routes through the configured RequestContextFactory implementation
     * so as to create a RequestContext implementation.
     * 
     * @param request
     * @throws RequestContextException
     */
    public static RequestContext initRequestContext(ServletRequest request)
        throws RequestContextException
    {
        /**
         * Retrieve the configured RequestContextFactory implementation.
         * If one has already been retrieved, it will be reused.
         * If not, then a new one will be instantiated.
         */
        RequestContextFactory factory = RequestContextFactoryBuilder.newFactory();
        if(factory == null)
        {
            throw new RequestContextException("Unable to load RequestContextFactory");
        }
        
        /**
         * Using the factory, produce a new RequestContext instance for the
         * given request.
         */
        RequestContext context = factory.newInstance(request);
        if(context == null)
        {
            throw new RequestContextException("A request context was not manufactured");
        }
        
        /**
         * Bind the new request context instance to the request.
         */
        RequestUtil.setRequestContext(request, context);
        
        return context;
    }
    
    public static boolean isInitialized()
    {
        return (getModel() != null);
    }
    
    public static Model getModel()
    {
        return model;
    }

    public static void setModel(Model model)
    {
        FrameworkHelper.model = model;
    }
    
    public static Log getLogger()
    {
        return logger;
    }
    
    public static RemoteConfigElement getRemoteConfig()
    {
    	return remoteConfig;
    }
    
    public static WebFrameworkConfigElement getConfig()
    {
    	return webFrameworkConfig;
    }
    
    public static ConfigService getConfigService()
    {
    	return (ConfigService)applicationContext.getBean("web.config");
    }
    
    public static ApplicationContext getApplicationContext()
    {
    	return applicationContext;
    }
    
    public static CredentialVault getCredentialVault()
    {
    	return credentialVault;
    }
    
    public static ConnectorFactory getConnectorFactory()
    {
    	return ConnectorFactory.getInstance(getConfigService());
    }
    
    public static EndpointDescriptor getEndpoint(String endpointId)
    {
    	return getRemoteConfig().getEndpointDescriptor(endpointId);
    }
    
    private static Model model = null;
    private static ApplicationContext applicationContext = null;
    private static CredentialVault credentialVault = null;
    private static RemoteConfigElement remoteConfig = null;
    private static WebFrameworkConfigElement webFrameworkConfig = null;
}
