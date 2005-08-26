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
package org.alfresco.config.xml.elementreader;

import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Element;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.GenericConfigElement;

/**
 * Implementation of a generic element reader. This class can be used to 
 * convert any config element into a GenericConfigElement.
 * 
 * @author gavinc
 */
public class GenericElementReader implements ConfigElementReader
{
   /**
    * @see org.alfresco.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      GenericConfigElement configElement = null;
      
      if (element != null)
      {
         configElement = createConfigElement(element);
         
         // process any children there may be
         processChildren(element, configElement);
      }
      
      return configElement;
   }

   /**
    * Recursively processes the children creating the required config element
    * objects as it goes
    * 
    * @param element
    * @param parentConfig
    */
   private void processChildren(Element element, GenericConfigElement parentConfig)
   {
      // get the list of children for the given element
      Iterator<Element> children = element.elementIterator();
      while (children.hasNext())
      {
         Element child = children.next();
         GenericConfigElement childConfigElement = createConfigElement(child);
         parentConfig.addChild(childConfigElement);
         
         // recurse down the children
         processChildren(child, childConfigElement);
      }
   }
   
   /**
    * Creates a ConfigElementImpl object from the given element.
    * 
    * @param element
    * @return
    */
   private GenericConfigElement createConfigElement(Element element)
   {
      // get the name and value of the given element
      String name = element.getName();
      
      // create the config element object and populate with value
      // and attributes
      GenericConfigElement configElement = new GenericConfigElement(name);
      if ((element.hasContent()) && (element.hasMixedContent() == false))
      {
         String value = element.getTextTrim();
         if (value != null && value.length() > 0)
         {
            configElement.setValue(value);
         }
      }
      
      Iterator<Attribute> attrs = element.attributeIterator();
      while (attrs.hasNext())
      {
         Attribute attr = attrs.next();
         String attrName = attr.getName();
         String attrValue = attr.getValue();
         
         configElement.addAttribute(attrName, attrValue);
      }
      
      return configElement;
   }
}
