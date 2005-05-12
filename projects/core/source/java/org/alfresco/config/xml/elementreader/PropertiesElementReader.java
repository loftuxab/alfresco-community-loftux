package org.alfresco.config.xml.elementreader;

import java.util.Iterator;

import org.dom4j.Element;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.element.PropertiesConfigElement;

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
    * @see org.alfresco.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
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
         
         Iterator<Element> properties = element.elementIterator(ELEMENT_PROPERTY);
         while (properties.hasNext())
         {
            Element property = properties.next();
            String propName = property.attributeValue(ATTR_NAME);
            configElement.addProperty(propName);
         }
      }
      
      return configElement;
   }

}
