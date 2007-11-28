package org.alfresco.jlan.oncrpc.nfs;

/*
 * BadCookieException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Bad Cookie Exception Class
 */
public class BadCookieException extends Exception {

  private static final long serialVersionUID = -1889652677960375867L;

  /**
	 * Default constructor
	 */
	public BadCookieException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public BadCookieException(String msg) {
		super(msg);
	}
}
