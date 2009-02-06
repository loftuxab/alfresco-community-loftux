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
package org.alfresco.web.site;

import java.util.Map;

import org.alfresco.web.config.WebFrameworkConfigElement;
import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.model.Configuration;
import org.alfresco.web.framework.model.Page;

/**
 * Helper functions for web sites
 * 
 * @author muzquiano
 */
public class SiteUtil
{
    private static final String DEFAULT_SITE_CONFIGURATION_ID = "default.site.configuration";

    /**
     * Returns the root page for the current request context
     * 
     * @param context the context
     * 
     * @return the root page
     */
    public static Page getRootPage(RequestContext context)
    {
        return getRootPage(context, getSiteConfiguration(context));        
    }
    
    /**
     * Returns the root page for the given site configuration
     * 
     * @param context
     * @param siteConfiguration
     * 
     * @return the root page instance
     */
    public static Page getRootPage(RequestContext context, Configuration siteConfiguration)
    {
        Page rootPage = null;
        
        // check the site configuration
        if (siteConfiguration != null)
        {
            String rootPageId = siteConfiguration.getProperty("root-page");
            if (rootPageId != null)
            {
                Page page = context.getModel().getPage(rootPageId);
                if (page != null)
                {
                    rootPage = page;
                }
            }
        }
                
        return rootPage;
    }

    /**
     * Returns the site configuration object to use for the current request.
     * 
     * At present, this is a very simple calculation since we either look to
     * the current application default site id or we use a default.
     * 
     * In the future, we will seek to support multiple site configurations
     * per web application (i.e. one might be for html, another for wireless,
     * another for print channel).
     * 
     * @param context the context
     * 
     * @return the site configuration
     */
    public static Configuration getSiteConfiguration(RequestContext context)
    {
        // try to load the site configuration id specified by the application default
        String siteConfigId = getConfig().getDefaultSiteConfigurationId();
        
        Configuration configuration = (Configuration) context.getModel().getConfiguration(siteConfigId);
        if (configuration == null)
        {
            // if nothing was found, try to load the "stock" configuration id
            siteConfigId = DEFAULT_SITE_CONFIGURATION_ID;
            
            configuration = (Configuration) context.getModel().getConfiguration(siteConfigId);            
            if (configuration == null)
            {
                // if we still haven't found an object, then we can do an exhaustive lookup
                // this is a last resort effort to find the site config object                
                Map<String,ModelObject> configs = context.getModel().findConfigurations("site");
                if (configs != null && configs.size() > 0)
                {
                    configuration = (Configuration) configs.values().iterator().next();
                    
                    if (configuration != null && context.getLogger().isWarnEnabled())
                        context.getLogger().warn("Site configuration '" + configuration.getId() + "' discovered via exhaustive lookup.  Please adjust configuration files to optimize performance.");
                }                
            }
        }
        
        return configuration;
    }
        
    /**
     * Returns the web framework configuration element
     * 
     * @return the config
     */
    protected static WebFrameworkConfigElement getConfig()
    {
        return FrameworkHelper.getConfig();
    }

}
