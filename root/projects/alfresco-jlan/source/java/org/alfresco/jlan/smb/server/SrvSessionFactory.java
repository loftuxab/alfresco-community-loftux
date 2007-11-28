package org.alfresco.jlan.smb.server;

/*
 * SrvSessionFactory.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Server Session Factory Interface
 */
public interface SrvSessionFactory {

	/**
	 * Create a new server session object
	 * 
	 * @param handler PacketHandler
	 * @param server SMBServer
   * @param sessId int
	 * @return SMBSrvSession
	 */
	public SMBSrvSession createSession(PacketHandler handler, SMBServer server, int sessId);
}
