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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.config.element;

import java.util.Iterator;
import java.util.List;

import org.alfresco.config.ConfigElement;

/**
 * Implementation of a generic configuration element. This class can handle the
 * representation of any config element in a generic manner.
 * 
 * @author gavinc
 */
public class GenericConfigElement extends ConfigElementAdapter
{
    /**
     * Default constructor
     * 
     * @param name Name of the config element
     */
    public GenericConfigElement(String name) 
    {
        super(name);
    }
    
    /**
     * @see org.alfresco.web.config.ConfigElement#combine(org.alfresco.web.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement configElement)
    {
        GenericConfigElement combined = new GenericConfigElement(this.name);
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
                combined.addChild(ce);
            }
        }

        return combined;
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
