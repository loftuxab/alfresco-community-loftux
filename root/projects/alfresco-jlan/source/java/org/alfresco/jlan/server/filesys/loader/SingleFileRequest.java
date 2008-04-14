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

import org.alfresco.jlan.server.filesys.cache.FileState;

/**
 * Single File Request Class
 * 
 * <p>Contains the details of a single file load or save request.
 *
 * @author gkspencer
 */
public class SingleFileRequest extends FileRequest {

	//	File id and stream id
	
	private int m_fid;
	private int m_stid;
	
	//	Unique request id
	
	private int m_seqNo = -1;

	//	Temporary file path
	
	private String m_tempPath;
		
	//	Virtual path of file
	
	private String m_virtPath;

	//	Associated file state
	
	private FileState m_state;
	
	/**
	 * Class constructor
	 * 
	 * @param typ int
	 * @param fid int
	 * @param stid int
	 * @param tempPath String
	 * @param virtPath String
	 * @param state FileState
	 */
	public SingleFileRequest(int typ, int fid, int stid, String tempPath, String virtPath, FileState state) {
		super(typ);
		m_fid      = fid;
		m_stid     = stid;
		m_tempPath = tempPath;
		m_virtPath = virtPath;
		m_state    = state;
	}
	
	/**
	 * Class constructor
	 * 
	 * @param typ int
	 * @param fid int
	 * @param stid int
	 * @param segInfo FileSegmentInfo
	 * @param virtPath String
	 * @param state FileState
	 */
	public SingleFileRequest(int typ, int fid, int stid, FileSegmentInfo segInfo, String virtPath, FileState state) {
		super(typ);
		m_fid      = fid;
		m_stid     = stid;
		m_tempPath = segInfo.getTemporaryFile();
		m_virtPath = virtPath;
		m_state    = state;
		
		//	Mark the file segment as queued
		
		segInfo.setQueued(true);
	}

	/**
	 * Class constructor
	 * 
	 * @param typ int
	 * @param fid int
	 * @param stid int
	 * @param tempPath String
	 * @param virtPath String
	 * @param seq int
	 * @param state FileState
	 */
	public SingleFileRequest(int typ, int fid, int stid, String tempPath, String virtPath, int seq, FileState state) {
		super(typ);
		m_fid      = fid;
		m_stid     = stid;
		m_tempPath = tempPath;
		m_virtPath = virtPath;
		m_seqNo    = seq;
	}
	
	/**
	 * Return the file identifier
	 * 
	 * @return int
	 */
	public final int getFileId() {
		return m_fid;
	}

	/**
	 * Return the stream identifier, zero for the main file stream
	 * 
	 * @return int
	 */
	public final int getStreamId() {
		return m_stid;
	}
	
	/**
	 * Return the unique request id
	 * 
	 * @return int
	 */
	public final int getSequenceNumber() {
		return m_seqNo;
	}
		
	/**
	 * Return the files virtual path
	 * 
	 * @return String
	 */
	public final String getVirtualPath() {
		return m_virtPath;
	}
	
	/**
	 * Return the temporary file path
	 * 
	 * @return String
	 */
	public final String getTemporaryFile() {
		return m_tempPath;
	}

	/**
	 * Check if the request has an associated file state
	 * 
	 * @return boolean
	 */
	public final boolean hasFileState () {
		return m_state != null ? true : false;
	}

	/**
	 * Return the associated file state
	 * 
	 * @return FileState
	 */
	public final FileState getFileState() {
		return m_state;
	}
		
	/**
	 * Set the associated file state for the request
	 * 
	 * @param state FileState
	 */
	public final void setFileState(FileState state) {
		m_state = state;
	}
		
	/**
	 * Set the request unique id
	 * 
	 * @param id int
	 */
	public final void setSequenceNumber(int id) {
		m_seqNo = id;
	}

	/**
	 * Set the files virtual path
	 * 
	 * @param path String
	 */
	public final void setVirtualPath(String path) {
		m_virtPath = path;
	}

	/**
	 * Return the file request as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		
		str.append("[FID=");
		str.append(getFileId());
		str.append(",STID=");
		str.append(getStreamId());
		str.append(",Seq=");
		str.append(getSequenceNumber());
				
		if ( isTransaction()) {
			str.append(",Tran=");
			str.append(getTransactionId());
			
			if ( isLastTransactionFile())
				str.append("(Last)");
		}
		
		if ( isType() == LOAD)
			str.append(",LOAD:");
		else
			str.append(",SAVE:");

		str.append(getTemporaryFile());
		str.append(",");
		str.append(getVirtualPath());
		
		str.append(",State=");
		str.append(getFileState());
		
    if ( hasAttributes()) {
      str.append(",Attr=");
      str.append( getAttributes());
    }
    
		str.append("]");
		
		return str.toString();
	}
}
