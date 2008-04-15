/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.config;

import java.util.Iterator;
import java.util.List;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.GenericConfigElement;

/**
 * An extension of the GenericConfigElement which allows for specific
 * child XML elements to be combined and merged.
 * 
 * The configuration file for the Web Framework specifies in several
 * places one or more implementations which are held inside of a
 * container element.  These container elements have names like
 * "libraries", "definitions" and so forth.
 * 
 * Thus, this class allows for these container elements to be merged
 * and makes it possible for the framework to support loading of
 * configuration from multiple web-site-config.xml and 
 * web-site-config-*.xml files.
 * 
 * @author muzquiano
 */
public class WebSiteConfigElement extends GenericConfigElement
{
    public WebSiteConfigElement(String name)
    {
        super(name);
    }

    public ConfigElement combine(ConfigElement configElement)
    {
        WebSiteConfigElement combined = new WebSiteConfigElement(this.name);
        combined.setValue(configElement.getValue());

        // add the existing attributes to the new instance
        if (this.attributes != null)
        {
            Iterator<String> attrs = this.getAttributes().keySet().iterator();
            while (attrs.hasNext())
            {
                String attrName = attrs.next();
                String attrValue = configElement.getAttribute(attrName);
                combined.addAttribute(attrName, attrValue);
            }
        }

        // add/combine the attributes from the given instance
        if (configElement.getAttributes() != null)
        {
            Iterator<String> attrs = configElement.getAttributes().keySet().iterator();
            while (attrs.hasNext())
            {
                String attrName = attrs.next();
                String attrValue = configElement.getAttribute(attrName);
                combined.addAttribute(attrName, attrValue);
            }
        }

        // add the existing children to the new instance
        List<ConfigElement> kids = this.getChildren();
        if (kids != null)
        {
            for (int x = 0; x < kids.size(); x++)
            {
                ConfigElement ce = kids.get(x);
                combined.addChild(ce);
            }
        }

        // add the children from the given instance
        kids = configElement.getChildren();
        if (kids != null)
        {
            for (int x = 0; x < kids.size(); x++)
            {
                ConfigElement ce = kids.get(x);
                
                // "container" node
                if(isMergeChildElement(ce.getName()))
                {
                	// find the existing child on the combined object
                	int combinedIndex = -1;
                	ConfigElement combinedChild = null;
                	for(int c = 0; c < combined.children.size(); c++)
                	{
                		ConfigElement _child = (ConfigElement) combined.children.get(c);
                		if(_child.getName().equals(ce.getName()))
                		{
                			combinedChild = _child;
                			combinedIndex = c;
                		}
                	}
                	
                	// get all of the "libraries" children and place them into the combinedChild children
                	ConfigElement newCombinedChild = combinedChild.combine(ce);
                	
                	// replace the combined child with the new one
                	combined.children.remove(combinedIndex);
                	combined.children.add(combinedIndex, newCombinedChild);
                }
                else
                {
                	combined.addChild(ce);
                }
            }
        }

        return combined;
    }

    protected static String[] mergeChildNames = new String[] { "definitions", "elements", "types", "libraries", "factories", "connectors", "authenticators" };

    protected boolean isMergeChildElement(String name)
    {
        for (int i = 0; i < mergeChildNames.length; i++)
        {
            if (name.equals(mergeChildNames[i]))
                return true;
        }
        return false;
    }

    /**
     * Adds the attribute with the given name and value
     * 
     * @param name
     *            Name of the attribute
     * @param value
     *            Value of the attribute
     */
    public void addAttribute(String name, String value)
    {
        this.attributes.put(name, value);
    }

    /**
     * Adds the given config element as a child of this element
     * 
     * @param configElement
     *            The child config element
     */
    public void addChild(ConfigElement configElement)
    {
        this.children.add(configElement);
    }
}
