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
package org.alfresco.config.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.config.BaseConfigService;
import org.alfresco.config.ConfigDeployer;
import org.alfresco.config.ConfigDeployment;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.ConfigSection;
import org.alfresco.config.ConfigSectionImpl;
import org.alfresco.config.ConfigSource;
import org.alfresco.config.evaluator.Evaluator;
import org.alfresco.config.xml.elementreader.ConfigElementReader;
import org.alfresco.config.xml.elementreader.GenericElementReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * XML based configuration service
 * 
 * @author gavinc
 */
public class XMLConfigService extends BaseConfigService implements XMLConfigConstants
{
    private static final Log logger = LogFactory.getLog(XMLConfigService.class);

    private Map<String, ConfigElementReader> elementReaders;
    
    /**
     * Constructs an XMLConfigService using the given config source
     * 
     * @param configSource
     *            A ConfigSource
     */
    public XMLConfigService(ConfigSource configSource)
    {
        super(configSource);
    }

    public List<ConfigDeployment> initConfig()
    {
        if (logger.isDebugEnabled())
            logger.debug("Commencing initialisation");

        List<ConfigDeployment> configDeployments = super.initConfig();

        // initialise the element readers map with built-in readers
        putElementReaders(new HashMap<String, ConfigElementReader>());

        List<ConfigDeployment> deployments = parse();
        configDeployments.addAll(deployments);
                
    	// append additional config, if any
        for (ConfigDeployer configDeployer : configDeployers)
        {
        	deployments = configDeployer.initConfig();
        	configDeployments.addAll(deployments);
        }

        if (logger.isDebugEnabled())
            logger.debug("Completed initialisation");
        
        return configDeployments;
    }
    
    public void destroy()
    {
        removeElementReaders();
        super.destroy();
    }

    protected void parse(InputStream stream)
    {
    	Map<String, ConfigElementReader> parsedElementReaders = null;
    	Map<String, Evaluator> parsedEvaluators = null;
    	List<ConfigSection> parsedConfigSections = new ArrayList<ConfigSection>();
    	
    	String currentArea = null;
        try
        {
            // get the root element
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element rootElement = document.getRootElement();

            // see if there is an area defined
            currentArea = rootElement.attributeValue("area");

            // parse the plug-ins section of a config file
            Element pluginsElement = rootElement.element(ELEMENT_PLUG_INS);
            if (pluginsElement != null)
            {
                // parse the evaluators section
            	parsedEvaluators = parseEvaluatorsElement(pluginsElement.element(ELEMENT_EVALUATORS));

                // parse the element readers section
            	parsedElementReaders = parseElementReadersElement(pluginsElement.element(ELEMENT_ELEMENT_READERS));
            }

            // parse each config section in turn
            Iterator configElements = rootElement.elementIterator(ELEMENT_CONFIG);
            while (configElements.hasNext())
            {
                Element configElement = (Element) configElements.next();
                parsedConfigSections.add(parseConfigElement(parsedElementReaders, configElement, currentArea));
            }
        }
        catch (Throwable e)
        {
            if (e instanceof ConfigException)
            {
               throw (ConfigException)e;
            }
            else
            {
               throw new ConfigException("Failed to parse config stream", e);
            }
        }
        
        try 
        {
	        // valid for this stream, now add to config service ...
	        
	        if (parsedEvaluators != null)
	        {
		        for (Map.Entry<String, Evaluator> entry : parsedEvaluators.entrySet())
		        {
		        	// add the evaluators to the config service
		        	addEvaluator(entry.getKey(), entry.getValue());
		        }
	        }
	        
	        if (parsedElementReaders != null)
	        {
		        for (Map.Entry<String, ConfigElementReader> entry : parsedElementReaders.entrySet())
		        {
		        	// add the element readers to the config service
		        	addConfigElementReader(entry.getKey(), entry.getValue());
		        }
	        }
	        
	        if (parsedConfigSections != null)
	        {
	        	for (ConfigSection section : parsedConfigSections)
		        {
	        		// add the config sections to the config service
	        		addConfigSection(section, currentArea);
		        }
	        }
	    }
	    catch (Throwable e)
	    {
	        throw new ConfigException("Failed to add config to config service", e);
	    }
    }


    /**
     * Parses the evaluators element
     * 
     * @param evaluatorsElement
     */
    private Map<String, Evaluator> parseEvaluatorsElement(Element evaluatorsElement)
    {
        if (evaluatorsElement != null)
        {
        	Map<String, Evaluator> parsedEvaluators = new HashMap<String, Evaluator>();
        	
            Iterator evaluators = evaluatorsElement.elementIterator();
            while (evaluators.hasNext())
            {
                Element evaluatorElement = (Element) evaluators.next();
                String evaluatorName = evaluatorElement.attributeValue(ATTR_ID);
                String evaluatorClass = evaluatorElement.attributeValue(ATTR_CLASS);

                // TODO: Can these checks be removed if we use a DTD and/or
                // schema??
                if (evaluatorName == null || evaluatorName.length() == 0)
                {
                    throw new ConfigException("All evaluator elements must define an id attribute");
                }

                if (evaluatorClass == null || evaluatorClass.length() == 0)
                {
                    throw new ConfigException("Evaluator '" + evaluatorName + "' must define a class attribute");
                }

                // add the evaluator
                parsedEvaluators.put(evaluatorName, createEvaluator(evaluatorName, evaluatorClass));
            }
            
            return parsedEvaluators;
        }
        
        return null;
    }

