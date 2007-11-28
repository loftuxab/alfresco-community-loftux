package org.alfresco.jlan.smb.server;

/*
 * NetBIOSSessionSocketHandler.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.smb.mailslot.TcpipNetBIOSHostAnnouncer;


/**
 * NetBIOS Socket Session Handler Class
 */
public class NetBIOSSessionSocketHandler extends SessionSocketHandler {

  // Thread group
  
  private static final ThreadGroup NetBIOSGroup = new ThreadGroup( "NetBIOSSessions");

  /**
	 * Class constructor
	 * 
	 * @param srv SMBServer
	 * @param port int
	 * @param bindAddr InetAddress
	 * @param debug boolean
	 */
	public NetBIOSSessionSocketHandler(SMBServer srv, int port, InetAddress bindAddr, boolean debug) {
		super("NetBIOS", srv, port, bindAddr, debug);
	}
	
	/**
	 * Run the NetBIOS session socket handler
	 */
	public void run() {

		try {
		
			//	Clear the shutdown flag
			
			clearShutdown();
			
		  //  Wait for incoming connection requests
		
		  while (hasShutdown() == false) {
		
		    //  Debug
		
		    if (Debug.EnableInfo && hasDebug())
		      Debug.println("[SMB] Waiting for NetBIOS session request ...");
		
		    //  Wait for a connection
		
		    Socket sessSock = getSocket().accept();
		
		    //  Debug
		
		    if (Debug.EnableInfo && hasDebug())
		      Debug.println("[SMB] NetBIOS session request received from " + sessSock.getInetAddress().getHostAddress());

				try {

					//	Create a packet handler for the session
					
					PacketHandler pktHandler = new NetBIOSPacketHandler(sessSock);
												
			    //  Create a server session for the new request, and set the session id.
			
			    SMBSrvSession srvSess = SMBSrvSession.createSession(pktHandler, getServer(), getNextSessionId());
						
			    //  Start the new session in a seperate thread
			
			    Thread srvThread = new Thread(NetBIOSGroup, srvSess);
			    srvThread.setDaemon(true);
			    srvThread.setName("Sess_N" + srvSess.getSessionId() + "_" + sessSock.getInetAddress().getHostAddress());
			    srvThread.start();
				}
				catch (Exception ex) {
					
					//	Debug
					
					if ( Debug.EnableError && hasDebug())
						Debug.println("[SMB] NetBIOS Failed to create session, " + ex.toString());
				}
		  }
		}
		catch (SocketException ex) {
		
		  //	Do not report an error if the server has shutdown, closing the server socket
		  //	causes an exception to be thrown.
		
		  if ( Debug.EnableError && hasShutdown() == false) {
		    Debug.println("[SMB] NetBIOS Socket error : " + ex.toString());
		  	Debug.println(ex);
		  }
		}
		catch (Exception ex) {
		
		  //	Do not report an error if the server has shutdown, closing the server socket
		  //	causes an exception to be thrown.
		
			if ( hasShutdown() == false) {
		    Debug.println("[SMB] NetBIOS Server error : " + ex.toString());
				Debug.println(ex);
			}
		}
		
		//	Debug
		
		if (Debug.EnableInfo && hasDebug())
			Debug.println("[SMB] NetBIOS session handler closed");
	}
	
	/**
	 * Create the TCP/IP NetBIOS session socket handlers for the main SMB/CIFS server
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
		
		SessionSocketHandler sessHandler = new NetBIOSSessionSocketHandler( server, cifsConfig.getSessionPort(), cifsConfig.getSMBBindAddress(), sockDbg);
		sessHandler.initialize();
	  
	  //	Add the session handler to the list of active handlers
	  
		server.addSessionHandler(sessHandler);

		//	Run the NetBIOS session handler in a seperate thread
					
		Thread nbThread = new Thread(sessHandler);
		nbThread.setName("NetBIOS_Handler");
		nbThread.start();

		//	DEBUG
	  
	  if ( Debug.EnableError && sockDbg)
	    Debug.println("[SMB] TCP NetBIOS session handler created");

	  //	Check if a host announcer should be created
	  
	  if ( cifsConfig.hasEnableAnnouncer()) {

	    //	Create the TCP NetBIOS host announcer
	    
	    TcpipNetBIOSHostAnnouncer announcer = new TcpipNetBIOSHostAnnouncer();
	    
		  //  Set the host name to be announced
	
	    announcer.addHostName( cifsConfig.getServerName());
	    announcer.setDomain( cifsConfig.getDomainName());
	    announcer.setComment( cifsConfig.getComment());
	    announcer.setBindAddress( cifsConfig.getSMBBindAddress());
	    if ( cifsConfig.getHostAnnouncerPort() != 0)
	      announcer.setPort( cifsConfig.getHostAnnouncerPort());
	
			//	Check if there are alias names to be announced
			
			if ( cifsConfig.hasAliasNames())
				announcer.addHostNames( cifsConfig.getAliasNames());
				
			//	Set the announcement interval
			    
	    if ( cifsConfig.getHostAnnounceInterval() > 0)
	    	announcer.setInterval( cifsConfig.getHostAnnounceInterval());
	
	    try {
	      announcer.setBroadcastAddress( cifsConfig.getBroadcastMask());
	    }
	    catch (Exception ex) {
	    }
	
	    //  Set the server type flags
	
	    announcer.setServerType( cifsConfig.getServerType());
	
			//	Enable debug output
			
			if ( cifsConfig.hasHostAnnounceDebug())
				announcer.setDebug(true);
				
			//	Add the host announcer to the SMS servers list
			
			server.addHostAnnouncer( announcer);
			
	    //  Start the host announcer thread
	
	    announcer.start();

	    //	DEBUG
		  
		  if ( Debug.EnableError && sockDbg)
		    Debug.println("[SMB] TCP NetBIOS host announcer created");
	  }
	}	
}
