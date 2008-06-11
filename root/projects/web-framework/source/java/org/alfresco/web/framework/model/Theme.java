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

import java.util.List;

import org.alfresco.web.framework.AbstractModelObject;
import org.alfresco.web.framework.ModelObjectKey;
import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Theme model object.
 * 
 * @author muzquiano
 */
public class Theme extends AbstractModelObject
{
    public static String TYPE_ID = "theme";
    
    /**
     * Instantiates a new theme for a given XML document.
     * 
     * @param document the document
     */
    public Theme(ModelObjectKey key, Document document)
    {
        super(key, document);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Theme: " + getId() + ", " + toXML();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }
    
    /**
     * Gets the page id given the specified page type. If the theme supplies a
     * specific page for a given page type it will be returned, if not null.
     * 
     * @param pageTypeId the page type id
     * 
     * @return the page id
     */
    public String getPageId(String pageTypeId)
    {
        Element pageTypesEl = document.getRootElement().element("page-types");
        if(pageTypesEl != null)
        {
            List pageTypes = pageTypesEl.elements("page-type");
            for(int i = 0; i < pageTypes.size(); i++)
            {
                Element pageType = (Element) pageTypes.get(i);
                
                String id = pageType.elementText("id");
                if(id != null && id.equals(pageTypeId))
                {
                    return pageType.elementText("page-instance-id");
                }
            }
        }
        
        return null;
    }
    
    /**
     * Sets the page id for a page type.
     * 
     * @param pageTypeId the page type id
     * @param pageId the page id
     */
    public void setDefaultPageId(String pageTypeId, String pageId)
    {
        Element pageTypesEl = document.getRootElement().element("page-types");
        if(pageTypesEl == null)
        {
            pageTypesEl = document.getRootElement().addElement("page-types");
        }
        
        Element theElement = null;
        
        List pageTypes = pageTypesEl.elements("page-type");
        for(int i = 0; i < pageTypes.size(); i++)
        {
            Element pageType = (Element) pageTypes.get(i);
            
            String id = pageType.elementText("id");
            if(id != null && id.equals(pageTypeId))
            {
                theElement = pageType;
            }
        }
        
        if(theElement != null)
        {
            theElement = pageTypesEl.addElement("page-type");
        }
        
        // add the id property
        Element idElement = theElement.addElement("id");
        idElement.setText(pageTypeId);
        
        // add the page instance id property
        Element pageInstanceIdElement = theElement.addElement("page-instance-id");
        pageInstanceIdElement.setText(pageId);
    }
    
    /**
     * Gets the default component id for a given region id
     * 
     * @param regionId the region id
     * 
     * @return the default component id
     */
    public String getDefaultComponentId(String regionId)
    {
        String componentId = null;
        
        Element defaults = document.getRootElement().element("defaults");
        if(defaults != null)
        {
            List components = defaults.elements("component");
            for(int i = 0; i < components.size(); i++)
            {
                Element component = (Element) components.get(i);
                
                String _regionId = (String) component.elementText("region-id");
                if(_regionId != null && _regionId.equals(regionId))
                {
                    componentId = component.elementText("component-id");
                }
            }
        }
        
        return componentId;
    }
    
    /**
     * Removes the default component for a given region id
     * 
     * @param regionId the region id
     */
    public void removeDefaultComponentId(String regionId)
    {
        Element defaults = document.getRootElement().element("defaults");
        if(defaults != null)
        {
            Element toRemove = null;
            
            List components = defaults.elements("component");
            for(int i = 0; i < components.size(); i++)
            {
                Element component = (Element) components.get(i);
                
                String _regionId = (String) component.elementText("region-id");
                if(_regionId != null && _regionId.equals(regionId))
                {
                    toRemove = component;
                }
            }
            
            defaults.remove(toRemove);
        }
    }
    
    /**
     * Sets the default component id.
     * 
     * @param regionId the region id
     * @param sourceId the source id
     * @param scope the scope
     * @param componentId the component id
     */
    public void setDefaultComponentId(String regionId, String componentId)
    {
        // remove the default component bound to this region id
        // (if it already exists)
        removeDefaultComponentId(regionId);
        
        // set the component
        Element defaults = document.getRootElement().element("defaults");
        if(defaults == null)
        {
            defaults = document.getRootElement().addElement("defaults");
        }
        if(defaults != null)
        {
            Element component = defaults.addElement("component");
            component.addElement("region-id", regionId);
            component.addElement("component-id", componentId);
        }
    }
    
    /**
     * Gets the default component.
     * 
     * @param context the context
     * @param regionId the region id
     * 
     * @return the default component
     */
    public Component getDefaultComponent(RequestContext context, String regionId)
    {
        Component component = null;
        
        String id = getDefaultComponentId(regionId);
        if(id != null)
        {
            component = context.getModel().getComponent(id);
        }
        
        return component;
    }    
}
