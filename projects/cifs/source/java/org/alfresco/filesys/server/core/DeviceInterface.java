package org.alfresco.filesys.server.core;

import org.alfresco.config.ConfigElement;
import org.alfresco.filesys.server.SrvSession;
import org.alfresco.filesys.server.filesys.TreeConnection;

/**
 * The device interface is the base of the shared device interfaces that are used by shared devices
 * on the SMB server.
 */
public interface DeviceInterface
{

    /**
     * Parse and validate the parameter string and create a device context object for this instance
     * of the shared device. The same DeviceInterface implementation may be used for multiple
     * shares.
     * 
     * @param args ConfigElement
     * @return DeviceContext
     * @exception DeviceContextException
     */
    public DeviceContext createContext(ConfigElement args) throws DeviceContextException;

    /**
     * Connection opened to this disk device
     * 
     * @param sess Server session
     * @param tree Tree connection
     */
    public void treeOpened(SrvSession sess, TreeConnection tree);

    /**
     * Connection closed to this device
     * 
     * @param sess Server session
     * @param tree Tree connection
     */
    public void treeClosed(SrvSession sess, TreeConnection tree);
}