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

import java.util.List;
import java.util.Map;

/**
 * Definition of a configuration element
 * 
 * @author gavinc
 */
public interface ConfigElement
{
    /**
     * Returns the name of this config element
     * 
     * @return Name of this config element
     */
    public String getName();

    /**
     * Gets the value of the attrbiute with the given name
     * 
     * @param name
     *            The name of the attrbiute to get the value for
     * @return The value of the attrbiute or null if the attribute doesn't exist
     */
    public String getAttribute(String name);

    /**
     * Returns the list of attributes held by this config element
     * 
     * @return The list of attrbiutes
     */
    public Map<String, String> getAttributes();

    /**
     * Determines whether the config element has the named attribute
     * 
     * @param name
     *            Name of the attribute to check existence for
     * @return true if it exists, false otherwise
     */
    public boolean hasAttribute(String name);

    /**
     * Returns the number of attributes this config element has
     * 
     * @return The number of attributes
     */
    public int getAttributeCount();
    
    /**
     * Gets the value of this config element. If this config element has
     * children then this method may return null
     * 
     * @return Value of this config element or null if there is no value
     */
    public String getValue();

    /**
     * Returns a child config element of the given name 
     * 
     * @param name The name of the config element to retrieve
     * @return The ConfigElement or null if it does not exist
     */
    public ConfigElement getChild(String name);
    
    /**
     * Shortcut method to return a child config element value of the given name.
     * Returns null as the value if the element does not exist.
     * 
     * @param name The name of the config element to retrieve the value from.
     * @return The ConfigElement value or null if it does not exist
     */
    public String getChildValue(String name);
    
    /**
     * Returns a list of children held by this ConfigElement with the given name.
     * 
     * @param name The name of the config element to retrieve
     * @return The list of children.
     */
    public List<ConfigElement> getChildren(String name);

    /**
     * Returns a list of children held by this ConfigElement
     * 
     * @return The list of children.
     */
    public List<ConfigElement> getChildren();

    /**
     * Determines whether this config element has any children. It is more
     * effecient to call this method rather than getChildren().size() as a
     * collection is not created if it is not required
     * 
     * @return true if it has children, false otherwise
     */
    public boolean hasChildren();

    /**
     * Returns the number of children this config element has
     * 
     * @return The number of children
     */
    public int getChildCount();
    
    /**
     * Combines the given config element with this config element and returns a
     * new instance containing the resulting combination. The combination of the
     * two objects MUST NOT change this instance.
     * 
     * @param configElement
     *            The config element to combine into this one
     * @return The combined ConfigElement
     */
    public ConfigElement combine(ConfigElement configElement);
}
