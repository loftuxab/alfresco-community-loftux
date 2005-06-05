package org.alfresco.config;

import java.util.List;

import org.alfresco.config.evaluator.Evaluator;
import org.apache.log4j.Logger;

/**
 * The default algorithm used to determine whether a section applies to the object being looked up
 * 
 * @author gavinc
 */
public class DefaultLookupAlgorithm implements ConfigLookupAlgorithm
{
   private static final Logger logger = Logger.getLogger(DefaultLookupAlgorithm.class);
   
   /**
    * @see org.alfresco.config.ConfigLookupAlgorithm#process(org.alfresco.config.ConfigSection, org.alfresco.config.evaluator.Evaluator, java.lang.Object, org.alfresco.config.Config)
    */
   public void process(ConfigSection section, Evaluator evaluator, Object object, Config results)
   {
      // if the config section applies to the given object extract all the
      // config elements inside and add them to the Config object
      if (evaluator.applies(object, section.getCondition()))
      {
         if (logger.isDebugEnabled())
            logger.debug(section + " matches");

         List<ConfigElement> sectionConfigElements = section.getConfigElements();
         for (ConfigElement newConfigElement : sectionConfigElements)
         {
            // if the config element being added already exists we need to combine it
            String name = newConfigElement.getName();
            ConfigElement existingConfigElement = (ConfigElement)results.getConfigElements().get(name);
            if (existingConfigElement != null)
            {
               ConfigElement combinedConfigElement = existingConfigElement.combine(newConfigElement);
               results.getConfigElements().put(name, combinedConfigElement);
            
               if (logger.isDebugEnabled())
               {
                  logger.debug("Combined " + newConfigElement + " with " + existingConfigElement + 
                               " to create " + combinedConfigElement);
               }
            }
            else
            {
               results.getConfigElements().put(name, newConfigElement);
            }
         }
      }
   }
}
