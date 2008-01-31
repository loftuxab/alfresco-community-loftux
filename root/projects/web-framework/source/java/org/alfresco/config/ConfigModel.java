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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Object used to represent configuration as a root object in a script or template model.
 * 
 * @author gavinc
 */
public class ConfigModel
{
   private ConfigService configService;
   private Map<String, Object> globalConfig = null;
   
   private static Log logger = LogFactory.getLog(ConfigModel.class);
   
   public ConfigModel(ConfigService configService)
   {
      this.configService = configService;
      this.globalConfig = new HashMap<String, Object>();
      
      if (this.configService != null)
      {
         if (logger.isDebugEnabled())
            logger.debug("Constructing ConfigModel from global config...");
         
         // get the global config from the config service and
         // create a map representation of it
         populateMap(this.globalConfig, this.configService.getGlobalConfig());
      }
   }

   /**
    * Retrieves the global configuration as a Map.
    * 
    * @return Map of the global config
    */
   public Map<String, Object> getGlobal()
   {
      return this.globalConfig;
   }
   
   @SuppressWarnings("unchecked")
   public Map<String, Object> getScoped()
   {
      return new ScopedConfigMap();
   }
   
   @SuppressWarnings("unchecked")
   private void populateMap(Map<String, Object> map, Config config)
   {
      if (logger.isDebugEnabled())
         logger.debug("Populating map with config: " + config);
      
      // go through each top level element and add it to the map
      for (String configElemName : config.getConfigElements().keySet())
      {
         ConfigElement elem = config.getConfigElement(configElemName);
         if (elem instanceof ModelAware)
         {
            // if the config element is model aware get its map and add
            Map model = ((ModelAware)elem).getModel();
            map.put(configElemName, model);
            
            if (logger.isDebugEnabled())
               logger.debug("Added model to map for '" + configElemName + "': " + model);
         }
         else
         {
            // just add the config element as is to the map, the
            // downside to this is that the script & template
            // writer must be aware of the API of the object, 
            // this may either be a custom implementation or the
            // GenericConfigElement implementation
            map.put(configElemName, elem);
            
            if (logger.isDebugEnabled())
               logger.debug("Added object to map for '" + configElemName + "': " + elem);
         }
      }
   }
   
   /**
    * Map to allow access to scoped config in a unified way 
    * for scripts and templates.
    * 
    * @author gavinc
    */
   @SuppressWarnings({ "unchecked", "serial" })
   public class ScopedConfigMap extends HashMap
   {
      @Override
      public Object get(Object identifier)
      {
         if (logger.isDebugEnabled())
            logger.debug("Getting scoped config for '" + identifier + "'");
         
         Map<String, Object> map = new HashMap<String, Object>();
         
         if (configService != null)
         {
            populateMap(map, configService.getConfig(identifier));
         }
         
         return map;
      }
   }
}




