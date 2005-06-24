package org.alfresco.filesys.netbios.server;

/**
 * NetBIOS add name listener interface.
 */
public interface AddNameListener
{
    /**
     * Signal that a NetBIOS name has been added, or an error occurred whilst trying to add a new
     * NetBIOS name.
     * 
     * @param evt NetBIOSNameEvent
     */
    public void netbiosNameAdded(NetBIOSNameEvent evt);
}