/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * A set of XML utility functions that make use of the Dom4j package.
 * 
 * This class originally existed as a means of accomplishing much of what
 * Dom4j now allows us to do.  As such, the expectation is that it will
 * be phased out over time.
 * 
 * @author muzquiano
 */
public class XMLUtil
{
    
    /** The logger. */
    protected static Log logger = LogFactory.getLog(XMLUtil.class);

    /**
     * Adds the child value.
     * 
     * @param parent the parent
     * @param childName the child name
     * @param childValue the child value
     */
    public static void addChildValue(Element parent, String childName,
            String childValue)
    {
        setChildValue(parent, childName, childValue);
    }

    /**
     * Gets the child.
     * 
     * @param parent the parent
     * @param name the name
     * 
     * @return the child
     */
    public static Element getChild(Element parent, String name)
    {
        return parent.element(name);
    }

    /**
     * Gets the value.
     * 
     * @param element the element
     * 
     * @return the value
     */
    public static String getValue(Element element)
    {
        return element.getTextTrim();
    }

    /**
     * Sets the value.
     * 
     * @param element the element
     * @param value the value
     */
    public static void setValue(Element element, String value)
    {
        element.clearContent();
        element.setText(value);
    }

    /**
     * Gets the child value.
     * 
     * @param element the element
     * @param name the name
     * 
     * @return the child value
     */
    public static String getChildValue(Element element, String name)
    {
        Element child = getChild(element, name);
        if (child != null)
        {
            return getValue(child);
        }
        return null;
    }

    /**
     * Sets the child value.
     * 
     * @param element the element
     * @param name the name
     * @param value the value
     */
    public static void setChildValue(Element element, String name, String value)
    {
        Element child = getChild(element, name);
        if (child == null)
        {
            /**
             * A child did not yet exist, so create one
             */
            child = element.addElement(name);
        }
        if (child != null)
        {
            setValue(child, value);
        }
    }

    /**
     * Gets the children.
     * 
     * @param element the element
     * 
     * @return the children
     */
    public static List getChildren(Element element)
    {
        return getChildren(element, null);
    }

    /**
     * Gets the children.
     * 
     * @param element the element
     * @param name the name
     * 
     * @return the children
     */
    public static List getChildren(Element element, String name)
    {
        if (name == null)
        {
            return element.elements();
        }
        return element.elements(name);
    }

    /**
     * Gets an attribute from the given element with the given attribute name
     * 
     * @param element the element
     * @param attributeName the attribute name
     * 
     * @return the attribute
     */
    public static String getAttribute(Element element, String attributeName)
    {
        return element.attributeValue(attributeName);
    }

    /**
     * Gets the document child value.
     * 
     * @param d the d
     * @param name the name
     * 
     * @return the document child value
     */
    public static String getDocumentChildValue(Document d, String name)
    {
        return getChildValue(d.getRootElement(), name);
    }

    /**
     * Sets the document child value.
     * 
     * @param d the d
     * @param name the name
     * @param value the value
     * 
     * @throws Exception the exception
     */
    public static void setDocumentChildValue(Document d, String name,
            String value) throws Exception
    {
        setChildValue(d.getRootElement(), name, value);
    }

    /**
     * Parses the.
     * 
     * @param xml the xml
     * 
     * @return the document
     * 
     * @throws DocumentException the document exception
     */
    public static Document parse(String xml) throws DocumentException
    {
        return org.dom4j.DocumentHelper.parseText(xml);
    }

    /**
     * Parses the given stream to an XML document (in UTF-8 format).
     * 
     * @param stream the stream
     * 
     * @return the document
     * 
     * @throws DocumentException the document exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Document parse(InputStream stream) throws DocumentException,
            IOException
    {
        return parse(DataUtil.copyToString(stream, "UTF-8", true));
    }

    /**
     * Converts the document to XML.  This uses an efficient approach so
     * that the XML is kept to a minimal.
     * 
     * @param document the document
     * 
     * @return the string
     */
    public static String toXML(Document document)
    {
        return toXML(document, false);
    }

    /**
     * Converts the document to XML.  The pretty switch can be used to produce
     * human readable, or pretty, XML.
     * 
     * @param document the document
     * @param pretty whether to produce human readable XML
     * 
     * @return the string
     */
    public static String toXML(Document document, boolean pretty)
    {
        String xml = null;
        
        if (pretty)
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setSuppressDeclaration(false);

            StringBuilderWriter writer = new StringBuilderWriter(256);
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            try
            {
                xmlWriter.write(document);
                xmlWriter.flush();
                xml = writer.toString();
            }
            catch (IOException ioe)
            {
                /**
                 * If this exception occurs, we'll log a note to the console
                 * and then proceed to serialze the old fashioned way.
                 */
                logger.debug(ioe);
            }
        }

        /**
         * If the XML wasn't created already, we'll serialize the
         * standard way.
         */
        if (xml == null)
        {
            xml = document.asXML();
        }
        
        return xml;
    }
}
