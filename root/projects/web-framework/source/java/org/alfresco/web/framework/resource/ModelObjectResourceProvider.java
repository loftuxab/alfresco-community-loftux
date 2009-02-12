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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.web.framework.ModelObject;
import org.dom4j.Element;

/**
 * An implementation of ResourceProvider which enables model objects
 * to manage the configuration of resources.
 * 
 * Resources are stored as part of the model object configuration.
 * 
 * Examples:
 * 
 * <resource id="abc1" type="space" endpoint="alfresco">workspace...</resource>
 * <resource id="abc2" type="space" endpoint="alfresco">/Company Home/Data Dictionary/...</resource>
 * <resource id="abc3" type="uri">/a/b/c.gif</resource>
 * <resource id="abc4" type="uri" endpoint="alfresco">/a/b/c.gif</resource>
 * <resource id="abc5" type="site" site="mysite" endpoint="alfresco">/document_library/abc.doc</resource>
 * <resource id="abc6" type="webapp">/a/b/c.gif</resource>
 * 
 * @author muzquiano
 */
public class ModelObjectResourceProvider implements ResourceProvider
{
    protected ModelObject object;
    protected Map<String, Resource> resources;

    /**
     * Instantiates a new model object resource provider.
     * 
     * @param object the object
     */
    public ModelObjectResourceProvider(ModelObject object)
    {
        this.object = object;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#getResource(java.lang.String)
     */
    public Resource getResource(String id)
    {
        return getResourcesMap().get(id);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#getResources()
     */
    public Resource[] getResources()
    {
        Map<String, Resource> map = getResourcesMap();
        return map.values().toArray(new Resource[map.size()]);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String)
     */
    public Resource addResource(String id)
    {
        return addResource(id, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String, java.lang.String)
     */
    public synchronized Resource addResource(String id, String type)
    {
        Resource resource = getResourcesMap().get(id);
        if (resource == null)
        {
            Element rootElement = this.getResourcesElement(this.object);

            Element resourceElement = rootElement.addElement("resource");
            resourceElement.addAttribute(Resource.ATTR_ID, id);
            resourceElement.addAttribute("type", type);

            // update our cache map
            resource = loadResource(this.object, id);
            this.resources.put(id, resource);
        }

        return resource;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#updateResource(java.lang.String, org.alfresco.web.framework.resource.Resource)
     */
    public void updateResource(String id, Resource resource)
    {
        Element element = this.getResourceElement(this.object, id);
        if (element != null)
        {
            String[] names = resource.getAttributeNames();
            for (int i = 0; i < names.length; i++)
            {
                String value = resource.getAttribute(names[i]);
                element.addAttribute(names[i], value);

                // update our cache map
                this.resources.put(id, resource);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#removeResource(java.lang.String)
     */
    public void removeResource(String id)
    {
        Element element = this.getResourceElement(this.object, id);
        if (element != null)
        {
            Element rootElement = this.getResourcesElement(this.object);
            rootElement.remove(element);

            // update our cache map
            this.resources.remove(id);

        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#getResourcesMap()
     */
    public synchronized Map<String, Resource> getResourcesMap()
    {
        if (this.resources == null)
        {
            this.resources = new HashMap<String, Resource>(8, 1.0f);

            Element rootElement = this.getResourcesElement(this.object);
            List elements = rootElement.elements("resource");
            for (int i = 0; i < elements.size(); i++)
            {
                Element el = (Element) elements.get(i);

                String id = el.attributeValue("id");

                Resource resource = loadResource(this.object, id);
                this.resources.put(id, resource);
            }
        }

        return this.resources;
    }

    /**
     * Gets the resources element.
     * 
     * @param object the object
     * 
     * @return the resources element
     */
    protected static Element getResourcesElement(ModelObject object)
    {
        Element result = null;

        List elements = object.getDocument().getRootElement().elements(
                "resources");
        if (elements.size() > 0)
        {
            result = (Element) elements.get(0);
        }
        else
        {
            result = object.getDocument().getRootElement().addElement(
                    "resources");
        }

        return result;
    }

    /**
     * Gets the resource element.
     * 
     * @param object the object
     * @param id the id
     * 
     * @return the resource element
     */
    protected static Element getResourceElement(ModelObject object, String id)
    {
        Element result = null;

        Element rootElement = getResourcesElement(object);

        List elements = rootElement.elements("resource");
        for (int i = 0; i < elements.size(); i++)
        {
            Element el = (Element) elements.get(i);
            String _id = el.attributeValue("id");
            if (_id.equals(id))
            {
                result = el;
                break;
            }
        }
        return result;
    }

    /**
     * Load resource.
     * 
     * @param object the object
     * @param id the id
     * 
     * @return the resource
     */
    protected static Resource loadResource(ModelObject object, String id)
    {
        ResourceStore store = new ModelObjectResourceStore(object);

        // get the element
        Element el = getResourceElement(object, id);

        return new ResourceImpl(store, id);
    }
}