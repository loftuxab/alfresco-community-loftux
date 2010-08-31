/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.Resource;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 * Resource interface implementation
 * 
 * @author Roy Wetherall
 * @author Brian
 */
public abstract class ResourceBaseImpl implements Resource 
{	
    private static final long serialVersionUID = 2137271248424335766L;
    
    protected Map<String, Serializable> properties = new TreeMap<String,Serializable>();
    protected SectionFactory sectionFactory;
    protected AssetFactory assetFactory;
    protected CollectionFactory collectionFactory;

    protected String primarySectionId;

    protected String id;
    protected String typeId;
    protected String name;

	public ResourceBaseImpl() 
	{
	}

	/**
	 * Set resources properties
	 * @param props		property map
	 */
	public void setProperties(Map<String,Serializable> props)
	{
	    properties = new TreeMap<String, Serializable>(props);
	    id = (String)properties.get(PropertyIds.OBJECT_ID);
	    typeId = (String)properties.get(PropertyIds.OBJECT_TYPE_ID);
	    name = (String)properties.get(PropertyIds.NAME);
	}
	
    /**
	 *  @see org.alfresco.wcm.client.Resource#getId()
	 */
	@Override
	public String getId() 
	{
		return id;
	}
	
	/**
	 *  @see org.alfresco.wcm.client.Resource#getName()
	 */	
	@Override
	public String getName() 
	{
		return name;
	}

	/**
	 *  @see org.alfresco.wcm.client.Resource#getTitle()
	 */	
	@Override
	public String getTitle() 
	{
		return (String)properties.get(PROPERTY_TITLE);
	}

    /**
     * @see org.alfresco.wcm.client.Resource#getType()
     */
    @Override
    public String getType()
    {
        return (String)getProperties().get(PropertyIds.OBJECT_TYPE_ID);
    }
    
	/**
	 *  @see org.alfresco.wcm.client.Resource#getDescription()
	 */	
	@Override
	public String getDescription() 
	{
		return (String)properties.get(PROPERTY_DESCRIPTION);
	}

	/**
	 *  @see org.alfresco.wcm.client.Resource#getContainingSection()
	 */	
	@Override
	public Section getContainingSection() 
	{
        Section section = (primarySectionId == null) ? null : getSectionFactory().getSection(primarySectionId);
        return section;
	}

	/**
	 *  @see org.alfresco.wcm.client.Resource#getProperty()
	 */
	@Override
	public Serializable getProperty(String propertyName) 
	{
		return properties.get(propertyName);
	}
	
	/**
	 *  @see org.alfresco.wcm.client.Resource#getProperties()
	 */
	@Override
	public Map<String, Serializable> getProperties() 
	{
		return properties;
	}

    public SectionFactory getSectionFactory()
    {
        return sectionFactory;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }

    public AssetFactory getAssetFactory()
    {
        return assetFactory;
    }

    public void setAssetFactory(AssetFactory resourceFactory)
    {
        this.assetFactory = resourceFactory;
    }

    public CollectionFactory getCollectionFactory()
    {
        return collectionFactory;
    }

    public void setCollectionFactory(CollectionFactory collectionFactory)
    {
        this.collectionFactory = collectionFactory;
    }
	
    public void setPrimarySectionId(String sectionId)
    {
        this.primarySectionId = sectionId;
    }
}

