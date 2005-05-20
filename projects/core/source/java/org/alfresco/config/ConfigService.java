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
    * Retrieves the configuration for the given object.
    * 
    * @param object The object to use as the basis of the lookup
    * @param includeGlobalConfig Determines whether to include any configuration defined as global
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, boolean includeGlobalConfig);
   
   /**
    * Retrieves the configuration for the given object restricted to the named area.
    * The areas will be searched in the order they are passed.
    * 
    * @param object The object to use as the basis of the lookup
    * @param area A named area to restrict the lookup within, if null is passed all areas 
    *        will be searched
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, String area);

   /**
    * Retrieves the configuration for the given object restricted to the named area.
    * The areas will be searched in the order they are passed.
    * 
    * @param object The object to use as the basis of the lookup
    * @param area A named area to restrict the lookup within, if null is passed all areas 
    *        will be searched
    * @param includeGlobalConfig Determines whether to include any configuration defined as global
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, String area, boolean includeGlobalConfig);
   
   /**
    * Retrieves the configuration for the given object restricted to the named area.
    * The areas will be searched in the order they are passed.
    * 
    * @param object The object to use as the basis of the lookup
    * @param areas List of named areas to restrict the lookup within, if null
    *        is passed all areas will be searched
    * @param includeGlobalConfig Determines whether to include any configuration defined as global
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, String[] areas, boolean includeGlobalConfig);
   
   /**
    * Returns just the global configuration, this allows the config service to be 
    * used independently of objects if desired (all config is placed in a global section).
    * 
    * @return The global config section or null if there isn't one
    */
   public Config getGlobalConfig();
}
