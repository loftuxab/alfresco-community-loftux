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
package org.alfresco.web.framework;

import org.alfresco.web.scripts.RemoteStoreContextImpl;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThreadLocalRequestContext;

/**
 * An implementation class for RemoteStoreContext which empowers
 * the RemoteStore to pull values from the Web Framework.
 * 
 * @author muzquiano
 */
public class WebFrameworkRemoteStoreContext extends RemoteStoreContextImpl
{
	public WebFrameworkRemoteStoreContext()
	{
		super();
	}
				
	/* (non-Javadoc)
	 * @see org.alfresco.web.scripts.RemoteStoreContextProvider#getStoreId()
	 */
	public String getStoreId()
	{
		String storeId = null;
		
		// retrieve the request context
		RequestContext context = ThreadLocalRequestContext.getRequestContext();
		if(context != null)
		{
			// pull back request-context model store id 
			storeId = (String) context.getModel().getObjectManager().getContext().getValue(ModelPersistenceContext.REPO_STOREID);
		}
		
		return storeId;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.scripts.RemoteStoreContextImpl#getWebappId()
	 */
	public String getWebappId()
	{
		String webappId = null;
		
		// retrieve the request context
		RequestContext context = ThreadLocalRequestContext.getRequestContext();
		if(context != null)
		{
			// pull back request-context model store id 
			webappId = (String) context.getModel().getObjectManager().getContext().getValue(ModelPersistenceContext.REPO_WEBAPPID);
		}
		
		return webappId;
	}
		
	/* (non-Javadoc)
	 * @see org.alfresco.web.scripts.RemoteStoreContext#getStoreBasePath()
	 */
	public String getStoreBasePath()
	{
	    String basePath = null;
	    
	    String webapp = getWebappId();
	    if(webapp != null)
	    {
	        // if we have a WCM webapp in our context, then we should default to a WEB-INF base path
	        basePath = "/WEB-INF/classes";
	    }
	    
	    return basePath;
	}		
		
}
