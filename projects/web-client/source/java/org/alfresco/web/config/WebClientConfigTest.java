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
package org.alfresco.web.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.source.FileConfigSource;
import org.alfresco.config.xml.XMLConfigService;
import org.alfresco.util.BaseTest;
import org.alfresco.web.config.PropertySheetConfigElement.PropertyConfig;
import org.apache.log4j.Logger;

/**
 * JUnit tests to exercise the capabilities added to the web client config
 * service
 * 
 * @author gavinc
 */
public class WebClientConfigTest extends BaseTest
{
   private static Logger logger = Logger.getLogger(WebClientConfigTest.class);

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception
   {
      super.setUp();

      logger.info("******************************************************");
   }

   /**
    * Tests the property sheet configuration classes
    */
   public void testPropertySheetConfig()
   {
      // setup the config service
      String configFile = getResourcesDir() + "test-config.xml";
      XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
      svc.init();

      // get hold of the property sheet config from the global section
      Config global = svc.getGlobalConfig();
      ConfigElement globalPropSheet = global.getConfigElement("property-sheet");
      assertNotNull("global property sheet element should not be null", globalPropSheet);
      assertTrue("config element should be an instance of PropertySheetConfigElement",
            (globalPropSheet instanceof PropertySheetConfigElement));

      // get the property names from the global section and make sure it is the
      // name property
      List<String> propNames = ((PropertySheetConfigElement) globalPropSheet).getPropertyNamesToShow();
      logger.info("propNames = " + propNames);
      assertTrue("There should only be one property in the list", propNames.size() == 1);
      assertTrue("The property name should be 'name'", propNames.get(0).equals("name"));

      // get the config section representing a space aspect and make sure we get
      // 5 properties
      Config spaceAspectConfig = svc.getConfig("space-aspect");
      assertNotNull("Space aspect config should not be null", spaceAspectConfig);
      PropertySheetConfigElement spacePropConfig = (PropertySheetConfigElement) spaceAspectConfig
            .getConfigElement("property-sheet");
      assertNotNull("Space aspect property config should not be null", spacePropConfig);
      propNames = spacePropConfig.getPropertyNamesToShow();
      logger.info("propNames = " + propNames);
      assertTrue("There should be 5 properties in the list", propNames.size() == 5);

      // make sure the property sheet config has come back with the correct data
      Map<String, PropertyConfig> props = spacePropConfig.getPropertiesMapToShow();
      PropertyConfig descProp = props.get("description");
      assertNotNull("description property config should not be null", descProp);
      assertEquals("display label for description should be 'Description'", descProp.getDisplayLabel(), 
            "Description");
      assertFalse("read only for description should be 'false'", descProp.isReadOnly());

      PropertyConfig createdDataProp = props.get("createddate");
      assertNotNull("createddate property config should not be null", createdDataProp);
      assertEquals("display label for createddate should be null", null, createdDataProp.getDisplayLabel());
      assertTrue("read only for createddate should be 'true'", createdDataProp.isReadOnly());

      PropertyConfig iconProp = props.get("icon");
      assertNotNull("icon property config should not be null", iconProp);
      assertEquals("display label for icon should be null", null, iconProp.getDisplayLabel());
      assertFalse("read only for icon should be 'false'", iconProp.isReadOnly());
   }

   /**
    * Tests the config service by retrieving property sheet configuration using
    * the generic interfaces
    */
   public void testGenericConfigElement()
   {
      // setup the config service
      String configFiles = getResourcesDir() + "test-config.xml";
      XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
      svc.init();

      // get the space aspect configuration
      Config configProps = svc.getConfig("space-aspect");
      ConfigElement propsToDisplay = configProps.getConfigElement("property-sheet");
      assertNotNull("property sheet config should not be null", propsToDisplay);

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
      assertTrue("There should be 5 properties", propNames.size() == 5);
      assertFalse("The id attribute should not be present", propsToDisplay.hasAttribute("id"));
   }

   /**
    * Tests the config service by retrieving property sheet configuration using
    * the custom config objects
    */
   public void testGetProperties()
   {
      // setup the config service
      String configFiles = getResourcesDir() + "test-config.xml";
      XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
      svc.init();
      
      // get the space aspect configuration
      Config configProps = svc.getConfig("space-aspect");
      PropertySheetConfigElement propsToDisplay = (PropertySheetConfigElement)configProps.
            getConfigElement("property-sheet");
      assertNotNull("property sheet config should not be null", propsToDisplay);
      
      // get all the property names using the PropertySheetConfigElement implementation
      List<String> propNames = propsToDisplay.getPropertyNamesToShow();
              
      // make sure the generic interfaces are also returning the correct data
      List<ConfigElement> kids = propsToDisplay.getChildren();
      assertNotNull("kids should not be null", kids);
      assertTrue("There should be more than one child", kids.size() > 1);
      
      logger.info("propNames = " + propNames);
      assertEquals("There should be 5 properties", propNames.size() == 5, true);
      assertFalse("The id attribute should not be present", propsToDisplay.hasAttribute("id"));
   }
   
   /**
    * Tests the custom server configuration objects
    */
   public void testServerConfig()
   {
      // setup the config service
      String configFiles = getResourcesDir() + "test-config.xml";
      XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
      svc.init();
      
      // get the global config and from that the server config
      ServerConfigElement serverConfig = (ServerConfigElement)svc.getGlobalConfig().getConfigElement("server");
      assertNotNull("server config should not be null", serverConfig);

      String mode = serverConfig.getMode();
      logger.info("mode = " + mode);
      assertTrue("server mode should be 'servlet'", mode.equals("servlet"));
      logger.info("is portlet mode = " + serverConfig.isPortletMode());
      assertFalse("inPortletMode should return 'false'", serverConfig.isPortletMode());
      
      String errorPage = serverConfig.getErrorPage();
      logger.info("error page = " + errorPage);
      assertTrue("error page should be '/jsp/error.jsp'", errorPage.equals("/jsp/error.jsp"));
   }
}
