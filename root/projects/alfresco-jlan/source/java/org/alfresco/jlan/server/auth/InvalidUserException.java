package org.alfresco.jlan.server.auth;

/*
 * InvalidUserException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Invalid User Exception Class
 */
public class InvalidUserException extends Exception {

  private static final long serialVersionUID = -1842012611078254904L;

  /**
	 * Default constructor.
	 */
	public InvalidUserException() {
		super();
	}

	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public InvalidUserException(String s) {
		super(s);
	}
}
