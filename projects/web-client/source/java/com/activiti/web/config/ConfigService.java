package com.activiti.web.config;

/**
 * Definition of a Configuration Service
 * 
 * @author gavinc
 */
public interface ConfigService
{
   /**
    * Initialisation hook for configuration service implementations
    */
   public void init();
   
   /**
    * Destruction hook for configuration service implementations
    */
   public void destroy();
   
   /**
    * Sets the config source to use to retrieve the configuration data
    * 
    * @param configSource ConfigSource implementation to use
    */
   public void setConfigSource(ConfigSource configSource);
   
   /**
    * Retrieves the configuration for the given object
    * 
    * @param object The object to use as the basis of the lookup
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object);
   
   /**
    * Retrieves the configuration for the given object
    * 
    * @param object The object to use as the basis of the lookup
    * @param includeGlobalConfig Determines whether to include any configuration defined as global
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, boolean includeGlobalConfig);
   
   /**
    * Retrieves the configuration for the given object restricted to the named areas and to
    * those sections within those areas that have any of the listed evaluators.
    * The evaluators and areas will be searched in the order they are passed.
    * 
    * @param object The object to use as the basis of the lookup
    * @param areas List of named areas to restrict the lookup within, if null
    *        is passed all areas will be searched
    * @param evaluators List of named evaluators to use for the lookup, if null is passed
    *        all evaluators will be applied
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, String[] areas, String[] evaluators);
   
   /**
    * Retrieves the configuration for the given object restricted to the named areas and to
    * those sections within those areas that have any of the listed evaluators.
    * The evaluators and areas will be searched in the order they are passed.
    * 
    * @param object The object to use as the basis of the lookup
    * @param areas List of named areas to restrict the lookup within, if null
    *        is passed all areas will be searched
    * @param evaluators List of named evaluators to use for the lookup, if null is passed
    *        all evaluators will be applied
    * @param includeGlobalConfig Determines whether to include any configuration defined as global
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, String[] areas, String[] evaluators, boolean includeGlobalConfig);
}
