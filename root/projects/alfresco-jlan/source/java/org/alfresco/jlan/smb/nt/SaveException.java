package org.alfresco.jlan.smb.nt;

/*
 * SaveException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Save Exception Class
 * 
 * <p>Thrown when an error occurs saving an ACE, ACL, SID or security descriptor to a buffer.
 */
public class SaveException extends Exception {

  private static final long serialVersionUID = -4577888276843040784L;

  /**
	 * Default constructor
	 */
	public SaveException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public SaveException(String msg) {
		super(msg);
	}
}
