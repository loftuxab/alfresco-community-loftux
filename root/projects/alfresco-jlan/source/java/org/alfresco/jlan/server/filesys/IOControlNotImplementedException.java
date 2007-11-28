package org.alfresco.jlan.server.filesys;

/*
 * IOControlNotImplementedException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * I/O Control Not Implemented Exception Class
 * 
 * <p>This exception may be thrown by an IOCtlInterface implementation.
 */
public class IOControlNotImplementedException extends Exception {

  private static final long serialVersionUID = -3555727552298485187L;

  /**
   * Default constructor.
   */
  public IOControlNotImplementedException() {
    super();
  }

  /**
   * Class constructor.
   * 
   * @param s java.lang.String
   */
  public IOControlNotImplementedException(String s) {
    super(s);
  }
}
