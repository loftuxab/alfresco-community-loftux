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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.config.evaluator.Evaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for all config service implementations. This class implements the
 * basic algorithm for performing lookups, concrete classes read their
 * configuration medium and populate this object ready for lookups.
 * 
 * The algorithm used is as follows:
 * <p>
 * Lookup methods go through the list of sections (maybe restricted to an area)
 * and looks at the evaluator for each one. The Evaluator implementation is
 * extracted and applies() is called on it. If applies() returns true all the
 * ConfigElements from it are added to the Config object. If the ConfigElement
 * already exists in the Config object being built up the new one is combined()
 * with the existing one.
 * </p>
 * 
 * @author gavinc
 */
public abstract class BaseConfigService implements ConfigService
{
    private static final Log logger = LogFactory.getLog(BaseConfigService.class);

    protected ConfigSource configSource;
    protected ConfigImpl globalConfig;
    protected Map<String, Evaluator> evaluators;
    protected Map<String, List<ConfigSection>> sectionsByArea;
    protected List<ConfigSection> sections;

    /**
     * Construct the service with the source from which it must read
     * 
     * @param configSource
     *            the source of the configurations
     */
    public BaseConfigService(ConfigSource configSource)
    {
        if (configSource == null)
        {
            throw new IllegalArgumentException("The config source is mandatory");
        }
        this.configSource = configSource;
    }

    /**
     * Initialises the config service
     */
    public void init()
    {
        this.sections = new ArrayList<ConfigSection>();
        this.sectionsByArea = new HashMap<String, List<ConfigSection>>();
        this.evaluators = new HashMap<String, Evaluator>();
        this.globalConfig = new ConfigImpl();

        // Add the built-in evaluators
        addEvaluator("string-compare", "org.alfresco.config.evaluator.StringEvaluator");
        addEvaluator("object-type", "org.alfresco.config.evaluator.ObjectTypeEvaluator");
    }

    /**
     * Cleans up all the resources used by the config service
     */
    public void destroy()
    {
        this.sections.clear();
        this.sectionsByArea.clear();
        this.evaluators.clear();

        this.sections = null;
        this.sectionsByArea = null;
        this.evaluators = null;
    }
    
    /**
     * Resets the config service
     */
    public void reset()
    {
       if (logger.isDebugEnabled())
         logger.debug("Resetting config service");
       
       destroy();
       init();
    }

    /**
     * @see org.alfresco.config.ConfigService#getConfig(java.lang.Object)
     */
    public Config getConfig(Object object)
    {
        return getConfig(object, new ConfigLookupContext());
    }

    /**
     * @see org.alfresco.config.ConfigService#getConfig(java.lang.Object, org.alfresco.config.ConfigLookupContext)
     */
    public Config getConfig(Object object, ConfigLookupContext context)
    {
        if (logger.isDebugEnabled())
            logger.debug("Retrieving configuration for '" + object + "'");

        ConfigImpl results = null;

        if (context.includeGlobalSection())
        {
            results = new ConfigImpl(this.globalConfig);

            if (logger.isDebugEnabled())
                logger.debug("Created initial config results using global section");
        } 
        else
        {
            results = new ConfigImpl();

            if (logger.isDebugEnabled())
                logger.debug("Created initial config results ignoring the global section");
        }

        if (context.getAreas().size() > 0)
        {
            if (logger.isDebugEnabled())
                logger.debug("Restricting search within following areas: " + context.getAreas());

            // add all the config elements from all sections (that match) in
            // each named area to the results
            for (String area : context.getAreas())
            {
                List<ConfigSection> areaSections = this.sectionsByArea.get(area);
                if (areaSections == null)
                {
                    throw new ConfigException("Requested area '" + area + "' has not been defined");
                }

                for (ConfigSection section: areaSections)
                {
                    processSection(section, object, context, results);
                }
            }
        }
        else
        {
            // add all the config elements from all sections (that match) to the results
            for (ConfigSection section: this.sections)
            {
                processSection(section, object, context, results);
            }
        }

        return results;
    }

    /**
     * @see org.alfresco.config.ConfigService#getGlobalConfig()
     */
    public Config getGlobalConfig()
    {
        return this.globalConfig;
    }
    
    /**
     * @see org.alfresco.config.ConfigService#appendConfig(org.alfresco.config.ConfigSource)
     */
    public void appendConfig(ConfigSource configSource)
    {
        for (InputStream inputStream : configSource)
        {
            if (logger.isDebugEnabled())
               logger.debug("Commencing parse of input stream for appended config");
            
            parse(inputStream);
            
            if (logger.isDebugEnabled())
               logger.debug("Completed parse of input stream for appended config");
        }
    }

