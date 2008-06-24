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

import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * Request Handler Class
 * 
 * <P>Base for all requets handler implementations.
 * 
 * @author gkspencer
 */
public abstract class RequestHandler {

	// Maximum number of sessions to handle
	
	private int m_maxSessions;
	
	// Debug enable flag
	
	private boolean m_debug;
	
	// Request handler listener
	
	private RequestHandlerListener m_listener;
	
	/**
	 * Class constructor
	 * 
	 * @param maxSess int
	 */
	public RequestHandler( int maxSess) {
		m_maxSessions = maxSess;
	}
	
	/**
	 * Return the current session count
	 * 
	 * @return int
	 */
	public abstract int getCurrentSessionCount();
	
	/**
	 * Return the maximum session count
	 * 
	 * @return int
	 */
	public final int getMaximumSessionCount() {
		return m_maxSessions;
	}
	
	/**
	 * Check if this request handler has free session slots available
	 * 
	 * @return boolean
	 */
	public abstract boolean hasFreeSessionSlot();

	/**
	 * Queue a new session to the request handler, wakeup the request handler thread to register it with the
	 * selector.
	 * 
	 * @param sess SMBSrvSession
	 */
	public abstract void queueSessionToHandler( SMBSrvSession sess);

	/**
	 * Return the request handler name
	 * 
	 * @return String
	 */
	public abstract String getName();

	/**
	 * Close the request handler
	 */
	public abstract void closeHandler();
	
	/**
	 * Check if debug output is enabled
	 * 
	 * @return boolean
	 */
	public final boolean hasDebug() {
		return m_debug;
	}

	/**
	 * Enable/disable debug output
	 * 
	 * @param ena boolean
	 */
	public final void setDebug( boolean ena) {
		m_debug = ena;
	}
	
	/**
	 * check if the request handler has an associated request handler listener
	 * 
	 * @return boolean
	 */
	public final boolean hasListener() {
		return m_listener != null ? true : false;
	}
	
	/**
	 * Return the associated request handler listener
	 * 
	 * @return RequestHandlerListener
	 */
	public final RequestHandlerListener getListener() {
		return m_listener;
	}
	
	/**
	 * Set the associated request handler listener
	 * 
	 * @param listener RequestHandlerListener
	 */
	public final void setListener( RequestHandlerListener listener) {
		m_listener = listener;
	}
	
	/**
	 * Inform the listener that this request handler has no sessions to listen for incoming
	 * requests.
	 */
	protected final void fireRequestHandlerEmptyEvent() {
		if ( hasListener())
			getListener().requestHandlerEmpty( this);
	}

	/**
	 * Equality test
	 * 
	 * @param obj Object
	 * @return boolean
	 */
	public boolean equals(Object obj) {
		
		// Check for the same type
		
		if ( obj instanceof RequestHandler) {
			RequestHandler reqHandler = (RequestHandler) obj;
			return reqHandler.getName().equals( getName());
		}
		return false;
	}
}
