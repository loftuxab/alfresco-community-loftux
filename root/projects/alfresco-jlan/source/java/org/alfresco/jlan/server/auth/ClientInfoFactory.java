package org.alfresco.jlan.server.auth;

/*
 * ClientInfoFactory.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 *	Client Info Factory Interface
 */

public interface ClientInfoFactory {

	/**
	 * Create a new client information object
	 * 
	 * @param user String
	 * @param password byte[]
	 * @return ClientInfo
	 */
	public ClientInfo createInfo(String user, byte[] password);
}
