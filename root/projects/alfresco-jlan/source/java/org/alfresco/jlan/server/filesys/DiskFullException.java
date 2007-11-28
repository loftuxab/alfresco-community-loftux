package org.alfresco.jlan.server.filesys;

/*
 * DiskFullException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

/**
 * <p>Thrown when a disk write or file extend will exceed the available disk quota for the shared filesystem.
 */
public class DiskFullException extends IOException {

  private static final long serialVersionUID = 1946175038087467669L;

  /**
	 * Default constructor
	 */
	public DiskFullException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public DiskFullException(String msg) {
		super(msg);
	}
}
