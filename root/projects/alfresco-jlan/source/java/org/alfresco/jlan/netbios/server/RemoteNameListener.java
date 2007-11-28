package org.alfresco.jlan.netbios.server;

/*
 * RemoteNameListener.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.net.InetAddress;

/**
 * NetBIOS remote name listener interface.
 */
public interface RemoteNameListener {

	/**
	 * Signal that a remote host has added a new NetBIOS name.
	 *
	 * @param evt NetBIOSNameEvent
	 * @param addr InetAddress
	 */
	public void netbiosAddRemoteName ( NetBIOSNameEvent evt, InetAddress addr);

	/**
	 * Signal that a remote host has released a NetBIOS name.
	 *
	 * @param evt NetBIOSNameEvent
	 * @param addr InetAddress
	 */
	public void netbiosReleaseRemoteName ( NetBIOSNameEvent evt, InetAddress addr);
}