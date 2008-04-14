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
 * Delete File Request Class
 * 
 * <p>Contains the details of a file that has been marked for delete when the database is offline.
 *
 * @author gkspencer
 */
public class DeleteFileRequest extends FileRequest {

  //  File id and stream id
  
  private int m_fid;
  private int m_stid;
  
  //  Temporary file path
  
  private String m_tempPath;
    
  //  Virtual path of file
  
  private String m_virtPath;

  //  Associated file state
  
  private FileState m_state;
  
  /**
   * Class constructor
   * 
   * @param fid int
   * @param stid int
   * @param tempPath String
   * @param virtPath String
   * @param state FileState
   */
  public DeleteFileRequest(int fid, int stid, String tempPath, String virtPath, FileState state) {
    super(FileRequest.DELETE);

    m_fid      = fid;
    m_stid     = stid;
    m_tempPath = tempPath;
    m_virtPath = virtPath;
    m_state    = state;
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
        
    str.append(",DELETE:");

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
