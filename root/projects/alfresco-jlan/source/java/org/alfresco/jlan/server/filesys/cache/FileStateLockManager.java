package org.alfresco.jlan.server.filesys.cache;

/*
 * FileStateLockManager.java
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
import org.alfresco.jlan.server.filesys.db.DBNetworkFile;
import org.alfresco.jlan.server.locking.LockManager;

/**
 * File State Lock Manager Class
 * 
 * <p>File locking implementation that uses the file state cache to keep track of locks on a file.
 */
public class FileStateLockManager implements LockManager {

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
		throws LockConflictException, IOException {
			
		//	Make sure the file is of the correct type
		
		if (( file instanceof DBNetworkFile) == false)
			throw new IllegalArgumentException("Invalid NetworkFile class");
			
		//	Get the file state associated with the file
		
		DBNetworkFile dbFile = (DBNetworkFile) file;
		FileState fstate = dbFile.getFileState();
		
		if ( fstate == null)
			throw new IOException("Open file without state (lock)");
			
		//	Add the lock to the active lock list for the file, check if the new lock conflicts with
		//	any existing locks. Add the lock to the file instance so that locks can be removed if the
		//	file is closed/session abnormally terminates.
		
		fstate.addLock(lock);
		file.addLock(lock);
	}

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
		throws NotLockedException, IOException {
			
		//	Make sure the file is of the correct type
	
		if (( file instanceof DBNetworkFile) == false)
			throw new IllegalArgumentException("Invalid NetworkFile class");
		
		//	Get the file state associated with the file
	
		DBNetworkFile dbFile = (DBNetworkFile) file;
		FileState fstate = dbFile.getFileState();
	
		if ( fstate == null)
			throw new IOException("Open file without state (unlock)");
		
		//	Remove the lock from the active lock list for the file, and the file instance
	
		fstate.removeLock(lock);
		file.removeLock(lock);
	}
	
	/**
	 * Create a lock object, use the standard FileLock object.
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param offset long
	 * @param len long
	 * @param pid int
	 */
	public FileLock createLockObject(SrvSession sess, TreeConnection tree, NetworkFile file, long offset, long len, int pid) {

		//	Create a lock object to represent the file lock
		
		return new FileLock(offset, len, pid);
	}
	
	/**
	 * Release all locks that a session has on a file. This method is called to perform cleanup if a file
	 * is closed that has active locks or if a session abnormally terminates.
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 */
	public void releaseLocksForFile(SrvSession sess, TreeConnection tree, NetworkFile file) {
		
		//	Check if the file has active locks
		
		if ( file.hasLocks()) {
			
			synchronized ( file) {
				
				//	Enumerate the locks and remove
				
				while ( file.numberOfLocks() > 0) {
					
					//	Get the current file lock
					
					FileLock curLock = file.getLockAt(0);
					
					//	Remove the lock, ignore errors
					
					try {
						
						//	Unlock will remove the lock from the global list and the local files list
						
						unlockFile(sess, tree, file, curLock);
					}
					catch (Exception ex) {
					}
				}
			}
		}
	}
}
