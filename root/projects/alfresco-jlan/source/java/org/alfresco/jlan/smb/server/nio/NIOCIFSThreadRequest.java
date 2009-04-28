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

package org.alfresco.jlan.smb.server.nio;

import java.nio.channels.SelectionKey;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.thread.ThreadRequest;
import org.alfresco.jlan.smb.server.PacketHandler;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * NIO CIFS Thread Request Class
 * 
 * <p>Holds the details of an NIO channel based CIFS session request for processing by a thread pool.
 * 
 * @author gkspencer
 */
public class NIOCIFSThreadRequest implements ThreadRequest {

	// CIFS session
	
	private SMBSrvSession m_sess;
	
	// Selection key for this socket channel
	
	private SelectionKey m_selectionKey;
	
	/**
	 * Class constructor
	 * 
	 * @param sess SMBSrvSession
	 * @param selKey SelectionKey
	 * @param handler CIFSRequestHandler
	 */
	public NIOCIFSThreadRequest( SMBSrvSession sess, SelectionKey selKey) {
		m_sess         = sess;
		m_selectionKey = selKey;
	}
	
	/**
	 * Run the CIFS request
	 */
	public void runRequest() {
		
		// Check if the session is still alive
		
		if ( m_sess.isShutdown() == false) {
			
			SMBSrvPacket smbPkt = null;
			
			try {
				
				// Get the packet handler and read in the CIFS request
				
				PacketHandler pktHandler = m_sess.getPacketHandler();
				smbPkt = pktHandler.readPacket();
				
				// If the request paket is not valid then close the session
				
				if ( smbPkt == null) {
					
					// Close the session
					
					m_sess.hangupSession( "Client closed socket");
					m_sess.processPacket( null);
					
					// Cancel the selection key
					
					m_selectionKey.cancel();
					m_selectionKey.selector().wakeup();
				}
				else {
					
					// Re-enable read events for this socket channel
					
					m_selectionKey.interestOps( m_selectionKey.interestOps() | SelectionKey.OP_READ);
					m_selectionKey.selector().wakeup();
				
					// Process the CIFS request
					
					m_sess.processPacket( smbPkt);
					smbPkt = null;
				}
			}
			catch ( Throwable ex) {
				Debug.println( ex);
			}
			finally {
				
				// Make sure the request packet is returned to the pool
				
				if ( smbPkt != null)
					m_sess.getPacketPool().releasePacket( smbPkt);
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
		
		str.append("[NIO CIFS Sess=");
		str.append( m_sess.getUniqueId());
		str.append("]");
		
		return str.toString();
	}
}
