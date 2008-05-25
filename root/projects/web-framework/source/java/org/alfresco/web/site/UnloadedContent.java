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
package org.alfresco.web.site;

import java.io.Serializable;
import java.util.Map;

/**
 * This class acts as a placeholder for content that was identified
 * and for which a load was attempted, but the load failed.
 * 
 * @author Uzquiano
 */
public class UnloadedContent extends AbstractContent 
{
	
	/**
	 * Instantiates a new unloaded content.
	 * 
	 * @param endpointId the endpoint id
	 * @param id the id
	 */
	public UnloadedContent(String endpointId, String id)
	{
		super(endpointId, id);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.AbstractContent#isLoaded()
	 */
	public boolean isLoaded()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getTypeId()
	 */
	public String getTypeId()
	{
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getProperty(java.lang.String)
	 */
	public Object getProperty(String propertyName)
	{
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getProperties()
	 */
	public Map<String, Serializable> getProperties()
	{
		return null;
	}	
}
