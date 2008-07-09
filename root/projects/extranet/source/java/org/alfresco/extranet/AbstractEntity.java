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
package org.alfresco.extranet;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class AbstractEntity.
 * 
 * @author muzquiano
 */
public abstract class AbstractEntity implements Entity
{
    protected Map<String, Object> properties;

    /**
     * Instantiates a new abstract entity.
     */
    public AbstractEntity()
    {
        this.properties = new HashMap<String, Object>(16);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.Entity#getEntityType()
     */
    public abstract String getEntityType();
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.Entity#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String propertyName, Object propertyValue)
    {
        this.properties.put(propertyName, propertyValue);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.Entity#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName)
    {
        return this.properties.get(propertyName);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.Entity#getStringProperty(java.lang.String)
     */
    public String getStringProperty(String propertyName)
    {
        return (String) getProperty(propertyName);
    }
    
    /**
     * Gets the property names.
     * 
     * @return the property names
     */
    public static String[] getPropertyNames()
    {
        return new String[] { };
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.Entity#getEntityId()
     */
    public abstract String getEntityId();
}
