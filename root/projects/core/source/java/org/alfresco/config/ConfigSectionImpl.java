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
package org.alfresco.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of a config section
 * 
 * @author gavinc
 */
public class ConfigSectionImpl implements ConfigSection
{
    private String evaluator;
    private String condition;
    private boolean replace = false;
    private List<ConfigElement> configElements;

    public ConfigSectionImpl(String evaluator, String condition, boolean replace)
    {
        this.evaluator = evaluator;
        this.condition = condition;
        this.replace = replace;
        this.configElements = new ArrayList<ConfigElement>();
        
        // don't allow empty strings
        if (this.evaluator != null && this.evaluator.length() == 0)
        {
           throw new ConfigException("The 'evaluator' attribute must have a value if it is present");
        }
        
        if (this.condition != null && this.condition.length() == 0)
        {
           throw new ConfigException("The 'condition' attribute must have a value if it is present");
        }
    }

    /**
     * @see org.alfresco.config.ConfigSection#getEvaluator()
     */
    public String getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * @see org.alfresco.config.ConfigSection#getCondition()
     */
    public String getCondition()
    {
        return this.condition;
    }

    /**
     * @see org.alfresco.config.ConfigSection#getConfigElements()
     */
    public List<ConfigElement> getConfigElements()
    {
        return this.configElements;
    }

    /**
     * Adds a config element to the results for the lookup
     * 
     * @param configElement
     */
    public void addConfigElement(ConfigElement configElement)
    {
        this.configElements.add(configElement);
    }

    /**
     * @see org.alfresco.config.ConfigSection#isGlobal()
     */
    public boolean isGlobal()
    {
        boolean global = false;

        if (this.evaluator == null && this.condition == null)
        {
            global = true;
        }

        return global;
    }
    
    /**
     * @see org.alfresco.config.ConfigSection#isReplace()
     */
    public boolean isReplace()
    {
       return this.replace;
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append(" (evaluator=").append(this.evaluator);
        buffer.append(" condition=").append(this.condition);
        buffer.append(" replace=").append(this.replace).append(")");
        return buffer.toString();
    }
}
