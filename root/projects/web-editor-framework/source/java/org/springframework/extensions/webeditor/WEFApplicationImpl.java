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
package org.springframework.extensions.webeditor;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a WEFApplication that is capable of registering itself
 * with a plugin registry.
 *
 * @author Gavin Cornwell
 */
public class WEFApplicationImpl extends WEFPluginImpl implements WEFApplication
{
    public static final String TYPE_APPLICATION = "application";
    
    /**
     * Default constructor.
     */
    public WEFApplicationImpl()
    {
        this.type = TYPE_APPLICATION;
    }

    /**
     * Returns the list of dependencies for the application, this includes
     * the configured dependencies and automatically includes 
     * all non-application plugins.
     * 
     * @return List of WEFResource objects representing the dependencies
     */
    @Override
    public List<WEFResource> getDependencies()
    {
        // start with direct dependencies 
        List<WEFResource> dependencies = new ArrayList<WEFResource>(super.getDependencies());
        
        // add all non application plugins as dependencies
        List<WEFPlugin> plugins = this.registry.getPlugins();
        for (WEFPlugin plugin : plugins)
        {
            if ((plugin instanceof WEFApplication) == false)
            {
                dependencies.add(plugin);
            }
        }
        
        return dependencies;
    }
}
