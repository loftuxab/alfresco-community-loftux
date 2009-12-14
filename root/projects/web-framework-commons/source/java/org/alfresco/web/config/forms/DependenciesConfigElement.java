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
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents &lt;dependencies&gt; values for the
 * client.
 * 
 * @author Neil McErlean.
 */
public class DependenciesConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = -8573715101320883067L;

    public static final String CONFIG_ELEMENT_ID = "dependencies";
    private final List<String> cssDependencies = new ArrayList<String>();
    private final List<String> jsDependencies = new ArrayList<String>();

    /**
     * This constructor creates an instance with the default name.
     */
    public DependenciesConfigElement()
    {
        super(CONFIG_ELEMENT_ID);
    }

    /**
     * This constructor creates an instance with the specified name.
     * 
     * @param name the name for the ConfigElement.
     */
    public DependenciesConfigElement(String name)
    {
        super(name);
    }
    
    /**
     * This method returns the css dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return
     */
    public String[] getCss()
    {
        if (this.cssDependencies.isEmpty())
        {
            return null;
        }
        else
        {
            return this.cssDependencies.toArray(new String[0]);
        }
    }
    
    /**
     * This method returns the JavaScript dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return
     */
    public String[] getJs()
    {
        if (this.jsDependencies.isEmpty())
        {
            return null;
        }
        else
        {
            return this.jsDependencies.toArray(new String[0]);
        }
    }

    /**
     * @see org.alfresco.config.ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the default-controls config via the generic interfaces is not supported");
    }

    /**
     * @see org.alfresco.config.ConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement configElement)
    {
        if (configElement == null)
        {
            return this;
        }

        DependenciesConfigElement otherDepsElement = (DependenciesConfigElement) configElement;

        DependenciesConfigElement result = new DependenciesConfigElement();

        if (otherDepsElement.cssDependencies.isEmpty() == false)
        {
            result.addCssDependencies(otherDepsElement.cssDependencies);
        } else
        {
            result.addCssDependencies(this.cssDependencies);
        }

        if (otherDepsElement.jsDependencies.isEmpty() == false)
        {
            result.addJsDependencies(otherDepsElement.jsDependencies);
        } else
        {
            result.addJsDependencies(this.jsDependencies);
        }

        return result;
    }

    void addCssDependencies(List<String> cssDeps)
    {
        if (cssDeps == null)
        {
            return;
        }
        this.cssDependencies.addAll(cssDeps);
    }

    void addJsDependencies(List<String> jsDeps)
    {
        if (jsDeps == null)
        {
            return;
        }
        this.jsDependencies.addAll(jsDeps);
    }
}
