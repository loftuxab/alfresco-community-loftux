package org.alfresco.jlan.ftp;

/*
 * InvalidPathException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Invalid FTP Path Exception Class
 */
public class InvalidPathException extends Exception {

  private static final long serialVersionUID = -5705545880668486554L;

  /**
	 * Default constructor
	 */
	public InvalidPathException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public InvalidPathException(String msg) {
		super(msg);
	}
}
