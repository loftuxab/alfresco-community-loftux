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
import org.alfresco.web.framework.ModelObjectKey;
import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;

/**
 * ContentAssociation model object
 * 
 * @author muzquiano
 */
public class ContentAssociation extends AbstractModelObject
{
    public static String TYPE_ID = "content-association";
    public static String PROP_SOURCE_ID = "source-id";
    public static String PROP_DEST_ID = "dest-id";
    public static String PROP_ASSOC_TYPE = "assoc-type";
    public static String PROP_FORMAT_ID = "format-id";
    
    /**
     * Instantiates a new content association for the given XML document
     * 
     * @param document the document
     */
    public ContentAssociation(ModelObjectKey key, Document document)
    {
        super(key, document);
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

    /**
     * Gets the dest id.
     * 
     * @return the dest id
     */
    public String getDestId()
    {
        return getProperty(PROP_DEST_ID);
    }

    /**
     * Sets the dest id.
     * 
     * @param destId the new dest id
     */
    public void setDestId(String destId)
    {
        setProperty(PROP_DEST_ID, destId);
    }

    /**
     * Gets the association type.
     * 
     * @return the association type
     */
    public String getAssociationType()
    {
        return getProperty(PROP_ASSOC_TYPE);
    }

    /**
     * Sets the association type.
     * 
     * @param associationType the new association type
     */
    public void setAssociationType(String associationType)
    {
        setProperty(PROP_ASSOC_TYPE, associationType);
    }

    /**
     * Gets the format id.
     * 
     * @return the format id
     */
    public String getFormatId()
    {
        return getProperty(PROP_FORMAT_ID);
    }

    /**
     * Sets the format id.
     * 
     * @param formatId the new format id
     */
    public void setFormatId(String formatId)
    {
        setProperty(PROP_FORMAT_ID, formatId);
    }

    // Helpers

    /**
     * Gets the page.
     * 
     * @param context the context
     * 
     * @return the page
     */
    public Page getPage(RequestContext context)
    {
        return context.getModel().getPage(getDestId());
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }
    
}
