package org.alfresco.jlan.smb.dcerpc;

/*
 * DCEBufferException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * DCE Buffer Exception Class
 */
public class DCEBufferException extends Exception {

  private static final long serialVersionUID = -1828486773104314960L;

  /**
	 * Class constructor
	 */
	public DCEBufferException() {
	  super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param str String
	 */
	public DCEBufferException(String str) {
	  super(str);
	}
}
