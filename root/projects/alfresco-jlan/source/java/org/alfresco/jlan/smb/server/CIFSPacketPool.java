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

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.core.NoPooledMemoryException;
import org.alfresco.jlan.server.memory.ByteBufferPool;

/**
 * CIFs Packet Pool Class
 * 
 * <p>Allocates buffers from the main byte buffer pool and wraps them in a CIFS specific packet.
 * 
 * @author gkspencer
 */
public class CIFSPacketPool {

	// Constants
	
	public static final long CIFSAllocateWaitTime	= 250;	// milliseconds
	
	// Main byte buffer pool
	
	private ByteBufferPool m_bufferPool;
	
	// Debug enable
	
	private boolean m_debug;
	
	/**
	 * Class constructor
	 * 
	 * @param bufPool byteBufferPool
	 */
	public CIFSPacketPool( ByteBufferPool bufPool) {
		m_bufferPool = bufPool;
	}
	
	/**
	 * Allocate a CIFS packet with the specified buffer size
	 * 
	 * @param reqSiz int
	 * @return SMBSrvPacket
	 * @exception NoPooledMemoryException
	 */
	public final SMBSrvPacket allocatePacket( int reqSiz)
		throws NoPooledMemoryException {
		
		// Allocate the byte buffer for the CIFS packet
		
		byte[] buf = m_bufferPool.allocateBuffer( reqSiz, CIFSAllocateWaitTime);
		if ( buf == null) {
			
			// DEBUG
		
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("[SMB] CIFS Packet allocate failed, reqSiz=" + reqSiz);
			
			// Throw an exception, no memory available
			
			throw new NoPooledMemoryException( "Request size " + reqSiz);
		}

		// DEBUG
		
		if ( Debug.EnableDbg && hasDebug())
			Debug.println("[SMB] CIFS Packet allocate reqSiz=" + reqSiz + ", allocSiz=" + buf.length);
		
		// Create the CIFS packet
		
		return new SMBSrvPacket( buf);
	}
	
	/**
	 * Allocate a CIFS packet with the specified buffer size, copy the header from the
	 * request packet
	 * 
	 * @param reqSiz int
	 * @param reqPkt SMBSrvPacket
	 * @return SMBSrvPacket
	 * @exception NoPooledMemoryException
	 */
	public final SMBSrvPacket allocatePacket( int reqSiz, SMBSrvPacket reqPkt)
		throws NoPooledMemoryException {

		// Allocate a new packet, copy the standard header length
		
		return allocatePacket( reqSiz, reqPkt, -1);
	}
	
	/**
	 * Allocate a CIFS packet with the specified buffer size, copy the header from the
	 * request packet
	 * 
	 * @param reqSiz int
	 * @param reqPkt SMBSrvPacket
	 * @param copyLen int
	 * @return SMBSrvPacket
	 * @exception NoPooledMemoryException
	 */
	public final SMBSrvPacket allocatePacket( int reqSiz, SMBSrvPacket reqPkt, int copyLen)
		throws NoPooledMemoryException {

		// Allocate the response packet
		
		SMBSrvPacket respPkt = allocatePacket( reqSiz);

		// Copy the header from the request to the response
		
		System.arraycopy( reqPkt.getBuffer(), 4, respPkt.getBuffer(), 4, copyLen == -1 ? SMBSrvPacket.HeaderLength : copyLen);

		// Attach the response packet to the request
		
		reqPkt.setAssociatedPacket( respPkt);
		
		// DEBUG
		
		if ( Debug.EnableDbg && hasDebug())
			Debug.println("[SMB]  Associated packet reqSiz=" + reqSiz + " with pktSiz=" + reqPkt.getBuffer().length);
		
		// Return the new packet
		
		return respPkt;
	}
	
	/**
	 * Release a CIFS packet buffer back to the pool
	 * 
	 * @param smbPkt SMBSrvPacket
	 */
	public final void releasePacket( SMBSrvPacket smbPkt) {
		
		// Release the buffer from the CIFS packet back to the pool
		
		m_bufferPool.releaseBuffer( smbPkt.getBuffer());
		
		// DEBUG
		
		if ( Debug.EnableDbg && hasDebug() && smbPkt.hasAssociatedPacket() == false)
			Debug.println("[SMB] CIFS Packet released bufSiz=" + smbPkt.getBuffer().length);
		
		// Check if the packet has an associated packet which also needs releasing
		
		if ( smbPkt.hasAssociatedPacket()) {

			// Release the associated packets buffer back to the pool
			
			m_bufferPool.releaseBuffer( smbPkt.getAssociatedPacket().getBuffer());

			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("[SMB] CIFS Packet released bufSiz=" + smbPkt.getBuffer().length + " and assoc packet, bufSiz=" + smbPkt.getAssociatedPacket().getBuffer().length);

			// Clear the associated packet
			
			smbPkt.clearAssociatedPacket();
		}
	}
	
	/**
	 * Return the length of the smallest packet size available
	 * 
	 * @return int
	 */
	public final int getSmallestSize() {
		return m_bufferPool.getSmallestSize();
	}
	
	/**
	 * Return the length of the largest packet size available
	 * 
	 * @return int
	 */
	public final int getLargestSize() {
		return m_bufferPool.getLargestSize();
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
	 * Check if debug output is enabled
	 * 
	 * @return boolean
	 */
	public final boolean hasDebug() {
		return m_debug;
	}
	
	/**
	 * Return the packet pool details as a string
	 * 
	 * @return String
	 */
	public String toString() {
		return m_bufferPool.toString();
	}
 }
 
