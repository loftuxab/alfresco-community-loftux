package org.alfresco.jlan.server.filesys;

/*
 * FileExistsException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>This exception may be thrown by a disk interface when an attempt to create a new file fails because
 * the file already exists.
 */
public class FileExistsException extends java.io.IOException {

  private static final long serialVersionUID = 6314398441511723019L;

  /**
   * FileExistsException constructor.
   */
  public FileExistsException() {
    super();
  }
  
  /**
   * FileExistsException constructor.
   * 
   * @param s java.lang.String
   */
  public FileExistsException(String s) {
    super(s);
  }
}