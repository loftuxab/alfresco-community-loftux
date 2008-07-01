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
import org.alfresco.web.site.RequestContext;

/*
 * @author muzquiano
 */
public final class ScriptCredentials extends ScriptBase
{
    protected CredentialVault vault;
    protected Credentials credentials;
    protected boolean hideNonPersistent;

    public ScriptCredentials(RequestContext context, CredentialVault vault, Credentials credentials)
    {
        this(context, vault, credentials, false);
    }
    
    public ScriptCredentials(RequestContext context, CredentialVault vault, Credentials credentials, boolean hideNonPersistent)
    {
        super(context);
        
        this.vault = vault;
        this.credentials = credentials;
        this.hideNonPersistent = hideNonPersistent;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        if(this.properties == null)
        {
            this.properties = new ScriptableMap<String, Serializable>();
            
            // show either persistent credentials
            // or non-persistent credentials (when persistentOnly = false)
            if(!isHidden())
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
    //
    public boolean isHidden()
    {
        return !isPersistent() && hideNonPersistent;
    }
    
    public boolean isPersistent()
    {
        return credentials.isPersistent();
    }

}

