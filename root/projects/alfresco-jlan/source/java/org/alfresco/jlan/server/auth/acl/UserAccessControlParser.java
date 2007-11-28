package org.alfresco.jlan.server.auth.acl;

/*
 * UserAccessControlParser.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.config.ConfigElement;

/**
 * User Access Control Parser Class
 */
public class UserAccessControlParser extends AccessControlParser {

	/**
	 * Default constructor
	 */
	public UserAccessControlParser() {
	}
	
	/**
	 * Return the parser type
	 * 
	 * @return String
	 */
	public String getType() {
		return "user";
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
		
		//	Get the user name to check for
		
		ConfigElement val = params.getChild("name");
		if ( val == null || val.getValue().length() == 0)
			throw new ACLParseException("User name not specified");
			
		String userName = val.getValue().trim();
		if ( userName.length() == 0)
			throw new ACLParseException("User name not valid");
			
		//	Create the user access control
		
		return new UserAccessControl(val.getValue(), getType(), access);
	}
}
