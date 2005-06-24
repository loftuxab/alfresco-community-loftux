package org.alfresco.filesys.netbios.server;

import java.net.InetAddress;

/**
 * NetBIOS name query listener interface.
 */
public interface QueryNameListener
{

    /**
     * Signal that a NetBIOS name query has been received, for the specified local NetBIOS name.
     * 
     * @param evt Local NetBIOS name details.
     * @param addr IP address of the remote node that sent the name query request.
     */
    public void netbiosNameQuery(NetBIOSNameEvent evt, InetAddress addr);
}