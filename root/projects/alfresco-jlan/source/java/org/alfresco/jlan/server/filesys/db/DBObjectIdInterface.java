package org.alfresco.jlan.server.filesys.db;

/*
 * DBObjectIdInterface.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

/**
 * Database Object Id Interface
 * 
 * <p>Provides methods for loading, saving and deleting file id to object id mappings in a database table.
 */
public interface DBObjectIdInterface {

  /**
   * Create a file id to object id mapping
   *
   * @param fileId int
   * @param streamId int
   * @param objectId String
   * @exception DBException
   */
  public void saveObjectId(int fileId, int streamId, String objectId)
  	throws DBException;
  
  /**
   * Load the object id for the specified file id
   * 
   * @param fileId int
   * @param streamId int
   * @return String
   * @exception DBException
   */
  public String loadObjectId(int fileId, int streamId)
  	throws DBException;
  
  /**
   * Delete a file id/object id mapping
   *
   * @param fileId int
   * @param streamId int
   * @param objectId String
   * @exception DBException
   */
  public void deleteObjectId(int fileId, int streamId, String objectId)
  	throws DBException;
}
