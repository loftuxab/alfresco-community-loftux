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

import java.util.List;

import org.alfresco.config.evaluator.Evaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The default algorithm used to determine whether a section applies to the object being looked up
 * 
 * @author gavinc
 */
public class DefaultLookupAlgorithm implements ConfigLookupAlgorithm
{
   private static final Log logger = LogFactory.getLog(DefaultLookupAlgorithm.class);
   
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
            // if the config element being added already exists we need to combine it or replace it
            String name = newConfigElement.getName();
            ConfigElement existingConfigElement = (ConfigElement)results.getConfigElements().get(name);
            if (existingConfigElement != null)
            {
               if (section.isReplace())
               {
                  // if the section has been marked as 'replace' and a config element
                  // with this name has already been found, replace it
                  results.getConfigElements().put(name, newConfigElement);
                  
                  if (logger.isDebugEnabled())
                     logger.debug("Replaced " + existingConfigElement + " with " + newConfigElement);
               }
               else
               {
                  // combine this config element with the previous one found with the same name
                  ConfigElement combinedConfigElement = existingConfigElement.combine(newConfigElement);
                  results.getConfigElements().put(name, combinedConfigElement);
               
                  if (logger.isDebugEnabled())
                  {
                     logger.debug("Combined " + newConfigElement + " with " + existingConfigElement + 
                                  " to create " + combinedConfigElement);
                  }
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
