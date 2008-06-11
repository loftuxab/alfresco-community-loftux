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
import org.dom4j.Document;

/**
 * TemplateType model object
 * 
 * @author muzquiano
 */
public class TemplateType extends AbstractModelObject
{
    public static String TYPE_ID = "template-type";
    public static String PROP_URI = "uri";
    public static String PROP_RENDERER = "renderer";
    public static String PROP_RENDERER_TYPE = "renderer-type";    
    
    /**
     * Instantiates a new template type for a given XML document
     * 
     * @param document the document
     */
    public TemplateType(ModelObjectKey key, Document document)
    {
        super(key, document);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "TemplateType Instance: " + getId() + ", " + toXML();
    }

    /**
     * Gets the uRI.
     * 
     * @return the uRI
     */
    public String getURI()
    {
        return getProperty(PROP_URI);
    }

    /**
     * Sets the uRI.
     * 
     * @param uri the new uRI
     */
    public void setURI(String uri)
    {
        setProperty(PROP_URI, uri);
    }

    /**
     * Sets the renderer.
     * 
     * @param renderer the new renderer
     */
    public void setRenderer(String renderer)
    {
        setProperty(PROP_RENDERER, renderer);
    }

    /**
     * Gets the renderer.
     * 
     * @return the renderer
     */
    public String getRenderer()
    {
        return getProperty(PROP_RENDERER);
    }

    /**
     * Sets the renderer type.
     * 
     * @param rendererType the new renderer type
     */
    public void setRendererType(String rendererType)
    {
        setProperty(PROP_RENDERER_TYPE, rendererType);
    }

    /**
     * Gets the renderer type.
     * 
     * @return the renderer type
     */
    public String getRendererType()
    {
        return getProperty(PROP_RENDERER_TYPE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }
    
}
