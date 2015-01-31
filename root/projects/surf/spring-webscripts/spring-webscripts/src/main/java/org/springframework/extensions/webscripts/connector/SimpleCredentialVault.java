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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;

/**
 * A simple implementation of a credential vault.
 * <p>
 * Credentials can be stored and retrieved from this vault but they will be lost
 * when the server is restarted.
 */
public class SimpleCredentialVault implements CredentialVault, Serializable
{
    final protected String id;
    final protected Map<String, Credentials> credentialsMap = new HashMap<String, Credentials>(8, 1.0f);
    final protected RemoteConfigElement remote;
    
    /**
     * Instantiates a new simple credential vault.
     * 
     * @param id the id
     */    
    public SimpleCredentialVault(String id, RemoteConfigElement remote)
    {
        this.id = id;
        this.remote = remote;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#store(java.lang.String, java.lang.String, org.alfresco.connector.Credentials)
     */
    public void store(Credentials credentials)
    {
        this.credentialsMap.put(credentials.getEndpointId(), credentials);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#retrieve(java.lang.String, java.lang.String)
     */
    public Credentials retrieve(String endpointId)
    {
        Credentials credentials = this.credentialsMap.get(endpointId);
        if (credentials == null)
        {
            // Remap endpoint allow an endpoint to share another endpoint credentials
            // @see ConnectorService.getConnectorSession()
            EndpointDescriptor desc = this.remote.getEndpointDescriptor(endpointId);
            String remapId = desc.getParentId();
            if (remapId != null)
            {
                credentials = this.credentialsMap.get(remapId);
            }
        }
        return credentials;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#remove(java.lang.String)
     */
    public void remove(String endpointId)
    {
        this.credentialsMap.remove(endpointId);
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#hasCredentials(java.lang.String, java.lang.String)
     */
    public boolean hasCredentials(String endpointId)
    {
        return (retrieve(endpointId) != null);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#getStoredIds()
     */
    public String[] getStoredIds()
    {
        return this.credentialsMap.keySet().toArray(new String[this.credentialsMap.size()]);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#newCredentials(java.lang.String)
     */
    public Credentials newCredentials(String endpointId)
    {
        CredentialsImpl credentials = new CredentialsImpl(endpointId);
        store(credentials);
        
        return credentials;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "SimpleCredentialVault - " + this.id;
    }    
}
