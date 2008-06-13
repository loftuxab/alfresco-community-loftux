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
 * PageAssociation model object
 * 
 * @author muzquiano
 */
public class PageAssociation extends AbstractModelObject
{
    public static String TYPE_ID = "page-association";
    public static String CHILD_ASSOCIATION_TYPE_ID = "child";
    public static String PROP_SOURCE_ID = "source-id";
    public static String PROP_DEST_ID = "dest-id";
    public static String PROP_ASSOC_TYPE = "assoc-type";
    public static String PROP_ORDER_ID = "order-id";

    /**
     * Instantiates a new page association for a given XML document
     * 
     * @param document the document
     */
    public PageAssociation(ModelObjectKey key, Document document)
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
     * Gets the order id.
     * 
     * @return the order id
     */
    public String getOrderId()
    {
        return getProperty(PROP_ORDER_ID);
    }

    /**
     * Sets the order id.
     * 
     * @param orderId the new order id
     */
    public void setOrderId(String orderId)
    {
        setProperty(PROP_ORDER_ID, orderId);
    }

    // Helpers

    /**
     * Gets the source page.
     * 
     * @param context the context
     * 
     * @return the source page
     */
    public Page getSourcePage(RequestContext context)
    {
        return context.getModel().getPage(getSourceId());
    }

    /**
     * Gets the dest object.
     * 
     * @param context the context
     * 
     * @return the dest object
     */
    public Page getDestPage(RequestContext context)
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
