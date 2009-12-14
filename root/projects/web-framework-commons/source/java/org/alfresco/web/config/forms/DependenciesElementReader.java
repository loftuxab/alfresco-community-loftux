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

import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;dependencies&gt; elements.
 * 
 * @author Neil McErlean.
 */
class DependenciesElementReader implements ConfigElementReader
{
    public static final String ELEMENT_DEPENDENCIES = "dependencies";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element dependenciesElem)
    {
        DependenciesConfigElement result = null;
        if (dependenciesElem == null)
        {
            return null;
        }

        String name = dependenciesElem.getName();
        if (!name.equals(ELEMENT_DEPENDENCIES))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_DEPENDENCIES
                    + " elements, the element passed was '" + name + "'");
        }

        result = new DependenciesConfigElement();

        List<String> cssDependencies = getSrcDependencies(dependenciesElem, "./css");
        List<String> jsDependencies = getSrcDependencies(dependenciesElem, "./js");

        result.addCssDependencies(cssDependencies);
        result.addJsDependencies(jsDependencies);

        return result;
    }

    /**
     * This method takes the specified xml node, finds children matching the specified
     * xpath expression and returns a List<String> containing the values of the "src"
     * attribute on each of those child nodes.
     * 
     * @param typeNode
     * @param xpathExpression
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> getSrcDependencies(Element typeNode, final String xpathExpression)
    {
        List<String> result = new ArrayList<String>();
        
        for (Object cssObj : typeNode.selectNodes(xpathExpression))
        {
            Element cssElem = (Element)cssObj;
            List<Attribute> cssAttributes = cssElem.selectNodes("./@*");
            for (Attribute nextAttr : cssAttributes)
            {
                String nextAttrName = nextAttr.getName();
                if (nextAttrName.equals("src"))
                {
                    String nextAttrValue = nextAttr.getValue();
                    result.add(nextAttrValue);
                }
                // Ignore attributes not called "src".
            }
        }
        
        return result;
    }
}
