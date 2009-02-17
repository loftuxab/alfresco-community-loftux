/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.web.config;
 
/**
 * This class represents a single control-param configuration item.
 * 
 * @author Neil McErlean.
 */
public class ControlParam
{
    private final String name;
    private String value;

    /**
     * Constructs a ControlParam object with the specified name and value.
     * 
     * @param name the name of the param.
     * @param value the value associated with that name.
     */
    public ControlParam(String name, String value)
    {
    	if (value == null)
    	{
    		value = "";
    	}
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name of this ControlParam.
     * @return the param name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the value of this ControlParam.
     * @return the value.
     */
    public String getValue()
    {
        return value;
    }
    
    /* default */ void setValue(String newValue)
    {
    	this.value = newValue;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(name).append(":").append(value);
        return result.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return name.hashCode() + 7 * value.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == this)
        {
            return true;
        }
        else if (otherObj == null
                || !otherObj.getClass().equals(this.getClass()))
        {
            return false;
        }
        ControlParam otherCP = (ControlParam) otherObj;
        return otherCP.name.equals(this.name)
                && otherCP.value.equals(this.value);
    }
}