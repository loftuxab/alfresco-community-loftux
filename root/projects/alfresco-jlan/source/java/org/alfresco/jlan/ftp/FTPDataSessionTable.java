package org.alfresco.jlan.ftp;

/*
 * FTPDataSessionTable.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.Hashtable;

/**
 * FTP Data Session Table Class
 * 
 * <p>Keeps track of FTP data session objects using the local port number as the index.
 */
public class FTPDataSessionTable {

  //	Data session table
  
  private Hashtable<Integer, FTPDataSession> m_sessTable;
  
  /**
   * Default constructor
   */
  public FTPDataSessionTable() {
    m_sessTable = new Hashtable<Integer, FTPDataSession>();
  }
  
  /**
   * Add a session to the table
   * 
   * @param port int
   * @param sess FTPDataSession
   */
  public final void addSession(int port, FTPDataSession sess) {
    m_sessTable.put(new Integer(port), sess);
  }
  
  /**
   * Find the session using the specified local port
   * 
   * @param port int
   * @return FTPDataSession
   */
  public final FTPDataSession findSession(int port) {
    return m_sessTable.get(new Integer(port));
  }
  
  /**
   * Return the number of sessions in the table
   * 
   * @return int
   */
  public final int numberOfSessions() {
    return m_sessTable.size();
  }
  
  /**
   * Remove a session from the table
   * 
   * @param sess FTPDataSession
   * @return FTPDataSession
   */
  public final FTPDataSession removeSession(FTPDataSession sess) {
    return m_sessTable.remove(new Integer(sess.getAllocatedPort()));
  }
  
  /**
   * Remove all sessions from the table
   */
  public final void removeAllSessions() {
    m_sessTable.clear();
  }
}
