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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a single control configuration item. These items can exist
 * within a group of &lt;default-controls&gt; or underneath a &lt;field&gt;.
 * 
 * @author Neil McErlean.
 */
public class Control
{
    private String template;
    private final Map<String, ControlParam> controlParams = new LinkedHashMap<String, ControlParam>();
    private final List<String> cssDependencies = new ArrayList<String>();
    private final List<String> jsDependencies = new ArrayList<String>();

    /**
     * Constructs a Control object with a null template.
     */
    public Control()
    {
        this(null);
    }
    
    /**
     * Constructs a Control object with the specified template.
     * 
     * @param template.
     */
    public Control(String template)
    {
        this.template = template;
    }

    void addControlParam(String cpName, String cpValue)
    {
        ControlParam cp = new ControlParam(cpName, cpValue);
        this.addControlParam(cp);
    }

    void addControlParam(ControlParam param)
    {
        controlParams.put(param.getName(), param);
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
    public List<ControlParam> getParams()
    {
        List<ControlParam> result = new ArrayList<ControlParam>(controlParams.size());
        for (Map.Entry<String, ControlParam> entry : controlParams.entrySet())
        {
            result.add(entry.getValue());
        }
        return Collections.unmodifiableList(result);
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
        String combinedTemplate = otherControl.template == null ? this.template : otherControl.template;
        Control result = new Control(combinedTemplate);
        
        for (Map.Entry<String, ControlParam> thisEntry : this.controlParams.entrySet())
        {
            ControlParam thisCP = thisEntry.getValue();
            result.controlParams.put(thisCP.getName(), thisCP);
        }
        for (Map.Entry<String, ControlParam> otherEntry : otherControl.controlParams.entrySet())
        {
            ControlParam otherCP = otherEntry.getValue();
            // This call to 'put' will replace any cp entries with the same name
            // that are already in the map.
            result.controlParams.put(otherCP.getName(), otherCP);
        }
        
        if (otherControl.cssDependencies.isEmpty() == false)
        {
            result.addCssDependencies(otherControl.cssDependencies);
        }
        else
        {
            result.addCssDependencies(this.cssDependencies);
        }
        
        if (otherControl.jsDependencies.isEmpty() == false)
        {
            result.addJsDependencies(otherControl.jsDependencies);
        }
        else
        {
            result.addJsDependencies(this.jsDependencies);
        }
        
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(template);
        result.append(controlParams);
        return result.toString();
    }
}