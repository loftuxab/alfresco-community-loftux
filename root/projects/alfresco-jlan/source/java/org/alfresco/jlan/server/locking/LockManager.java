package org.alfresco.jlan.server.locking;

/*
 * LockManager.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;

import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.locking.LockConflictException;
import org.alfresco.jlan.locking.NotLockedException;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.TreeConnection;

/**
 * Lock Manager Interface
 * 
 * <p>A lock manager implementation provides file locking support for a virtual filesystem.
 */
public interface LockManager {

	/**
	 * Lock a byte range within a file, or the whole file. 
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param lock FileLock
	 * @exception LockConflictException
	 * @exception IOException
	 */
	public void lockFile(SrvSession sess, TreeConnection tree, NetworkFile file, FileLock lock)
		throws LockConflictException, IOException;
		
	/**
	 * Unlock a byte range within a file, or the whole file
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param lock FileLock
	 * @exception NotLockedException
	 * @exception IOException
	 */
	public void unlockFile(SrvSession sess, TreeConnection tree, NetworkFile file, FileLock lock)
		throws NotLockedException, IOException;
		
	/**
	 * Create a lock object, allows the FileLock object to be extended
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param offset long
	 * @param len long
	 * @param pid int
	 * @return FileLock
	 */
	public FileLock createLockObject(SrvSession sess, TreeConnection tree, NetworkFile file,
																	 long offset, long len, int pid);
																	 
	/**
	 * Release all locks that a session has on a file. This method is called to perform cleanup if a file
	 * is closed that has active locks or if a session abnormally terminates.
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 */
	public void releaseLocksForFile(SrvSession sess, TreeConnection tree, NetworkFile file);
}
