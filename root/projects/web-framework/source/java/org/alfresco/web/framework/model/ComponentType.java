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

import java.util.Map;

import org.alfresco.web.framework.ModelPersisterInfo;
import org.alfresco.web.framework.render.AbstractRenderableModelObject;
import org.alfresco.web.framework.resource.ModelObjectResourceProvider;
import org.alfresco.web.framework.resource.Resource;
import org.alfresco.web.framework.resource.ResourceProvider;
import org.dom4j.Document;

/**
 * ComponentType model object
 * 
 * @author muzquiano
 */
public class ComponentType extends AbstractRenderableModelObject implements ResourceProvider
{
    public static String TYPE_ID = "component-type";
    public static String PROP_URI = "uri";
    
    protected ResourceProvider resourceContainer = null;
    
    /**
     * Instantiates a new component type for the given XML document.
     * 
     * @param document the document
     */
    public ComponentType(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }

    // resource provider methods
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceContainer#getResource(java.lang.String)
     */
    public Resource getResource(String id)
    {
        return getResourceContainer().getResource(id);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceContainer#getResources()
     */
    public Resource[] getResources()
    {
        return getResourceContainer().getResources();
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceContainer#getResourcesMap()
     */
    public Map<String, Resource> getResourcesMap()
    {
        return getResourceContainer().getResourcesMap();
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String)
     */
    public Resource addResource(String id)
    {
        return getResourceContainer().addResource(id);        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String, java.lang.String)
     */
    public Resource addResource(String id, String type)
    {
        return getResourceContainer().addResource(id, type);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#updateResource(java.lang.String, org.alfresco.web.framework.resource.Resource)
     */
    public void updateResource(String id, Resource resource)
    {
        getResourceContainer().updateResource(id, resource);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#removeResource(java.lang.String)
     */
    public void removeResource(String id)
    {
        getResourceContainer().removeResource(id);        
    }
 
    protected synchronized ResourceProvider getResourceContainer()
    {
        if (this.resourceContainer == null)
        {
            this.resourceContainer = new ModelObjectResourceProvider(this);
        }
        return this.resourceContainer;
    }    
}