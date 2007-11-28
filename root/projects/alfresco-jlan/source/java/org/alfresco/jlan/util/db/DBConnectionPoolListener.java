package org.alfresco.jlan.util.db;

/*
 * DBConnectionPoolListener.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

/**
 * Connection Pool Listener Interface
 * 
 * <p>Used to communicate database connection events such as the database server online/offline status.
 */
public interface DBConnectionPoolListener {

  /**
   * Database online/offline status event
   * 
   * @param dbonline boolean
   */
  public void databaseOnlineStatus( boolean dbonline);
}
