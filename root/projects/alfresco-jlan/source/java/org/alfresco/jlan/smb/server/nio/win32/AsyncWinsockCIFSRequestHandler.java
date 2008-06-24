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

package org.alfresco.jlan.smb.server.nio.win32;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.util.Iterator;
import java.util.Vector;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.netbios.win32.NetBIOSSelectionKey;
import org.alfresco.jlan.netbios.win32.NetBIOSSelector;
import org.alfresco.jlan.netbios.win32.NetBIOSSocket;
import org.alfresco.jlan.netbios.win32.WinsockNetBIOSException;
import org.alfresco.jlan.server.SrvSessionQueue;
import org.alfresco.jlan.server.thread.ThreadRequest;
import org.alfresco.jlan.server.thread.ThreadRequestPool;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.nio.RequestHandler;
import org.alfresco.jlan.smb.server.win32.WinsockNetBIOSPacketHandler;

/**
 * CIFS Request Handler Class
 * 
 * <p>Handles the receiving of CIFS requests for a number of CIFS sessions.
 * 
 * @author gkspencer
 */
public class AsyncWinsockCIFSRequestHandler extends RequestHandler implements Runnable {

	// Request handler index, used to generate the thread name
	
	private static int _handlerId;
	
	// NetBIOS name that the clients are connecting to, required to make a loopback connection
	
	private NetBIOSName m_srvName;
	private int m_srvLANA;
	
	// Selector used to monitor a group of socket channels for incoming requests
	
	private NetBIOSSelector m_nbSelector;
	
	// Thread that the request handler runs in
	
	private Thread m_thread;
	
	// Thread pool for processing requests
	
	private ThreadRequestPool m_threadPool;
	
	// Queue of sessions that are pending setup with the selector
	
	private SrvSessionQueue m_sessQueue;
	
	// Loopback connection socket used to wakeup the selector, client and server side sockets
	
	private NetBIOSSocket m_wakeupSockClient;
	private NetBIOSSocket m_wakeupSockSrv;
	
	private byte[] m_wakeupBuf = new byte[1];
	
	// shutdown request flag
	
	private boolean m_shutdown;
	
	/**
	 * Class constructor
	 *
	 * @param srvName NetBIOSName
	 * @param srvLANA int
	 * @param threadPool ThreadRequestPool
	 * @param maxSess int
	 */
	public AsyncWinsockCIFSRequestHandler( NetBIOSName srvName, int srvLANA, ThreadRequestPool threadPool, int maxSess) {
		super( maxSess);
		
		// File server name and LANA
		
		m_srvName = srvName;
		m_srvLANA = srvLANA;
		
		// Set the thread pool to use for request processing
		
		m_threadPool = threadPool;
		
		// Create the session queue
		
		m_sessQueue = new SrvSessionQueue();
		
		// Start the request handler in a seperate thread
		
		m_thread = new Thread( this);
		m_thread.setName( "AsyncWinsockRequestHandler_" + ++_handlerId);
		m_thread.setDaemon( false);
		
		m_thread.start();
	}
	
	/**
	 * Return the current session count
	 * 
	 * @return int
	 */
	public final int getCurrentSessionCount() {
		
		int sessCnt = 0;
	
		if ( m_nbSelector != null)
			sessCnt = m_nbSelector.keys().size();
		
		return sessCnt;
	}
	
	/**
	 * Check if this request handler has free session slots available
	 * 
	 * @return boolean
	 */
	public final boolean hasFreeSessionSlot() {
		return ( getCurrentSessionCount() + m_sessQueue.numberOfSessions()) < getMaximumSessionCount() ? true : false;
	}

	/**
	 * Queue a new session to the request handler, wakeup the request handler thread to register it with the
	 * selector.
	 * 
	 * @param sess SMBSrvSession
	 */
	public final void queueSessionToHandler( SMBSrvSession sess) {
	
		// Add the new session to the pending queue
		
		m_sessQueue.addSession( sess);
		
		// Wakeup the main thread to process the new session queue
		
		if ( m_nbSelector != null)
			wakeupSelector();
	}
	
	/**
	 * Return the request handler name
	 * 
	 * @return String
	 */
	public final String getName() {
		if ( m_thread != null)
			return m_thread.getName();
		return "AsyncWinsockRequestHandler";
	}
	
