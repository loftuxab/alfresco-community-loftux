/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.springframework.extensions.webeditor.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webeditor.WEFApplication;
import org.springframework.extensions.webeditor.WEFPlugin;
import org.springframework.extensions.webeditor.WEFPluginRegistry;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * WebScript implementation for the WEF application script call.
 * <p>
 * Responsible for generating an WEFApplication object that retrieves and 
 * initialises all regsitered plugins.
 *
 * @author Gavin Cornwell
 */
public class WEFApplicationGet extends DeclarativeWebScript
{
    protected WEFPluginRegistry pluginRegistry;
    
    public void setPluginRegistry(WEFPluginRegistry registry)
    {
        this.pluginRegistry = registry;
    }
    
    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        // ensure an application plugin has been registered
        if (this.pluginRegistry.getApplications().size() == 0)
        {
            throw new IllegalStateException("A WEF application plugin could not be found");
        }
        
        // add the application name to the model
        model.put("appName", this.pluginRegistry.getApplications().get(0).getName());
        
        // build list of plugins excluding application plugins
        ArrayList<WEFPlugin> plugins = new ArrayList<WEFPlugin>(8);
        for (WEFPlugin plugin : this.pluginRegistry.getPlugins())
        {
            if ((plugin instanceof WEFApplication) == false)
            {
                plugins.add(plugin);
            }
        }
        
        // add the plugins to the model
        model.put("plugins", plugins);
    	
        return model;
    }
}