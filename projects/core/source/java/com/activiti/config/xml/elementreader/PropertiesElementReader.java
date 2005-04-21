package com.activiti.config.xml.elementreader;

import java.util.Iterator;

import org.dom4j.Element;

import com.activiti.config.ConfigElement;
import com.activiti.config.ConfigException;
import com.activiti.config.element.PropertiesConfigElement;

/**
 * Element reader that knows how to parse properties config blocks
 * 
 * @author gavinc
 */
public class PropertiesElementReader implements ConfigElementReader
{
   private static final String ELEMENT_PROPERTIES = "properties";
   private static final String ELEMENT_PROPERTY = "property";
   private static final String ATTR_NAME = "name";
   
   /**
    * @see com.activiti.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      PropertiesConfigElement configElement = null;
      
      if (element != null)
      {
         String name = element.getName();
         if (name.equalsIgnoreCase(ELEMENT_PROPERTIES) == false)
         {
            throw new ConfigException("PropertiesElementReader can only parse properties elements, " +
                  "the element passed was '" + name + "'");
         }
         
         configElement = new PropertiesConfigElement();
         
         Iterator properties = element.elementIterator(ELEMENT_PROPERTY);
         while (properties.hasNext())
         {
            Element property = (Element)properties.next();
            String propName = property.attributeValue(ATTR_NAME);
            configElement.addProperty(propName);
         }
      }
      
      return configElement;
   }

}
