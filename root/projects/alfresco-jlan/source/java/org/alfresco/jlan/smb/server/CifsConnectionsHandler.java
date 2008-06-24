/*
 * Copyright (C) 2005-2008 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jlan.smb.server;

import org.alfresco.jlan.server.config.InvalidConfigurationException;

/**
 * CIFS Connections Handler Interface
 * 
 * @author gkspencer
 */
public interface CifsConnectionsHandler {

	/**
	 * Initialize the connections handler
	 * 
	 * @param srv SMBServer
	 * @param config CIFSConfigSection
	 * @exception InvalidConfigurationException
	 */
	public void initializeHandler( SMBServer srv, CIFSConfigSection config)
		throws InvalidConfigurationException;
	
	/**
	 * Start the connection handler thread
	 */
	public void startHandler();

	/**
	 * Stop the connections handler
	 */
	public void stopHandler();
	
	/**
	 *  Return the count of active session handlers
	 *  
	 *  @return int
	 */
	public int numberOfSessionHandlers();
}
