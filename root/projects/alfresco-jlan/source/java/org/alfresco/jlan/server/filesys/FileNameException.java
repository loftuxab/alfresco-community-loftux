package org.alfresco.jlan.server.filesys;

import java.io.IOException;

/*
 * FileNameException.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

/**
 * <p>This ecxeption may be thrown when a file name is too long or contains invalid characters.
 */
public class FileNameException extends IOException {

  private static final long serialVersionUID = 2394682802933348980L;

  /**
   * Class constructor.
   */
  public FileNameException() {
    super();
  }
  
  /**
   * Class constructor.
   * 
   * @param s java.lang.String
   */
  public FileNameException(String s) {
    super(s);
  }

}
