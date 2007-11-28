package org.alfresco.jlan.server.auth.acl;

/*
 * GidAccessControl.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * Group Id Access Control Class
 * 
 * <p>Allow/disallow access to a shared device by checking the Unix group ids of the client.
 */
public class GidAccessControl extends AccessControl {

  //	Group id to check for
  
  private int m_gid;
  
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
