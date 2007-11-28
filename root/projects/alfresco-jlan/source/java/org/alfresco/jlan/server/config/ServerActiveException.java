package org.alfresco.jlan.server.config;

/*
 * ServerActiveException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>This exception may be thrown when certain operations are performed on a server that
 * is currently in a running state.
 */
public class ServerActiveException extends Exception {

  private static final long serialVersionUID = 5705188958170354512L;

  /**
   * ServerActiveException constructor.
   */
  public ServerActiveException() {
    super();
  }
  
  /**
   * ServerActiveException constructor.
   * 
   * @param s java.lang.String
   */
  public ServerActiveException(String s) {
    super(s);
  }
}