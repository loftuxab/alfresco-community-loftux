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
 * Resolves URI references to resources
 * 
 * @author muzquiano
 */
public interface ResourceResolver 
{
	/**
	 * Returns the download URI for the given resource
	 * 
	 * @param request
	 * @return
	 */
	public String getDownloadURI(HttpServletRequest request);

	/**
	 * Returns the proxied download URI for the given resource
	 * 
	 * @param request
	 * @return
	 */
	public String getProxiedDownloadURI(HttpServletRequest request);
	
	/**
	 * Returns the metadata URI for the given resource
	 * 
	 * @param request
	 * @return
	 */
	public String getMetadataURI(HttpServletRequest request);

	/**
	 * Returns the metadata URI for the given resource
	 * 
	 * @param request
	 * @return
	 */
	public String getProxiedMetadataURI(HttpServletRequest request);
	
	/**
	 * Retrieves the container-formatted metadata for the 
	 * current resource
	 * 
	 * @param request
	 * @return
	 */
	public String getMetadata(HttpServletRequest request);
	
	/**
	 * Retrieves the raw metadata for the current resource
	 * 
	 * @param request
	 * @return
	 */
	public String getRawMetadata(HttpServletRequest request);
}
