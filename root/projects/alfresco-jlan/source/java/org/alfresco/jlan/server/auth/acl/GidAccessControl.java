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

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * Group Id Access Control Class
 * 
 * <p>Allow/disallow access to a shared device by checking the Unix group ids of the client.
 *
 * @author gkspencer
 */
public class GidAccessControl extends AccessControl {

  //	Group id to check for
  
  private int m_gid;
  
    /**
     * Default constructor to allow container initialization.
     */
    protected GidAccessControl()
    {        
    }
    
	public void setGid(int gid)
    {
        m_gid = gid;
    }

    /**
	 * Class constructor
	 *
	 * @param gidStr String
	 * @param gid int
	 * @param type String
	 * @param access int 
	 */	
	protected GidAccessControl(String gidStr, int gid, String type, int access) {
		super(gidStr, type, access);
		
		//	Set the required group id
		
		m_gid = gid;
	}
  
  /**
	 * Check if the session is an RPC session (NFS/mount) and the client is a member of the required
	 * group.
	 * 
	 * @param sess SrvSession
	 * @param share SharedDevice
	 * @param mgr AccessControlManager
	 * @return int
   */
  public int allowsAccess(SrvSession sess, SharedDevice share, AccessControlManager mgr) {
		
		//	Check if the session has client information
		
		if ( sess.hasClientInformation() == false)
			return Default;

		//	Check if the client main group id is set and matches the required group id
		
		ClientInfo cInfo = sess.getClientInformation();
		
		if ( cInfo.getGid() != -1 && cInfo.getGid() == m_gid)
			return getAccess();
		
		//	Check if the client has a group list, if so check if any of the group match the required group id
		
		if ( cInfo.hasGroupsList()) {
		  
		  //	Get the groups list and check for a matching group id
		  
		  int[] groups = cInfo.getGroupsList();
		  
		  for ( int i = 0; i < groups.length; i++) {
		    if ( groups[i] == m_gid)
		      return getAccess();
		  }
		}
		return Default;			
  }
}
