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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.config;

import java.util.List;

/**
 * Definition of a config section.
 * 
 * @author gavinc
 */
public interface ConfigSection
{
    /**
     * Returns the name of an evaluator to use to determine whether the config
     * section this object represents is applicable to the current lookup
     * operation
     * 
     * @return Name of an evaluator held by the config service
     */
    public String getEvaluator();

    /**
     * Returns the condition to use to determine whether the config section this
     * object represents is applicable. The condition is passed to the evaluator
     * 
     * @return Condition to test
     */
    public String getCondition();

    /**
     * Returns the config elements that make up this config section
     * 
     * @return List of config elements
     */
    public List<ConfigElement> getConfigElements();

    /**
     * Determines whether this config section is global
     * 
     * @return true if it is global, false otherwise
     */
    public boolean isGlobal();
    
    /**
     * Determines whether the config elements found inside this section
     * should replace any previously found config elements with the same
     * name
     * 
     * @return true if config elements should replace existing ones, false otherwise
     */
    public boolean isReplace();
}
