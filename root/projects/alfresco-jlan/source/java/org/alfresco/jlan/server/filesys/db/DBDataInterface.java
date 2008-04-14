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

import java.io.IOException;

import org.alfresco.jlan.server.filesys.loader.FileSegment;


/**
 * Database Data Interface
 * 
 * <p>The database data interface provides methods for loading/saving file data to database fields.
 *
 * @author gkspencer
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
