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

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the Config interface, this should be used as the
 * base class for any customisations
 * 
 * @author gavinc
 */
public class ConfigImpl implements Config
{
   private Map<String, ConfigElement> configElements;

   /**
    * Default constructor
    */
   public ConfigImpl()
   {
      this.configElements = new HashMap<String, ConfigElement>();
   }

   /**
    * Construct a ConfigImpl using the contents of an existing ConfigImpl
    * 
    * @param config The instance to create this one from
    */
   public ConfigImpl(ConfigImpl config)
   {
      this();

      this.configElements.putAll(config.getConfigElements());
   }

   /**
    * @see org.alfresco.config.Config#getConfigElement(java.lang.String)
    */
   public ConfigElement getConfigElement(String name)
   {
      return (ConfigElement) this.configElements.get(name);
   }

   /**
    * @see org.alfresco.config.Config#hasConfigElement(java.lang.String)
    */
   public boolean hasConfigElement(String name)
   {
      return this.configElements.containsKey(name);
   }

   /**
    * @see org.alfresco.config.Config#getConfigElements()
    */
   public Map<String, ConfigElement> getConfigElements()
   {
      return this.configElements;
   }

   /**
    * Adds a config element to the results of the lookup replacing any config
    * element already present with the same name
    * 
    * @param configElement
    *           The config element to add
    */
   public void putConfigElement(ConfigElement configElement)
   {
      this.configElements.put(configElement.getName(), configElement);
   }
}
