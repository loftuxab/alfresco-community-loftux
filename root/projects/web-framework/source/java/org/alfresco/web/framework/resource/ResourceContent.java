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
package org.alfresco.web.framework.resource;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface that describes a content object which is located on the
 * repository (or services) tier.
 * 
 * @author muzquiano
 */
public interface ResourceContent
{
    /**
     * A link back to the resource of which this content is a part
     * 
     * @return
     */
    public Resource getResource();

    /**
     * Gets the id of the object
     * 
     * @return the id
     */
    public String getId();

    /**
     * Gets the type id of hte object
     * 
     * @return
     */
    public String getTypeId();

    /**
     * Returns the timestamp when this content convenience instance
     * was created
     * 
     * @return
     */
    public long getTimestamp();

    /**
     * Gets a property.
     * 
     * @param propertyName the property name
     * 
     * @return the property
     */
    public Object getProperty(String propertyName);

    /**
     * Gets all properties.
     * 
     * @return the properties
     */
    public Map<String, Serializable> getProperties();

    /**
     * Returns whether the content was successfully loaded
     * 
     * @return
     */
    public boolean isLoaded();

    /**
     * Sets the exception which occurred on the load (if any)
     * 
     * @param loaderException
     */
    public void setLoaderException(Throwable loaderException);

    /**
     * The loader exception, if any
     * 
     * @return
     */
    public Throwable getLoaderException();

    /**
     * Returns the JSON for the metadata
     * 
     * @return
     */
    public String getJSON();

}
