package org.alfresco.jlan.oncrpc.nfs;

/*
 * BadHandleException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Bad Handle Exception Class
 */
public class BadHandleException extends Exception {

  private static final long serialVersionUID = 5373805930325929139L;

  /**
	 * Default constructor
	 */
	public BadHandleException() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param msg String
	 */
	public BadHandleException(String msg) {
		super(msg);
	}
}
