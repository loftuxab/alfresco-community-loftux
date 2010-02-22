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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.wef.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.wef.WEFApplication;
import org.alfresco.wef.WEFPlugin;
import org.alfresco.wef.WEFPluginRegistry;
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