    /**
     * Parses the element-readers element
     * 
     * @param readersElement
     */
    private Map<String, ConfigElementReader> parseElementReadersElement(Element readersElement)
    {
        if (readersElement != null)
        {
        	Map<String, ConfigElementReader> parsedElementReaders = new HashMap<String, ConfigElementReader>();
        	
            Iterator readers = readersElement.elementIterator();
            while (readers.hasNext())
            {
                Element readerElement = (Element) readers.next();
                String readerElementName = readerElement.attributeValue(ATTR_ELEMENT_NAME);
                String readerElementClass = readerElement.attributeValue(ATTR_CLASS);

                if (readerElementName == null || readerElementName.length() == 0)
                {
                    throw new ConfigException("All element-reader elements must define an element-name attribute");
                }

                if (readerElementClass == null || readerElementClass.length() == 0)
                {
                    throw new ConfigException("Element-reader '" + readerElementName
                            + "' must define a class attribute");
                }

                // add the element reader
                parsedElementReaders.put(readerElementName, createConfigElementReader(readerElementName, readerElementClass));
            }
            
            return parsedElementReaders;
        }
        
        return null;
    }

    /**
     * Parses a config element of a config file
     * 
     * @param configElement The config element
     * @param currentArea The current area
     */
    private ConfigSection parseConfigElement(Map<String, ConfigElementReader> parsedElementReaders, Element configElement, String currentArea)
    {
        if (configElement != null)
        {
            boolean replace = false;
            String evaluatorName = configElement.attributeValue(ATTR_EVALUATOR);
            String condition = configElement.attributeValue(ATTR_CONDITION);
            String replaceValue = configElement.attributeValue(ATTR_REPLACE);
            if (replaceValue != null && replaceValue.equalsIgnoreCase("true"))
            {
               replace = true;
            }

            // create the section object
            ConfigSectionImpl section = new ConfigSectionImpl(evaluatorName, condition, replace);

            // retrieve the config elements for the section
            Iterator children = configElement.elementIterator();
            while (children.hasNext())
            {
                Element child = (Element) children.next();
                String elementName = child.getName();

                // get the element reader for the child
                ConfigElementReader elementReader = null;
                if (parsedElementReaders != null)
                {
                	elementReader = parsedElementReaders.get(elementName);
                }
                
                if (elementReader == null)
                {
                	elementReader = getConfigElementReader(elementName);
                }
                
                if (logger.isDebugEnabled())
                    logger.debug("Retrieved element reader " + elementReader + " for element named '" + elementName
                            + "'");

                if (elementReader == null)
                {
                    elementReader = new GenericElementReader();

                    if (logger.isDebugEnabled())
                        logger.debug("Defaulting to " + elementReader + " as there wasn't an element "
                                + "reader registered for element '" + elementName + "'");
                }

                ConfigElement cfgElement = elementReader.parse(child);
                section.addConfigElement(cfgElement);

                if (logger.isDebugEnabled())
                    logger.debug("Added " + cfgElement + " to " + section);
            }
            
            return section;
        }
        
        return null;
    }

    /**
     * Adds the config element reader to the config service
     * 
     * @param name
     *            Name of the element
     * @param elementReader
     *            The element reader
     */
    private void addConfigElementReader(String elementName, ConfigElementReader elementReader)
    {
        putConfigElementReader(elementName, elementReader);

        if (logger.isDebugEnabled())
            logger.debug("Added element reader '" + elementName + "': " + elementReader.getClass().getName());
    }
    
    /**
     * Instantiate the config element reader with the given name and class
     * 
     * @param name
     *            Name of the element
     * @param className
     *            Class name of the element reader
     */
    private ConfigElementReader createConfigElementReader(String elementName, String className)
    {
        ConfigElementReader elementReader = null;

        try
        {
            Class clazz = Class.forName(className);
            elementReader = (ConfigElementReader) clazz.newInstance();
        }
        catch (Throwable e)
        {
            throw new ConfigException("Could not instantiate element reader for '" + elementName + "' with class: "
                    + className, e);

        }

        return elementReader;
    }

    /**
     * Gets the element reader from the in-memory 'cache' for the given element name
     * 
     * @param elementName Name of the element to get the reader for
     * @return ConfigElementReader object or null if it doesn't exist
     */
    private ConfigElementReader getConfigElementReader(String elementName)
    {
        return (ConfigElementReader) getElementReaders().get(elementName);
    }
    
    /**
     * Put the config element reader into the in-memory 'cache' for the given element name
     * 
     * @param elementName
     * @param elementReader
     */
    private void putConfigElementReader(String elementName, ConfigElementReader elementReader)
    {
        getElementReaders().put(elementName, elementReader);
    }
    
    /**
     * Get the elementReaders from the in-memory 'cache'
     * 
     * @return elementReaders
     */
    protected Map<String, ConfigElementReader> getElementReaders()
    {
        return elementReaders;
    }  
    
    /**
     * Put the elementReaders into the in-memory 'cache'
     * 
     * @param elementReaders
     */
    protected void putElementReaders(Map<String, ConfigElementReader> elementReaders)
    {
        this.elementReaders = elementReaders;
    }  
    
    /**
     * Remove the elementReaders from the in-memory 'cache'
     */
    protected void removeElementReaders()
    {
        elementReaders.clear();
        elementReaders = null;
    } 
}
