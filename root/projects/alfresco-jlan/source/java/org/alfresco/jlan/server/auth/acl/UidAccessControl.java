package org.alfresco.jlan.server.auth.acl;

/*
 * UidAccessControl.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * User Id Access Control Class
 * 
 * <p>Allow/disallow access to a shared device by checking the Unix user id of the client.
 */
public class UidAccessControl extends AccessControl {

  //	User id to check for
  
  private int m_uid;
  
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
		
		m_uid = uid;
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
