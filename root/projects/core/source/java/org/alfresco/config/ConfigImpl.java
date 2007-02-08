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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
