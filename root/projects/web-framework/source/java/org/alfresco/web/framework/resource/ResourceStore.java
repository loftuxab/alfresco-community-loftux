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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;


/**
 * Interface which Resource abstractions can use to look up
 * attributes and values from resources.
 * 
 * @author muzquiano
 */
public interface ResourceStore 
{
    /**
     * Returns the names of attributes on the given resource
     * 
     * @param id
     * @return
     */
    public String[] getAttributeNames(String id);
    
    /**
     * Returns the given attribute for the given resource
     * 
     * @param id
     * @param name
     * 
     * @return value
     */
    public String getAttribute(String id, String name);
    
    /**
     * Sets an attribute for a given resource
     * 
     * @param id
     * @param name
     * @param value
     */
    public void setAttribute(String id, String name, String value);
    
    /**
     * Removes an attribute with the given name for the given
     * resource
     * 
     * @param id
     * @param name
     */
    public void removeAttribute(String id, String name);
    
    /**
     * Gets the resource data value
     * 
     * @param id
     * 
     * @return the value
     */
    public String getValue(String id);
    
    /**
     * Sets the resource data value
     * 
     * @param id
     * @param value
     * 
     * @return the value
     */
    public void setValue(String id, String value);    
}