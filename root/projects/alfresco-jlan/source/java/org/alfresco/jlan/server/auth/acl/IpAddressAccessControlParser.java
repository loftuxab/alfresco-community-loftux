package org.alfresco.jlan.server.auth.acl;

/*
 * IpAddressAccessControlParser.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.jlan.util.IPAddress;
import org.alfresco.config.ConfigElement;

/**
 * Ip Address Access Control Parser Class
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
		
		ConfigElement val = params.getChild("ip");
		if ( val != null) {
			
			//	Validate the parameters
			
			if ( val.getValue().length() == 0 || IPAddress.isNumericAddress(val.getValue()) == false)
				throw new ACLParseException("Invalid IP address, " + val.getValue());
				
			if ( params.getChildCount() != 2)
				throw new ACLParseException("Invalid parameter(s) specified for address");
				
			//	Create a single TCP/IP address access control rule
			
			return new IpAddressAccessControl(val.getValue(), null, getType(), access);
		}
		
		//	Check if a subnet address and mask have been specified
		
		val = params.getChild("subnet");
		if ( val != null) {
			
			//	Get the network mask parameter
			
			ConfigElement maskVal = params.getChild("mask");
			
			//	Validate the parameters
			
			if ( val.getValue().length() == 0 || maskVal == null || maskVal.getValue().length() == 0)
				throw new ACLParseException("Invalid subnet/mask parameter");
				
			if ( IPAddress.isNumericAddress(val.getValue()) == false)
				throw new ACLParseException("Invalid subnet parameter, " + val.getValue());
				
			if ( IPAddress.isNumericAddress(maskVal.getValue()) == false)
				throw new ACLParseException("Invalid mask parameter, " + maskVal.getValue());
				
			//	Create a subnet address access control rule
			
			return new IpAddressAccessControl(val.getValue(), maskVal.getValue(), getType(), access);
		}
		
		//	Invalid parameters
		
		throw new ACLParseException("Unknown address parameter(s)");
	}
}
