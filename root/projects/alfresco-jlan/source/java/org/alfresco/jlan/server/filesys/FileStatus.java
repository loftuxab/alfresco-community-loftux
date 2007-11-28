package org.alfresco.jlan.server.filesys;

/*
 * FileStatus.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * File Status Class
 */
public class FileStatus {

	//	File status constants
	
  public final static int Unknown					= -1;
	public final static int NotExist				= 0;
	public final static int FileExists			= 1;
	public final static int DirectoryExists	= 2;
	
	/**
	 * Return the file status as a string
	 * 
	 * @param sts int
	 * @return String
	 */
	public final static String asString(int sts) {
		
		//	Convert the status to a string
		
		String ret = "";
		
		switch (sts) {
			case Unknown:
			  ret = "Unknown";
			  break;
			case NotExist:
				ret = "NotExist";
				break;
			case FileExists:
				ret = "FileExists";
				break;
			case DirectoryExists:
				ret = "DirExists";
				break;
		}
		
		return ret;
	}
}
