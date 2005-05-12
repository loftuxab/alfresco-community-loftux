package org.alfresco.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.alfresco.config.evaluator.Evaluator;

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
    private static final Logger logger = Logger.getLogger(BaseConfigService.class);

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
     * @see org.alfresco.config.ConfigService#init()
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
     * @see org.alfresco.config.ConfigService#destroy()
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
     * @see org.alfresco.config.ConfigService#getConfig(java.lang.Object)
     */
    public Config getConfig(Object object)
    {
        return getConfig(object, new String[] {}, true);
    }

    /**
     * @see org.alfresco.config.ConfigService#getConfig(java.lang.Object,
     *      boolean)
     */
    public Config getConfig(Object object, boolean includeGlobalConfig)
    {
        return getConfig(object, new String[] {}, includeGlobalConfig);
    }

    /**
     * @see org.alfresco.config.ConfigService#getConfig(java.lang.Object,
     *      java.lang.String)
     */
    public Config getConfig(Object object, String area)
    {
        return getConfig(object, area, true);
    }

    /**
     * @see org.alfresco.config.ConfigService#getConfig(java.lang.Object,
     *      java.lang.String, boolean)
     */
    public Config getConfig(Object object, String area, boolean includeGlobalConfig)
    {
        return getConfig(object, new String[] { area }, includeGlobalConfig);
    }

    /**
     * @see org.alfresco.config.ConfigService#getConfig(java.lang.Object,
     *      java.lang.String[], boolean)
     */
    public Config getConfig(Object object, String[] areas, boolean includeGlobalConfig)
    {
        if (logger.isDebugEnabled())
            logger.debug("Retrieving configuration for " + object);

        ConfigImpl results = null;

        if (includeGlobalConfig)
        {
            results = new ConfigImpl(this.globalConfig);

            if (logger.isDebugEnabled())
                logger.debug("Created initial config results using global section");
        } else
        {
            results = new ConfigImpl();

            if (logger.isDebugEnabled())
                logger.debug("Created initial config results ignoring the global section");
        }

        if (areas != null && areas.length > 0)
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder searchAreas = new StringBuilder();
                for (int x = 0; x < areas.length; x++)
                {
                    if (x > 0)
                    {
                        searchAreas.append(", ");
                    }

                    searchAreas.append(areas[x]);
                }

                logger.debug("Restricting search within following areas: " + searchAreas.toString());
            }

            // add all the config elements from all sections (that match) in
            // each named area to the results
            for (int x = 0; x < areas.length; x++)
            {
                String area = areas[x];
                List areaSections = (List) this.sectionsByArea.get(area);
                if (areaSections == null)
                {
                    throw new ConfigException("Requested area '" + area + "' has not been defined");
                }

                Iterator iterAreaSections = areaSections.iterator();
                while (iterAreaSections.hasNext())
                {
                    ConfigSection section = (ConfigSection) iterAreaSections.next();
                    processSection(section, object, results);
                }
            }
        }
        else
        {
            // add all the config elements from all sections (that match) to the
            // results
            Iterator sections = this.sections.iterator();
            while (sections.hasNext())
            {
                ConfigSection section = (ConfigSection) sections.next();
                processSection(section, object, results);
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
     * Parses all the files passed to this config service
     */
    protected void parse()
    {
        for (InputStream inputStream : this.configSource)
        {
            parse(inputStream);
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
            // get all the config elements from this section and add them to
            // the global section, if any already exist we must combine them

            List<ConfigElement> globalConfigElements = section.getConfigElements();
            for (int x = 0; x < globalConfigElements.size(); x++)
            {
                this.globalConfig.addConfigElement(globalConfigElements.get(x));
            }

            if (logger.isDebugEnabled())
                logger.debug("Added config elements from " + section + " to the global section");
        }
        else
        {
            // add the section to the relevant collections
            this.sections.add(section);

            if (logger.isDebugEnabled())
                logger.debug("Added " + section + " to the sections list");

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
        } catch (Throwable e)
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
     * @param results
     *            The resulting config object for the search
     */
    protected void processSection(ConfigSection section, Object object, ConfigImpl results)
    {
        String evaluatorName = section.getEvaluator();
        Evaluator evaluator = getEvaluator(evaluatorName);

        if (evaluator == null)
        {
            throw new ConfigException("Unable to locate evaluator implementation for '" + evaluatorName + "' for "
                    + section);
        }

        // if the config section applies to the given object exract all the
        // config
        // elements inside and add them to the Config object
        if (evaluator.applies(object, section.getCondition()))
        {
            if (logger.isDebugEnabled())
                logger.debug(section + " matches");

            List sectionConfigElements = section.getConfigElements();
            for (int x = 0; x < sectionConfigElements.size(); x++)
            {
                results.addConfigElement((ConfigElement) sectionConfigElements.get(x));
            }
        }
    }
}
