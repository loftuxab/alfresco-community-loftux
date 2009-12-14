/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config.forms;

import java.util.Iterator;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import org.dom4j.Element;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;constraint-handlers&gt; elements.
 * 
 * @author Neil McErlean.
 */
class ConstraintHandlersElementReader implements ConfigElementReader
{
   // <constraint-handler> elements can appear at a number of different places within
   // the config file, currently underneath the <config> element and also underneath
   // <field> elements.
   //
   // Note: this class' parse method is called directly by FormElementReader in order
   // to read <constraint-handlers> defined at a field level. There is an interesting
   // note here: if a customer chooses to override the element-reader defined for
   // "constraint-handlers" that new implementation of parse will be called as normal
   // for constraint-handlers elements defined underneath a <config> element.
   //
   // However, if the "forms" element-reader is not also overridden, the standard parse
   // implementation will be called which - for field-level constraint-handlers - delegates
   // from FormsElementReader to FormElementReader and from there to this class.
   public static final String ELEMENT_CONSTRAINT_HANDLERS = "constraint-handlers";
   public static final String ATTR_TYPE = "type";
   public static final String ATTR_VALIDATOR_HANDLER = "validation-handler";
   public static final String ATTR_MESSAGE = "message";
   public static final String ATTR_MESSAGE_ID = "message-id";
   public static final String ATTR_EVENT = "event";
   
   /**
    * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   @SuppressWarnings("unchecked")
   public ConfigElement parse(Element element)
   {
	   ConstraintHandlersConfigElement result = null;
	   if (element == null)
	   {
		   return null;
	   }
	   
	   String name = element.getName();
	   if (!name.equals(ELEMENT_CONSTRAINT_HANDLERS))
	   {
		   throw new ConfigException(this.getClass().getName() + " can only parse " +
				   ELEMENT_CONSTRAINT_HANDLERS + " elements, the element passed was '" + name + "'");
	   }
	   
	   result = new ConstraintHandlersConfigElement();
	   
	   Iterator<Element> xmlNodes = element.elementIterator();
	   while (xmlNodes.hasNext())
	   {
		   Element nextNode = xmlNodes.next();
           String type = nextNode.attributeValue(ATTR_TYPE);
           String validationHandler = nextNode.attributeValue(ATTR_VALIDATOR_HANDLER);
           String message = nextNode.attributeValue(ATTR_MESSAGE);
           String messageId = nextNode.attributeValue(ATTR_MESSAGE_ID);
           String event = nextNode.attributeValue(ATTR_EVENT);
           
           result.addDataMapping(type, validationHandler, message, messageId, event);
	   }
	   
	   return result;
   }
}
