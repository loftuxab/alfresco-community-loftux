package org.alfresco.jlan.smb.nt;

/*
 * LoadException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Load Exception Class
 * 
 * <p>Thrown when an error occurs loading an ACE, ACL, SID or security descriptor from a buffer.
 */
public class LoadException extends Exception {

  private static final long serialVersionUID = 1593103389281754446L;

  /**
	 * Default constructor
	 */
	public LoadException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public LoadException(String msg) {
		super(msg);
	}
}
