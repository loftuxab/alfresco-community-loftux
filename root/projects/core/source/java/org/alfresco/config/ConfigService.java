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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.config;

/**
 * Definition of a Configuration Service
 * 
 * @author gavinc
 */
public interface ConfigService
{  
   /**
    * Retrieves the configuration for the given object
    * 
    * @param object The object to use as the basis of the lookup
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object);

   /**
    * Retrieves the configuration for the given object using the given context
    * 
    * @param object The object to use as the basis of the lookup
    * @param context The context to use for the lookup
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, ConfigLookupContext context);
   
   /**
    * Returns just the global configuration, this allows the config service to be 
    * used independently of objects if desired (all config is placed in a global section).
    * 
    * @return The global config section or null if there isn't one
    */
   public Config getGlobalConfig();
   
   public void appendConfig(ConfigSource configSource);
}
