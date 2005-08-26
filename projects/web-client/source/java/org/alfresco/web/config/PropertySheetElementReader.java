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
package org.alfresco.web.config;

import java.util.Iterator;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.xml.elementreader.ConfigElementReader;
import org.dom4j.Element;

/**
 * Custom element reader to parse config for property sheets
 * 
 * @author gavinc
 */
public class PropertySheetElementReader implements ConfigElementReader
{
   public static final String ELEMENT_PROPERTY_SHEET = "property-sheet";
   public static final String ELEMENT_SHOW_PROPERTY = "show-property";
   public static final String ATTR_NAME = "name";
   public static final String ATTR_DISPLAY_LABEL = "displayLabel";
   public static final String ATTR_READ_ONLY = "readOnly";
   public static final String ATTR_CONVERTER = "converter";
   
   /**
    * @see org.alfresco.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      PropertySheetConfigElement configElement = null;
      
      if (element != null)
      {
         String name = element.getName();
         if (name.equals(ELEMENT_PROPERTY_SHEET) == false)
         {
            throw new ConfigException("PropertySheetElementReader can only parse " +
                  ELEMENT_PROPERTY_SHEET + "elements, " + "the element passed was '" + 
                  name + "'");
         }
         
         configElement = new PropertySheetConfigElement();
         
         // go through the properties to show
         Iterator<Element> properties = element.elementIterator(ELEMENT_SHOW_PROPERTY);
         while (properties.hasNext())
         {
            Element property = properties.next(); 
            String propName = property.attributeValue(ATTR_NAME);
            String label = property.attributeValue(ATTR_DISPLAY_LABEL);
            String readOnly = property.attributeValue(ATTR_READ_ONLY);
            String converter = property.attributeValue(ATTR_CONVERTER);
            
            // add the property to show to the custom config element
            configElement.addProperty(propName, label, readOnly, converter);
         }
      }
      
      return configElement;
   }

}
