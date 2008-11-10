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

import java.util.Map;

/**
 * Describes a Web Framework object as a provider of resources.
 * 
 * @author muzquiano
 */
public interface ResourceProvider 
{
	/**
	 * Looks up a resource with the given id
	 * 
	 * @param id
	 * @return
	 */
	public Resource getResource(String id);
	
	/**
	 * Returns the set of all resources
	 * 
	 * @return
	 */
	public Resource[] getResources();
	
	/**
	 * Returns the map of resources
	 * 
	 * @return
	 */
	public Map<String, Resource> getResourcesMap();
	
	/**
	 * Adds a resource with the given id
	 * 
	 * @param id
	 * 
	 * @return resource
	 */
	public Resource addResource(String id);

	/**
	 * Adds a resource with the given id and type
	 * 
	 * @param id
	 * @param type
	 * 
	 * @return resource
	 */	
	public Resource addResource(String id, String type);
	
	/**
	 * Updates a resource for the given id
	 * 
	 * @param id
	 * @param resource
	 */
	public void updateResource(String id, Resource resource);
	
	/**
	 * Removes a resource with the given id
	 * 
	 * @param id
	 */
	public void removeResource(String id);
	
}
