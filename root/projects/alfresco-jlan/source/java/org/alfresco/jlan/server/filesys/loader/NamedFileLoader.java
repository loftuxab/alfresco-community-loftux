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

package org.alfresco.jlan.server.filesys.loader;

import java.io.IOException;

import org.alfresco.jlan.server.filesys.FileInfo;


/**
 * Named File Loader Interface
 * 
 * <p>The NamedFileLoader adds methods that are required to keep track of directory trees and renaming of
 * files/directories by a FileLoader.
 *
 * @author gkspencer
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
