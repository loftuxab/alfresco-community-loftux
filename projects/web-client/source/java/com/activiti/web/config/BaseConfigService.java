package com.activiti.web.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.activiti.web.config.evaluator.Evaluator;

/**
 * Base class for all config service implementations. This class
 * implements the basic algorithm for performing lookups, concrete
 * classes read their configuration medium and populate this object
 * ready for lookups.
 * 
 * The algorithm used is as follows:
 * <p>
 * Lookup methods go through the list of sections (maybe restricted to an area) 
 * and looks at the evaluator for each one. If the Evaluator is in the list (or 
 * lookup() has been called) the implementation is extracted and applies() is 
 * called on it. If applies() returns true all the ConfigElements from it are 
 * added to the Config object. If the ConfigElement already exists in the Config
 * object being built up the new one is combined() with the existing one.
 * </p>
 * 
 * @author gavinc
 */
public abstract class BaseConfigService implements ConfigService
{
   private static final Logger logger = Logger.getLogger(BaseConfigService.class);
   
   private ConfigSection globalSection;
   private List sections;
   private Map evaluators;
   private Map sectionsByArea;
   
   // TODO: remove
   public List getSections() { return this.sections; }
   
   /**
    * @see com.activiti.web.config.ConfigService#init()
    */
   public void init()
   {
      this.sections = new ArrayList();
      this.sectionsByArea = new HashMap();
      this.evaluators = new HashMap();
   }

   /**
    * @see com.activiti.web.config.ConfigService#destroy()
    */
   public void destroy()
   {
      this.sections.clear();
      this.sectionsByArea.clear();
      this.evaluators.clear();
      
      this.sections = null;
      this.sectionsByArea = null;
      this.evaluators = null;
      this.globalSection = null;
   }

   /**
    * @see com.activiti.web.config.ConfigService#getConfig(java.lang.Object)
    */
   public Config getConfig(Object object)
   {
      return getConfig(object, null, null, true);
   }

   /**
    * @see com.activiti.web.config.ConfigService#getConfig(java.lang.Object, boolean)
    */
   public Config getConfig(Object object, boolean includeGlobalConfig)
   {
      return getConfig(object, null, null, includeGlobalConfig);
   }

   public Config getConfig(Object object, String[] areas, String[] evaluators)
   {
      return getConfig(object, areas, evaluators, true);
   }
   
   /**
    * @see com.activiti.web.config.ConfigService#getConfig(java.lang.Object, java.lang.String[], java.lang.String[], boolean)
    */
   public Config getConfig(Object object, String[] areas, String[] evaluators, boolean includeGlobalConfig)
   {
      // ********************************************************
      // ** TODO: Implement the area and evaluator restrictions
      // ********************************************************
      
      if (logger.isDebugEnabled())
         logger.debug("Retrieving configuration for " + object + "...");
      
      ConfigImpl results = new ConfigImpl();
      
      if (this.globalSection != null)
      {
         if (includeGlobalConfig)
         {
            if (logger.isDebugEnabled())
               logger.debug("Adding global section...");
            
            // add all the config elements from the global section to the results
            List globalConfigElements = this.globalSection.getConfigElements();
            for (int x = 0; x < globalConfigElements.size(); x++)
            {
               results.addConfigElement((ConfigElement)globalConfigElements.get(x));
            }
         }
         else
         {
            if (logger.isDebugEnabled())
               logger.debug("Ignoring global section");
         }
      }
      
      // add all the config elements from all sections to the results
      Iterator sections = this.sections.iterator();
      while (sections.hasNext())
      {
         // for each section get hold of the evaluator
         ConfigSection section = (ConfigSection)sections.next();
         String evaluatorName = section.getEvaluator();
         Evaluator evaluator = getEvaluator(evaluatorName);
         
         if (evaluator == null)
         {
            throw new ConfigException("Unable to locate evaluator implementation for '" + 
                                      evaluatorName + "' for " + section);
         }
         
         // if the config section applies to the given object exract all the config
         // elements inside and add them to the Config object
         if (evaluator.applies(object, section.getCondition()))
         {
            if (logger.isDebugEnabled())
               logger.debug(section + " matched");
            
            List sectionConfigElements = section.getConfigElements();
            for (int x = 0; x < sectionConfigElements.size(); x++)
            {
               results.addConfigElement((ConfigElement)sectionConfigElements.get(x));
            }
         }
      }
      
      return results;
   }

   /**
    * Adds the given config section to the config service and optionally within a named area
    * 
    * @param section The config section to add
    * @param area The name of the area to add the section to, if null the section is only added to
    *             the global section list
    */
   protected void addConfigSection(ConfigSection section, String area)
   {
      if (section.isGlobal())
      {
         // TODO: Deal with adding multiple global sections, need
         //       to combine the contained config elements
         this.globalSection = section;
         
         if (logger.isDebugEnabled())
            logger.debug("Set " + section + " to be global");
      }
      else
      {
         // add the section to the relevant collections
         this.sections.add(section);
         
         if (logger.isDebugEnabled())
            logger.debug("Added " + section);
         
         if (area != null && area.length() > 0)
         {
            this.sectionsByArea.put(area, section);
            
            if (logger.isDebugEnabled())
               logger.debug("Added " + section + " to the '" + area + "' area");
         }
         
         // TODO: store the sections by evaluator type too?
      }
   }
   
   /**
    * Retrieves the implementation of the named evaluator
    * 
    * @param name Name of the evaluator to retrieve
    * @return The evaluator, null if it doesn't exist
    */
   protected Evaluator getEvaluator(String name)
   {
      return (Evaluator)this.evaluators.get(name);
   }
   
   /**
    * Adds the evaluator with the given name and class to the config service
    * 
    * @param name Name of the evaluator
    * @param className Class name of the evaluator
    */
   protected void addEvaluator(String name, String className)
   {
      Evaluator evaluator = null;
      
      try
      {
         Class clazz = Class.forName(className);
         evaluator = (Evaluator)clazz.newInstance();
      }
      catch (Throwable e)
      {
         throw new ConfigException("Could not instantiate evaluator for '" + name + 
                                   "' with class: " + className, e);  
      }
      
      this.evaluators.put(name, evaluator);
      
      if (logger.isDebugEnabled())
         logger.debug("Added evaluator '" + name + "': " + className);
   }
}
