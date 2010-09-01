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

import java.util.List;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Path;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.UgcService;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.util.UrlUtils;

/**
 * Web Site Implementation
 * 
 * @author Roy Wetherall
 */
public class WebSiteImpl implements WebSite
{
	private static final long serialVersionUID = 1L;

	private String id;
	
	/** Host name */
	private String hostName;
	
	/** Context **/
	private String context;
	
	/** Host port */
	private int hostPort;
	
	/** Site title */
	private String title;
	
	/** Site description */
	private String description;

	/** Logo */
	private Asset logo;
	
	private transient SectionFactory sectionFactory;
	private transient UgcService ugcService;
	private transient UrlUtils urlUtils;

    private String rootSectionId;

	/**
	 * Constructor 
	 * 
	 * @param id		id 
	 * @param hostName  host name
	 * @param hostPort  host port
	 */
	public WebSiteImpl(String id, String hostName, int hostPort, int sectonsRefreshAfter)
	{
		this.id = id;
		this.rootSectionId = id;
		this.hostName = hostName;
		this.hostPort = hostPort;		
	}
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getHostName()
	 */
	@Override
	public String getHostName() 
	{
		return hostName;
	}

	/**
	 * @see org.alfresco.wcm.client.WebSite#getHostPort()
	 */
	@Override
	public int getHostPort()
	{
		return hostPort;
	}
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getContext()
	 */
	@Override	
	public String getContext()
	{
		return context;
	}
	
	public void setContext(String context) 
	{
		this.context = context;
	}
	
	public String getId()
    {
        return id;
    }

    /**
	 * @see org.alfresco.wcm.client.WebSite#getRootSection()
	 */
	@Override
	public Section getRootSection() 
	{
		return sectionFactory.getSection(rootSectionId);
	}
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getSections()
	 */
	@Override
	public List<Section> getSections() 
	{ 
		return getRootSection().getSections();	
	}	
	
	/**
	 * @throws Exception 
	 * @see org.alfresco.wcm.client.WebSite#getAssetByPath(java.lang.String)
	 */
	@Override
	public Asset getAssetByPath(String path)
	{
		Asset asset = null;
		
		Path segmentedPath = new PathImpl(path); 
		String[] sectionPath = segmentedPath.getPathSegments();
		String resourceName = segmentedPath.getResourceName();
		
		Section section = sectionFactory.getSectionFromPathSegments(rootSectionId, sectionPath);
		if (section != null)
		{
			if (resourceName != null)
			{
				String decodedResourceName = urlUtils.decodeResourceName(resourceName); //TODO Having UrlUtils depedency here is not nice.
				asset = section.getAsset(decodedResourceName);
				// If not found then try filename from URL just in case the name originally included
				// + instead of spaces etc.
				if (asset == null) 
				{
					asset = section.getAsset(resourceName);
				}
			}
			else
			{
				asset = section.getIndexPage();
			}
		}
		
		return asset;
	}
	
	/**
	 * @throws Exception 
	 * @see org.alfresco.wcm.client.WebSite#getSectionByPath(java.lang.String)
	 */
	@Override
	public Section getSectionByPath(String path)
	{
		Path segmentedPath = new PathImpl(path); 
		String[] sectionPath = segmentedPath.getPathSegments();
		
		return sectionFactory.getSectionFromPathSegments(rootSectionId, sectionPath);
	}	
	
	public void setSectionFactory(SectionFactory sectionFactory) 
	{
		this.sectionFactory = sectionFactory; 
	}

    @Override
    public UgcService getUgcService()
    {
        return ugcService;
    }

    public void setUgcService(UgcService ugcService)
    {
        this.ugcService = ugcService;
    }
    
	@Override
    public String getDescription()
    {
	    return description;
    }
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Override
    public String getTitle()
    {
	    return title;
    }

	public void setTitle(String title) 
	{
		this.title = title;
	}

	@Override
	public Asset getLogo() {
		return logo;
	}
	
	public void setLogo(Asset logo)
    {
		this.logo = logo;
    }
	
	public void setUrlUtils(UrlUtils urlUtils) {
		this.urlUtils = urlUtils;
	}	

    public void setRootSectionId(String rootSectionId)
    {
        this.rootSectionId = rootSectionId;
    }
}
