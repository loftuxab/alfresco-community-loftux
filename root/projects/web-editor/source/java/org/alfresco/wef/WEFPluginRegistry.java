/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.wef;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Registry of Web Editor Framework plugins.
 *
 * @author Gavin Cornwell
 */
public class WEFPluginRegistry
{
    public static final String BEAN_NAME = "wefPluginRegistry";
    
    private static final Log logger = LogFactory.getLog(WEFPluginRegistry.class);
    
    private Map<String, WEFPlugin> plugins;
    private Map<String, WEFApplication> applications;
    
    /**
     * Default constructor.
     */
    public WEFPluginRegistry()
    {
        this.plugins = new LinkedHashMap<String, WEFPlugin>(8);
        this.applications = new LinkedHashMap<String, WEFApplication>(1);
    }
    
    /**
     * Adds a plugin.
     * 
     * @param plugin The plugin to add
     */
    public void addPlugin(WEFPlugin plugin)
    {
        // add the plugin
        this.plugins.put(plugin.getName(), plugin);
        
        if (logger.isDebugEnabled())
            logger.debug("Added plugin to registry: " + plugin);
        
        if (plugin instanceof WEFApplication)
        {
            // check there isn't already an application registered
            if (this.applications.size() > 0)
            {
                throw new IllegalStateException("Only one WEF application plugin is currently supported");
            }
            
            this.applications.put(plugin.getName(), (WEFApplication)plugin);
            
            if (logger.isDebugEnabled())
                logger.debug("Added application plugin to registry: " + plugin);
        }
    }
    
    /**
     * Returns all the regsistered plugins.
     * 
     * @return List of all registered plugins
     */
    public List<WEFPlugin> getPlugins()
    {
        return new ArrayList<WEFPlugin>(this.plugins.values());
    }
    
    /**
     * Returns the plugin with the given name.
     * 
     * @param name Name of a plugin to retrieve
     * @return A WEFPlugin object or null if a plugin with the given name does not exist
     */
    public WEFPlugin getPlugin(String name)
    {
        return this.plugins.get(name);
    }
    
    /**
     * Returns all the registered application plugins.
     * 
     * @return List of all registered application plugins
     */
    public List<WEFApplication> getApplications()
    {
        return new ArrayList<WEFApplication>(this.applications.values());
    }
    
    /**
     * Returns the application plugin with the given name.
     * 
     * @param name Name of an application plugin to retrieve
     * @return A WEFApplication object or null if an application plugin with the given name does not exist
     */
    public WEFApplication getApplication(String name)
    {
        return this.applications.get(name);
    }
    
    /**
     * Returns a list of unique resources for all the registered plugins
     * and their dependencies.
     * 
     * @return List of WEFResource objects representing all resources
     */
    public List<WEFResource> getPluginResources()
    {
        // create map to hold unique list of resources
        LinkedHashMap<String, WEFResource> resourcesMap = new LinkedHashMap<String, WEFResource>(32);
        
        if (this.applications.size() > 0)
        {
            for (WEFApplication app : this.applications.values())
            {
                // recursively build a map of all the application's dependencies
                buildResourceMap(app, resourcesMap);
            }
        }
        else
        {
            for (WEFPlugin plugin : this.plugins.values())
            {
                // recursively build a map of all the plugin's dependencies
                buildResourceMap(plugin, resourcesMap);
            }
        }
        
        return new ArrayList<WEFResource>(resourcesMap.values());
    }
 
    /**
     * Returns a list of unique resources for the given resource
     * and all of it's dependencies.
     * 
     * @param resource A WEFResource object 
     * @return List of WEFResource objects representing all resources
     */
    public List<WEFResource> getAllResources(WEFResource resource)
    {
        // recursively build a map of all the resource's dependencies 
        LinkedHashMap<String, WEFResource> resourcesMap = new LinkedHashMap<String, WEFResource>(32);
        buildResourceMap(resource, resourcesMap);
        
        // return the list of unique resources
        return new ArrayList<WEFResource>(resourcesMap.values());
    }
    
    /**
     * Recursively builds a Map of unique resources for the given resource object.
     * 
     * @param resource The WEFResource to get all resources for
     * @param resources Map of unique resources found
     */
    protected void buildResourceMap(WEFResource resource, Map<String, WEFResource> resources)
    {
        // recurse through the resource's dependencies
        for (WEFResource dependency : resource.getDependencies())
        {
            buildResourceMap(dependency, resources);
        }
        
        // add the given resource to the map, keyed by the name
        if (resources.containsKey(resource.getName()) == false)
        {
            resources.put(resource.getName(), resource);
        }
    }
}
