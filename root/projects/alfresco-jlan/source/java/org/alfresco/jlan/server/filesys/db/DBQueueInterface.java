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

import java.io.File;

import org.alfresco.jlan.server.filesys.loader.FileRequest;
import org.alfresco.jlan.server.filesys.loader.FileRequestQueue;
import org.alfresco.jlan.server.filesys.loader.MultipleFileRequest;

/**
 * Database Queue Interface
 * 
 * <p>The database queue interface provides methods for the queueing of file load and save requests for the
 * multi-threaded background load/save class.
 *
 * @author gkspencer
 */
public interface DBQueueInterface {

  /**
   * Queue a file request.
   *
   * @param fileReq FileRequest
   * @exception DBException 
   */
  public void queueFileRequest(FileRequest fileReq)
  	throws DBException;
  
  /**
   * Perform a queue cleanup deleting temporary cache files that do not have an associated save or transaction
   * request.
   * 
   * @param tempDir File
   * @param tempDirPrefix String
   * @param tempFilePrefix String
   * @param jarFilePrefix String
   * @return FileRequestQueue
   * @throws DBException
   */
  public FileRequestQueue performQueueCleanup(File tempDir, String tempDirPrefix, String tempFilePrefix, String jarFilePrefix)
    throws DBException;
  
  /**
   * Delete a file request from the pending queue.
   * 
   * @param fileReq FileRequest
   * @exception DBException
   */
  public void deleteFileRequest(FileRequest fileReq)
  	throws DBException;
  
  /**
   * Load a block of file requests from the database into the specified queue.
   *
   * @param seqNo int
   * @param reqType int
   * @param reqQueue FileRequestQueue
   * @param recLimit int
   * @return int
   * @exception DBException 
   */
  public int loadFileRequests(int seqNo, int reqType, FileRequestQueue reqQueue, int recLimit)
  	throws DBException;
  
  /**
   * Load a transaction request from the queue.
   *
   * @param tranReq MultipleFileRequest
   * @return MultipleFileRequest
   * @exception DBException
   */
  public MultipleFileRequest loadTransactionRequest(MultipleFileRequest tranReq)
  	throws DBException;
}
