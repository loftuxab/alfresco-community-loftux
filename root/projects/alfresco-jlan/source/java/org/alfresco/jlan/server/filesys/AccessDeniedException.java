package org.alfresco.jlan.server.filesys;

/*
 * AccessDeniedException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>Thrown when an attempt is made to write to a file that is read-only or the user only has read access to, or
 * open a file that is actually a directory.
 */
public class AccessDeniedException extends java.io.IOException {

  private static final long serialVersionUID = -7914373730318995028L;

  /**
   * AccessDeniedException constructor
   */
  public AccessDeniedException() {
    super();
  }

  /**
   * AccessDeniedException constructor.
   *
   * @param s java.lang.String
   */
  public AccessDeniedException(String s) {
    super(s);
  }
}