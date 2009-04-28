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

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.win32.Win32NetBIOS;
import org.alfresco.jlan.server.thread.ThreadRequest;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.nio.AsynchronousWritesHandler;

/**
 * Asynchronous Winsock NIO CIFS Write Request Class
 * 
 * <p>Holds the details of a Winsock NetBIOS JNI socket based CIFS session request for processing by a thread pool.
 * 
 * @author gkspencer
 */
public class AsyncWinsockCIFSWriteRequest implements ThreadRequest {

	// CIFS session
	
	private SMBSrvSession m_sess;
	
	// Socket event for this NetBIOS socket
	
	private int m_socketEvent;
	
	// Request handler
	
	private AsyncWinsockCIFSRequestHandler m_reqHandler;
	
	/**
	 * Class constructor
	 * 
	 * @param sess SMBSrvSession
	 * @param sockEvent int
	 * @param reqHandler AsyncWinsockCIFSRequestHandler
	 */
	public AsyncWinsockCIFSWriteRequest( SMBSrvSession sess, int sockEvent, AsyncWinsockCIFSRequestHandler reqHandler) {
		m_sess         = sess;
		m_socketEvent  = sockEvent;
		m_reqHandler   = reqHandler;
	}
	
	/**
	 * Run the CIFS request
	 */
	public void runRequest() {
		
		// Check if the session is still alive
		
		if ( m_sess.isShutdown() == false &&
				m_sess.getPacketHandler() instanceof AsynchronousWritesHandler) {
			
			try {
				
				// Get the packet handler and check if there are queued write requests
				
				AsynchronousWritesHandler writeHandler = (AsynchronousWritesHandler) m_sess.getPacketHandler();
				
				if ( writeHandler.getQueuedWriteCount() > 0) {
					
					Debug.println("%%% Processing queued writes, queued=" + writeHandler.getQueuedWriteCount() + " %%%");
					
					// Process the queued write requests
					
					int wrCnt = writeHandler.processQueuedWrites();
					
					// DEBUG
					
					Debug.println("%%% Processed " + wrCnt + " queued write requests, queued=" + writeHandler.getQueuedWriteCount() + " %%%");
				}
				
			}
			catch ( Throwable ex) {
				Debug.println(ex);
			}
		}
	}
	
	/**
	 * Return the CIFS request details as a string
	 * 
	 * @reurun String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[Async Winsock CIFS Sess=");
		str.append( m_sess.getUniqueId());
		str.append("-Write]");
		
		return str.toString();
	}
}
