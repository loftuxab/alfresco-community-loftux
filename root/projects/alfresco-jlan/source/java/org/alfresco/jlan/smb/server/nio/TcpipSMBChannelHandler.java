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

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.smb.server.CIFSPacketPool;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.util.DataPacker;

/**
 * Tcpip SMB Packet Handler Class
 * 
 * @author gkspencer
 */
public class TcpipSMBChannelHandler extends ChannelPacketHandler {

	/**
	 * Class constructor
	 * 
	 * @param sockChannel SocketChannel
	 * @param packetPool CIFSPacketPool
	 * @exception IOException If a network error occurs
	 */
	public TcpipSMBChannelHandler(SocketChannel sockChannel, CIFSPacketPool packetPool)
		throws IOException {
		super(sockChannel, SMBSrvPacket.PROTOCOL_TCPIP, "TCP-SMB", "T", packetPool);
	}

	/**
	 * Read a packet from the input stream
	 * 
	 * @return SMBSrvPacket
	 * @exception IOexception If a network error occurs
	 */
	public SMBSrvPacket readPacket()
		throws IOException {

		// Read the packet header

		int len = readBytes( m_headerBuf, 0, 4);

		// Check if the connection has been closed, read length equals -1

		if ( len == -1)
			return null;
//			throw new IOException("Connection closed (header read)");

		// Check if we received a valid header

		if ( len < RFCNetBIOSProtocol.HEADER_LEN)
			throw new IOException("Invalid header, len=" + len);

		// Get the packet type from the header

//		int typ = (int) ( m_headerBuf[0] & 0xFF);
		int dlen = (int) DataPacker.getShort( m_headerBuf, 2);

		// Check for a large packet, add to the data length

		if ( m_headerBuf[1] != 0) {
			int llen = (int) m_headerBuf[1];
			dlen += (llen << 16);
		}

		// Get a packet from the pool to hold the request data, allow for the NetBIOS header length
		// so that the CIFS request lines up with other implementations.
		
		SMBSrvPacket pkt = getPacketPool().allocatePacket( dlen + RFCNetBIOSProtocol.HEADER_LEN);
		
		// Read the data part of the packet into the users buffer, this may take
		// several reads

		int offset = RFCNetBIOSProtocol.HEADER_LEN;
		int totlen = offset;

		try {
			
			while (dlen > 0) {
	
				// Read the data
	
				len = readBytes( pkt.getBuffer(), offset, dlen);
	
				// Check if the connection has been closed
	
				if ( len == -1)
					throw new IOException("Connection closed (request read)");
	
				// Update the received length and remaining data length
	
				totlen += len;
				dlen -= len;
	
				// Update the user buffer offset as more reads will be required
				// to complete the data read
	
				offset += len;
	
			}
		}
		catch (IOException ex) {
			
			// Release the packet back to the pool
			
			getPacketPool().releasePacket( pkt);
			
			// Rethrow the exception
			
			throw ex;
		}

		// Set the received request length
		
		pkt.setReceivedLength( totlen);
		
		// Return the received packet

		return pkt;
	}

	/**
	 * Send a packet to the output stream
	 * 
	 * @param pkt SMBSrvPacket
	 * @param len int
	 * @param writeRaw boolean
	 * @exception IOexception If a network error occurs
	 */
	public void writePacket(SMBSrvPacket pkt, int len, boolean writeRaw)
		throws IOException {

		// Fill in the TCP SMB message header, this is already allocated as
		// part of the users buffer.

		byte[] buf = pkt.getBuffer();
		DataPacker.putInt(len, buf, 0);

		// Output the data packet

		int bufSiz = len + RFCNetBIOSProtocol.HEADER_LEN;
		writeBytes(buf, 0, bufSiz);
	}
}
