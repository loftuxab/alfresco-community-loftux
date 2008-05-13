/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.model;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;

/**
 * @author muzquiano
 */
public interface ModelObject extends Serializable
{
	/**
	 * Returns the id of the model object.  This is either explicitly
	 * declared by the object or it is assumed from the file name
	 * of the serialized XML.
	 * 
	 * @return The id
	 */
    public String getId();
    
    /**
     * Returns the title property of the model object.
     * 
     * @return The title
     */
    public String getTitle();
    
    /**
     * Sets the title property of the model object
     * 
     * @param The new title
     */
    public void setTitle(String value);
    
    /**
     * Returns the description property of the model object
     * 
     * @return The description
     */
    public String getDescription();
    
    /**
     * Sets the description property of the model object
     * 
     * @param The description
     */
    public void setDescription(String value);
    
    /**
     * Saves the model object to its storage location.  The storage
     * location is a Store implementation (either local or remote).
     * 
     * @param context
     */ 
    public void save(RequestContext context);
    
    /**
     * Reloads the object from its Store
     * 
     * @param context
     */
    public void reload(RequestContext context);
    
    /**
     * Removes the object from its Store
     * 
     * @param context
     */
    public void remove(RequestContext context);
    
    /**
     * Indicates whether the object is currently persisted (saved)
     * or not.  A new object will have this flag set to false prior
     * to a save and true once the save operation has completed.
     * 
     * @return Whether the object is currently saved
     */
    public boolean isSaved();

    /**
     * Serializes the object to XML.  By default, this uses a 
     * pretty XML renderer so that the resulting XML is
     * human readable.
     * 
     * @return The XML string
     */
    public String toXML();

    // general property accessors
    public boolean getBooleanProperty(String propertyName);
    public String getProperty(String propertyName);
    public void setProperty(String propertyName, String propertyValue);
    public void removeProperty(String propertyName);
    public Map<String, Object> getProperties();

    // model properties
    public String getModelProperty(String propertyName);
    public void setModelProperty(String propertyName, String propertyValue);
    public void removeModelProperty(String propertyName);
    public Map<String, Object> getModelProperties();
    
    // custom properties
    public String getCustomProperty(String propertyName);
    public void setCustomProperty(String propertyName, String propertyValue);
    public void removeCustomProperty(String propertyName);
    public Map<String, Object> getCustomProperties();
    
    // modification stamps
    public long getModificationTime();
    public void setModificationTime(long modificationTime);
    public void touch();

    // model object management
    public String getRelativePath();
    public String getFileName();
    public String getRelativeFilePath();
    public String getTypeName();
    
    // version
    public String getModelVersion();
    
    // allow xml retrieval via document
    public Document getDocument();    
}
