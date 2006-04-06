/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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