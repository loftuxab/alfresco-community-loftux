/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.config;

import java.util.List;

import org.alfresco.config.source.UrlConfigSource;
import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Spring bean used to deploy additional config files into the
 * injected config service.
 *
 * @author Gavin Cornwell
 */
public class ConfigBootstrap implements ConfigDeployer
{
    protected ConfigService configService;
    protected List<String> configs;
    
    /**
     * Set the configs
     * 
     * @param configs the configs
     */
    public void setConfigs(List<String> configs)
    {
        this.configs = configs;
    }
    
    /**
     * Sets the ConfigService instance to deploy to
     * 
     * @param configService ConfigService instance to deploy to
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * Method called by ConfigService when the configuration files
     * represented by this ConfigDeployer need to be initialised.
     * 
     *  @return List of ConfigDeployment objects
     */
    public List<ConfigDeployment> initConfig()
    {
        List<ConfigDeployment> deployed = null;
        
        if (configService != null && this.configs != null && this.configs.size() != 0)
        {
            UrlConfigSource configSource = new UrlConfigSource(this.configs);
            deployed = configService.appendConfig(configSource);
        }
        
        return deployed;
    }

    /**
     * Registers this object with the injected ConfigService
     */
    public void register()
    {
        if (configService == null)
        {
            throw new AlfrescoRuntimeException("Config service must be provided");
        }
        
        configService.addDeployer(this);
    }
}
