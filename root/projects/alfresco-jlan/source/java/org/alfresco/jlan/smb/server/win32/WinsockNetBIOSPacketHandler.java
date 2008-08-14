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

package org.alfresco.jlan.smb.server.win32;

import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.win32.NetBIOSSocket;
import org.alfresco.jlan.smb.server.CIFSPacketPool;
import org.alfresco.jlan.smb.server.PacketHandler;
import org.alfresco.jlan.smb.server.SMBSrvPacket;

/**
 * Winsock NetBIOS Packet Handler Class
 * 
 * <p>
 * Uses a Windows Winsock NetBIOS socket to provide the low level session layer for better
 * integration with Windows.
 * 
 * @author gkspencer
 */
public class WinsockNetBIOSPacketHandler extends PacketHandler {

	// Constants
	//
	// Receive error indicating a receive buffer error

	private static final int ReceiveBufferSizeError = 0x80000000;

	// Network LAN adapter to use

	private int m_lana;

	// NetBIOS session socket

	private NetBIOSSocket m_sessSock;

	/**
	 * Class constructor
	 * 
	 * @param lana int
	 * @param sock NetBIOSSocket
	 * @param packetPool CIFSPacketPool
	 */
	public WinsockNetBIOSPacketHandler(int lana, NetBIOSSocket sock, CIFSPacketPool packetPool) {
		super(SMBSrvPacket.PROTOCOL_WIN32NETBIOS, "WinsockNB", "WSNB", sock.getName().getName(), packetPool);

		m_lana = lana;
		m_sessSock = sock;
	}

	/**
	 * Return the LANA number
	 * 
	 * @return int
	 */
	public final int getLANA() {
		return m_lana;
	}

	/**
	 * Return the NetBIOS socket
	 * 
	 * @return NetBIOSSocket
	 */
	public final NetBIOSSocket getSocket() {
		return m_sessSock;
	}

	/**
	 * Return the count of available bytes in the receive input stream
	 * 
	 * @return int
	 * @exception IOException If a network error occurs.
	 */
	public int availableBytes()
		throws IOException {

		// Do not know the available byte count

		return -1;
	}

	/**
	 * Read a packet from the client
	 * 
	 * @return SMBSrvPacket
	 * @throws IOException
	 */
	public SMBSrvPacket readPacket()
		throws IOException {

		// Get the length of the pending receive data, so we can allocate the correct sized buffer
		
		int rxlen = m_sessSock.available();
		SMBSrvPacket pkt = getPacketPool().allocatePacket( rxlen + 8);
		
		// Receive an SMB/CIFS request packet via the Winsock NetBIOS socket

		try {

			// Read a packet of data

			rxlen = m_sessSock.read(pkt.getBuffer(), 4, pkt.getBufferLength() - 4);

			// Check if the buffer is not big enough to receive the entire packet, extend the buffer
			// and read the remaining part of the packet

			if ( rxlen == ReceiveBufferSizeError) {

				// Check if there is a larger buffer size available from the packet pool
				
				if ( pkt.getBufferLength() >= getPacketPool().getLargestSize()) {
					
					// Release the packet back to the pool
					
					getPacketPool().releasePacket( pkt);
				
					// Throw an exception
					
					throw new RuntimeException("Winsock NetBIOS receive over max available buffer size");
				}
				
				// Get the remaining data length
				
				int rxlen2 = m_sessSock.available();
				
				if ( rxlen2 > 0) {
					
					// Allocate a larger buffer to hold the full packet
					
					SMBSrvPacket pkt2 = getPacketPool().allocatePacket( getPacketPool().getLargestSize());
					
					// Copy the existing receive data to the new packet
					
					rxlen = pkt.getBufferLength();
					System.arraycopy(pkt.getBuffer(), 4, pkt2.getBuffer(), 4, rxlen - 4);
					
					// Release the original packet buffer, switch to the new packet
					
					getPacketPool().releasePacket( pkt);
					pkt = pkt2;
					
					// Read the remaining data
					
					rxlen2 = m_sessSock.read( pkt.getBuffer(), rxlen, pkt.getBufferLength() - rxlen);
					
					// Update the total received length
					
					if ( rxlen2 == ReceiveBufferSizeError) {
						
						// Release the packet back to the pool
						
						getPacketPool().releasePacket( pkt);
					
						// Throw an exception
						
						throw new RuntimeException("Winsock NetBIOS receive error on second stage receive");
					}
					
					// Update the total receive length
					
					rxlen += rxlen2 - 4;
				}
			}
		}
		catch (IOException ex) {

			// Release the packet back to the pool
			
			getPacketPool().releasePacket( pkt);
			
			// Clear the received packet to indicate error
			
			pkt = null;
		}

		// Set the received packet length
		
		if ( pkt != null)
			pkt.setReceivedLength( rxlen);
		
		// Return the received packet

		return pkt;
	}

	/**
	 * Write a packet to the client
	 * 
	 * @param pkt SMBSrvPacket
	 * @param len int
	 * @param writeRaw boolean
	 * @throws IOException
	 */
	public void writePacket(SMBSrvPacket pkt, int len, boolean writeRaw)
		throws IOException {

		// Output the packet via the Winsock NetBIOS socket
		//
		// As Windows is handling the NetBIOS session layer we do not send the 4 byte header that is
		// used by the NetBIOS over TCP/IP and native SMB packet handlers.

		int pos = 4;
		int wrlen = len;
		int txlen = 0;
		
		while ( wrlen > 0) {
			
			// Write the packet
		
			txlen = m_sessSock.write(pkt.getBuffer(), pos, wrlen);
			
			// If the write length is zero wait a short while before retrying
			
			if ( txlen == 0) {
				try {
					Thread.sleep( 50);
					System.out.println( "*** Zero length write, wait 50ms ***");
				}
				catch ( InterruptedException ex) {
				}
			}
			else {
				
				// Adjust the write length
				
				wrlen -= txlen;
				pos   += txlen;
			}
		}

		// Do not check the status, if the session has been closed the next receive will fail
	}

	/**
	 * Flush the output socket
	 * 
	 * @exception IOException If a network error occurs
	 */
	public void flushPacket()
		throws IOException {

		// Nothing to do
	}

	/**
	 * Close the Winsock NetBIOS packet handler.
	 */
	public void closeHandler() {

		super.closeHandler();

		// Close the session socket

		if ( m_sessSock != null)
			m_sessSock.closeSocket();
	}
}
