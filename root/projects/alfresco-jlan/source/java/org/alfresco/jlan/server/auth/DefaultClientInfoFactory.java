package org.alfresco.jlan.server.auth;

/*
 * DefaultClientInfoFactory.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Default Client Information Object Factory Class
 */
public class DefaultClientInfoFactory implements ClientInfoFactory {

	/**
	 * Create a client information object
	 * 
	 * @param user String
	 * @param password byte[]
	 * @return ClientInfo
	 */
	public ClientInfo createInfo(String user, byte[] password) {
		return new ClientInfo(user, password);
	}
}
