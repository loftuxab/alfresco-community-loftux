/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.studio;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.alfresco.web.framework.ModelPersistenceContext;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * @author muzquiano
 */
public class WebStudioUtil 
{
	// TODO: This method must be rethought and reimplemented
    public static String getContentEditURL(RequestContext context,
            String endpointId, String itemRelativePath)
    {
    	return null;    	
    }	
    
	/**
	 * Gets a cached object from the user session
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static Object getCachedObject(HttpServletRequest request, String key)
	{
		return (Object) request.getSession().getAttribute("CACHED_RESOURCE_" + key);
	}

	/**
	 * Caches an object into the user session
	 * 
	 * @param request
	 * @param key
	 * @param object
	 */
	public static void setCachedObject(HttpServletRequest request, String key, Object object)
	{
		request.getSession().setAttribute("CACHED_RESOURCE_" + key, object);
	}
	
	public static String getCurrentUserId(HttpServletRequest request)
	{
		return (String) getCachedObject(request, "CurrentUserId");
	}
	
	public static void setCurrentUserId(HttpServletRequest request, String currentUserId)
	{
		setCachedObject(request, "CurrentUserId", currentUserId);
	}
	
	public static String getCurrentWebProject(HttpServletRequest request)
	{		
		return (String) getCachedObject(request, "CurrentWebProject");
	}
	
	public static void setCurrentWebProject(HttpServletRequest request, String webProjectId)
	{
		setCachedObject(request, "CurrentWebProject", webProjectId);
	}
	
	public static String getCurrentSandbox(HttpServletRequest request)
	{		
		return (String) getCachedObject(request, "CurrentSandbox");
	}
	
	public static void setCurrentSandbox(HttpServletRequest request, String sandboxId)
	{
		setCachedObject(request, "CurrentSandbox", sandboxId);
	}

	public static String getCurrentStore(HttpServletRequest request)
	{		
		return (String) getCachedObject(request, "CurrentStore");
	}
	
	public static void setCurrentStore(HttpServletRequest request, String storeId)
	{
		setCachedObject(request, "CurrentStore", storeId);
	}

	public static String getCurrentWebapp(HttpServletRequest request)
	{		
		return (String) getCachedObject(request, "CurrentWebapp");
	}
	
	public static void setCurrentWebapp(HttpServletRequest request, String webappId)
	{
		setCachedObject(request, "CurrentWebapp", webappId);
	}
	
}
