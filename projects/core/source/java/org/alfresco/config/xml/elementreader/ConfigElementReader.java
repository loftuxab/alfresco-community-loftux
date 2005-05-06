package org.alfresco.config.xml.elementreader;

import org.dom4j.Element;

import org.alfresco.config.ConfigElement;

/**
 * Definition of an object responsible for converting the XML representation of a config
 * element into an in-memory object representation
 * 
 * @author gavinc
 */
public interface ConfigElementReader
{
   /**
    * Parses the given XML element into a ConfigElement object
    * 
    * @param element The XML element to parse
    * @return The object representation of the XML element
    */
   public ConfigElement parse(Element element);
}
