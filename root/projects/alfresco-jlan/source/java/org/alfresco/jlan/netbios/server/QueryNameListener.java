package org.alfresco.jlan.netbios.server;

/*
 * QueryNameListener.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.net.InetAddress;

/**
 * NetBIOS name query listener interface.
 */
public interface QueryNameListener {

	/**
	 * Signal that a NetBIOS name query has been received, for the specified local NetBIOS name.
	 *
	 * @param evt   Local NetBIOS name details.
	 * @param addr  IP address of the remote node that sent the name query request.
	 */
	public void netbiosNameQuery ( NetBIOSNameEvent evt, InetAddress addr);
}