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
package org.alfresco.jlan.app;

import org.alfresco.jlan.oncrpc.nfs.NFSConfigSection;
import org.alfresco.jlan.oncrpc.portmap.PortMapperServer;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.util.ConsoleIO;

/**
 * Portmapper service class
 * 
 * @author gkspencer
 */
public class Portmap {

	/**
	 * Main application
	 * 
	 * @param args String[]
	 */
	public static void main(String[] args) {
		
		try {
			
			// Create the default configuration
			
			ServerConfiguration srvConfig = new ServerConfiguration( "PORTMAP");
			NFSConfigSection nfsConfig = new NFSConfigSection(srvConfig);
			
			nfsConfig.setPortMapperDebug( true);
			
			// Create the portmapper service
			
			PortMapperServer portMapper = new PortMapperServer( srvConfig);
			
			// Start the portmapper
			
			portMapper.startServer();
			
			//  Wait while the server runs, user may stop server by typing a key

			boolean shutdown = false;
		      
			while (shutdown == false) {
						
				//	Check if the user has requested a shutdown, if running interactively
						 
				int inChar = ConsoleIO.readCharacter();
		          
				if ( inChar == 'x' || inChar == 'X')
					shutdown = true;
						  
				//	Sleep for a short while
							
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException ex) {
				}
			}
			
			// Shutdown the portmapper service
			
			portMapper.shutdownServer( false);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
