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

/**
 * Implementation of a WEFPlugin that is capable of registering itself
 * with a plugin registry.
 *
 * @author Gavin Cornwell
 */
public class WEFPluginImpl extends WEFResourceImpl implements WEFPlugin
{
    public static final String TYPE_PLUGIN = "plugin"; 
    
    protected WEFPluginRegistry registry;
    
    /**
     * Default constructor
     */
    public WEFPluginImpl()
    {
        this.type = TYPE_PLUGIN;
    }
    
    /**
     * Sets the WEFPluginRegistry instance to register all plugins with.
     * 
     * @param registry The WEFPluginRegistry instance
     */
    public void setPluginRegistry(WEFPluginRegistry registry)
    {
        this.registry = registry;
    }
    
    /**
     * Registers this plugin with the plugin registry.
     */
    public void register()
    {
        if (this.registry != null)
        {
            this.registry.addPlugin(this);
        }
    }
}
