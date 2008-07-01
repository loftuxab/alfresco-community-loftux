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

/**
 * Interface that describes the credentials for a given service 
 * or user.
 * 
 * @author muzquiano
 */
public interface Credentials
{
    public final static String CREDENTIAL_USERNAME = "cleartextUsername";
    public final static String CREDENTIAL_PASSWORD = "cleartextPassword";

    /**
     * Gets the endpoint id.
     * 
     * @return the endpoint id
     */
    public String getEndpointId();

    /**
     * Gets a given property
     * 
     * @param key the key
     * 
     * @return the property
     */
    public Object getProperty(String key);

    /**
     * Sets a given property
     * 
     * @param key the key
     * @param value the value
     */
    public void setProperty(String key, Object value);

    /**
     * Removes a given property
     * 
     * @param key
     */
    public void removeProperty(String key);
    
    /**
     * Removes all properties
     * 
     * @param key
     */
    public void removeAllProperties(String key);
    
    /**
     * Returns the property keys
     * 
     * @return array of property keys
     */
    public String[] getPropertyKeys();
    
    /**
     * Returns whether this credential is persistent
     * 
     * A persistent credential is written to a persistent vault.
     * A non-persistent credential is loaded into the vault but never stored
     * 
     * @return
     */
    public boolean isPersistent();    
}
