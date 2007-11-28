package org.alfresco.jlan.server.auth.acl;

/*
 * InvalidACLTypeException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 *	Invalid ACL Type Exception Class
 */
public class InvalidACLTypeException extends Exception {

  private static final long serialVersionUID = -6324691061028222442L;

  /**
	 * Default constructor.
	 */
	public InvalidACLTypeException() {
		super();
	}

	/**
	 * Class constructor.
	 * 
	 * @param s java.lang.String
	 */
	public InvalidACLTypeException(String s) {
		super(s);
	}
}
