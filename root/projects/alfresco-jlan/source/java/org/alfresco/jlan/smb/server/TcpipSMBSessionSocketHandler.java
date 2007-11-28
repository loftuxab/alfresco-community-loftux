package org.alfresco.jlan.smb.server;

/*
 * TcpipSMBSessionSocketHandler.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.ServerConfiguration;


/**
 * Native SMB Session Socket Handler Class
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