    /**
     * Parses all the files passed to this config service
     */
    protected void parse()
    {
        for (InputStream inputStream : this.configSource)
        {
            if (logger.isDebugEnabled())
               logger.debug("Commencing parse of input stream");
            
            parse(inputStream);
            
            if (logger.isDebugEnabled())
               logger.debug("Completed parse of input stream");
        }
    }

    /**
     * Parses the given config stream
     * 
     * @param stream
     *            The input stream representing the config data
     */
    protected abstract void parse(InputStream stream);

    /**
     * Adds the given config section to the config service and optionally within
     * a named area
     * 
     * @param section
     *            The config section to add
     * @param area
     *            The name of the area to add the section to, if null the
     *            section is only added to the global section list
     */
    protected void addConfigSection(ConfigSection section, String area)
    {
        if (section.isGlobal())
        {
            // get all the config elements from this section and add them to the
            // global section, if any already exist we must combine or replace them
            for (ConfigElement ce : section.getConfigElements())
            {
               ConfigElement existing = this.globalConfig.getConfigElement(ce.getName());
               
               if (existing != null)
               {
                  if (section.isReplace())
                  {
                     // if the section has been marked as 'replace' and a config element
                     // with this name has already been found, replace it
                     this.globalConfig.putConfigElement(ce);
                     
                     if (logger.isDebugEnabled())
                        logger.debug("Replaced " + existing + " with " + ce);
                  }
                  else
                  {
                     // combine the config elements
                     ConfigElement combined = existing.combine(ce);
                     this.globalConfig.putConfigElement(combined);
                     
                     if (logger.isDebugEnabled())
                     {
                        logger.debug("Combined " + existing + " with " + ce + 
                                     " to create " + combined);
                     }
                  }
               }
               else
               {
                  this.globalConfig.putConfigElement(ce);
               }
            }
            

            if (logger.isDebugEnabled())
                logger.debug("Added config elements from " + section + " to the global section");
        }
        else
        {
            if (area != null && area.length() > 0)
            {
                // get the list of sections for the given area name (create the
                // list if required)
                List<ConfigSection> areaSections = this.sectionsByArea.get(area);
                if (areaSections == null)
                {
                    areaSections = new ArrayList<ConfigSection>();
                    this.sectionsByArea.put(area, areaSections);
                }

                // add the section to the list
                areaSections.add(section);

                if (logger.isDebugEnabled())
                    logger.debug("Added " + section + " to the '" + area + "' area");
            }
            else
            {
                // add the section to the relevant collections
                this.sections.add(section);

                if (logger.isDebugEnabled())
                    logger.debug("Added " + section + " to the sections list");
            }
        }
    }

    /**
     * Retrieves the implementation of the named evaluator
     * 
     * @param name
     *            Name of the evaluator to retrieve
     * @return The evaluator, null if it doesn't exist
     */
    protected Evaluator getEvaluator(String name)
    {
        return (Evaluator) this.evaluators.get(name);
    }

    /**
     * Adds the evaluator with the given name and class to the config service
     * 
     * @param name
     *            Name of the evaluator
     * @param className
     *            Class name of the evaluator
     */
    protected void addEvaluator(String name, String className)
    {
        Evaluator evaluator = null;

        try
        {
            Class clazz = Class.forName(className);
            evaluator = (Evaluator) clazz.newInstance();
        } 
        catch (Throwable e)
        {
            throw new ConfigException("Could not instantiate evaluator for '" + name + "' with class: " + className, e);
        }

        this.evaluators.put(name, evaluator);

        if (logger.isDebugEnabled())
            logger.debug("Added evaluator '" + name + "': " + className);
    }
    
    /**
     * Determines whether the given section applies for the given object, if it
     * does, the section is added to given results object.
     * 
     * @param section
     *            The section to process
     * @param object
     *            The object to retrieve config for
     * @param context
     *            The context to use for the lookup
     * @param results
     *            The resulting config object for the search
     */
    protected void processSection(ConfigSection section, Object object, ConfigLookupContext context,
                                  ConfigImpl results)
    {
        String evaluatorName = section.getEvaluator();
        Evaluator evaluator = getEvaluator(evaluatorName);

        if (evaluator == null)
        {
            throw new ConfigException("Unable to locate evaluator implementation for '" + evaluatorName + 
                                      "' for " + section);
        }

        context.getAlgorithm().process(section, evaluator, object, results);
    }
}
