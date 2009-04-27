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
package org.alfresco.web.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a single control configuration item. These items can exist
 * within a group of &lt;default-controls&gt; or underneath a &lt;field&gt;.
 * 
 * @author Neil McErlean.
 */
public class Control
{
    //TODO Can I remove this field and simply use a key in an external map?
    private final String name;
    private String template;
    private final List<ControlParam> controlParams = new ArrayList<ControlParam>();
    private final List<String> cssDependencies = new ArrayList<String>();
    private final List<String> jsDependencies = new ArrayList<String>();

    /**
     * Constructs a Control object with a null name.
     */
    public Control()
    {
        this(null, null);
    }
    
    /**
     * Constructs a Control object with the specified name and template.
     * 
     * @param name the name of the type.
     * @param template the template associated with that name.
     */
    public Control(String name, String template)
    {
        this.name = name;
        this.template = template;
    }

    void addControlParam(ControlParam param)
    {
        controlParams.add(param);
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
    
    /**
     * This method returns the name of the type of this Control.
     * @return the name of the type.
     */
    public String getName()
    {
        return name;
    }

    /**
     * This method returns the template path of this Control.
     * @return the template path.
     */
    public String getTemplate()
    {
        return template;
    }
    
    void setTemplate(String newTemplate)
    {
        this.template = newTemplate;
    }

    /**
     * This method returns an unmodifiable List of <code>ControlParam</code>
     * objects that are associated with this Control.
     * @return an unmodifiable List of ControlParam references.
     */
    public List<ControlParam> getControlParams()
    {
        return Collections.unmodifiableList(controlParams);
    }
    
    /**
     * This method returns the css dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return
     */
    public String[] getCssDependencies()
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
     * This method returns the js dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return
     */
    public String[] getJsDependencies()
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
    
    public Control combine(Control otherControl)
    {
        // We should only combine controls that have the same name.
        
        //TODO Implement control

        String name = otherControl.name == null ? this.name : otherControl.name;
        String template = otherControl.template == null ? this.template : otherControl.template;
        
        Control result = new Control(name, template);
        
        return null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(name).append(":").append(template);
        result.append(controlParams);
        return result.toString();
    }
}