	/**
	 * Run the main processing in a seperate thread
	 */
	public void run() {

		// Clear the shutdown flag, may have been restarted
		
		m_shutdown = false;
		
		// Create the selector
			
		m_nbSelector = new NetBIOSSelector();

		// Create a loopback connection to the workstation name for unblocking the select call
		
		try {
			NetBIOSName wksName = new NetBIOSName( m_srvName);
			wksName.setType( NetBIOSName.WorkStation);
			
			m_wakeupSockClient = NetBIOSSocket.connectSocket( m_srvLANA, wksName);
		}
		catch ( Exception ex) {
			
			// DEBUG
			
			if ( Debug.EnableError && hasDebug()) {
				Debug.println( "[SMB] Failed to connect wakeup socket");
				Debug.println( ex);
			}
			
			// Do not run
			
			m_shutdown = true;
		}
		
		// Loop until shutdown
		
		Vector<ThreadRequest> reqList = new Vector<ThreadRequest>();
		byte[] wakeupBuf = new byte[8];
		
		while ( m_shutdown == false) {
			
			// Check if there are any sessions registered

			int sessCnt = 0;
			
			if ( m_nbSelector.keys().size() == 0) {

				// Indicate that this request handler has no active sessions
				
				fireRequestHandlerEmptyEvent();
				
				// DEBUG
				
				if ( Debug.EnableInfo && hasDebug())
					Debug.println( "[SMB] Request handler " + m_thread.getName() + " waiting for session ...");
				
				// Wait for a session to be added to the handler
				
				try {
					m_sessQueue.waitWhileEmpty();
				}
				catch ( InterruptedException ex) {
				}
			}
			else {
				
				// Wait for client requests
				
				try {
					sessCnt = m_nbSelector.select();
				}
				catch ( CancelledKeyException ex) {
					
					// DEBUG
					
					if ( Debug.EnableError && hasDebug() && m_shutdown == false) {
						Debug.println( "[SMB] Request handler error waiting for events");
						Debug.println(ex);
					}
				}
				catch ( IOException ex) {
					
					// DEBUG
					
					if ( Debug.EnableError && hasDebug()) {
						Debug.println( "[SMB] Request handler error waiting for events");
						Debug.println(ex);
					}
				}
			}
			
			// Check if the shutdown flag has been set
			
			if ( m_shutdown == true)
				continue;
			
			// Check if there are any events to process
			
			if ( sessCnt > 0) {
			
				// DEBUG
				
//				if ( Debug.EnableInfo && hasDebug() && sessCnt > 1)
//					Debug.println( "[SMB] Request handler " + m_thread.getName() + " session events, sessCnt=" + sessCnt + "/" + m_nbSelector.keys().size());

				// Clear the thread request list
				
				reqList.clear();
				
				// Iterate the selected keys
				
				Iterator<NetBIOSSelectionKey> keysIter = m_nbSelector.selectedKeys().iterator();
				
				while ( keysIter.hasNext()) {
					
					// Get the current selection key and check if has an incoming request
					
					NetBIOSSelectionKey selKey = keysIter.next();
					
					if ( selKey.isReadable()) {

						// Check if the key has an associated session, if not then it is a wakeup socket event
						
						if ( selKey.attachment() != null) {
							
							// Switch off read events for this channel until the current processing is complete
							
							selKey.interestOps( selKey.interestOps() & ~NetBIOSSelectionKey.OP_READ);
							
							// Get the associated session and queue a request to the thread pool to read and process the CIFS request
							
							SMBSrvSession sess = (SMBSrvSession) selKey.attachment();
							reqList.add(  new AsyncWinsockCIFSThreadRequest( sess, selKey, this));
	
							// Remove the selection key from the selected list
							
							keysIter.remove();
	
							// Check if there are enough thread requests to be queued
							
							if ( reqList.size() >= 5) {
								
								// Queue the requests to the thread pool
								
								m_threadPool.queueRequests( reqList);
								reqList.clear();
							}
						}
						else {
							
							try {
								
								// Remove the wakeup socket from the triggered list
								
								keysIter.remove();
								
								// Clear the wakeup buffer
								
								int rxCnt = selKey.socket().read( wakeupBuf, 0, wakeupBuf.length);
								
								// DEBUG
								
//								if ( Debug.EnableInfo && hasDebug())
//									Debug.println( "[SMB] Clearing out wakeup socket, read " + rxCnt + " bytes");
							}
							catch ( WinsockNetBIOSException ex) {
								
								// DEBUG
								
								if ( Debug.EnableError && hasDebug()) {
									Debug.println( "[SMB] Error clearing out wakeup buffer");
									Debug.println( ex);
								}
							}
						}
					}
					else if ( selKey.isValid() == false) {
						
						// Remove the selection key from the selected list

						keysIter.remove();
						
						// DEBUG
						
						if ( Debug.EnableInfo && hasDebug())
							Debug.println( "[SMB] Winsock NetBIOS Selection key not valid, sess=" + selKey.attachment());
					}
				}
				
				// Queue the thread requests
				
				if ( reqList.size() > 0) {
					
					// Queue the requests to the thread pool
					
					m_threadPool.queueRequests( reqList);
					reqList.clear();
				}
			}
			
			// Check if there are any new sessions that need to be registered with the selector
			
			if ( m_sessQueue.numberOfSessions() > 0) {
				
				// Register the new sessions with the selector
				
				while ( m_sessQueue.numberOfSessions() > 0) {
					
					// Get a new session from the queue
					
					SMBSrvSession sess = (SMBSrvSession) m_sessQueue.removeSessionNoWait();
					
					if ( sess != null) {
						
						// DEBUG
						
						if ( Debug.EnableError && hasDebug())
							Debug.println( "[SMB] Register session with request handler, handler=" + m_thread.getName() + ", sess=" + sess.getUniqueId());
						
						// Get the NetBIOS socket from the sessions packet handler
						
						if ( sess.getPacketHandler() instanceof WinsockNetBIOSPacketHandler) {
							
							// Get the channel packet handler and register the NetBIOS socket with the selector
							
							WinsockNetBIOSPacketHandler winsockPktHandler = (WinsockNetBIOSPacketHandler) sess.getPacketHandler();
							NetBIOSSocket nbSocket = winsockPktHandler.getSocket();
							
							try {
								
								// Register the NetBIOS socket with the selector
								
								nbSocket.configureBlocking( false);
								nbSocket.register( m_nbSelector, NetBIOSSelectionKey.OP_READ, sess);
							}
							catch ( IOException ex) {
								
								// DEBUG
								
								if ( Debug.EnableError && hasDebug())
									Debug.println( "[SMB] Failed to register NetBIOS session socket with selector, " + ex.getMessage());
							}
						}
					}
				}
			}
		}
		
		// Close all sessions
		
		if ( m_nbSelector != null) {
			
			// Enumerate the selector keys to get the session list
			
			Iterator<Integer> selKeys = m_nbSelector.keys().iterator();
			
			while ( selKeys.hasNext()) {
				
				// Get the current session via the selection key
				
				NetBIOSSelectionKey curKey = m_nbSelector.getSelectionKey( selKeys.next());
				if ( curKey != null) {
					
					// Get the session from the seletion key
				
					SMBSrvSession sess = (SMBSrvSession) curKey.attachment();
				
					// Close the session
				
					if ( sess != null)
						sess.closeSession();
				}
			}
			
			// Close the wakeup sockets, client and server side
			
			try {
				m_wakeupSockSrv.closeSocket();
			}
			catch (Exception ex) {
			}

			try {
				m_wakeupSockClient.closeSocket();
			}
			catch (Exception ex) {
			}
			
			// Close the selector
			
			m_nbSelector.close();
		}

		// DEBUG
		
		if ( Debug.EnableInfo && hasDebug())
			Debug.println( "[SMB] Closed CIFS request handler, " + m_thread.getName());
	}
	
