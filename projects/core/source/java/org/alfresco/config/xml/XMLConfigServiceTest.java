/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.config.xml;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.ConfigLookupContext;
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
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
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
     * Tests the retrieval of a named child
     */
    public void testGetNamedChild()
    {
       // setup the config service
        String configFile = getResourcesDir() + "config.xml";
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
        svc.init();
        
        // get the "Named Child Test" config
        Config cfg = svc.getConfig("Named Child Test");
        assertNotNull("Named child test config should not be null", cfg);
        
        // get the children config element
        ConfigElement children = cfg.getConfigElement("children");
        logger.info("Number of children: " + children.getChildCount());
        // check the getNumberOfChildren method works
        assertEquals("There should be four children", 4, children.getChildCount());
        
        // try and get a named child
        ConfigElement childTwo = children.getChild("child-two");
        assertNotNull("Child two config element should not be null", childTwo);
        assertEquals("Child two value should be 'child two value'", "child two value", 
              childTwo.getValue());
        logger.info("Number of attributes for for child-two: " + 
              childTwo.getAttributeCount());
        assertEquals("The number of attributes should be 0", 0, childTwo.getAttributeCount());
        
        // try and get a non existent child and check its null
        ConfigElement noChild = children.getChild("not-there");
        assertNull("The noChild config element should be null", noChild);
        
        // test the retrieval of grand children
        ConfigElement childThree = children.getChild("child-three");
        assertNotNull("Child three config element should not be null", childThree);
        ConfigElement grandKids = childThree.getChild("grand-children");
        assertNotNull("Grand child config element should not be null", grandKids);
        logger.info("Number of grand-children: " + grandKids.getChildCount());
        assertEquals("There should be 2 grand child config elements", 2, 
              grandKids.getChildCount());
        ConfigElement grandKidOne = grandKids.getChild("grand-child-one");
        assertNotNull("Grand child one config element should not be null", grandKidOne);
        logger.info("Number of attributes for for grand-child-one: " + 
              grandKidOne.getAttributeCount());
        logger.info("Number of children for for grand-child-one: " + 
              grandKidOne.getChildCount());
        assertEquals("The number of attributes for grand child one should be 1", 
              1, grandKidOne.getAttributeCount());
        assertEquals("The number of children for grand child one should be 0", 
              0, grandKidOne.getChildCount());
        assertTrue("The attribute 'an-attribute' should be present", 
              grandKidOne.getAttribute("an-attribute") != null);
    }
    
    /**
     * Tests the config service's ability to reset
     */
    public void testReset()
    {
       // setup the config service
        String configFile = getResourcesDir() + "config.xml";
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
        svc.init();

        // try and get the global item
        Config unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest should not be null", unitTest);
        
        // reset the config service then try to retrieve some config again
        svc.reset();
        unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest should not be null", unitTest);
    }

    /**
     * Tests the use of the class path source config
     * 
     * TODO: Enable this test when we have a classpath config resource to load!
     */
    public void xtestClasspathSource()
    {
        String configFile = "org/alfresco/config-classpath.xml"; 
        XMLConfigService svc = new XMLConfigService(new ClassPathConfigSource(configFile));
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
        XMLConfigService svc = new XMLConfigService(new HTTPConfigSource(configFile));
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
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
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
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.init();

        // try and get an section defined in an area (without restricting the
        // area)
        Config config = svc.getConfig("Area Specific Config");
        ConfigElement areaTest = config.getConfigElement("parent-item");
        assertNotNull("areaTest should not be null as a global lookup was performed", areaTest);

        // try and get an section defined in an area (with an area restricted
        // search)
        ConfigLookupContext lookupContext = new ConfigLookupContext();
        lookupContext.addArea("test-area");
        config = svc.getConfig("Area Specific Config", lookupContext);
        areaTest = config.getConfigElement("parent-item");
        assertNotNull("areaTest should not be null as it is defined in test-area", areaTest);

        // try and find a section defined outside an area with an area
        // restricted search
        config = svc.getConfig("Unit Test", lookupContext);
        ConfigElement unitTest = config.getConfigElement("item");
        assertNull("unitTest should be null as it is not defined in test-area", unitTest);

        // try and find some config in area that has not been defined, ensure we
        // get an error
        try
        {
            Config notThere = svc.getConfig("Unit Test", new ConfigLookupContext("not-there"));
            fail("Retrieving a non existent area should have thrown an exception!");
        }
        catch (ConfigException ce)
        {
            // expected to get this error
        }
    }

    public void testMerging()
    {
        // setup the config service
        List<String> configFiles = new ArrayList<String>(2);
        configFiles.add(getResourcesDir() + "config.xml");
        configFiles.add(getResourcesDir() + "config-multi.xml");
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.init();
        
        // try and get the global config section
        Config globalSection = svc.getGlobalConfig();
        assertNotNull("global section should not be null", globalSection);
        
        // make sure that the override config value got overridden in the global
        // section
        ConfigElement overrideItem = globalSection.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should be true", "true", overrideItem.getValue());
        logger.info("overrideItem = " + overrideItem.getValue());
        
        // make sure the global section gets merged properly
        ConfigElement mergeChildren = globalSection.getConfigElement("merge-children");
        assertNotNull("mergeChildren should not be null", mergeChildren);
        List<ConfigElement> kids = mergeChildren.getChildren();
        assertEquals("There should be 2 children", 2, kids.size());
        
        // get the merge test config section
        Config mergeTest = svc.getConfig("Merge Test");
        assertNotNull("Merge test config should not be null", mergeTest);
        
        // check that there is a first, second, thrid and fourth config element
        ConfigElement first = mergeTest.getConfigElement("first-item");
        ConfigElement second = mergeTest.getConfigElement("second-item");
        ConfigElement third = mergeTest.getConfigElement("third-item");
        ConfigElement fourth = mergeTest.getConfigElement("fourth-item");
        assertNotNull("first should not be null", first);
        assertNotNull("second should not be null", second);
        assertNotNull("third should not be null", third);
        assertNotNull("fourth should not be null", fourth);
        
        // test that the first-item got overridden
        String firstValue = first.getValue();
        assertEquals("The first value is wrong", "the overridden first value", firstValue);
        
        // test that there are two child items under the children config element
        ConfigElement children = mergeTest.getConfigElement("children");
        assertNotNull("children should not be null", children);
        kids = children.getChildren();
        assertEquals("There should be 3 children", 3, kids.size());
    }
}
