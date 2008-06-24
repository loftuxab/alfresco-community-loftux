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

package org.alfresco.jlan.netbios.win32;

/**
 * NetBIOs Selection Key Class
 * 
 * <p>SelectionKey type class for NetBIOSSocket connections.
 * 
 * @author gkspencer
 */
public class NetBIOSSelectionKey {

	// Constants
	//
	// Operations that are to be monitored for this socket
	
	public static final int OP_ACCEPT  = 0x0001;
	public static final int OP_CONNECT = 0x0002;
	public static final int OP_READ    = 0x0004;
	public static final int OP_WRITE   = 0x0008;
	
	// Key valid state
	
	private boolean m_valid;

	// Operations that should be monitored for events for this socket
	
	private int m_interestOps;

	// Operations that have triggered
	
	private int m_triggerOps;
	
	// Selector that this key belongs to
	
	private NetBIOSSelector m_selector;
	
	// NetBIOS socket
	
	private NetBIOSSocket m_socket;
	
	// Attached object associated with this key
	
	private Object m_attachment;
	
	/**
	 * Class constructor
	 * 
	 * @param selector NetBIOSSelector
	 * @param socket NetBIOSSocket
	 * @param ops int
	 * @param attachment Object
	 */
	protected NetBIOSSelectionKey( NetBIOSSelector selector, NetBIOSSocket socket, int ops, Object attachment) {
		m_selector   = selector;
		m_socket     = socket;
		m_attachment = attachment;

		// Set the operations to be monitored
		
		m_interestOps = ops;
		
		// Indicate that the key/socket is valid
		
		m_valid = true;
	}
	
	/**
	 * Attach an object to this selection key
	 * 
	 * @param obj Object
	 */
	public final void attach(Object obj) {
		m_attachment = obj;
	}
	
	/**
	 * Return the associated attached object
	 * 
	 * @return Object
	 */
	public final Object attachment() {
		return m_attachment;
	}

	/**
	 * Check if a new socket connection has been received by a listening socket
	 * 
	 * @return boolean
	 */
	public final boolean isAcceptable() {
		return hasTrigger(OP_ACCEPT);
	}
	
	/**
	 * Check if the socket is readable
	 * 
	 * @return boolean
	 */
	public final boolean isReadable() {
		return hasTrigger(OP_READ);
	}

	/**
	 * Check if the socket is writable
	 * 
	 * @return boolean
	 */
	public final boolean isWritable() {
		return hasTrigger(OP_READ);
	}

	/**
	 * Check if the client side socket has connected
	 * 
	 * @return boolean
	 */
	public final boolean isConnectable() {
		return hasTrigger(OP_CONNECT);
	}

	/**
	 * Return the list of operations that are enabled for this socket
	 * 
	 * @return int
	 */
	public final int interestOps() {
		return m_interestOps;
	}
	
	/**
	 * Set the list of interested operations for this socket
	 * 
	 * @param ops int
	 */
	public final void interestOps(int ops) {
		m_interestOps = ops;
	}
	
	/**
	 * Return the list of troggered operations for this socket
	 * 
	 * @return int
	 */
	public final int readyOps() {
		return m_triggerOps;
	}
	
	/**
	 * Check if the socket is valid
	 * 
	 * @return boolean
	 */
	public final boolean isValid() {
		return m_valid;
	}
	
	/**
	 * Check if the specified operation trigger is set
	 * 
	 * @param flag int
	 * @return boolean
	 */
	private final boolean hasTrigger(int flag) {
		return ( m_triggerOps & flag) != 0 ? true : false;
	}
	/**
	 * Set the triggerd ops for the socket
	 * 
	 * @param ops int
	 */
	protected final void setTriggers( int ops) {
		m_triggerOps = ops;
	}
	
	/**
	 * Return the selector that this key belongs to
	 * 
	 * @return NetBIOSSelector
	 */
	public final NetBIOSSelector selector() {
		return m_selector;
	}
	
	/**
	 * Set or clear the valid status for this socket
	 * 
	 * @param valid boolean
	 */
	protected final void setValid(boolean valid) {
		m_valid = valid;
	}
	
	/**
	 * Return the NetBIOS socket
	 * 
	 * @return NetBIOSSocket
	 */
	public final NetBIOSSocket socket() {
		return m_socket;
	}
	
	/**
	 * Return a hash code for the selection key
	 * 
	 * @return int
	 */
	public int hashcode() {
		return m_socket.getSocket();
	}
	
	/**
	 * Return the selection key as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[Socket=");
		str.append( socket());

		str.append(",Ops=0x");
		str.append(Integer.toHexString(interestOps()));
		str.append("/0x");
		str.append(Integer.toHexString(readyOps()));
		
		str.append(",Attachment=");
		str.append( attachment());

		if ( isValid() == false)
			str.append(",Invalid");
		
		str.append("]");
		
		return str.toString();
	}
}
