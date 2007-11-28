package org.alfresco.jlan.smb;

/*
 * SharingMode.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * File Sharing Mode Class
 * 
 * <p>Defines sharing mode constants used when opening a file via the CIFSDiskSession.NTCreate() method.
 */
public class SharingMode {

	//	File sharing mode constants
	
	public final static int NOSHARING = 0x0000;
	public final static int READ			= 0x0001;
	public final static int WRITE			= 0x0002;
	public final static int DELETE		= 0x0004;
	
	public final static int READWRITE	= READ + WRITE;
}
