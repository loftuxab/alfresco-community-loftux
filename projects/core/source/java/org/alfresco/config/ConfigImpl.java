package org.alfresco.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Default implementation of the Config interface
 * 
 * @author gavinc
 */
public class ConfigImpl implements Config
{
    private static final Logger logger = Logger.getLogger(ConfigImpl.class);

    private Map<String, ConfigElement> configElements;

    /**
     * Default constructor
     */
    public ConfigImpl()
    {
        this.configElements = new HashMap<String, ConfigElement>();
    }

    /**
     * Construct a ConfigImpl using the contents of an existing ConfigImpl
     * 
     * @param config
     *            The instance to create this one from
     */
    public ConfigImpl(ConfigImpl config)
    {
        this();

        this.configElements.putAll(config.getConfigElements());
    }

    /**
     * @see org.alfresco.config.Config#getConfigElement(java.lang.String)
     */
    public ConfigElement getConfigElement(String name)
    {
        return (ConfigElement) this.configElements.get(name);
    }

    /**
     * @see org.alfresco.config.Config#getConfigElements()
     */
    public Map<String, ConfigElement> getConfigElements()
    {
        return this.configElements;
    }

    /**
     * Adds a config element to the results for the lookup
     * 
     * @param newConfigElement
     */
    public void addConfigElement(ConfigElement newConfigElement)
    {
        // if the config element being added already exists we need to combine
        // it
        String name = newConfigElement.getName();
        if (this.configElements.containsKey(name))
        {
            ConfigElement existing = this.configElements.get(name);
            ConfigElement combined = existing.combine(newConfigElement);
            this.configElements.put(name, combined);

            if (logger.isDebugEnabled())
                logger.debug("Combined " + newConfigElement + " with " + existing + " to create " + combined);
        }
        else
        {
            this.configElements.put(name, newConfigElement);
        }
    }
}
