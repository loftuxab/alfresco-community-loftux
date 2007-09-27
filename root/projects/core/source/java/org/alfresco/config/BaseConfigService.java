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

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.config.evaluator.Evaluator;
import org.alfresco.util.AbstractLifecycleBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;

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
public abstract class BaseConfigService extends AbstractLifecycleBean implements ConfigService
{
    private static final Log logger = LogFactory.getLog(BaseConfigService.class);

    protected ConfigSource configSource;
    
    private ConfigImpl globalConfig;   
    private Map<String, Evaluator> evaluators;
    private Map<String, List<ConfigSection>> sectionsByArea;
    private List<ConfigSection> sections;
    
    private boolean isInited = false;
    
    // registered list of additional config deployers, if any
    protected List<ConfigDeployer> configDeployers = new ArrayList<ConfigDeployer>();

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
     * Initialises the config service - via init-method
     * 
     * @deprecated Should now be initialised via bootstrap mechanism.
     */
    public void init()
    {
        initConfig();
        isInited = true;
    }
    
    /**
     * Initialises the config service - via bootstrap
     */
    public List<ConfigDeployment> initConfig()
    {
        putSections(new ArrayList<ConfigSection>());
        putSectionsByArea(new HashMap<String, List<ConfigSection>>());
        putEvaluators(new HashMap<String, Evaluator>());
        putGlobalConfig(new ConfigImpl());

        // Add the built-in evaluators
        String stringCompare = "string-compare";
        addEvaluator(stringCompare, createEvaluator(stringCompare, "org.alfresco.config.evaluator.StringEvaluator"));
        String objectType = "object-type";
        addEvaluator(objectType, createEvaluator(objectType, "org.alfresco.config.evaluator.ObjectTypeEvaluator"));
        
        ConfigDeployment builtin = new ConfigDeployment("<Built-in evaluators>", null);
        builtin.setDeploymentStatus(ConfigDeployment.STATUS_OK);

        List<ConfigDeployment> builtins = new ArrayList<ConfigDeployment>();
        builtins.add(builtin);
        return builtins;
    }

    /**
     * Cleans up all the resources used by the config service
     */
    public void destroy()
    {
        removeSections();
        removeSectionsByArea();
        removeEvaluators();
        removeGlobalConfig();
        
        isInited = false;
    }
    
    /**
     * Resets the config service
     */
    public void reset()
    {
       if (logger.isDebugEnabled())
         logger.debug("Resetting config service");
       
       destroy();
       initConfig();
    }
    
