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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Container for Alfresco content that has been loaded from a
 * remote server.
 * 
 * @author muzquiano
 */
public abstract class AbstractContent implements Content
{	
	protected static Log logger = LogFactory.getLog(Content.class);
	protected String id;
	protected String endpointId;
	protected long timestamp;
	protected int statusCode;
	protected String statusMessage;
	protected Throwable statusException;
	
	/**
	 * Instantiates a new abstract content.
	 * 
	 * @param endpointId the endpoint id
	 * @param id the id
	 */
	public AbstractContent(String endpointId, String id)
	{
		this.id = id;
		this.timestamp = System.currentTimeMillis();
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getId()
	 */
	public String getId()
	{
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getTimestamp()
	 */
	public long getTimestamp()
	{
		return this.timestamp;
	}
		
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getEndpointId()
	 */
	public String getEndpointId()
	{
		return this.endpointId;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#isLoaded()
	 */
	public boolean isLoaded()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#setStatusCode(int)
	 */
	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getStatusCode()
	 */
	public int getStatusCode()
	{
		return this.statusCode;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#setStatusMessage(java.lang.String)
	 */
	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getStatusMessage()
	 */
	public String getStatusMessage()
	{
		return this.statusMessage;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#setStatusException(java.lang.Throwable)
	 */
	public void setStatusException(Throwable statusException)
	{
		this.statusException = statusException;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.Content#getStatusException()
	 */
	public Throwable getStatusException()
	{
		return this.statusException;
	}
	
}
