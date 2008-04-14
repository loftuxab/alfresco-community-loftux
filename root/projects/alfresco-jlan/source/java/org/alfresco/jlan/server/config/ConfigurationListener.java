/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
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

package org.alfresco.jlan.server.config;

/**
 * Server Configuration Listener Interface
 * 
 * <p>The configuration listener receives server configuration change notifications that can be used to provide dynamic
 * updating of various server components.
 * 
 * <p>The configuration listener may throw an InvalidConfigurationException if the updated value is invalid or there is
 * a problem during the dynamic component update. The listener also returns a status to indicate if it ignored the update,
 * a server restart is required or the change was accepted.
 *
 * @author gkspencer
 */
public interface ConfigurationListener {

	//	Configuration listener status codes
	
	public static final int StsIgnored					= 0;
	public static final int StsAccepted					= 1;
	public static final int StsNewSessionsOnly	= 2;
	public static final int StsRestartRequired	= 3;
	
	/**
	 * Configuration variable changed
	 * 
	 * @param id int
	 * @param config ServerConfiguration
	 * @param newVal Object
	 * @return int
	 * @exception InvalidConfigurationException
	 */
	public int configurationChanged(int id, ServerConfiguration config, Object newVal)
		throws InvalidConfigurationException;
}
