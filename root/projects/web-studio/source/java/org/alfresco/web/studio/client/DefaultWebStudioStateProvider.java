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
package org.alfresco.web.studio.client;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.alfresco.web.config.WebStudioConfigElement.AppletDescriptor;
import org.alfresco.web.config.WebStudioConfigElement.ApplicationDescriptor;
import org.alfresco.web.studio.WebStudio;

/**
 * Default in-context state factory which produces session-bound
 * in-context state objects
 * 
 * @author muzquiano
 */
public class DefaultWebStudioStateProvider implements WebStudioStateProvider 
{
	public final static String SESSION_ATTR_WEBSTUDIO = "webstudio_incontext_state";
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.studio.client.WebStudioStateProvider#provide(javax.servlet.http.HttpServletRequest)
	 */
	public synchronized WebStudioStateBean provide(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		
		WebStudioStateBean state = (WebStudioStateBean) session.getAttribute(SESSION_ATTR_WEBSTUDIO);
		if(state == null)
		{
			state = new WebStudioStateBean();
			session.setAttribute(SESSION_ATTR_WEBSTUDIO, state);
			
			// initialize the client state with settings from
			// Web Studio configuration

			// applets
			String[] appletIds = WebStudio.getConfig().getAppletIds();
			for(int i = 0; i < appletIds.length; i++)
			{
				AppletDescriptor appletDescriptor = WebStudio.getConfig().getApplet(appletIds[i]);
				
				// create client-state container for the applet
				AppletStateBean appletState = new AppletStateBean(appletDescriptor.getId());
				appletState.setTitle(appletDescriptor.getTitle());
				appletState.setDescription(appletDescriptor.getDescription());
				appletState.setBootstrapClassname(appletDescriptor.getBootstrapClassName());
				appletState.setBootstrapLocation(appletDescriptor.getBootstrapLocation());
				
				// add this applet to the webstudio state
				state.applets.put(appletState.getId(), appletState);
			}
			
			// applications
			String[] appIds = WebStudio.getConfig().getApplicationIds();
			for(int i = 0; i < appIds.length; i++)
			{
				ApplicationDescriptor appDescriptor = WebStudio.getConfig().getApplication(appIds[i]);
				
				// create client-state container for the application
				ApplicationStateBean appState = new ApplicationStateBean(appDescriptor.getId());
				appState.setTitle(appDescriptor.getTitle());
				appState.setDescription(appDescriptor.getDescription());
				appState.setBootstrapClassname(appDescriptor.getBootstrapClassName());
				appState.setBootstrapLocation(appDescriptor.getBootstrapLocation());
				
				// add this application to the webstudio state
				state.applications.put(appState.getId(), appState);
				
				// walk the included applets
				List<String> includedAppletIds = appDescriptor.getAppletIncludes();
				for(int z = 0; z < includedAppletIds.size(); z++)
				{
					String appletId = (String) includedAppletIds.get(z);
					
					AppletStateBean appletBean = state.getAppletState(appletId);
					if(appletBean != null)
					{
						appState.applets.put(appletBean.getId(), appletBean);
					}
				}
			}
		}
		
		return state;
	}
}
