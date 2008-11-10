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

import org.alfresco.connector.Response;
import org.alfresco.web.scripts.ScriptRemoteConnector;
import org.alfresco.web.scripts.WebFrameworkScriptRemote;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * @author muzquiano
 */
public abstract class AbstractResource implements Resource
{	
	protected ResourceStore store = null;
	protected String id = null;
	
	public AbstractResource(ResourceStore store, String id)
	{
		this.store = store;
		this.id = id;
	
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
		return this.store.getAttributeNames(id);
	}
		
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name)
	{
		return this.store.getAttribute(this.id, name);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String name, String value)
	{
		this.store.setAttribute(this.id, name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name)
	{
		this.store.removeAttribute(this.id, name);		
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#getValue()
	 */
	public String getValue()
	{
		return this.store.getValue(this.id);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.resource.Resource#setValue(java.lang.String)
	 */
	public void setValue(String value)
	{
		this.store.setValue(this.id, value);
	}
}
