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

import java.util.Vector;

/**
 * Database Data Details List Class
 * 
 * <p>Contains a list of DBDataDetail objects.
 *
 * @author gkspencer
 */
public class DBDataDetailsList {

  //	List of database file/stream id details
  
  private Vector<DBDataDetails> m_list;
  
  /**
   * Default constructor
   */
  public DBDataDetailsList() {
  
  	//	Allocate the list
  
  	m_list = new Vector<DBDataDetails>();
  }

  /**
   * Return the number of files in the list
   *
   * @return int
   */
  public final int numberOfFiles() {
    return m_list.size();
  }
  
  /**
   * Add file details to the list
   *
   * @param details DBDataDetails
   */
  public final void addFile(DBDataDetails details) {
    m_list.addElement(details);
  }
  
  /**
   * Return the file details at the specified index
   *
   * @param idx int
   * @return DBDataDetails
   */
  public final DBDataDetails getFileAt(int idx) {
    if ( idx < 0 || idx >= m_list.size())
      return null;
    return m_list.elementAt(idx);
  }

  /**
   * Remove the file at the specified index within the list
   * 
   * @param idx int
   * @return DBDataDetails
   */
  public final DBDataDetails removeFileAt(int idx) {
    if ( idx < 0 || idx >= m_list.size())
      return null;
    DBDataDetails dbDetails = m_list.elementAt(idx);
    m_list.removeElementAt(idx);
    return dbDetails;
  }
  
  /**
   * Clear the file details from the list
   */
  public final void remoteAllFiles() {
    m_list.removeAllElements();
  }
}
