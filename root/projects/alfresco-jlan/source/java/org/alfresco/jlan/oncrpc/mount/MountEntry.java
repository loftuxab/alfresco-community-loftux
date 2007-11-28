package org.alfresco.jlan.oncrpc.mount;

/*
 * MountEntry.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Mount Entry Class
 * 
 * <p>Contains the details of an active NFS mount.
 */
public class MountEntry {

	//	Remote host name/address
		
	private String m_host;
		
	//	Mount path
		
	private String m_path;
				
	/**
	 * Class constructor
	 * 
	 * @param host String
	 * @param path String
	 */
	public MountEntry(String host, String path) {
		m_host = host;
		m_path = path;
	}
	
	/**
	 * Return the host name/address
	 * 
	 * @return String
	 */
	public final String getHost() {
		return m_host;
	}
	
	/**
	 * Return the mount path
	 *
	 * @return String
	 */
	public final String getPath() {
		return m_path;
	}
	
	/**
	 * Return the mount entry as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		
		str.append("[");
		str.append(getHost());
		str.append(":");
		str.append(getPath());
		str.append("]");
		
		return str.toString();
	}
}
