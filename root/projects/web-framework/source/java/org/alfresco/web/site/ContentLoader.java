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

import org.alfresco.web.site.exception.ContentLoaderException;

/**
 * Interface that describes a loader that can handle content with a
 * given id or a given type id.
 * 
 * If the loader is able to handle the content object, the loader
 * can call over to the services or repository tier, load the data
 * and return a Content object.
 * 
 * @author muzquiano
 */
public interface ContentLoader
{
	
	/**
	 * Can handle.
	 * 
	 * @param objectId the object id
	 * 
	 * @return true, if successful
	 */
	public boolean canHandle(String objectId);
	
	/**
	 * Load.
	 * 
	 * @param context the context
	 * @param objectId the object id
	 * 
	 * @return the content
	 * 
	 * @throws ContentLoaderException the content loader exception
	 */
	public Content load(RequestContext context, String objectId)
		throws ContentLoaderException;
	
	/**
	 * Returns the endpoint from which this content loader loads.
	 * 
	 * @return The endpoint id
	 */
	public String getEndpointId();
}
