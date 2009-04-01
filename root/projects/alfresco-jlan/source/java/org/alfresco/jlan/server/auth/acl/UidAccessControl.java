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
 * User Id Access Control Class
 * 
 * <p>Allow/disallow access to a shared device by checking the Unix user id of the client.
 *
 * @author gkspencer
 */
public class UidAccessControl extends AccessControl {

  //	User id to check for
  
  private int m_uid;
  
    /**
     * Default constructor to allow container initialization.
     */
    protected UidAccessControl()
    {        
    }
    
    public void setUid(int uid)
    {
        this.m_uid = uid;
    }

    /**
	 * Class constructor
	 *
	 * @param uidStr String
	 * @param uid int
	 * @param type String
	 * @param access int 
	 */	
	protected UidAccessControl(String uidStr, int uid, String type, int access) {
		super(uidStr, type, access);
		
		//	Set the required user id
		
		setUid(uid);
	}
  

  /**
	 * Check if the session is an RPC session (NFS/mount) and the client has the required Unix user id.
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
		
		if ( cInfo.getUid() != -1 && cInfo.getUid() == m_uid)
			return getAccess();
		return Default;			
  }
}
