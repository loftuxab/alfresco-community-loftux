package org.alfresco.jlan.smb.server;

/*
 * DefaultSrvSessionFactory.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Default Server Session Factory Class
 */
public class DefaultSrvSessionFactory implements SrvSessionFactory {

	/**
	 *	Create a server session object
	 * 
	 * @param handler PacketHandler
	 * @param server SMBServer
   * @param sessId int
	 * @return SMBSrvSession
	 */
	public SMBSrvSession createSession(PacketHandler handler, SMBServer server, int sessId) {
    
    // Create a new SMB session
    
    SMBSrvSession sess = new SMBSrvSession(handler, server);
    
    sess.setSessionId(sessId);
    sess.setUniqueId(handler.getShortName() + sess.getSessionId());
    sess.setDebugPrefix("[" + handler.getShortName() + sessId + "] ");

    //  Add the session to the active session list

    server.addSession(sess);
    
    // Return the new session
    
    return sess;
	}
}
