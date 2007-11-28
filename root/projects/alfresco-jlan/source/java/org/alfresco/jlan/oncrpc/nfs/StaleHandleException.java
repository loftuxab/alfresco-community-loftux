package org.alfresco.jlan.oncrpc.nfs;

/*
 * StaleHandleException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Stale Handle Exception Class
 */
public class StaleHandleException extends Exception {

  private static final long serialVersionUID = -6397758549941044528L;

  /**
	 * Default constructor
	 */
	public StaleHandleException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public StaleHandleException(String msg) {
		super(msg);
	}
}