    /**
     * Register deployer
     * 
     * @param configDeployer
     */
    public void addDeployer(ConfigDeployer configDeployer)
    {
        if (! configDeployers.contains(configDeployer))
        {
        	configDeployers.add(configDeployer);
        }
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
            results = new ConfigImpl(getGlobalConfigImpl());

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
            
            Map<String, List<ConfigSection>> sectionsByArea = getSectionsByArea();           
            
            for (String area : context.getAreas())
            {
                List<ConfigSection> areaSections = sectionsByArea.get(area);
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
            
            for (ConfigSection section: getSections())
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
        return getGlobalConfigImpl();
    }
    
    /**
     * @see org.alfresco.config.ConfigService#appendConfig(org.alfresco.config.ConfigSource)
     */
    public List<ConfigDeployment> appendConfig(ConfigSource configSource)
    {
    	List<ConfigDeployment> configDeployments = configSource.getConfigDeployments();
        for (ConfigDeployment configDeployment : configDeployments)
        {
            if (configDeployment.getStream() != null)
            {
	            if (logger.isDebugEnabled())
	               logger.debug("Commencing parse of input stream for source: " + configDeployment.getName());
	            
	            try
	            {
	            	parse(configDeployment.getStream());
	            	configDeployment.setDeploymentStatus(ConfigDeployment.STATUS_OK);
	            }
	            catch (Throwable t)
	            {
	                logger.error("Input stream invalid - skipped for source: " + configDeployment.getName() + "' ", t);
	                
	            	StringWriter stringWriter = new StringWriter();
	                t.printStackTrace(new PrintWriter(stringWriter));
	                configDeployment.setDeploymentStatus("Skipped - invalid: " + stringWriter.toString());
	            }
	            
	            if (logger.isDebugEnabled())
	               logger.debug("Completed parse of input stream for source: " + configDeployment.getName());
	        }
            else
            {
            	logger.debug("Input stream not available - skipped for source: " + configDeployment.getName());
            	configDeployment.setDeploymentStatus("Skipped - not available");
            }
        }
        
        return configDeployments;
    }

    /**
     * Parses all the files passed to this config service
     */
    protected List<ConfigDeployment> parse()
    {
        return appendConfig(configSource);
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
            
            ConfigImpl globalConfig = getGlobalConfigImpl();
            
            for (ConfigElement ce : section.getConfigElements())
            {               
               ConfigElement existing = globalConfig.getConfigElement(ce.getName());
               
               if (existing != null)
               {
                  if (section.isReplace())
                  {
                     // if the section has been marked as 'replace' and a config element
                     // with this name has already been found, replace it                     
                     globalConfig.putConfigElement(ce);
                     
                     if (logger.isDebugEnabled())
                        logger.debug("Replaced " + existing + " with " + ce);
                  }
                  else
                  {
                     // combine the config elements
                     ConfigElement combined = existing.combine(ce);                     
                     globalConfig.putConfigElement(combined);
                     
                     if (logger.isDebugEnabled())
                     {
                        logger.debug("Combined " + existing + " with " + ce + 
                                     " to create " + combined);
                     }
                  }
               }
               else
               {
                   globalConfig.putConfigElement(ce);
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
                Map<String, List<ConfigSection>> sectionsByArea = getSectionsByArea();               
                List<ConfigSection> areaSections = sectionsByArea.get(area);
                
                if (areaSections == null)
                {
                    areaSections = new ArrayList<ConfigSection>();                   
                    sectionsByArea.put(area, areaSections);
                }

                // add the section to the list
                areaSections.add(section);

                if (logger.isDebugEnabled())
                    logger.debug("Added " + section + " to the '" + area + "' area");
            }
            else
            {
                // add the section to the relevant collections
                getSections().add(section);

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
        return (Evaluator)getEvaluators().get(name);
    }

    /**
     * Adds the evaluator to the config service
     * 
     * @param name
     *            Name of the evaluator
     * @param evaluator
     *            The evaluator
     */
    protected void addEvaluator(String name, Evaluator evaluator)
    {
        getEvaluators().put(name, evaluator);

        if (logger.isDebugEnabled())
            logger.debug("Added evaluator '" + name + "': " + evaluator.getClass().getName());
    }
    
    /**
     * Instantiate the evaluator with the given name and class
     * 
     * @param name
     *            Name of the evaluator
     * @param className
     *            Class name of the evaluator
     */
    protected Evaluator createEvaluator(String name, String className)
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
	    
	    return evaluator;
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
    
    /**
     * Get globalConfig from the in-memory 'cache'
     * 
     * @return globalConfig
     */
    protected ConfigImpl getGlobalConfigImpl()
    {
        return globalConfig;
    }
    
    /**
     * Put globalConfig into the in-memory 'cache'
     * 
     * @param globalConfig
     */
    protected void putGlobalConfig(ConfigImpl globalConfig)
    {
        this.globalConfig = globalConfig;
    }  
    
    /**
     * Remove globalConfig from the in-memory 'cache'
     */
    protected void removeGlobalConfig()
    {
        this.globalConfig = null;
    } 

    /**
     * Get evaluators from the in-memory 'cache'
     * 
     * @return
     */
    protected Map<String, Evaluator> getEvaluators()
    {
        return evaluators;
    } 

    /**
     * Put evaluators into the in-memory 'cache'
     * 
     * @param evaluators
     */
    protected void putEvaluators(Map<String, Evaluator> evaluators)
    {
        this.evaluators = evaluators;
    }  
    
    /**
     * Remove evaluators from in-memory 'cache'
     */
    protected void removeEvaluators()
    {        
        evaluators.clear();
        evaluators = null;
    } 
    
    /**
     * Get the sectionsByArea from the in-memory 'cache'
     * 
     * @return sectionsByArea
     */
    protected Map<String, List<ConfigSection>> getSectionsByArea()
    {
        return sectionsByArea;
    }  
    
    /**
     * Put the sectionsByArea into the in-memory 'cache'
     * 
     * @param sectionsByArea
     */
    protected void putSectionsByArea(Map<String, List<ConfigSection>> sectionsByArea)
    {
        this.sectionsByArea = sectionsByArea;
    }  
    
    /**
     * Remove the sectionsByArea from the in-memory 'cache'
     */
    protected void removeSectionsByArea()
    {
        sectionsByArea.clear();
        sectionsByArea = null;
    } 
    
    /**
     * Get the sections from the in-memory 'cache'
     * 
     * @return sections
     */
    protected List<ConfigSection> getSections()
    {
        return sections;
    }  

    /**
     * Put the sections into the in-memory 'cache'
     * 
     * @param sections
     */
    protected void putSections(List<ConfigSection> sections)
    {
        this.sections = sections;
    }  
    
    /**
     * Remove the sections from the in-memory 'cache'
     */
    protected void removeSections()
    {
        sections.clear();
        sections = null;
    } 
    
    /**
     * Initialise config on bootstrap
     */
    @Override
    protected void onBootstrap(ApplicationEvent event)
    {       
    	// TODO - see JIRA Task AR-1714 - can remove isInited flag, as and when configService (and its callers) come under bootstrap control
    	if (! isInited)
    	{
    		initConfig();
    	}
    }
    
    /**
     * Destroy config in shutdown
     */
    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        destroy();
    }
}
