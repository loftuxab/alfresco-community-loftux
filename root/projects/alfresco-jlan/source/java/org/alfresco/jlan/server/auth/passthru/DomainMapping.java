package org.alfresco.jlan.server.auth.passthru;

/*
 * DomainMapping.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */
 

/**
 * Domain Mapping Class
 *
 * @author gkspencer
 */
public abstract class DomainMapping {

	// Domain name
	
	private String m_domain;
	
	/**
	 * Class consructor
	 * 
	 * @param domain String
	 */
	public DomainMapping( String domain)
	{
		m_domain = domain;
	}
	
	/**
	 * Return the domain name
	 * 
	 * @return String
	 */
	public final String getDomain()
	{
		return m_domain;
	}
	
	/**
	 * Check if the client address is a member of this domain
	 * 
	 * @param clientIP int
	 * @return boolean
	 */
	public abstract boolean isMemberOfDomain( int clientIP);
}
