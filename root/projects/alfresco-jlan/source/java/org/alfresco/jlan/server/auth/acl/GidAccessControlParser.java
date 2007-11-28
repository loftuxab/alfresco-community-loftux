package org.alfresco.jlan.server.auth.acl;

/*
 * GidAccessControlParser.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.config.ConfigElement;

/**
 *	Group Id Access Control Parser Class
 */
public class GidAccessControlParser extends AccessControlParser {

  /**
   * Default constructor
   */
  public GidAccessControlParser() {
  }
  
  /**
   * Return the parser type
   * 
   * @return String
   */
  public String getType() {
    return "gid";
  }

  /**
	 * Validate the parameters and create a group id access control
	 * 
	 * @param params ConfigElement
	 * @return AccessControl
	 * @throws ACLParseException
   */
  public AccessControl createAccessControl(ConfigElement params)
  	throws ACLParseException {
		
		//	Get the access type
		
		int access = parseAccessType(params);
		
		//	Get the group id to check for
		
		ConfigElement val = params.getChild("id");
		if ( val == null || val.getValue().length() == 0)
			throw new ACLParseException("Group id not specified");
		
		//	Validate the group id
		
		String groupId = val.getValue().trim();
		int gid = -1;
		
		try {
		  gid = Integer.parseInt(groupId);
		  
		  if ( gid < 0 || gid > 32768)
		    throw new ACLParseException("Invalid group id, out of valid range");
		}
		catch (NumberFormatException ex) {
			throw new ACLParseException("Group id not valid");
		}
			
		//	Create the group id access control
		
		return new GidAccessControl(groupId, gid, getType(), access);
  }
}
