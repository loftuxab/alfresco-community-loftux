package org.alfresco.jlan.server.filesys;

/*
 * FileSharingException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * File sharing exception class.
 */
public class FileSharingException extends java.io.IOException {

  private static final long serialVersionUID = -6023977250681511964L;

  /**
	 * Class constructor
	 */
	public FileSharingException() {
		super();
	}
  
	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public FileSharingException(String s) {
		super(s);
	}
}
