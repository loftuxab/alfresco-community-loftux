/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author muzquiano
 */
public class XMLUtil
{
    protected static Log logger = LogFactory.getLog(XMLUtil.class);

    public static Log getLogger()
    {
        return logger;
    }

    public static void addChildValue(Element parent, String childName,
            String childValue)
    {
        setChildValue(parent, childName, childValue);
    }

    public static Element getChild(Element parent, String name)
    {
        return parent.element(name);
    }

    public static String getValue(Element element)
    {
        return element.getTextTrim();
    }

    public static void setValue(Element element, String value)
    {
        element.clearContent();
        element.setText(value);
    }

    public static String getChildValue(Element element, String name)
    {
        Element child = getChild(element, name);
        if (child != null)
        {
            return getValue(child);
        }
        return null;
    }

    public static void setChildValue(Element element, String name, String value)
    {
        Element child = getChild(element, name);
        if (child == null)
            child = element.addElement(name);
        if (child != null)
            setValue(child, value);
    }

    public static List getChildren(Element element)
    {
        return getChildren(element, null);
    }

    public static List getChildren(Element element, String name)
    {
        return element.elements(name);
    }

    public static String getAttribute(Element element, String attributeName)
    {
        return element.attributeValue(attributeName);
    }

    public static String getDocumentChildValue(Document d, String name)
    {
        return getChildValue(d.getRootElement(), name);
    }

    public static void setDocumentChildValue(Document d, String name,
            String value) throws Exception
    {
        setChildValue(d.getRootElement(), name, value);
    }

    public static Document parse(String xml) throws DocumentException
    {
        return org.dom4j.DocumentHelper.parseText(xml);
    }

    public static Document parse(InputStream stream) throws DocumentException,
            IOException
    {
        String xml = DataUtil.copyToString(stream, true);
        return parse(xml);
    }

    public static String toXML(Document document)
    {
        return toXML(document, false);
    }

    public static String toXML(Document document, boolean pretty)
    {
        if (pretty)
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setSuppressDeclaration(false);

            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            try
            {
                xmlWriter.write(document);
                xmlWriter.flush();
                return writer.toString();
            }
            catch (IOException ioe)
            {
                // if this fails, we'll just opt out and let it serialize
                // in the default way (which is compact)
                ioe.printStackTrace();
            }
        }

        return document.asXML();
    }

}