	/**
	 * Close the request handler
	 */
	public final void closeHandler() {
		
		// Check if the thread is running
		
		if ( m_thread != null) {
			m_shutdown = true;
			
			try {
				
				// Wakeup the thread, might be waiting on the session queue
				
				m_thread.interrupt();
				
				// Wakeup the selector, if valid
				
				if ( m_nbSelector != null)
					wakeupSelector();
			}
			catch (Exception ex) {
			}
		}
	}
	
	/**
	 * Set the server side wakeup socket
	 * 
	 * @param srvSock NetBIOSSocket
	 */
	protected final void setServerSideWakeupSocket( NetBIOSSocket srvSock) {
		
		// Check if the wakeup socket has already been set
		
		if ( m_wakeupSockSrv != null)
			throw new RuntimeException( "Server-side wakeup socket already set for Winsock request handler");
		
		// Set the server-side of the wakeup socket, required for select
		
		m_wakeupSockSrv = srvSock;
		
		try {
			
			// Register the server-side of the loopback connection with the selector
			
			m_wakeupSockSrv.configureBlocking( false);
			m_wakeupSockSrv.register( m_nbSelector, NetBIOSSelectionKey.OP_READ, null);
			
			// Wakeup the main thread to start listening for socket events
			
			synchronized ( m_sessQueue) {
				m_sessQueue.notify();
			}
		}
		catch (IOException ex) {
			
			// DEBUG
			
			if ( Debug.EnableError && hasDebug()) {
				Debug.println("[SMB] Failed to register wakeup socket with selector");
				Debug.println( ex);
			}
		}
	}
	
	/**
	 * Wake up the selector thread by writing to the loopback connection socket
	 */
	protected final void wakeupSelector() {
		if ( m_wakeupSockClient != null) {
			try {
				m_wakeupSockClient.write( m_wakeupBuf, 0, m_wakeupBuf.length);
			}
			catch ( WinsockNetBIOSException ex) {
				
				// DEBUg
				
				if ( Debug.EnableError && hasDebug()) {
					Debug.println( "[SMB] Error waking up request handler select");
					Debug.println(ex);
				}
			}
		}
	}
}
