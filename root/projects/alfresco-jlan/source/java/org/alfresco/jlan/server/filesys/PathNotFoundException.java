package org.alfresco.jlan.server.filesys;

/*
 * PathNotFoundException.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import java.io.IOException;

/**
 * Path Not Found Exception Class
 * 
 * <p>Indicates that the upper part of a path does not exist, as opposed to the file/folder at the
 * end of the path.
 */
public class PathNotFoundException extends IOException {

  private static final long serialVersionUID = -537450129331293825L;

  /**
	 * Class constructor.
	 */
	public PathNotFoundException() {
		super();
	}
  
	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public PathNotFoundException(String s) {
		super(s);
	}
}
