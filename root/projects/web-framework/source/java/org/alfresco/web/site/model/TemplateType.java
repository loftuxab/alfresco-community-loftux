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

import org.dom4j.Document;

/**
 * @author muzquiano
 */
public class TemplateType extends ModelObject
{
    public static String TYPE_NAME = "template-type";
    public static String PROP_URI = "uri";
    public static String PROP_RENDERER = "renderer";
    public static String PROP_RENDERER_TYPE = "renderer-type";    
    
    public TemplateType(Document document)
    {
        super(document);
    }

    @Override
    public String toString()
    {
        return "TemplateType Instance: " + getId() + ", " + toXML();
    }

    public String getURI()
    {
        return getProperty(PROP_URI);
    }

    public void setURI(String uri)
    {
        setProperty(PROP_URI, uri);
    }

    public void setRenderer(String renderer)
    {
        setProperty(PROP_RENDERER, renderer);
    }

    public String getRenderer()
    {
        return getProperty(PROP_RENDERER);
    }

    public void setRendererType(String rendererType)
    {
        setProperty(PROP_RENDERER_TYPE, rendererType);
    }

    public String getRendererType()
    {
        return getProperty(PROP_RENDERER_TYPE);
    }

    public String getTypeName() 
    {
        return TYPE_NAME;
    }
    
}
