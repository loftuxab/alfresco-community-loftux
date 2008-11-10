/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

/**
 * Binding context for the remote store
 * 
 * @author muzquiano
 */
public interface RemoteStoreContext
{
	/**
	 * Returns the id of the store to which to bind
	 * 
	 * @return the store id
	 */
	public String getStoreId();
	
	/**
	 * Sets the id of the store
	 * 
	 * @param storeId the store id
	 */
	public void setStoreId(String storeId);
	
	/**
	 * Returns the id of the web application to utilize in the store.
	 * If null, then content references are based from the store root.
	 * 
	 * @return
	 */
	public String getWebappId();
	
	/**
	 * Sets the id of the web application to utilize in the store.
	 * 
	 * @param webappId
	 */
	public void setWebappId(String webappId);
	
	/**
	 * Gets the store base path
	 */
	public String getStoreBasePath();
	
	/**
	 * Sets the store base path
	 * 
	 * @param basePath
	 */
	public void setStoreBasePath(String basePath);	
}
