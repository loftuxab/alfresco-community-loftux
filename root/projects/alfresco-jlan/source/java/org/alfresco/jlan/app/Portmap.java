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

package org.alfresco.jlan.app;

import org.alfresco.jlan.debug.Debug;
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
			Debug.println( ex);
		}
	}
}
