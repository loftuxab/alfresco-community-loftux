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

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.framework.ModelPersistenceContext;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * Resolves URI references to Alfresco Repository objects
 * hosted within Alfresco 3.0 Sites
 * 
 * @author muzquiano
 */
public class AlfrescoWebProjectResourceResolver extends AbstractAlfrescoResourceResolver 
{
	public AlfrescoWebProjectResourceResolver(Resource resource)
	{
		super(resource);
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.ResourceResolver#getDownloadURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getDownloadURI(HttpServletRequest request)
	{
		StringBuilder builder = new StringBuilder(512);

		if(FrameworkHelper.getConfig().isWebStudioEnabled())
		{
			builder.append("/remotestore/get");
			
			String value = this.resource.getValue();
			if(value != null)
			{
				if(!value.startsWith("/"))
				{
					value = "/" + value;
				}
				
				builder.append(value);
			}
			
			// append store
			RequestContext requestContext = null;
			try
			{
				requestContext = RequestUtil.getRequestContext(request);

				ModelPersistenceContext mpc = requestContext.getModel().getObjectManager().getContext();
				String storeId = (String) mpc.getValue(ModelPersistenceContext.REPO_STOREID);
				
				// append the store id
				builder.append("?s=" + storeId);
				
				String webappId = (String) mpc.getValue(ModelPersistenceContext.REPO_WEBAPPID);
				if(webappId != null)
				{
					builder.append("&w=" + webappId);
				}
			}
			catch(RequestContextException rce)
			{
				rce.printStackTrace();
				return null;
			}
		}
		else
		{
			builder.append(this.resource.getValue());
		}
		
		return builder.toString();
	}
			
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.ResourceResolver#getMetadataURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getMetadataURI(HttpServletRequest request)
	{
		StringBuilder builder = new StringBuilder(512);
		if(FrameworkHelper.getConfig().isWebStudioEnabled())
		{
			RequestContext context = null;
			try
			{
				context = RequestUtil.getRequestContext(request);
			}
			catch(RequestContextException rce)
			{
				rce.printStackTrace();
				return null;
			}
		
			if(context != null)
			{
				String webappId = "ROOT";
				String path = this.resource.getValue();
			
				String storeId = (String) context.getModel().getObjectManager().getContext().getValue(ModelPersistenceContext.REPO_STOREID);
				if(storeId != null)
				{				
					builder.append("/webframework/avm/metadata/");
					builder.append(storeId);
					builder.append("/");
					builder.append(webappId);
					builder.append("/");
					builder.append(path);
				}
			}
		}
		
		return builder.toString();
	}	
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.ResourceResolver#getProxiedDownloadURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getProxiedDownloadURI(HttpServletRequest request)
	{
		String url = getDownloadURI(request);
		
		if(FrameworkHelper.getConfig().isWebStudioEnabled())
		{
			url = "/proxy/{endpoint}" + url;
			
			String ep = this.resource.getEndpoint();
			if(ep == null)
			{
				ep = "alfresco";
			}
			url = url.replace("{endpoint}", ep);
		}
		
		return url;
	}
		
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.ResourceResolver#getProxiedMetadataURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getProxiedMetadataURI(HttpServletRequest request)
	{
		String url = getMetadataURI(request);
		
		if(FrameworkHelper.getConfig().isWebStudioEnabled())
		{
			url = "/proxy/{endpoint}" + url;
			
			String ep = this.resource.getEndpoint();
			if(ep == null)
			{
				ep = "alfresco";
			}
			url = url.replace("{endpoint}", ep);
		}
		
		return url;
	}	
	
}
