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

package org.alfresco.jlan.smb.server;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.ServerConfiguration;


/**
 * Native SMB Session Socket Handler Class
 *
 * @author gkspencer
 */
public class TcpipSMBSessionSocketHandler extends SessionSocketHandler {

  // Thread group
  
  private static final ThreadGroup TcpipSMBGroup = new ThreadGroup( "TcpipSMBSessions");

	/**
	 * Class constructor
	 * 
	 * @param srv SMBServer
	 * @param port int
	 * @param bindAddr InetAddress
	 * @param debug boolean
	 */
	public TcpipSMBSessionSocketHandler(SMBServer srv, int port, InetAddress bindAddr, boolean debug) {
		super("TCP-SMB", srv, port, bindAddr, debug);
	}
	
	/**
	 * Run the native SMB session socket handler
	 */
	public void run() {

		try {
		
			//	Clear the shutdown flag
			
			clearShutdown();
			
		  //  Wait for incoming connection requests
		
		  while (hasShutdown() == false) {
		
		    //  Debug
		
		    if (Debug.EnableInfo && hasDebug())
		      Debug.println("[SMB] Waiting for TCP-SMB session request ...");
		
		    //  Wait for a connection
		
		    Socket sessSock = getSocket().accept();
		
		    //  Debug
		
		    if (Debug.EnableInfo && hasDebug())
		      Debug.println("[SMB] TCP-SMB session request received from " + sessSock.getInetAddress().getHostAddress());

				try {

					//	Create a packet handler for the session
					
					PacketHandler pktHandler = new TcpipSMBPacketHandler(sessSock);
		
			    //  Create a server session for the new request, and set the session id.
			
			    SMBSrvSession srvSess = SMBSrvSession.createSession(pktHandler, getServer(), getNextSessionId());

          //  Start the new session in a seperate thread
			
			    Thread srvThread = new Thread(TcpipSMBGroup, srvSess);
			    srvThread.setDaemon(true);
			    srvThread.setName("Sess_T" + srvSess.getSessionId() + "_" + sessSock.getInetAddress().getHostAddress());
			    srvThread.start();
			  }
				catch (Exception ex) {
					
					//	Debug
					
					if ( Debug.EnableInfo && hasDebug())
						Debug.println("[SMB] TCP-SMB Failed to create session, " + ex.toString());
				}
		  }
		}
		catch (SocketException ex) {
		
		  //	Do not report an error if the server has shutdown, closing the server socket
		  //	causes an exception to be thrown.
		
		  if ( hasShutdown() == false) {
		    Debug.println("[SMB] TCP-SMB Socket error : " + ex.toString());
		  	Debug.println(ex);
		  }
		}
		catch (Exception ex) {
		
		  //	Do not report an error if the server has shutdown, closing the server socket
		  //	causes an exception to be thrown.
		
			if ( hasShutdown() == false) {
		    Debug.println("[SMB] TCP-SMB Server error : " + ex.toString());
				Debug.println(ex);
			}
		}
		
		//	Debug
		
		if (Debug.EnableInfo && hasDebug())
			Debug.println("[SMB] TCP-SMB session handler closed");
	}
	
	/**
	 * Create the TCP/IP native SMB/CIFS session socket handlers for the main SMB/CIFS server
	 * 
	 * @param server SMBServer
	 * @param sockDbg boolean
	 * @exception Exception
	 */
	public final static void createSessionHandlers(SMBServer server, boolean sockDbg)
		throws Exception {
	  
	  //	Access the CIFS server configuration
	  
	  ServerConfiguration config = server.getConfiguration();
    CIFSConfigSection cifsConfig = (CIFSConfigSection) config.getConfigSection( CIFSConfigSection.SectionName);

	  //	Create the NetBIOS SMB handler
		
		SessionSocketHandler sessHandler = new TcpipSMBSessionSocketHandler( server, cifsConfig.getTcpipSMBPort(), cifsConfig.getSMBBindAddress(), sockDbg);

		sessHandler.initialize();
		server.addSessionHandler(sessHandler);

		//	Run the TCP/IP SMB session handler in a seperate thread
					
		Thread tcpThread = new Thread(sessHandler);
		tcpThread.setName("TcpipSMB_Handler");
		tcpThread.start();

		//	DEBUG
	  
	  if ( Debug.EnableError && sockDbg)
	    Debug.println("[SMB] Native SMB TCP session handler created");
	}	
}
