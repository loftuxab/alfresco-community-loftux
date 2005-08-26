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
