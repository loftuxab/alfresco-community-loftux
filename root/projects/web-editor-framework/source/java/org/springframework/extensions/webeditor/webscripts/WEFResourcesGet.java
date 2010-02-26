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
package org.springframework.extensions.webeditor.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webeditor.WEFPluginRegistry;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * WebScript implementation for the WEF resources script call.
 * <p>
 * Responsible for generating a WEF.addResource() JavaScript call for each resource 
 * required by the application and registered plugins.
 * Also responsible for generating the WEF.run("app name") JavaScript call to
 * execute the application.
 *
 * @author Gavin Cornwell
 */
public class WEFResourcesGet extends DeclarativeWebScript
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
        
        // add all the application and plugin resources to the model
    	model.put("resources", this.pluginRegistry.getPluginResources());
    	
        return model;
    }
}