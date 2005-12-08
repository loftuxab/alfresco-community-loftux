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
     * @param name
     *            Name of the config element to retrieve
     * @return The ConfigElement object or null if it doesn't exist
     */
    public ConfigElement getConfigElement(String name);

    /**
     * Returns the given config element as a list.
     * 
     * @param name Name of the config element to retrieve
     * @return A list of the config elements with the given name 
     *         or null if it doesn't exist
     */
    public List<ConfigElement> getConfigElementList(String name);
    
    /**
     * Returns all the config elements
     * 
     * @return All the config elements
     */
    public Map<String, Object> getConfigElements();

    /**
     * Determines whether the given config element exists
     *  
     * @param name The name of the config element to look for
     * @return true if the config element exists
     */
    public boolean hasConfigElement(String name);
    
    // TODO: Add more methods to this interface to allow for easier client
    // access to the results i.e. by using an XPath expression for
    // example?
}
