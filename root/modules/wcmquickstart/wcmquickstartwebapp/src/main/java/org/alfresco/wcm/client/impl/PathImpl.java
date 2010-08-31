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

import org.alfresco.wcm.client.Path;

/** 
 * Path splits a uri into a resource name and array of path segments.
 * @author Chris Lack
 */
public class PathImpl implements Path
{
	private String[] pathSegments;
	private String resourceName;
	
	public PathImpl(String uri) 
	{
		if (uri == null) 
		{
			this.pathSegments = new String[]{};
			return;
		}
		
		String cleanUri = uri.trim();
		String path = null;

		int index = cleanUri.lastIndexOf("/");
		if (index != -1)
		{
			String resource = cleanUri.substring(index+1);
			if (resource.contains(".") == true)
			{
				path = cleanUri.substring(0, index);
				this.resourceName = resource;
			}
			else
			{
				path = cleanUri;
			}
		}
		else
		{
			path = cleanUri;
		}
		
		this.pathSegments = path.split("/");
	}
	
	/**
	 * @see org.alfresco.wcm.client.Path#getPathSegments()
	 */
	public String[] getPathSegments()
    {
    	return pathSegments;
    }
	
	/**
	 * @see org.alfresco.wcm.client.Path#getResourceName()
	 */
	public String getResourceName()
    {
    	return resourceName;
    }

}
