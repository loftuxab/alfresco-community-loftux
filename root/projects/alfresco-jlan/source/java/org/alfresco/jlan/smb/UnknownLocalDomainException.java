package org.alfresco.jlan.smb;

/*
 * UnknownLocalDomainException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Unknown local domain exception.
 *
 *  <p>The UnknownLocalDomainException is thrown when the local domain name cannot be determined,
 *	this may be due to the local node not actually running a Windows networking/SMB server.
 */
public class UnknownLocalDomainException extends Exception {

  private static final long serialVersionUID = 87619071488565868L;

  /**
	 * UnknownLocalDomainException constructor comment.
	 */
	public UnknownLocalDomainException() {
	  super();
	}
	
	/**
	 * UnknownLocalDomainException constructor comment.
	 * 
	 * @param s java.lang.String
	 */
	public UnknownLocalDomainException(String s) {
	  super(s);
	}
}