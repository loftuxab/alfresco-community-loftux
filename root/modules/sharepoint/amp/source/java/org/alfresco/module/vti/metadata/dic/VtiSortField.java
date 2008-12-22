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
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard sorting fields.</p>
 * 
 * @author PavelYur
 */
public enum VtiSortField
{
    /**
     * sort by name
     */
    NAME ("BaseName"),

    /**
     * sort by type
     */
    TYPE ("DocIcon"),

    /**
     * sort by modifier
     */
    MODIFIEDBY ("Editor"),

    /**
     * sort by last modified date
     */
    MODIFIED ("Last_x0020_Modified"),

    /**
     * sort by checked out username
     */
    CHECKEDOUTTO ("CheckedOutTitle");

    private String value;
    
    VtiSortField(String value)
    {
        this.value = value;
        
    }    
    
    public String toString()
    {        
        return value;
    }
    
    public static VtiSortField value(String stringValue)
    {
        VtiSortField[] values = values();
        for (VtiSortField value:values)
        {
            if (stringValue.equals(value.value))
                return value;
        }
        return null;
    }
    
}
