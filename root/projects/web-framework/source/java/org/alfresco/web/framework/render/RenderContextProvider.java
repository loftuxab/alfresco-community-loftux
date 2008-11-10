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
package org.alfresco.web.framework.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.RequestContext;

/**
 * @author muzquiano
 * @author kevinr
 */
public interface RenderContextProvider
{
	/**
	 * Provides a root RenderContext which wraps the request context and servlet objects
	 * The render context is set to the VIEW render mode.
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 */
	public RenderContext provide(RequestContext context,  HttpServletRequest request, HttpServletResponse response);

	/**
	 * Provides a root RenderContext which wraps the request context and servlet objects
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @param renderMode
	 * @return
	 */
	public RenderContext provide(RequestContext context,  HttpServletRequest request, HttpServletResponse response, RenderMode renderMode);

	/**
	 * Provides a RenderContext based on a current render context.
	 * 
	 * @param renderContext
	 * @return
	 */
	public RenderContext provide(RenderContext renderContext);
	
	/**
	 * Provides a RenderContext based on a current render context.
	 * The new RenderContext is bound to the given model object.
	 * 
	 * @param renderContext
	 * @param modelObject
	 * @return
	 */
	public RenderContext provide(RenderContext renderContext, ModelObject modelObject);
		
	/**
	 * Merges a model object into the render context.
	 * 
	 * @param renderContext
	 * @param modelObject
	 */
	public void merge(RenderContext renderContext, ModelObject modelObject);
	
	/**
	 * Releases a given render context
	 * 
	 * @param renderContext
	 */
	public void release(RenderContext renderContext);
}
