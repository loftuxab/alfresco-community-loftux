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
