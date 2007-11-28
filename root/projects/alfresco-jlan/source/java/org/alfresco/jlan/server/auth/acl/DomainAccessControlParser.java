package org.alfresco.jlan.server.auth.acl;

/*
 * DomainAccessControlParser.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.config.ConfigElement;

/**
 * Domain Name Access Control Parser Class
 */
public class DomainAccessControlParser extends AccessControlParser {

	/**
	 * Default constructor
	 */
	public DomainAccessControlParser() {
	}
	
	/**
	 * Return the parser type
	 * 
	 * @return String
	 */
	public String getType() {
		return "domain";
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
		
		//	Get the domain name to check for
		
		ConfigElement val = params.getChild("name");
		if ( val == null || val.getValue().length() == 0)
			throw new ACLParseException("Domain name not specified");
			
		String domainName = val.getValue().trim();
		if ( domainName.length() == 0)
			throw new ACLParseException("Domain name not valid");
			
		//	Create the domain access control
		
		return new DomainAccessControl(val.getValue(), getType(), access);
	}
}
