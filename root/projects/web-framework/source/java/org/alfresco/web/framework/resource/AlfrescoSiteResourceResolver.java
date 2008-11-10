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

/**
 * Resolves URI references to Alfresco Repository objects
 * hosted within Alfresco 3.0 Sites
 * 
 * @author muzquiano
 */
public class AlfrescoSiteResourceResolver extends AbstractAlfrescoResourceResolver 
{
	public AlfrescoSiteResourceResolver(Resource resource)
	{
		super(resource);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.ResourceResolver#getDownloadURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getDownloadURI(HttpServletRequest request)
	{
		String url ="/api/node/{value}/content";
		
		url = url.replace("{endpoint}", this.resource.getEndpoint());
		url = url.replace("{value}", this.resource.getValue());
		
		return url;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.ResourceResolver#getMetadataURI(javax.servlet.http.HttpServletRequest)
	 */
	public String getMetadataURI(HttpServletRequest request)
	{
		String url = "/webframework/content/metadata?id={nodeRef}";

		String nodeRef = toNodeRefString(this.resource.getValue());		
		url = url.replace("{rodeRef}", nodeRef);
		
		return url;
	}			
}
