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

package org.alfresco.jlan.server.auth.acl;

import org.alfresco.jlan.util.IPAddress;
import org.springframework.extensions.config.ConfigElement;

/**
 * Ip Address Access Control Parser Class
 *
 * @author gkspencer
 */
public class IpAddressAccessControlParser extends AccessControlParser {

	/**
	 * Default constructor
	 */
	public IpAddressAccessControlParser() {
	}
	
	/**
	 * Return the parser type
	 * 
	 * @return String
	 */
	public String getType() {
		return "address";
	}

	/**
	 * Validate the parameters and create an address access control
	 * 
	 * @param params ConfigElement
	 * @return AccessControl
	 * @throws ACLParseException
	 */
	public AccessControl createAccessControl(ConfigElement params)
		throws ACLParseException {

		//	Get the access type
	
		int access = parseAccessType(params);
	
		//	Check if the single IP address format has been specified
		
		String val = params.getAttribute("ip");
		if ( val != null) {
			
			//	Validate the parameters
			
			if ( val.length() == 0 || IPAddress.isNumericAddress(val) == false)
				throw new ACLParseException("Invalid IP address, " + val);
				
			if ( params.getAttributeCount() != 2)
				throw new ACLParseException("Invalid parameter(s) specified for address");
				
			//	Create a single TCP/IP address access control rule
			
			return new IpAddressAccessControl(val, null, getType(), access);
		}
		
		//	Check if a subnet address and mask have been specified
		
		val = params.getAttribute("subnet");
		if ( val != null) {
			
			//	Get the network mask parameter
			
			String maskVal = params.getAttribute("mask");
			
			//	Validate the parameters
			
			if ( maskVal.length() == 0 || maskVal == null)
				throw new ACLParseException("Invalid subnet/mask parameter");
				
			if ( IPAddress.isNumericAddress(val) == false)
				throw new ACLParseException("Invalid subnet parameter, " + val);
				
			if ( IPAddress.isNumericAddress(maskVal) == false)
				throw new ACLParseException("Invalid mask parameter, " + maskVal);
				
			//	Create a subnet address access control rule
			
			return new IpAddressAccessControl(val, maskVal, getType(), access);
		}
		
		//	Invalid parameters
		
		throw new ACLParseException("Unknown address parameter(s)");
	}
}
