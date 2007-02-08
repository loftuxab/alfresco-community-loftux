/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
   @SuppressWarnings("unchecked")
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
   @SuppressWarnings("unchecked")
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
