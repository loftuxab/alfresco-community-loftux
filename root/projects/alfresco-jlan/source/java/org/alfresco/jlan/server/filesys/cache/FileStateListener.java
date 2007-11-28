package org.alfresco.jlan.server.filesys.cache;

/*
 * FileStateListener.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * File State Listener Interface
 */
public interface FileStateListener {

	/**
	 * File state has expired. The listener can control whether the file state is removed
	 * from the cache, or not.
	 * 
	 * @param state FileState
	 * @return true to remove the file state from the cache, or false to leave the file state in the cache
	 */
	public boolean fileStateExpired(FileState state);
	
	/**
	 * File state cache is closing down, any resources attached to the file state must be released.
	 * 
	 * @param state FileState
	 */
	public void fileStateClosed(FileState state);
}
