package org.alfresco.jlan.server.filesys.db;

/*
 * DBException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Database Interface Exception Class
 */
public class DBException extends Exception {

  private static final long serialVersionUID = -570556453282747263L;

  /**
   * Default constructor
   */
  public DBException() {
    super();
  }
  
  /**
   * Class constructor
   *
   * @param msg String
   */
  public DBException(String msg) {
    super(msg);
  }
}
