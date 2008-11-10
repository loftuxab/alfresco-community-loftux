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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.RequestContext;

/**
 * Defines a render response context
 * 
 * @author Uzquiano
 *
 */
public interface RenderContext extends RequestContext 
{
	public int SCOPE_ANY = 0;
	public int SCOPE_RENDERING = 1;
	public int SCOPE_REQUEST = 2;
	
	public RenderMode getRenderMode();
	public void setRenderMode(RenderMode renderMode);
		
	// we do not need to define this method as it is now defined on request context
	//public HttpServletRequest getRequest();
	public void setRequest(HttpServletRequest request);
	
	public HttpServletResponse getResponse();
	public void setResponse(HttpServletResponse response);
	
	public ModelObject getObject();
	public void setObject(ModelObject modelObject);
	
	public String getRenderId();
	
	public void setPassiveMode(boolean passiveMode);
	public boolean isPassiveMode();
	
	/*
	public Component[] getRenderingComponents();
	public void setRenderingComponents(Component[] components);
	*/
	
	public RenderContextProvider getProvider();
	
	public void release();
	
    public void setValue(String key, Serializable value, int scope);
    public Serializable getValue(String key, int scope);
    public void removeValue(String key, int scope);
    public boolean hasValue(String key, int scope);	
}
