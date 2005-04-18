package com.activiti.web.config.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsftest.repository.NodeRef;
import jsftest.repository.NodeService;
import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.activiti.web.config.Config;
import com.activiti.web.config.ConfigElement;
import com.activiti.web.config.ConfigService;
import com.activiti.web.config.element.PropertiesConfigElement;
import com.activiti.web.config.source.FileConfigSource;
import com.activiti.web.jsf.component.data.UIRichList;
import com.activiti.web.repository.Node;

/**
 * Unit tests for the XML based configuration service
 * 
 * @author gavinc
 */
public class XMLConfigServiceTest extends TestCase
{
   private static Logger logger = Logger.getLogger(XMLConfigServiceTest.class);
   private static ConfigService configService;
   
   /**
    * Tests the config service by retrieving properties configuration using
    * the generic interfaces
    */
   public void xtestGetPropertiesViaInterfaces()
   {
      NodeRef nodeRef = NodeService.getNodeRef("/gav.doc");
      Node node = new Node(NodeService.getType(nodeRef));
      node.setProperties(NodeService.getProperties(nodeRef));
    
      ConfigService svc = XMLConfigServiceTest.getConfigService();
      assertNotNull(svc);
      
      Config configProps = svc.getConfig(node);
      ConfigElement propsToDisplay = configProps.getConfigElement("properties");
      assertNotNull(propsToDisplay);
      
      List kids = propsToDisplay.getChildren();
      List propNames = new ArrayList();
      for (Iterator iter = kids.iterator(); iter.hasNext();)
      {
         ConfigElement propElement = (ConfigElement)iter.next();
         String value = propElement.getValue();
         assertNull(value);
         String propName = propElement.getAttribute("name");
         propNames.add(propName);
      }
      
      logger.info("propNames = " + propNames);
      assertEquals(propNames.size() != 0, true);
   }
   
   /**
    * Tests the config service by retrieving properties configuration using
    * the Properties specific config objects
    */
   public void testGetProperties()
   {
      NodeRef nodeRef = NodeService.getNodeRef("/gav.doc");
      Node node = new Node(NodeService.getType(nodeRef));
      node.setProperties(NodeService.getProperties(nodeRef));
      
      ConfigService svc = XMLConfigServiceTest.getConfigService();
      assertNotNull(svc);
      
      Config configProps = svc.getConfig(node);
      PropertiesConfigElement propsToDisplay = (PropertiesConfigElement)configProps.getConfigElement("properties");
      List propNames = propsToDisplay.getProperties();
      
      logger.info("propNames = " + propNames);
      assertEquals(propNames.size() != 0, true);
   }
   
   /**
    * Tests the config service retrieving the renderers config for a component
    */
   public void testGetRenderers()
   {
      // create an instance of the component
      UIRichList richList = new UIRichList();
      
      ConfigService svc = XMLConfigServiceTest.getConfigService();
      assertNotNull(svc);
      
      Config configProps = svc.getConfig(richList);
      ConfigElement renderers = configProps.getConfigElement("renderers");
      
      logger.info("renderers = " + renderers);
      assertNotNull(renderers);
   }
   
   /**
    * @return The ConfigService
    */
   private static ConfigService getConfigService()
   {
      if (configService == null)
      {
         String file = "w:\\sandbox\\projects\\web-client\\source\\web\\WEB-INF\\web-client-config.xml";
         configService = new XMLConfigService(new FileConfigSource(file));
         configService.init();
      }
      
      return configService;
   }
}
