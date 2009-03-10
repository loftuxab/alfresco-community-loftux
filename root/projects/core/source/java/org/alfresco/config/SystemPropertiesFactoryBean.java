/*
 * Copyright (C) 2005-20087 Alfresco Software Limited.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Constants;

/**
 * Like the parent <code>PropertiesFactoryBean</code>, but pulls in any defined VM properties
 * that.<br/>
 * <ul>
 *   <li><b>SYSTEM_PROPERTIES_MODE_NEVER:             </b>Don't use system properties at all.</li>
 *   <li><b>SYSTEM_PROPERTIES_MODE_FALLBACK:          </b>Fallback to a system property only for undefined properties.</li>
 *   <li><b>SYSTEM_PROPERTIES_MODE_OVERRIDE: (DEFAULT)</b>Use a system property if it is available.</li>
 * </ul>
 * The list of VM system properties that can be used must be explicitly set.  Any system properties not
 * in the list will be ignored.
 * 
 * @author Derek Hulley
 */
public class SystemPropertiesFactoryBean extends PropertiesFactoryBean
{
    private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);

    private int systemPropertiesMode = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE;
    private List<String> systemProperties = new ArrayList<String>(0);
    
    /**
     * Set the system property mode by the name of the corresponding constant,
     * e.g. "SYSTEM_PROPERTIES_MODE_OVERRIDE".
     * @param constantName name of the constant
     * @throws java.lang.IllegalArgumentException if an invalid constant was specified
     * @see #setSystemPropertiesMode
     */
    public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException
    {
        this.systemPropertiesMode = constants.asNumber(constantName).intValue();
    }

    /**
     * Set the names of the properties that can be considered for overriding.
     * 
     * @param systemProperties      a list of properties that can be fetched from the system properties
     */
    public void setSystemProperties(List<String> systemProperties)
    {
        this.systemProperties = systemProperties;
    }

    @Override
    protected Properties mergeProperties() throws IOException
    {
        // First do the default merge
        Properties props = super.mergeProperties();
        for (String systemProperty : systemProperties)
        {
            if (systemPropertiesMode == PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER)
            {
                // Break out immediately
                break;
            }
            if (systemPropertiesMode == PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK
                    && props.containsKey(systemProperty))
            {
                // It's already there
                continue;
            }
            // Get the system value and assign if present
            String systemPropertyValue = System.getProperty(systemProperty);
            if (systemPropertyValue != null)
            {
                props.put(systemProperty, systemPropertyValue);
            }
        }
        return props;
    }

}
