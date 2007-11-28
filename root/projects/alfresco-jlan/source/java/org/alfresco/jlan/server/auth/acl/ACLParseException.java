package org.alfresco.jlan.server.auth.acl;

/*
 * ACLParseException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Access Control Parse Exception Class
 */
public class ACLParseException extends Exception {

  private static final long serialVersionUID = -2973165291611645733L;

  /**
	 * Default constructor.
	 */
	public ACLParseException() {
		super();
	}

	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public ACLParseException(String s) {
		super(s);
	}
}
