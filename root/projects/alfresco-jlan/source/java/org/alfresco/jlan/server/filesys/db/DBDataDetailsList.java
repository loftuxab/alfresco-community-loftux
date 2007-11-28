package org.alfresco.jlan.server.filesys.db;

/*
 * DBDataDetailsList.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.Vector;

/**
 * Database Data Details List Class
 * 
 * <p>Contains a list of DBDataDetail objects.
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
