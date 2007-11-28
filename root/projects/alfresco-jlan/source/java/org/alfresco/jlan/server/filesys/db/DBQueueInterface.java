package org.alfresco.jlan.server.filesys.db;

/*
 * DBQueueInterface.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.File;

import org.alfresco.jlan.server.filesys.loader.FileRequest;
import org.alfresco.jlan.server.filesys.loader.FileRequestQueue;
import org.alfresco.jlan.server.filesys.loader.MultipleFileRequest;

/**
 * Database Queue Interface
 * 
 * <p>The database queue interface provides methods for the queueing of file load and save requests for the
 * multi-threaded background load/save class.
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
