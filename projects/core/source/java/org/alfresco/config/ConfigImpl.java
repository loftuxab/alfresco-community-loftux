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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of the Config interface, this should be used as the
 * base class for any customisations
 * 
 * @author gavinc
 */
public class ConfigImpl implements Config
{
   private static final Log logger = LogFactory.getLog(ConfigImpl.class);

   private Map<String, Object> configElements;

   /**
    * Default constructor
    */
   public ConfigImpl()
   {
      this.configElements = new HashMap<String, Object>();
   }

   /**
    * Construct a ConfigImpl using the contents of an existing ConfigImpl
    * 
    * @param config
    *           The instance to create this one from
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
    * @see org.alfresco.config.Config#getConfigElementList(java.lang.String)
    */
   public List<ConfigElement> getConfigElementList(String name)
   {
      List<ConfigElement> list = null;
      
      Object obj = this.configElements.get(name);
      if (obj != null)
      {
         if (obj instanceof ConfigElement)
         {
            list = new ArrayList<ConfigElement>(1);
            list.add((ConfigElement)obj);
         }
         else if (obj instanceof List)
         {
            list = (List<ConfigElement>)obj;
         }
      }
      
      return list;
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
   public Map<String, Object> getConfigElements()
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

   /**
    * Adds a config element to the results of the lookup. If a config element
    * with the same name already exists a list of them is created
    * 
    * @param configElement
    *           The config element to add
    */
   public void addConfigElement(ConfigElement configElement)
   {
      Object obj = this.configElements.get(configElement.getName());
      if (obj == null)
      {
         putConfigElement(configElement);
      }
      else
      {
         if (obj instanceof ConfigElement)
         {
            List<ConfigElement> list = new ArrayList<ConfigElement>(2);
            list.add((ConfigElement)obj);
            list.add(configElement);
         }
         else if (obj instanceof List)
         {
            ((List<ConfigElement>)obj).add(configElement);
         }
      }
   }
}
