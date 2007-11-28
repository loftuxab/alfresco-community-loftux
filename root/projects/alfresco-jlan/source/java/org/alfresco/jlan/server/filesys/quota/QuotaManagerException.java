package org.alfresco.jlan.server.filesys.quota;

/*
 * QuotaManagerException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Quota Manager Exception Class
 */
public class QuotaManagerException extends Exception {

  private static final long serialVersionUID = -5561266951484827919L;

  /**
	 * Default constructor
	 */
	public QuotaManagerException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public QuotaManagerException(String msg) {
		super(msg);
	}
}
