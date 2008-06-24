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
package org.alfresco.jlan.smb.server;

import org.alfresco.jlan.server.thread.ThreadRequest;

/**
 * CIFS Thread Request Class
 * 
 * <p>Holds the details of a CIFS request for processing by a thread pool.
 * 
 * @author gkspencer
 */
public class CIFSThreadRequest implements ThreadRequest {

	// CIFS session and request packet
	
	private SMBSrvSession m_sess;
	private SMBSrvPacket m_smbPkt;
	
	/**
	 * Class constructor
	 * 
	 * @param sess SMBSrvSession
	 * @param smbPkt SMBSrvPacket
	 */
	public CIFSThreadRequest( SMBSrvSession sess, SMBSrvPacket smbPkt) {
		m_sess   = sess;
		m_smbPkt = smbPkt;
	}
	
	/**
	 * Run the CIFS request
	 */
	public void runRequest() {
		
		// Check if the session is still alive
		
		if ( m_sess.isShutdown() == false) {
			
			// Process the CIFS request
			
			m_sess.processPacket( m_smbPkt);
		}
		else {
			
			// Release the request back to the pool
			
			m_sess.getPacketPool().releasePacket( m_smbPkt);
		}
	}
	
	/**
	 * Return the CIFS request details as a string
	 * 
	 * @reurun String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[CIFS Sess=");
		str.append( m_sess.getUniqueId());
		str.append(", pkt=");
		str.append( m_smbPkt.toString());
		str.append("]");
		
		return str.toString();
	}
}
