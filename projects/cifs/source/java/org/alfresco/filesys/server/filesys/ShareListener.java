package org.alfresco.filesys.server.filesys;

import org.alfresco.filesys.server.SrvSession;

/**
 * <p>
 * The share listener interface provides a hook into the server so that an application is notified
 * when a session connects/disconnects from a particular share.
 */
public interface ShareListener
{

    /**
     * Called when a session connects to a share
     * 
     * @param sess SrvSession
     * @param tree TreeConnection
     */
    public void shareConnect(SrvSession sess, TreeConnection tree);

    /**
     * Called when a session disconnects from a share
     * 
     * @param sess SrvSession
     * @param tree TreeConnection
     */
    public void shareDisconnect(SrvSession sess, TreeConnection tree);
}
