package org.alfresco.jlan.server.auth.passthru;

/*
 * PassthruDetails.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.SrvSession;

/**
 * Passthru Details Class
 * 
 * <p>Contains the details of a passthru connection to a remote server and the local session that the
 * request originated from.
 */
public class PassthruDetails {

	//	Server session
	
	private SrvSession m_sess;

	//	Authentication session connected to the remote server
	
	private AuthenticateSession m_authSess;	
		
	/**
	 * Class constructor
	 *
	 * @param sess SrvSession
	 * @param authSess AuthenticateSession
	 */
	public PassthruDetails(SrvSession sess, AuthenticateSession authSess) {
		m_sess     = sess;
		m_authSess = authSess;
	}

	/**
	 * Return the session details
	 * 
	 * @return SrvSession
	 */
	public final SrvSession getSession() {
		return m_sess;
	}
	
	/**
	 * Return the authentication session that is connected to the remote server
	 * 
	 * @return AuthenticateSession
	 */
	public final AuthenticateSession getAuthenticateSession() {
		return m_authSess;	
	}
}
