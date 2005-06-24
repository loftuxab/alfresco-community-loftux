package org.alfresco.filesys.server.locking;

import org.alfresco.filesys.server.SrvSession;
import org.alfresco.filesys.server.filesys.TreeConnection;

/**
 * File Locking Interface
 * <p>
 * Optional interface that a DiskInterface driver can implement to provide file locking support.
 */
public interface FileLockingInterface
{

    /**
     * Return the lock manager implementation associated with this virtual filesystem
     * 
     * @param sess SrvSession
     * @param tree TreeConnection
     * @return LockManager
     */
    public LockManager getLockManager(SrvSession sess, TreeConnection tree);
}
