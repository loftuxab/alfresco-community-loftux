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
package org.alfresco.web.framework.model;

import org.alfresco.web.framework.AbstractModelObject;
import org.alfresco.web.framework.ModelPersisterInfo;
import org.dom4j.Document;

/**
 * Configuration model object
 * 
 * @author muzquiano
 */
public class Configuration extends AbstractModelObject
{
    public static String TYPE_ID = "configuration";
    public static String PROP_SOURCE_ID = "source-id";
    public static String VALUE_SOURCE_ID_SITE = "site";
    
    /**
     * Instantiates a new configuration for the given xml document.
     * 
     * @param document the document
     */
    public Configuration(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
    }

    /**
     * Gets the source id.
     * 
     * @return the source id
     */
    public String getSourceId()
    {
        return getProperty(PROP_SOURCE_ID);
    }

    /**
     * Sets the source id.
     * 
     * @param sourceId the new source id
     */
    public void setSourceId(String sourceId)
    {
        setProperty(PROP_SOURCE_ID, sourceId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }
    
}
