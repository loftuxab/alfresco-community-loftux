/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.connector;

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.surf.exception.CredentialVaultProviderException;

/**
 * Provides instances of credential vaults
 */
public class SimpleCredentialVaultProvider implements CredentialVaultProvider
{
    private ConfigService configService;
    private RemoteConfigElement config;
    
    /**
     * Reflection constructor
     */
    public SimpleCredentialVaultProvider()
    {
    }
    
    /**
     * @param configService the configService to set
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     * Provide a Credential Vault for the given id
     */
    public CredentialVault provide(String id) throws CredentialVaultProviderException
    {
        return new SimpleCredentialVault(id, getRemoteConfig());
    }

    /**
     * Generate a caching key
     */
    public String generateKey(String id, String userId)
    {
        return id;
    }
    
    /**
     * Gets the single instance of the remote config element.
     * 
     * @return the remote config
     */
    private RemoteConfigElement getRemoteConfig()
    {
        if (this.config == null)
        {
            // retrieve the remote configuration
            this.config = (RemoteConfigElement)this.configService.getConfig("Remote").getConfigElement("remote");
        }
        
        return this.config;
    }
}