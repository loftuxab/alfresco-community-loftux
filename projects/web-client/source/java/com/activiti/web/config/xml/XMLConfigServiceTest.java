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
import com.activiti.web.config.ConfigServiceFactory;
import com.activiti.web.repository.Node;

/**
 * Unit tests for the XML based configuration service
 * 
 * @author gavinc
 */
public class XMLConfigServiceTest extends TestCase
{
   private static Logger logger = Logger.getLogger(XMLConfigServiceTest.class);
   
   public void testGetPropertiesViaInterfaces()
   {
      NodeRef nodeRef = NodeService.getNodeRef("/gav.doc");
      Node node = new Node(NodeService.getType(nodeRef));
      node.setProperties(NodeService.getProperties(nodeRef));
    
      ConfigService svc = ConfigServiceFactory.getConfigService();
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
   
   public void xtestGetProperties()
   {
//      NodeRef nodeRef = NodeService.getNodeRef("/gav.doc");
//      Node node = new Node(NodeService.getType(nodeRef));
//      node.setProperties(NodeService.getProperties(nodeRef));
      
//      ConfigService configSvc = ConfigServiceFactory.getConfigService();
//      Config configProps = configSvc.getConfig(node);
//      PropertiesConfigElement propsToDisplay = (PropertiesConfigElement)configProps.getConfigElement("properties");
//      Map propNames = propsToDisplay.getPropertiesMap();
//      assertEquals(propNames.size() != 0, true);
   }
}
