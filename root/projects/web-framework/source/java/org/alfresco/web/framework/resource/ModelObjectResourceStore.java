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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;

import org.alfresco.tools.XMLUtil;
import org.alfresco.web.framework.ModelObject;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Interface which provides an adaptor pattern for resources that seek
 * to interact with the storage mechanism of model objects.
 * 
 * @author muzquiano
 */
public class ModelObjectResourceStore implements ResourceStore
{

    /** The object. */
    protected ModelObject object;

    /**
     * Instantiates a new model object resource store.
     * 
     * @param object the object
     */
    public ModelObjectResourceStore(ModelObject object)
    {
        this.object = object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceStore#getAttributeNames(java.lang.String)
     */
    public String[] getAttributeNames(String id)
    {
        Element el = ModelObjectResourceProvider.getResourceElement(
                this.object, id);

        String[] names = new String[el.attributeCount()];

        for (int i = 0; i < el.attributeCount(); i++)
        {
            Attribute attribute = (Attribute) el.attribute(i);
            names[i] = attribute.getName();
        }

        return names;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceStore#getAttribute(java.lang.String,
     *      java.lang.String)
     */
    public String getAttribute(String id, String name)
    {
        String value = null;

        Element el = ModelObjectResourceProvider.getResourceElement(
                this.object, id);
        if (el != null)
        {
            value = el.attributeValue(name);
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceStore#setAttribute(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void setAttribute(String id, String name, String value)
    {
        Element el = ModelObjectResourceProvider.getResourceElement(
                this.object, id);
        if (el != null)
        {
            el.addAttribute(name, value);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceStore#removeAttribute(java.lang.String,
     *      java.lang.String)
     */
    public void removeAttribute(String id, String name)
    {
        Element el = ModelObjectResourceProvider.getResourceElement(
                this.object, id);
        if (el != null)
        {
            Attribute attribute = el.attribute(name);
            if (attribute != null)
            {
                el.remove(attribute);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceStore#getValue(java.lang.String)
     */
    public String getValue(String id)
    {
        String value = null;

        Element el = ModelObjectResourceProvider.getResourceElement(
                this.object, id);
        if (el != null)
        {
            value = XMLUtil.getValue(el);
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceStore#setValue(java.lang.String,
     *      java.lang.String)
     */
    public void setValue(String id, String value)
    {
        Element el = ModelObjectResourceProvider.getResourceElement(
                this.object, id);
        if (el != null)
        {
            XMLUtil.setValue(el, value);
        }
    }
}