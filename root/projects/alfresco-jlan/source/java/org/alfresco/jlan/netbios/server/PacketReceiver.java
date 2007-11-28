package org.alfresco.jlan.netbios.server;

/*
 * PacketReceiver.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Interface for NetBIOS packet receivers.
 */
public interface PacketReceiver {

	/**
	 * Receive packets on the specified datagram socket.
	 *
	 * @param sock java.net.DatagramSocket
	 * @exception java.io.IOException The exception description.
	 */
	void ReceivePacket ( DatagramSocket sock) throws IOException;
}