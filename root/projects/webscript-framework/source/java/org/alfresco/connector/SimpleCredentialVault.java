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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.config.RemoteConfigElement.CredentialVaultDescriptor;

/**
 * A simple implementation of a credential vault that does not persist anything
 * to disk or database.
 * <p>
 * Credentials can be stored and retrieved from this vault but they will be lost
 * when the server is restarted.
 * <p>
 * That said, this implementation will likely be very useable for any situations
 * where you wish to explicitly challenge the end user but only challenge them
 * once.
 * 
 * @author muzquiano
 */
public class SimpleCredentialVault implements CredentialVault, Serializable
{
    public final Map<String, Credentials> credentialsMap = new HashMap<String, Credentials>(8, 1.0f);
    public final CredentialVaultDescriptor descriptor;
    public final String id;

    /**
     * Instantiates a new simple credential vault.
     * 
     * @param id the id
     * @param descriptor the descriptor
     */    
    public SimpleCredentialVault(String id, CredentialVaultDescriptor descriptor)
    {
        this.id = id;
        this.descriptor = descriptor;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#store(java.lang.String, java.lang.String, org.alfresco.connector.Credentials)
     */
    public void store(Credentials credentials)
    {
        credentialsMap.put(credentials.getEndpointId(), credentials);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#retrieve(java.lang.String, java.lang.String)
     */
    public Credentials retrieve(String endpointId)
    {
        return (Credentials) credentialsMap.get(endpointId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#remove(java.lang.String)
     */
    public void remove(String endpointId)
    {
        credentialsMap.remove(endpointId);
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
        SimpleCredentials credentials = new SimpleCredentials(endpointId);
        store(credentials);
        
        return credentials;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#load()
     */
    public void load()
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#save()
     */
    public void save()
    {
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "SimpleCredentialVault - " + credentialsMap.toString();
    }    
}
