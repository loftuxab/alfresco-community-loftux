package org.alfresco.jlan.server.auth.acl;

/*
 * UserAccessControl.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * User Access Control Class
 * 
 * <p>Allow/disallow access to a shared device by checking the user name.
 */
public class UserAccessControl extends AccessControl {

	/**
	 * Class constructor
	 *
	 * @param userName String
	 * @param type String
	 * @param access int 
	 */	
	protected UserAccessControl(String userName, String type, int access) {
		super(userName, type, access);
	}
	
	/**
	 * Check if the user name matches the access control user name and return the allowed access.
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

		//	Check if the user name matches the access control name
		
		ClientInfo cInfo = sess.getClientInformation();
		
		if ( cInfo.getUserName() != null && cInfo.getUserName().equalsIgnoreCase(getName()))
			return getAccess();
		return Default;			
	}
}
