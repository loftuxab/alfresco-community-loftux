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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.AuthenticationUtil;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.UserFactory;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.studio.WebStudioUtil;

/**
 * @author muzquiano
 */
public final class ScriptWebStudio implements Serializable
{
    protected RequestContext context = null;
    
    public ScriptWebStudio(RequestContext context)
    {
        this.context = context;
    }

    protected HttpServletRequest getHttpServletRequest()
    {
    	return context.getRequest();
    }
    
	public String getCurrentWebProject()
	{
		return WebStudioUtil.getCurrentWebProject(getHttpServletRequest());
	}
	
	public void setCurrentWebProject(String webProjectId)
	{
		WebStudioUtil.setCurrentWebProject(getHttpServletRequest(), webProjectId);
		
		// TODO: reset the staging store?
	}
	
	public String getCurrentSandbox()
	{
		return WebStudioUtil.getCurrentSandbox(getHttpServletRequest());
	}
	
	public void setCurrentSandbox(String sandboxId)
	{
		WebStudioUtil.setCurrentSandbox(getHttpServletRequest(), sandboxId);
		
		// this should be the same as the store id
		setCurrentStore(sandboxId);
	}

	public String getCurrentStore()
	{
		return WebStudioUtil.getCurrentStore(getHttpServletRequest());
	}
	
	public void setCurrentStore(String storeId)
	{
		WebStudioUtil.setCurrentStore(getHttpServletRequest(), storeId);
		
		// bind the web framework to the store
		getHttpServletRequest().getSession(true).setAttribute(WebFrameworkConstants.STORE_ID_SESSION_ATTRIBUTE_NAME, storeId);
		
		// set to the ROOT webapp if none set
		String webappId = getCurrentWebapp();
		if(webappId == null)
		{
			setCurrentWebapp("ROOT");
		}
	}
	
	public String getCurrentWebapp()
	{
		return WebStudioUtil.getCurrentWebapp(getHttpServletRequest());
	}
	
	public void setCurrentWebapp(String webappId)
	{
		WebStudioUtil.setCurrentWebapp(getHttpServletRequest(), webappId);

		// bind the web framework to the webapp
		getHttpServletRequest().getSession(true).setAttribute(WebFrameworkConstants.WEBAPP_ID_SESSION_ATTRIBUTE_NAME, webappId);
	}
	
	public String getCurrentUserId()
	{
		return WebStudioUtil.getCurrentUserId(getHttpServletRequest());
	}
	
	public void setCurrentUserId(String currentUserId)
	{
		WebStudioUtil.setCurrentUserId(getHttpServletRequest(), currentUserId);
	}

    public void setModel(Map<String, Object> model)
    {
        this.model = model;
    }

    public Map<String, Object> getModel()
    {
        return this.model;
    }

    protected Map<String, Object> model;    

    public boolean login(String username, String password)
    	throws Exception
    {
    	boolean success = false;
    	
    	HttpServletRequest request = this.getHttpServletRequest();
    	
    	UserFactory userFactory = FrameworkHelper.getUserFactory();
    	
        // see if we can authenticate the user
        boolean authenticated = userFactory.authenticate(request, username, password);
        if (authenticated)
        {
            // this will fully reset all connector sessions
            RequestContext context = RequestUtil.getRequestContext(request);
            AuthenticationUtil.login(request, username);
            
            // store the user
            setCurrentUserId(username);
            
            // mark the fact that we succeeded
            success = true;            
	    }
        
        return success;
    }
    
    public ScriptImporter getImporter()
    {
    	return new ScriptImporter(context);
    }
}
