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

package org.alfresco.jlan.server.filesys.db;

import org.alfresco.jlan.server.filesys.FileInfo;

/**
 * Database File Information Class
 *
 * @author gkspencer
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
