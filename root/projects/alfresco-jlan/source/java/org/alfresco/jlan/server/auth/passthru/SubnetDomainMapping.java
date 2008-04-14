/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.server.auth.passthru;

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
