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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract base class used for objects that represent configuration 
 * as a root object in a script or template model.
 * 
 * @author gavinc
 */
public abstract class ConfigModel
{
   protected ConfigService configService;
   protected Map<String, ConfigElement> globalConfig;
   protected String scriptConfig;
   
   private static Log logger = LogFactory.getLog(ConfigModel.class);
   
   public ConfigModel(ConfigService configService, String scriptConfig)
   {
      this.configService = configService;
      this.scriptConfig = scriptConfig;
      
      if (this.configService != null)
      {
         // get the global config
         this.globalConfig = this.configService.getGlobalConfig().getConfigElements();
      }
      
      // if no global config was found create an empty map
      if (this.globalConfig == null)
      {
         this.globalConfig = Collections.emptyMap();
      }
   }

   /**
    * Retrieves the global configuration as a Map.
    * 
    * @return Map of the global config
    */
   public Map<String, ConfigElement> getGlobal()
   {
      return this.globalConfig;
   }
   
   /**
    * Retrieves scoped configuration as a Map.
    * 
    * @return Map of the scoped config
    */
   @SuppressWarnings("unchecked")
   public Map<String, ConfigElement> getScoped()
   {
      return new ScopedConfigMap();
   }
   
   /**
    * Retrieves the script configuration.<br/>
    * It's up to the subclass what is returned to represent script config.
    * 
    * @return script configuration
    */
   public abstract Object getScript();
   
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
         
         Map<String, ConfigElement> map = null;
         
         if (configService != null)
         {
            Config result = configService.getConfig(identifier);
            map = result.getConfigElements();
         }
         else
         {
            map = Collections.emptyMap();
         }
         
         if (logger.isDebugEnabled())
            logger.debug("Returning config for '" + identifier + "': " + map);
         
         return map;
      }
   }
}




