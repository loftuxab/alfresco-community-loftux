package org.alfresco.jlan.server.filesys.loader;

/*
 * NamedFileLoader.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;

import org.alfresco.jlan.server.filesys.FileInfo;


/**
 * Named File Loader Interface
 * 
 * <p>The NamedFileLoader adds methods that are required to keep track of directory trees and renaming of
 * files/directories by a FileLoader.
 */
public interface NamedFileLoader {

	/**
	 * Create a directory
	 * 
	 * @param dir String
	 * @param fid int
	 * @exception IOException
	 */
	public void createDirectory(String dir, int fid)
		throws IOException;
		
	/**
	 * Delete a directory
	 * 
	 * @param dir String
	 * @param fid int
	 * @exception IOException
	 */
	public void deleteDirectory(String dir, int fid)
		throws IOException;
		
	/**
	 * Rename a file or directory
	 * 
	 * @param curName String
	 * @param fid int
	 * @param newName String
	 * @param isdir boolean
	 * @exception IOException
	 */
	public void renameFileDirectory(String curName, int fid, String newName, boolean isdir)
		throws IOException;
		
	/**
	 * Change file attributes/settings
	 * 
	 * @param path String
	 * @param fid int
	 * @param finfo FileInfo
	 * @exception IOException
	 */
	public void setFileInformation(String path, int fid, FileInfo finfo)
		throws IOException;
}
