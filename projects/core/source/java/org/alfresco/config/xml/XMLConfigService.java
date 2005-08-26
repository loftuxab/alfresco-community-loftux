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
package org.alfresco.config.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.config.BaseConfigService;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.ConfigSectionImpl;
import org.alfresco.config.ConfigSource;
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
    private String currentArea;

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

    /**
     * @see org.alfresco.web.config.BaseConfigService#init()
     */
    public void init()
    {
        if (logger.isDebugEnabled())
            logger.debug("Commencing initialisation");

        super.init();

        // initialise the element readers map with built-in readers
        this.elementReaders = new HashMap<String, ConfigElementReader>();

        parse();

        if (logger.isDebugEnabled())
            logger.debug("Completed initialisation");
    }
    
    /**
     * @see org.alfresco.config.BaseConfigService#destroy()
     */
    public void destroy()
    {
       this.elementReaders.clear();
       this.elementReaders = null;
       this.currentArea = null;
       
       super.destroy();
    }

    /**
     * @see org.alfresco.web.config.BaseConfigService#parse(java.io.InputStream)
     */
    protected void parse(InputStream stream)
    {
        try
        {
            // get the root element
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element rootElement = document.getRootElement();

            // see if there is an area defined
            this.currentArea = rootElement.attributeValue("area");

            // parse the plug-ins section first
            Element pluginsConfig = rootElement.element(ELEMENT_PLUG_INS);
            parsePluginsElement(pluginsConfig);

            // parse each config section in turn
            Iterator configElements = rootElement.elementIterator(ELEMENT_CONFIG);
            while (configElements.hasNext())
            {
                Element configElement = (Element) configElements.next();
                parseConfigElement(configElement);
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
    }

    /**
     * Parses the plug-ins section of a config file
     * 
     * @param pluginsElement
     *            The plug-ins element
     */
    private void parsePluginsElement(Element pluginsElement)
    {
        if (pluginsElement != null)
        {
            // parese the evaluators section
            parseEvaluatorsElement(pluginsElement.element(ELEMENT_EVALUATORS));

            // parse the element readers section
            parseElementReadersElement(pluginsElement.element(ELEMENT_ELEMENT_READERS));
        }
    }

    /**
     * Parses the evaluators element
     * 
     * @param evaluatorsElement
     */
    private void parseEvaluatorsElement(Element evaluatorsElement)
    {
        if (evaluatorsElement != null)
        {
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
                this.addEvaluator(evaluatorName, evaluatorClass);
            }
        }
    }

    /**
     * Parses the element-readers element
     * 
     * @param readersElement
     */
    private void parseElementReadersElement(Element readersElement)
    {
        if (readersElement != null)
        {
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

                // add the evaluator
                addConfigElementReader(readerElementName, readerElementClass);
            }
        }
    }

    /**
     * Parses a config element of a config file
     * 
     * @param configElement
     *            The config element
     */
    private void parseConfigElement(Element configElement)
    {
        if (configElement != null)
        {
            String evaluatorName = configElement.attributeValue(ATTR_EVALUATOR);
            String condition = configElement.attributeValue(ATTR_CONDITION);

            // create the section object
            ConfigSectionImpl section = new ConfigSectionImpl(evaluatorName, condition);

            // retrieve the config elements for the section
            Iterator children = configElement.elementIterator();
            while (children.hasNext())
            {
                Element child = (Element) children.next();
                String elementName = child.getName();

                // get the element reader for the child
                ConfigElementReader elementReader = getConfigElementReader(elementName);
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

            // now all the config elements are added, add the section to the
            // config service
            addConfigSection(section, this.currentArea);
        }
    }

    /**
     * Adds the config element reader with the given name and class name
     * 
     * @param elementName
     *            Name of the element the reader is for
     * @param className
     *            Class name of element reader implementation
     */
    private void addConfigElementReader(String elementName, String className)
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

        this.elementReaders.put(elementName, elementReader);

        if (logger.isDebugEnabled())
            logger.debug("Added element reader '" + elementName + "': " + className);
    }

    /**
     * Retrieves the element reader for the given element name
     * 
     * @param elementName
     *            Name of the element to get the reader for
     * @return ConfigElementReader object or null if it doesn't exist
     */
    private ConfigElementReader getConfigElementReader(String elementName)
    {
        return (ConfigElementReader) this.elementReaders.get(elementName);
    }
}
