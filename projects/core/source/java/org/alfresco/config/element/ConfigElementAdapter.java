/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.config.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    /**
     * @see org.alfresco.web.config.ConfigElement#getAttribute(java.lang.String)
     */
    public String getAttribute(String name)
    {
        return attributes.get(name);
    }

    /**
     * @see org.alfresco.web.config.ConfigElement#getAttributes()
     */
    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(this.attributes);
    }
    
    /**
     * @see org.alfresco.config.ConfigElement#getAttributeCount()
     */
    public int getAttributeCount()
    {
       return this.attributes.size();
    }

    /**
     * @see org.alfresco.web.config.ConfigElement#getChildren()
     */
    public List<ConfigElement> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }
    
    /**
     * @see org.alfresco.config.ConfigElement#getChildCount()
     */
    public int getChildCount()
    {
       return this.children.size();
    }

    /**
     * @see org.alfresco.config.ConfigElement#getChild(java.lang.String)
     */
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
    
    /**
     * @see org.alfresco.web.config.ConfigElement#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.alfresco.web.config.ConfigElement#getValue()
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Sets the value of this config element
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * @see org.alfresco.web.config.ConfigElement#hasAttribute(java.lang.String)
     */
    public boolean hasAttribute(String name)
    {
        return attributes.containsKey(name);
    }

    /**
     * @see org.alfresco.web.config.ConfigElement#hasChildren()
     */
    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append(" (name=").append(this.name).append(")");
        return buffer.toString();
    }
    
    /**
     * @see org.alfresco.web.config.ConfigElement#combine(org.alfresco.web.config.ConfigElement)
     */
    public abstract ConfigElement combine(ConfigElement configElement);
}
