package org.alfresco.jlan.server.filesys;

/*
 * TooManyConnectionsException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>This error indicates that too many tree connections are currently open
 * on a session. The new tree connection request will be rejected by the server.
 */
public class TooManyConnectionsException extends Exception {

  private static final long serialVersionUID = 6353813221614206049L;

  /**
   * TooManyConnectionsException constructor.
   */
  public TooManyConnectionsException() {
    super();
  }

  /**
   * TooManyConnectionsException constructor.
   * 
   * @param s java.lang.String
   */
  public TooManyConnectionsException(String s) {
    super(s);
  }
}