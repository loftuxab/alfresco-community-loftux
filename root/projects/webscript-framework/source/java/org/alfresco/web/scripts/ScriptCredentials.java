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
package org.alfresco.web.scripts;

import java.io.Serializable;

import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.Credentials;

/*
 * @author muzquiano
 */
public final class ScriptCredentials implements Serializable
{
    final private CredentialVault vault;
    final private Credentials credentials;
    final private boolean hideNonPersistent;
    
    protected ScriptableMap<String, Serializable> properties;

    public ScriptCredentials(CredentialVault vault, Credentials credentials)
    {
        this(vault, credentials, false);
    }
    
    public ScriptCredentials(CredentialVault vault, Credentials credentials, boolean hideNonPersistent)
    {
        this.vault = vault;
        this.credentials = credentials;
        this.hideNonPersistent = hideNonPersistent;
    }

    /**
     * Returns the properties of the credentials
     */
    public ScriptableMap<String, Serializable> getProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableLinkedHashMap<String, Serializable>();
            
            // show either persistent credentials
            // or non-persistent credentials (when persistentOnly = false)
            if (!isHidden())
            {            
                // put credentials properties onto the map
                String[] keys = this.credentials.getPropertyKeys();
                for(int i = 0; i < keys.length; i++)
                {
                    Object propertyValue = this.credentials.getProperty(keys[i]);
                    this.properties.put(keys[i], (Serializable)propertyValue);
                }
            }
        }
        
        return this.properties;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    public boolean isHidden()
    {
        return !isPersistent() && hideNonPersistent;
    }
    
    public boolean isPersistent()
    {
        return credentials.isPersistent();
    }
}
