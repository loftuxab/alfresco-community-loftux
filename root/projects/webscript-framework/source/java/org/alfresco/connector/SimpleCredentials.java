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

import java.util.HashMap;
import java.util.Iterator;

/**
 * Credentials for a given user. This stores credentials that are to be passed
 * to a back-end service in order to authenticate. Once these credentials are
 * used to authenticate, they may no longer be necessary as the service may hand
 * back "endpoint credentials" which are to be used on subsequent calls.
 * 
 * An example of a user credential might be username/password.
 * 
 * An example of an endpoint credential might be an Alfresco ticket.
 * 
 * @author muzquiano
 */
public class SimpleCredentials implements Credentials
{
    protected String endpointId;
    protected HashMap<String, Object> properties;

    /**
     * Instantiates a new user credential.
     * 
     * @param endpointId the endpoint id
     */
    public SimpleCredentials(String endpointId)
    {
        this.endpointId = endpointId;
        this.properties = new HashMap<String, Object>();
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#getEndpointId()
     */
    public String getEndpointId()
    {
        return this.endpointId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#getProperty(java.lang.String)
     */
    public Object getProperty(String key)
    {
        return this.properties.get(key);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String key, Object value)
    {
        this.properties.put(key, value);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#removeProperty(java.lang.String)
     */
    public void removeProperty(String key)
    {
        this.properties.remove(key);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#removeAllProperties(java.lang.String)
     */
    public void removeAllProperties(String key)
    {
        this.properties.clear();
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#getPropertyKeys()
     */
    public String[] getPropertyKeys()
    {
        String[] keys = new String[this.properties.keySet().size()];
        
        int count = 0;
        Iterator it = this.properties.keySet().iterator();
        while(it.hasNext())
        {
            keys[count] = (String) it.next();
            count++;
        }
        
        return keys;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.properties.toString();
    }
}
