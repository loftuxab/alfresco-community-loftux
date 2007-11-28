package org.alfresco.jlan.smb.dcerpc;

/*
 * DCEException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * DCE/RPC Exception Class
 */
public class DCEException extends Exception {

  private static final long serialVersionUID = -931481018707190373L;

  /**
	 * Class constructor
	 * 
	 * @param str String
	 */
	public DCEException(String str) {
	  super(str);
	}
}
