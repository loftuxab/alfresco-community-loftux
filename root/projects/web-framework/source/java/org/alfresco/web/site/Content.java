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
 * Interface that describes a content object which is located on
 * the repository (or services) tier.
 * 
 * @author muzquiano
 */
public interface Content
{	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId();
	
	/**
	 * Gets the type id
	 * 
	 * @return
	 */
	public String getTypeId();
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp();
	
	/**
	 * Gets the property.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the property
	 */
	public Object getProperty(String propertyName);
	
	/**
	 * Gets the properties.
	 * 
	 * @return the properties
	 */
	public Map<String, Serializable> getProperties();
	
	/**
	 * Returns the endpoint id from which the content was retrieved
	 * 
	 * @return
	 */
	public String getEndpointId();
	
	/**
	 * Returns whether the content was successfully loaded
	 * 
	 * @return
	 */
	public boolean isLoaded();

	/**
	 * Sets the status code
	 * 
	 * @param statusCode
	 */
	public void setStatusCode(int statusCode);
	
	/**
	 * Returns the status code associated with the load
	 * 
	 * @return
	 */
	public int getStatusCode();

	/**
	 * Sets the status message
	 * 
	 * @param statusMessage
	 */
	public void setStatusMessage(String statusMessage);
	
	/**
	 * Returns the status message associated with the load
	 * 
	 * @return
	 */
	public String getStatusMessage();
	
	/**
	 * Sets the status exception
	 * 
	 * @param statusException
	 */
	public void setStatusException(Throwable statusException);
	
	/**
	 * Returns the status exception associated with the load
	 * 
	 * @return
	 */
	public Throwable getStatusException();
}
