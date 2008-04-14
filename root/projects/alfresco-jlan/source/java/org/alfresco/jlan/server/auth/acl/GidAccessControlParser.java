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
 *	Group Id Access Control Parser Class
 *
 * @author gkspencer
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
