/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.benchmark.framework.dataprovider;

import java.util.HashMap;
import java.util.Map;

public class PropertyProfile
{
    /** The type of the property */
    private PropertyType type;
    
    /** The name of the property */
    private String name;
    
    /** The restrictions to apply to the property */
    private Map<PropertyRestriction, Object> restrictions = new HashMap<PropertyRestriction, Object>();
    
    /**
     * Helper method to create a property profile for the small test property
     * 
     * @param name
     * @return
     */
    public static PropertyProfile createSmallTextProperty(String name)
    {
        // Rely on the default constraints to provide a small text string
        return new PropertyProfile(name, PropertyType.TEXT);
    }
    
    /**
     * Constructor
     * 
     * @param name  the name of the property
     * @param type  the type of the property
     */
    public PropertyProfile(String name, PropertyType type)
    {
        this.name = name;
        this.type = type;
    }
    
    /**
     * Set the property name
     * 
     * @param name  the property name
     */
    public void setPropertyName(String name)
    {
        this.name = name;        
    }
    
    /**
     * Get the property name
     * 
     * @return  the property name
     */
    public String getPropertyName()
    {
        return name;
    }
    
    /**
     * Set the property type
     * 
     * @param type  the property type
     */
    public void setPropertyType(PropertyType type)
    {
        this.type = type;
    }
    
    /**
     * Get the property type
     * 
     * @return  the property type
     */
    public PropertyType getPropertyType()
    {
        return type;
    }
    
    /**
     * Set a restriction on the property
     * 
     * @param restriction
     * @param value
     */
    public void setRestriction(PropertyRestriction restriction, Object value)
    {
        this.restrictions.put(restriction, value);
    }
    
    /**
     * Get a property restriction value
     * 
     * @param restriction
     * @return
     */
    public Object getRestriction(PropertyRestriction restriction)
    {
        return this.restrictions.get(restriction);
    }
    
    public enum PropertyType
    {
        TEXT,
        CONTENT,
        INT,
        LONG,
        DOUBLE,
        DATE,
        DATETIME,
        BOOLEAN
    }  
    
    public enum PropertyRestriction
    {
        MIN_LENGTH,
        MAX_LENGTH
    }
}