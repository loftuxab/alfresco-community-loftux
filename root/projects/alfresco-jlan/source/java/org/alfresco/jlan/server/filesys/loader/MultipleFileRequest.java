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

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple File Request Class
 * 
 * <p>Contains the details of a transaction of multiple file requests.
 *
 * @author gkspencer
 */
public class MultipleFileRequest extends FileRequest {

	//	List of cached files that are part of this transaction
	
	private List<CachedFileInfo> m_files;
	
	/**
	 * Class constructor
	 * 
	 * @param typ int
	 * @param tranId int
	 */
	public MultipleFileRequest(int typ, int tranId) {
		super(typ);
		setTransactionId(tranId);
		
		//	Allocate the file list
		
		m_files = new ArrayList<CachedFileInfo>();
	}
	
	/**
	 * Return the number of files in this request
	 * 
	 * @return int
	 */
	public final int getNumberOfFiles() {
		return m_files.size();
	}
	
	/**
	 * Get file details for the specified file
	 * 
	 * @param idx int
	 * @return CachedFileInfo
	 */
	public final CachedFileInfo getFileInfo(int idx) {
		if ( idx > m_files.size())
			return null;
		return m_files.get(idx);
	}
	
	/**
	 * Add a file to this request
	 * 
	 * @param finfo CachedFileInfo
	 */
	public final void addFileInfo(CachedFileInfo finfo) {
		m_files.add(finfo);
	}

	/**
	 * Return the file request as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		
		if ( isTransaction()) {
			str.append("[Tran=");
			str.append(getTransactionId());
		}
		
		str.append(",Files=");
		str.append(getNumberOfFiles());
		
    if ( hasAttributes()) {
      str.append(",Attr=");
      str.append( getAttributes());
    }
    
		str.append("]");
		
		return str.toString();
	}
}
