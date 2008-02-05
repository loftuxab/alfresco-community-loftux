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
package org.alfresco.config.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigElement;

/**
 * Adapter class for implementing ConfigElement's. Extend this class and 
 * provide the implementation specific behaviour.
 * 
 * @author gavinc
 */
public abstract class ConfigElementAdapter implements ConfigElement
{
    protected String name;
    protected String value;
    protected Map<String, String> attributes;
    protected List<ConfigElement> children;

    /**
     * Default constructor
     * 
     * @param name Name of the config element
     */
    public ConfigElementAdapter(String name)
    {
        this.name = name;
        this.attributes = new HashMap<String, String>();
        this.children = new ArrayList<ConfigElement>();
    }

    public String getAttribute(String name)
    {
        return attributes.get(name);
    }

    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(this.attributes);
    }
    
    public int getAttributeCount()
    {
       return this.attributes.size();
    }

    public List<ConfigElement> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }

    public List<ConfigElement> getChildren(String name)
    {
       List<ConfigElement> result = new LinkedList<ConfigElement>();
       
       if (hasChildren())
       {
          for (ConfigElement ce : this.children)
          {
             if (ce.getName().equals(name))
             {
                result.add(ce);
             }
          }
       }
       
       return Collections.unmodifiableList(result);
    }
    
    public int getChildCount()
    {
       return this.children.size();
    }

    public ConfigElement getChild(String name)
    {
       ConfigElement child = null;
       
       if (hasChildren())
       {
          for (ConfigElement ce : this.children)
          {
             if (ce.getName().equals(name))
             {
                child = ce;
                break;
             }
          }
       }
       
       return child;
    }
    
    public String getChildValue(String name)
    {
       ConfigElement ce = getChild(name);
       return ce != null ? ce.getValue() : null;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<ConfigElement>> getChildrenMap()
    {
       Map<String, List<ConfigElement>> map = new LinkedHashMap<String, List<ConfigElement>>();
       
       if (hasChildren())
       {
          for (ConfigElement ce : this.children)
          {
             String name = ce.getName();
             if (map.containsKey(name))
             {
                List list = map.get(name);
                list.add(ce);
             }
             else
             {
                List<ConfigElement> list = new ArrayList<ConfigElement>();
                list.add(ce);
                map.put(name, list);
             }
          }
       }
       
       return map;
    }
    
    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    /**
     * Sets the value of this config element
     * 
     * @param value The value to set.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    public boolean hasAttribute(String name)
    {
        return attributes.containsKey(name);
    }

    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append(" (name=").append(this.name).append(")");
        return buffer.toString();
    }
    
    public abstract ConfigElement combine(ConfigElement configElement);
}
