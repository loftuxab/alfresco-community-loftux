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
package org.alfresco.connector;

import org.springframework.extensions.config.ConfigService;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;

/**
 * An abstract implementation of a persistent credential vault
 * where crednetials can be stored from a persistent location.
 *  
 * @author muzquiano
 */
public abstract class AbstractPersistentCredentialVault extends SimpleCredentialVault
{
    protected ConfigService configService;
    
    /**
     * Instantiates a new persistentcredential vault.
     * 
     * @param id the id
     */    
    public AbstractPersistentCredentialVault(String id)
    {
        super(id);
    }
    
    /**
     * Sets the config service.
     * 
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#store(java.lang.String, java.lang.String, org.alfresco.connector.Credentials)
     */
    public void store(Credentials credentials)
    {
        // check whether the given credentials should be flagged
        // as persistent
        String endpointId = credentials.getEndpointId();
        EndpointDescriptor descriptor = getRemoteConfig().getEndpointDescriptor(endpointId);
        if(descriptor != null)
        {
            // mark the persistence attribute onto the credentials
            ((CredentialsImpl)credentials).persistent = descriptor.getPersistent();
        }
        
        super.store(credentials);
    }
    
    /**
     * @return RemoteConfigElement
     */
    protected RemoteConfigElement getRemoteConfig()
    {
        RemoteConfigElement remoteConfig = (RemoteConfigElement)configService.getConfig(
                "Remote").getConfigElement("remote");
        return remoteConfig;
    }    

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "PersistentCredentialVault - " + this.id;
    }    
}
