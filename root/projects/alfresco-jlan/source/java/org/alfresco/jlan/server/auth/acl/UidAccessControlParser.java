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

import org.alfresco.config.ConfigElement;

/**
 *	User Id Access Control Parser Class
 *
 * @author gkspencer
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
