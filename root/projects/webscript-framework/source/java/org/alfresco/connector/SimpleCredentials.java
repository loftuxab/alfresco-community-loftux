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

/**
 * Credentials for a given user.  This stores credentials that are
 * to be passed to a back-end service in order to authenticate.  Once
 * these credentials are used to authenticate, they may no longer be
 * necessary as the service may hand back "endpoint credentials" which
 * are to be used on subsequent calls.
 * 
 * An example of a user credential might be username/password.
 * 
 * An example of an endpoint credential might be an Alfresco ticket.
 * 
 * @author muzquiano
 */
public class SimpleCredentials implements Credentials
{
	protected String id;
	protected String description;
	protected HashMap<String, Object> properties;
	
	/**
	 * Instantiates a new user credential.
	 * 
	 * @param id the id
	 */
	public SimpleCredentials(String id)
	{
		this(id, null);		
	}
	
	/**
	 * Instantiates a new user credential.
	 * 
	 * @param id the id
	 * @param description the description
	 */
	public SimpleCredentials(String id, String description)
	{
		this.id = id;
		this.description = description;
		this.properties = new HashMap<String, Object>();
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.connector.Credential#getId()
	 */
	public String getId()
	{
		return this.id;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Gets a given property.
	 * 
	 * @param key the key
	 * 
	 * @return the property
	 */
	public Object getProperty(String key)
	{
		return this.properties.get(key);
	}
	
	/**
	 * Sets a given property.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void setProperty(String key, Object value)
	{
		this.properties.put(key, value);
	}

    @Override
    public String toString()
    {
        return this.properties.toString();
    }
}
