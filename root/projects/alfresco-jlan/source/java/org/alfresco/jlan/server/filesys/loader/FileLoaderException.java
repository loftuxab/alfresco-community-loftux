package org.alfresco.jlan.server.filesys.loader;

/*
 * FileLoaderException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * File Loader Exception Class
 */
public class FileLoaderException extends Exception {

  private static final long serialVersionUID = 6801434749926078295L;

  /**
	 * Class constructor
	 */
	public FileLoaderException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public FileLoaderException(String msg) {
		super(msg);
	}
}
