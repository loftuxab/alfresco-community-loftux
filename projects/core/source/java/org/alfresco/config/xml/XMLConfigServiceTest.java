package org.alfresco.config.xml;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.ConfigService;
import org.alfresco.config.element.PropertiesConfigElement;
import org.alfresco.config.source.ClassPathConfigSource;
import org.alfresco.config.source.FileConfigSource;
import org.alfresco.config.source.HTTPConfigSource;
import org.alfresco.util.BaseTest;
import org.apache.log4j.Logger;

/**
 * Unit tests for the XML based configuration service
 * 
 * @author gavinc
 */
public class XMLConfigServiceTest extends BaseTest
{
    private static Logger logger = Logger.getLogger(XMLConfigServiceTest.class);
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        logger.info("******************************************************");
    }

    /**
     * Tests the config.xml file
     */
    public void testConfig()
    {
        // setup the config service
        String configFile = getResourcesDir() + "config.xml";
        ConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
        svc.init();

        // try and get the global item
        Config global = svc.getGlobalConfig();
        ConfigElement globalItem = global.getConfigElement("global-item");
        assertNotNull("globalItem should not be null", globalItem);
        assertEquals("The global-item value should be 'The global value'", "The global value", globalItem.getValue());
        logger.info("globalItem = " + globalItem.getValue());

        // try and get the override item
        ConfigElement overrideItem = global.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should be false", "false", overrideItem.getValue());
        logger.info("overrideItem = " + overrideItem.getValue());

        // test the string evaluator by getting the item config element
        // in the "Unit Test" config section
        Config unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest config result should not be null", unitTest);
        ConfigElement item = unitTest.getConfigElement("item");
        assertNotNull("item should not be null", item);
        assertEquals("The item value should be 'The value'", "The value", item.getValue());
        logger.info("item = " + item.getValue());

        // make sure the override value has changed when retrieved from item
        overrideItem = unitTest.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should now be true", "true", overrideItem.getValue());
        logger.info("overrideItem = " + overrideItem.getValue());
    }

    /**
     * Tests the use of the class path source config
     * 
     * TODO: Enable this test when we have a classpath config resource to load!
     */
    public void xtestClasspathSource()
    {
        String configFile = "org/alfresco/config-classpath.xml"; 
        ConfigService svc = new XMLConfigService(new ClassPathConfigSource(configFile));
        svc.init();
        
        Config config = svc.getGlobalConfig();
        assertNotNull(config);
    }
    
    /**
     * Tests the use of the HTTP source config
     * 
     * TODO: Enable this test when we have an HTTP config resource to load!
     */
    public void xtestHTTPSource()
    {
        List<String> configFile = new ArrayList<String>(1);
        configFile.add("http://localhost:8080/web-client/config-http.xml");
        ConfigService svc = new XMLConfigService(new HTTPConfigSource(configFile));
        svc.init();
        
        Config config = svc.getGlobalConfig();
        assertNotNull(config);
    }
    
    /**
     * Tests the config service's ability to load multiple files and merge the
     * results
     */
    public void testMultiConfig()
    {
        // setup the config service
        List<String> configFiles = new ArrayList<String>(2);
        configFiles.add(getResourcesDir() + "config.xml");
        configFiles.add(getResourcesDir() + "config-multi.xml");
        ConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.init();

        // try and get the global config section
        Config globalSection = svc.getGlobalConfig();

        // try and get items from the global section defined in each file
        ConfigElement globalItem = globalSection.getConfigElement("global-item");
        assertNotNull("globalItem should not be null", globalItem);
        assertEquals("The global-item value should be 'The global value'", "The global value", globalItem.getValue());
        logger.info("globalItem = " + globalItem.getValue());

        ConfigElement globalItem2 = globalSection.getConfigElement("another-global-item");
        assertNotNull("globalItem2 should not be null", globalItem2);
        assertEquals("The another-global-item value should be 'Another global value'", "Another global value",
                globalItem2.getValue());
        logger.info("globalItem2 = " + globalItem2.getValue());

        // make sure that the override config value got overridden in the global
        // section
        ConfigElement overrideItem = globalSection.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should be true", "true", overrideItem.getValue());
        logger.info("overrideItem = " + overrideItem.getValue());

        // lookup the "Unit Test" section, this should match a section in each
        // file so
        // we should be able to get hold of config elements "item" and
        // "another-item"
        Config unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest should not be null", unitTest);
        ConfigElement item = unitTest.getConfigElement("item");
        assertNotNull("item should not be null", item);
        ConfigElement anotherItem = unitTest.getConfigElement("another-item");
        assertNotNull("another-item should not be null", anotherItem);
    }

    /**
     * Tests the config service's ability to restrict searches to a named area
     */
    public void testAreaConfig()
    {
        // setup the config service
        List<String> configFiles = new ArrayList<String>(2);
        configFiles.add(getResourcesDir() + "config.xml");
        configFiles.add(getResourcesDir() + "config-areas.xml");
        ConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.init();

        // try and get an section defined in an area (without restricting the
        // area)
        Config config = svc.getConfig("Area Specific Config");
        ConfigElement areaTest = config.getConfigElement("parent-item");
        assertNotNull("areaTest should not be null as a global lookup was performed", areaTest);

        // try and get an section defined in an area (with an area restricted
        // search)
        config = svc.getConfig("Area Specific Config", "test-area");
        areaTest = config.getConfigElement("parent-item");
        assertNotNull("areaTest should not be null as it is defined in test-area", areaTest);

        // try and find a section defined outside an area with an area
        // restricted search
        config = svc.getConfig("Unit Test", "test-area");
        ConfigElement unitTest = config.getConfigElement("item");
        assertNull("unitTest should be null as it is not defined in test-area", unitTest);

        // try and find some config in area that has not been defined, ensure we
        // get an error
        try
        {
            Config notThere = svc.getConfig("Unit Test", "not-there");
            fail("Retrieving a non existent area should have thrown an exception!");
        }
        catch (ConfigException ce)
        {
            // expected to get this error
        }

        // TODO: Add more tests for searching multiple areas
    }

    public void xtestMerging()
    {
        // TODO: Add tests to make sure merging works (move tests from elsewhere
        // to here)
        // include tests including and excluding globals and areas
    }

    /**
     * Tests the config service by retrieving properties configuration using the
     * generic interfaces
     */
    public void testGenericConfigElement()
    {
        // setup the config service
        String configFiles = getResourcesDir() + "config-properties.xml";
        ConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.init();

        // get the base properties configuration
        Config configProps = svc.getConfig("base");
        ConfigElement propsToDisplay = configProps.getConfigElement("properties");
        assertNotNull("properties config should not be null", propsToDisplay);

        // get all the property names using the ConfigElement interface methods
        List<ConfigElement> kids = propsToDisplay.getChildren();
        List<String> propNames = new ArrayList<String>();
        for (ConfigElement propElement : propsToDisplay.getChildren())
        {
            String value = propElement.getValue();
            assertNull("property value should be null", value);
            String propName = propElement.getAttribute("name");
            propNames.add(propName);
        }

        logger.info("propNames = " + propNames);
        assertEquals("There should be 4 properties", propNames.size() == 4, true);

        logger.info("has attribute id: " + propsToDisplay.hasAttribute("id"));
        assertEquals("The id attribute should not be present", propsToDisplay.hasAttribute("id"), false);
    }

    /**
     * Tests the config service by retrieving properties configuration using the
     * Properties specific config objects
     */
    public void testGetProperties()
    {
        // setup the config service
        String configFiles = getResourcesDir() + "config-properties.xml";
        ConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.init();

        // get the SOP properties configuration
        Config configProps = svc.getConfig("SOP");
        PropertiesConfigElement propsToDisplay = (PropertiesConfigElement) configProps.getConfigElement("properties");
        assertNotNull("properties config should not be null", propsToDisplay);

        // get all the property names using the PropertiesConfigElement
        // implementation
        List<String> propNames = propsToDisplay.getProperties();
        
        // make sure the generic interfaces are also returning the correct data
        List<ConfigElement> kids = propsToDisplay.getChildren();
        assertNotNull("kids should not be null", kids);
        assertTrue("There should be more than one child", kids.size() > 1);

        logger.info("propNames = " + propNames);
        assertEquals("There should be 5 properties", propNames.size() == 5, true);

        logger.info("has attribute id: " + propsToDisplay.hasAttribute("id"));
        assertEquals("The id attribute should not be present", propsToDisplay.hasAttribute("id"), false);
    }
}
