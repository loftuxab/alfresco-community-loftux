package org.alfresco.jlan.server.filesys;

/*
 * FileListener.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.SrvSession;

/**
 * File Listener Interface.
 *
 * <p>Generates events when files are opened/closed on the server.
 */
public interface FileListener {

	/**
	 * File has been closed.
	 *
	 * @param sess SrvSession
	 * @param file NetworkFile
	 */
	void fileClosed(SrvSession sess, NetworkFile file);
	
	/**
	 * File has been opened.
	 *
	 * @param sess SrvSession
	 * @param file NetworkFile
	 */
	void fileOpened(SrvSession sess, NetworkFile file);
}