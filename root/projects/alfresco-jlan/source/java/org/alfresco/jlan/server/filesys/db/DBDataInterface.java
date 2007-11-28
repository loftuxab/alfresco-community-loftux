package org.alfresco.jlan.server.filesys.db;

/*
 * DBDataInterface.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

import org.alfresco.jlan.server.filesys.loader.FileSegment;


/**
 * Database Data Interface
 * 
 * <p>The database data interface provides methods for loading/saving file data to database fields.
 */
public interface DBDataInterface {

  /**
   * Return the file data details for the specified file or stream.
   * 
   * @param fileId
   * @param streamId
   * @return DBDataDetails
   * @throws DBException
   */
  public DBDataDetails getFileDataDetails(int fileId, int streamId)
  	throws DBException;
  
  /**
   * Return the maximum data fragment size supported
   *
   * @return long 
   */
  public long getMaximumFragmentSize();
  
  /**
   * Load file data from the database into a temporary/local file
   * 
   * @param fileId int
   * @param streamId int
   * @param fileSeg FileSegment
   * @throws DBException
   * @throws IOException
   */
  public void loadFileData(int fileId, int streamId, FileSegment fileSeg)
		throws DBException, IOException;
  
  /**
   * Load Jar file data from the database into a temporary file
   *
   * @param jarId int
   * @param jarSeg FileSegment
   * @throws DBException
   * @throws IOException
   */
  public void loadJarData(int jarId, FileSegment jarSeg)
		throws DBException, IOException;
  
  /**
   * Save the file data from the temporary/local file to the database
   *
   * @param fileId int
   * @param streamId int
   * @param fileSeg FileSegment
   * @return int
   * @throws DBException
   * @throws IOException
   */
  public int saveFileData(int fileId, int streamId, FileSegment fileSeg)
		throws DBException, IOException;
  
  /**
   * Save the file data from a Jar file to the database
   * 
   * @param jarFile String
   * @param fileList DBDataDetailsList
   * @return int
   * @throws DBException
   * @throws IOException
   */
  public int saveJarData( String jarFile, DBDataDetailsList fileList)
  	throws DBException, IOException;
  
  /**
   * Delete the file data for the specified file/stream
   * 
   * @param fileId int
   * @param streamId int
   * @throws DBException
   * @throws IOException
   */
  public void deleteFileData(int fileId, int streamId)
  	throws DBException, IOException;
  
  /**
   * Delete the file data for the specified Jar file
   *
   * @param jarId int
   * @throws DBException
   * @throws IOException
   */
  public void deleteJarData(int jarId)
		throws DBException, IOException;
}
