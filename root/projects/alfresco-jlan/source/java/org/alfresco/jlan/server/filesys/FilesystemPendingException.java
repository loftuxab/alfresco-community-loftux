package org.alfresco.jlan.server.filesys;

/*
 * FilesystemPendingException.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import java.io.IOException;

/**
 * Filesystem Pending Exception Class
 * 
 * <p>Used to indicate that an SMB/CIFS request will be dealt with by the filesystem driver out of sequence.
 */
public class FilesystemPendingException extends IOException {

  private static final long serialVersionUID = -6262965537099562665L;

  /**
	 * Default constructor
	 */
	public FilesystemPendingException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public FilesystemPendingException(String msg) {
		super(msg);
	}
}
