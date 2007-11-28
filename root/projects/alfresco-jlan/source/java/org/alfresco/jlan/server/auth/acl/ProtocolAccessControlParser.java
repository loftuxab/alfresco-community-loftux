package org.alfresco.jlan.server.auth.acl;

/*
 * ProtocolAccessControlParser.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.config.ConfigElement;

/**
 * Protocol Access Control Parser Class
 */
public class ProtocolAccessControlParser extends AccessControlParser {

	/**
	 * Default constructor
	 */
	public ProtocolAccessControlParser() {
	}
	
	/**
	 * Return the parser type
	 * 
	 * @return String
	 */
	public String getType() {
		return "protocol";
	}

	/**
	 * Validate the parameters and create a user access control
	 * 
	 * @param params ConfigElement
	 * @return AccessControl
	 * @throws ACLParseException
	 */
	public AccessControl createAccessControl(ConfigElement params)
		throws ACLParseException {
			
		//	Get the access type
	
		int access = parseAccessType(params);
	
		//	Get the list of protocols to check for
	
		ConfigElement val = params.getChild("type");
		if ( val == null || val.getValue().length() == 0)
			throw new ACLParseException("Protocol type not specified");
		
		String protList = val.getValue().trim();
		if ( protList.length() == 0)
			throw new ACLParseException("Protocol type not valid");
		
		//	Validate the protocol list
		
		if ( ProtocolAccessControl.validateProtocolList(protList) == false)
			throw new ACLParseException("Invalid protocol type");
		 
		//	Create the protocol access control
	
		return new ProtocolAccessControl(protList, getType(), access);
	}
}
