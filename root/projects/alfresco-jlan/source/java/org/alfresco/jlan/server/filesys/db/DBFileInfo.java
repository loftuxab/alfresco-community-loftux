package org.alfresco.jlan.server.filesys.db;

/*
 * DBFileInfo.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.filesys.FileInfo;

/**
 * Database File Information Class
 */
public class DBFileInfo extends FileInfo {

	//	Full file name
	
	private String m_fullName;
	
	/**
	 * Class constructor
	 */
	public DBFileInfo() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param fullName String
	 * @param fid int
	 * @param did int
	 */
	public DBFileInfo(String name, String fullName, int fid, int did) {
		super();
		setFileName(name);
		setFullName(fullName);
		setFileId(fid);
		setDirectoryId(did);
	}
	
	/**
	 * Return the full file path
	 * 
	 * @return String
	 */
	public final String getFullName() {
		return m_fullName;
	}
		
	/**
	 * Set the full file path
	 * 
	 * @param name String
	 */
	public final void setFullName(String name) {
		m_fullName = name;
	}

	/**
	 * Return the file information as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		
		str.append("[");
		str.append(super.toString());
		str.append(" - FID=");
		str.append(getFileId());
		str.append(",DID=");
		str.append(getDirectoryId());
		str.append("]");
		
		return str.toString();
	}
}
