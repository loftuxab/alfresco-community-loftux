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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Resource implementation which is non-persistent.
 * This is utilized to represent a temporary resource.
 * 
 * @author muzquiano
 */
public class TransientResourceImpl implements Resource 
{
	protected ResourceResolver resolver = null;
	
	protected String id;
	protected String value;
	
	protected Map<String, String> attributes;
	
	public TransientResourceImpl(String id, String type)
	{
		this.attributes = new HashMap<String, String>(16, 1.0f);
		
		this.id = id;
		this.setType(type);
		
		if("site".equals(type))
		{
			resolver = new AlfrescoSiteResourceResolver(this);
		}
		else if("space".equals(type))
		{
			resolver = new AlfrescoSpaceResourceResolver(this);
		}
		else if("webapp".equals(type))
		{
			resolver = new AlfrescoWebProjectResourceResolver(this);
		}
		else
		{
			resolver = new URIResourceResolver(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getId()
	 */
	public String getId()
	{
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getType()
	 */
	public String getType()
	{
		return this.getAttribute(ATTR_TYPE);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#setType(java.lang.String)
	 */
	public void setType(String type)
	{
		this.setAttribute(ATTR_TYPE, type);
	}
			
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#setEndpoint(java.lang.String)
	 */
	public void setEndpoint(String endpoint)
	{
		setAttribute(ATTR_ENDPOINT, endpoint);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getEndpoint()
	 */
	public String getEndpoint()
	{
		return getAttribute(ATTR_ENDPOINT);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getAttributeNames()
	 */
	public String[] getAttributeNames()
	{
		return this.attributes.keySet().toArray(new String[this.attributes.keySet().size()]);
	}
		
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String name, String value)
	{
		this.attributes.put(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name)
	{
		this.attributes.remove(name);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getValue()
	 */
	public String getValue()
	{
		return this.value;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#setValue(java.lang.String)
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
		
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getDownloadURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getDownloadURI(HttpServletRequest request)
	{
		return resolver.getDownloadURI(request);
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getProxiedDownloadURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getProxiedDownloadURI(HttpServletRequest request)
	{
		return resolver.getProxiedDownloadURI(request);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getMetadataURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getMetadataURI(HttpServletRequest request)
	{
		return resolver.getMetadataURI(request);
	}		

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getProxiedMetadataURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getProxiedMetadataURI(HttpServletRequest request)
	{
		return resolver.getProxiedMetadataURI(request);
	}		
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#rawMetadata(javax.servlet.http.HttpServletRequest)
	 */
	public String getRawMetadata(HttpServletRequest request)
	{
		return resolver.getRawMetadata(request);
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#metadata(javax.servlet.http.HttpServletRequest)
	 */
	public String getMetadata(HttpServletRequest request)
	{
		return resolver.getMetadata(request);
	}
	
}
