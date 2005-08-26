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
}
