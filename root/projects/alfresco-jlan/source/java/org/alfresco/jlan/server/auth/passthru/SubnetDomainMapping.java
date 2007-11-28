package org.alfresco.jlan.server.auth.passthru;

/*
 * SubnetDomainMapping.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.util.IPAddress;

/**
 * Subnet Domain Mapping Class
 *
 * @author gkspencer
 */
public class SubnetDomainMapping extends DomainMapping {

	// Subnet and mask for the domain
	
	private int m_subnet;
	private int m_mask;
	
	/**
	 * class constructor
	 * 
	 * @param domain String
	 * @param subnet int
	 * @param mask int
	 */
	public SubnetDomainMapping( String domain, int subnet, int mask)
	{
		super( domain);
		
		m_subnet = subnet;
		m_mask   = mask;
	}
	
	/**
	 * Return the subnet
	 * 
	 * @return int
	 */
	public final int getSubnet()
	{
		return m_subnet;
	}
	
	/**
	 * Return the subnet mask
	 * 
	 * @return int
	 */
	public final int getSubnetMask()
	{
		return m_mask;
	}
	
	/**
	 * Check if the client address is a member of this domain
	 * 
	 * @param clientIP int
	 * @return boolean
	 */
	public boolean isMemberOfDomain( int clientIP)
	{
		if (( clientIP & m_mask) == m_subnet)
			return true;
		return false;
	}
	
	/**
	 * Return the domain mapping as a string
	 * 
	 * @return String
	 */
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		str.append("[");
		str.append(getDomain());
		str.append(",");
		str.append(IPAddress.asString( getSubnet()));
		str.append(":");
		str.append(IPAddress.asString( getSubnetMask()));
		str.append("]");
		
		return str.toString();
	}
}
