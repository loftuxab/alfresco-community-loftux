/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.connector;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of a credential vault that does not persist anything
 * to disk or database.
 * 
 * Credentials can be stored and retrieved from this vault but they will perish
 * into lands unknown when the server is restarted.
 * 
 * That said, this implementation will likely be very useable for any situations
 * where you wish to explicitly challenge the end user but only challenge them
 * once. If the vault is loaded as a Spring bean, it will remain active across
 * user sessions.
 * 
 * @author muzquiano
 */
public class SimpleCredentialVault implements CredentialVault
{
    public static Map<String, Credentials> credentials = new HashMap<String, Credentials>(16, 1.0f);

    public SimpleCredentialVault()
    {
    }

    /**
     * Stores a given credential into the vault
     * 
     * @param key the key
     * @param credentials the credentials
     */
    public void store(String key, Credentials credentials)
    {
        this.credentials.put(key, credentials);
    }

    /**
     * Retrieves a credential from the vault
     * 
     * @param key the key
     * 
     * @return the credentials
     */
    public Credentials retrieve(String key)
    {
        return (Credentials) this.credentials.get(key);
    }

    /**
     * @return true if any credentials are stored for this user
     */
    public boolean hasCredentials(User user)
    {
        boolean found = false;

        String lookup = "_" + user.getId();
        for (String key : this.credentials.keySet())
        {
            if (key.endsWith(lookup))
            {
                found = true;
                break;
            }
        }

        return found;
    }

    /**
     * Tells the Credential Vault to load state from persisted store
     */
    public void load()
    {
    }

    /**
     * Tells the Credential Vault to write state to persisted store
     */
    public void save()
    {
    }

    @Override
    public String toString()
    {
        return this.credentials.toString();
    }
}
