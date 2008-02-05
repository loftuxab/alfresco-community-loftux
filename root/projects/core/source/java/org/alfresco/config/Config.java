/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.config;

import java.util.Map;

/**
 * Definition of a object that represents the results of a lookup.
 * 
 * @author gavinc
 */
public interface Config
{
    /**
     * Returns the config element with the given name, if there is more
     * than one with the given name the first one added is returned.
     * 
     * @param name   Name of the config element to retrieve
     * 
     * @return The ConfigElement object or null if it doesn't exist
     */
    public ConfigElement getConfigElement(String name);
    
    /**
     * Shortcut method to get the config element with the given name and
     * return its value. If the config element does not exist, null is
     * returned. If there is more than one with the given name the first
     * one added is returned.
     * 
     * @param name   Name of the config element value to retrieve
     * 
     * @return The ConfigElement value or null if it doesn't exist
     */
    public String getConfigElementValue(String name);
    
    /**
     * Returns all the config elements
     * 
     * @return All the config elements
     */
    public Map<String, ConfigElement> getConfigElements();

    /**
     * Determines whether the given config element exists
     *  
     * @param name The name of the config element to look for
     * @return true if the config element exists
     */
    public boolean hasConfigElement(String name);
}
