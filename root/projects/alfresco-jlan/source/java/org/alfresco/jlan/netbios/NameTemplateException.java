package org.alfresco.jlan.netbios;

/*
 * NameTemplateException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Name Template Exception Class
 * 
 * <p>Thrown when a NetBIOS name template contains invalid characters or is too long.
 */
public class NameTemplateException extends Exception {

  private static final long serialVersionUID = -1647718559236829109L;

  /**
	 * Default constructor.
	 */
	public NameTemplateException() {
		super();
	}

	/**
	 * Class constructor
	 * 
	 * @param s java.lang.String
	 */
	public NameTemplateException(String s) {
		super(s);
	}
}
