package org.alfresco.filesys.netbios.server;

import java.net.InetAddress;

/**
 * NetBIOS remote name listener interface.
 */
public interface RemoteNameListener
{

    /**
     * Signal that a remote host has added a new NetBIOS name.
     * 
     * @param evt NetBIOSNameEvent
     * @param addr java.net.InetAddress
     */
    public void netbiosAddRemoteName(NetBIOSNameEvent evt, InetAddress addr);

    /**
     * Signal that a remote host has released a NetBIOS name.
     * 
     * @param evt NetBIOSNameEvent
     * @param addr java.net.InetAddress
     */
    public void netbiosReleaseRemoteName(NetBIOSNameEvent evt, InetAddress addr);
}