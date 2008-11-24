/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.studio.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation class for a Web Studio applet.
 * 
 * @author muzquiano
 */
public class BrowserStateBean implements Serializable
{
    private static Log logger = LogFactory.getLog(BrowserStateBean.class);

    private String id = null;

    private String title = null;
    private String description = null;

    private String bootstrapLocation = null;
    private String bootstrapClassname = null;

    private Map<String, String> jsFiles = null;
    private Map<String, String> cssFiles = null;
    private Map<String, String> domFiles = null;

    private Map<String, String> properties = null;

    /**
     * Instantiates a new applet state bean.
     * 
     * @param id the id
     */
    public BrowserStateBean(String id)
    {
        this.id = id;

        this.jsFiles = new HashMap<String, String>(16, 1.0f);
        this.cssFiles = new HashMap<String, String>(16, 1.0f);
        this.domFiles = new HashMap<String, String>(16, 1.0f);

        this.properties = new HashMap<String, String>(16, 1.0f);

        // enable by default
        put("enabled", "true");
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Sets the title.
     * 
     * @param title the new title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Gets the js files.
     * 
     * @return the js files
     */
    public String[] getJsFiles()
    {
        return jsFiles.values().toArray(new String[jsFiles.size()]);
    }

    /**
     * Adds the js file.
     * 
     * @param file the file
     */
    public void addJsFile(String file)
    {
        jsFiles.put(file, file);
    }

    /**
     * Gets the css files.
     * 
     * @return the css files
     */
    public String[] getCssFiles()
    {
        return cssFiles.values().toArray(new String[cssFiles.size()]);
    }

    /**
     * Adds the css file.
     * 
     * @param file the file
     */
    public void addCssFile(String file)
    {
        cssFiles.put(file, file);
    }

    /**
     * Gets the dom files.
     * 
     * @return the dom files
     */
    public String[] getDomFiles()
    {
        return domFiles.values().toArray(new String[domFiles.size()]);
    }

    /**
     * Adds the dom file.
     * 
     * @param file the file
     */
    public void addDomFile(String file)
    {
        domFiles.put(file, file);
    }

    /**
     * Gets the bootstrap classname.
     * 
     * @return the bootstrap classname
     */
    public String getBootstrapClassname()
    {
        return this.bootstrapClassname;
    }

    /**
     * Sets the bootstrap classname.
     * 
     * @param bootstrapClassname the new bootstrap classname
     */
    public void setBootstrapClassname(String bootstrapClassname)
    {
        this.bootstrapClassname = bootstrapClassname;
    }

    /**
     * Gets the bootstrap location.
     * 
     * @return the bootstrap location
     */
    public String getBootstrapLocation()
    {
        return this.bootstrapLocation;
    }

    /**
     * Sets the bootstrap location.
     * 
     * @param bootstrapLocation the new bootstrap location
     */
    public void setBootstrapLocation(String bootstrapLocation)
    {
        this.bootstrapLocation = bootstrapLocation;
    }

    /**
     * Stores a property onto this element
     * 
     * @param key the key
     * @param value the value
     */
    public void put(String key, String value)
    {
        this.properties.put(key, value);
    }

    /**
     * Retrieves a property from this element
     * 
     * @param key the key
     */
    public String get(String key)
    {
        return this.properties.get(key);
    }

    /**
     * Returns the map of all properties
     * 
     * @return properties
     */
    public Map<String, String> getProperties()
    {
        return this.properties;
    }

    /**
     * Removes a property from this element
     * 
     * @param key
     */
    public void remove(String key)
    {
        this.properties.remove(key);
    }

    /**
     * Removes all properties
     */
    public void removeProperties()
    {
        this.properties = new HashMap<String, String>(16, 1.0f);
    }

    /**
     * Enables this browser component
     */
    public void enable()
    {
        put("enabled", "true");
    }

    /**
     * Disables this browser component
     */
    public void disable()
    {
        remove("enabled");
    }

    /**
     * Returns whether this browser component is enabled
     * 
     * @return enabled or not
     */
    public boolean isEnabled()
    {
        return "true".equals(get("enabled"));
    }

    /**
     * Clears all file dependencies
     */
    public void clearDependencies()
    {
        this.jsFiles = new HashMap<String, String>(16, 1.0f);
        this.cssFiles = new HashMap<String, String>(16, 1.0f);
        this.domFiles = new HashMap<String, String>(16, 1.0f);
    }
}
