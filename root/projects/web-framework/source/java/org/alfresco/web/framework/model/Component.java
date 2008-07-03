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
import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;
import org.dom4j.Document;

/**
 * Component model object
 * 
 * @author muzquiano
 */
public class Component extends AbstractModelObject
{
    public static String TYPE_ID = "component";
    public static String PROP_REGION_ID = "region-id";
    public static String PROP_SOURCE_ID = "source-id";
    public static String PROP_SCOPE = "scope";
    public static String PROP_COMPONENT_TYPE_ID = "component-type-id";
    public static String PROP_CHROME = "chrome";    
    public static String PROP_URL = "url";
    
    // cached values
    private String regionId = null;
    private String scope = null;
    private String sourceId = null;
    private String componentTypeId = null;
    
    /**
     * Instantiates a new component for a given XML document.
     * 
     * @param document the document
     */
    public Component(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
    }

    /**
     * Gets the region id.
     * 
     * @return the region id
     */
    public String getRegionId()
    {
        if (this.regionId == null)
        {
            this.regionId = getProperty(PROP_REGION_ID);
        }
        return this.regionId;
    }

    /**
     * Sets the region id.
     * 
     * @param regionId the new region id
     */
    public void setRegionId(String regionId)
    {
        setProperty(PROP_REGION_ID, regionId);
        this.regionId = regionId;
        
        // regenerate the id for this component when the region id changes
        regenerateId();
    }

    /**
     * Gets the source id.
     * 
     * @return the source id
     */
    public String getSourceId()
    {
        if (this.sourceId == null)
        {
            this.sourceId = getProperty(PROP_SOURCE_ID); 
        }
        return this.sourceId;
    }

    /**
     * Sets the source id.
     * 
     * @param sourceId the new source id
     */
    public void setSourceId(String sourceId)
    {
        setProperty(PROP_SOURCE_ID, sourceId);
        this.sourceId = sourceId;
        
        // regenerate the id for this component when the source id changes
        regenerateId();        
    }

    /**
     * Gets the scope.
     * 
     * @return the scope
     */
    public String getScope()
    {
        if (this.scope == null)
        {
            this.scope = getProperty(PROP_SCOPE);
        }
        return this.scope;
    }

    /**
     * Sets the scope.
     * 
     * @param scope the new scope
     */
    public void setScope(String scope)
    {
        setProperty(PROP_SCOPE, scope);
        this.scope = scope;
        
        // regenerate the id for this component when the source changes
        regenerateId();        
    }

    /**
     * Gets the component type id.
     * 
     * @return the component type id
     */
    public String getComponentTypeId()
    {
        if (this.componentTypeId == null)
        {
            this.componentTypeId = getProperty(PROP_COMPONENT_TYPE_ID);
        }
        return this.componentTypeId;
    }

    /**
     * Sets the component type id.
     * 
     * @param componentTypeId the new component type id
     */
    public void setComponentTypeId(String componentTypeId)
    {
        setProperty(PROP_COMPONENT_TYPE_ID, componentTypeId);
        this.componentTypeId = componentTypeId;
    }

    /**
     * Gets the chrome.
     * 
     * @return the chrome
     */
    public String getChrome()
    {
        return getProperty(PROP_CHROME);
    }
    
    /**
     * Sets the chrome.
     * 
     * @param chrome the new chrome
     */
    public void setChrome(String chrome)
    {
        setProperty(PROP_CHROME, chrome);
    }
    
    /**
     * Gets the uRL.
     * 
     * @return the uRL
     */
    public String getURL()
    {
        return getProperty(PROP_URL);
    }
    
    /**
     * Sets the uRL.
     * 
     * @param url the new uRL
     */
    public void setURL(String url)
    {
        setProperty(PROP_URL, url);
    }

    /**
     * Gets the source object.
     * 
     * @param context the context
     * 
     * @return the object
     */
    public Object getSourceObject(RequestContext context)
    {
        return RenderUtil.getComponentBindingSourceObject(context, this);
    }

    /**
     * Gets the component type.
     * 
     * @param context the context
     * 
     * @return the component type
     */
    public ComponentType getComponentType(RequestContext context)
    {
        return context.getModel().getComponentType(getComponentTypeId());
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId()
    {
        return TYPE_ID;
    }
    
    /**
     * Generates the deterministic component id from its key properties
     * 
     * @param scopeId
     * @param regionId
     * @param sourceId
     * 
     * @return deterministic component id based on supplied values
     */
    public static String generateId(String scopeId, String regionId, String sourceId)
    {
        StringBuilder id = new StringBuilder(64);
        id.append(scopeId).append('.').append(regionId);
        if (!WebFrameworkConstants.REGION_SCOPE_GLOBAL.equalsIgnoreCase(scopeId))
        {
            id.append('.').append(sourceId.replace('/', '~'));
        }
        return id.toString();
    }
    
    /**
     * Regenerate the ID for this component
     */
    protected void regenerateId()
    {
        this.id = generateId(this.scope, this.regionId, this.sourceId);
    }
}
