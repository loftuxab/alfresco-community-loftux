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

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages client side state for the Web Studio application
 * 
 * @author muzquiano
 */
public class WebStudioStateBean implements Serializable 
{
	private static Log logger = LogFactory.getLog(WebStudioStateBean.class);
	
	protected HashMap<String, ApplicationStateBean> applications = null;
	protected HashMap<String, AppletStateBean> applets = null;
	
	public WebStudioStateBean()
	{
	}

	/**
	 * Returns an array of application ids for Web Studio
	 * 
	 * @return array of ids
	 */
	public String[] getApplicationIds()
	{
		return applications.values().toArray(new String[applications.size()]);
	}
	
	/**
	 * Retrieves the application state for a given application id
	 * 
	 * @param id
	 * 
	 * @return application state
	 */
	public ApplicationStateBean getApplicationState(String id)
	{
		return (ApplicationStateBean) applications.get(id);		
	}
	
	/**
	 * Returns an array of applet ids for Web Studio
	 * 
	 * @return array of ids
	 */
	public String[] getAppletIds()
	{
		return applets.values().toArray(new String[applications.size()]);
	}
	
	/**
	 * Retrives the applet state for a given applet id
	 * 
	 * @param id
	 * 
	 * @return applet state
	 */
	public AppletStateBean getAppletState(String id)
	{
		return (AppletStateBean) applets.get(id);		
	}
}
