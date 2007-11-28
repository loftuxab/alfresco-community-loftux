package org.alfresco.jlan.server.locking;

/*
 * FileLockingInterface.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.TreeConnection;

/**
 * File Locking Interface
 * 
 * <p>Optional interface that a DiskInterface driver can implement to provide file locking support.
 */
public interface FileLockingInterface {

	/**
	 * Return the lock manager implementation associated with this virtual filesystem
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @return LockManager
	 */
	public LockManager getLockManager(SrvSession sess, TreeConnection tree);
}
