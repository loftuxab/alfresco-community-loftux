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
import java.net.InetAddress;
import java.nio.channels.SocketChannel;

import org.alfresco.jlan.server.ChannelSessionHandler;
import org.alfresco.jlan.server.NetworkServer;
import org.alfresco.jlan.smb.server.PacketHandler;
import org.alfresco.jlan.smb.server.SMBServer;

/**
 * NetBIOS SMB Channel Session Handler Class
 * 
 * <p>Handle CIFS socket connections via NetBIOS, usually port 139.
 *
 * @author gkspencer
 */
public class NetBIOSSMBChannelSessionHandler extends ChannelSessionHandler {

	/**
	 * Class constructor
	 * 
	 * @param server NetworkServer
	 * @param addr InetAddress
	 * @param port int
	 */
	public NetBIOSSMBChannelSessionHandler( NetworkServer server, InetAddress addr, int port) {
		super( "NetBIOS", "SMB", server, addr, port);
	}

	/**
	 * Create a packet handler for the new client socket connection
	 * 
	 * @param sockChannel SocketChannel
	 * @return PacketHandler
	 * @exception IOException
	 */
	public PacketHandler createPacketHandler( SocketChannel sockChannel)
		throws IOException {
		
		// Create a NetBIOS SMB packet handler
		
		return new NetBIOSSMBChannelHandler( sockChannel, getSMBServer().getPacketPool());
	}
	
	/**
	 * Return the CIFS server
	 * 
	 * @return SMBServer
	 */
	public final SMBServer getSMBServer() {
		return (SMBServer) getServer();
	}
}
