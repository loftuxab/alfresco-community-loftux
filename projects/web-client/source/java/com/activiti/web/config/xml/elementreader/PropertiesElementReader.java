package com.activiti.web.config.xml.elementreader;

import java.util.Iterator;

import org.dom4j.Element;

import com.activiti.web.config.ConfigElement;
import com.activiti.web.config.ConfigException;

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
    * @see com.activiti.web.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      ConfigElement configElement = null;
      
      if (element != null)
      {
         String name = element.getName();
         if (name.equalsIgnoreCase(ELEMENT_PROPERTIES) == false)
         {
            throw new ConfigException("PropertiesElementReader can only parse properties elements, " +
                  "the element passed was '" + name + "'");
         }
         
         Iterator properties = element.elementIterator(ELEMENT_PROPERTY);
         while (properties.hasNext())
         {
            Element property = (Element)properties.next();
            String propName = property.attributeValue(ATTR_NAME);
         }
      }
      
      return configElement;
   }

}
