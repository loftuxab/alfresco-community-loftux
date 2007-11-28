package org.alfresco.jlan.server.auth.acl;

/*
 * UidAccessControlParser.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.config.ConfigElement;

/**
 *	User Id Access Control Parser Class
 */
public class UidAccessControlParser extends AccessControlParser {

  /**
   * Default constructor
   */
  public UidAccessControlParser() {
  }
  
  /**
   * Return the parser type
   * 
   * @return String
   */
  public String getType() {
    return "uid";
  }

  /**
	 * Validate the parameters and create a user id access control
	 * 
	 * @param params ConfigElement
	 * @return AccessControl
	 * @throws ACLParseException
   */
  public AccessControl createAccessControl(ConfigElement params)
  	throws ACLParseException {
		
		//	Get the access type
		
		int access = parseAccessType(params);
		
		//	Get the user id to check for
		
		ConfigElement val = params.getChild("id");
		if ( val == null || val.getValue().length() == 0)
			throw new ACLParseException("User id not specified");
		
		//	Validate the user id
		
		String userId = val.getValue().trim();
		int uid = -1;
		
		try {
		  uid = Integer.parseInt(userId);
		  
		  if ( uid < 0 || uid > 32768)
		    throw new ACLParseException("Invalid user id, out of valid range");
		}
		catch (NumberFormatException ex) {
			throw new ACLParseException("User id not valid");
		}
			
		//	Create the user id access control
		
		return new UidAccessControl(userId, uid, getType(), access);
  }
}
