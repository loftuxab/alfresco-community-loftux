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
    // common properties
    public String getId();
    public String getTitle();
    public void setTitle(String value);
    public String getDescription();
    public void setDescription(String value);
    
    // persistence methods
    public void save(RequestContext context);
    public void reload(RequestContext context);
    public void remove(RequestContext context);
    public boolean isSaved();

    // xml methods
    public String toXML();

    // generic properties
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
    

    
    
    // TODO: legacy methods
    // TODO: remove these methods
    public String getName();
    public void setName(String value);
    public Map getSettings();
    public String getSetting(String settingName);
    public void setSetting(String settingName, String settingValue);
    public void removeSetting(String settingName);
}
