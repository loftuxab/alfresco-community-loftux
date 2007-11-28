package org.alfresco.jlan.server.filesys;

/*
 * DirectoryNotEmptyException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

/**
 * <p>Thrown when an attempt is made to delete a directory that contains files or directories.
 */
public class DirectoryNotEmptyException extends IOException {

  private static final long serialVersionUID = -4707262817813283889L;

  /**
	 * Default constructor
	 */
	public DirectoryNotEmptyException() {
		super();
	}

	/**
	 * Class constructor.
	 *
	 * @param s java.lang.String
	 */
	public DirectoryNotEmptyException(String s) {
		super(s);
	}
}
