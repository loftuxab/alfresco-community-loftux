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
