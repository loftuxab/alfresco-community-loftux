package org.alfresco.jlan.server.filesys;

/*
 * UnsupportedInfoLevelException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>This error is generated when a request is made for an information level that is not currently
 * supported by the SMB server.
 */
public class UnsupportedInfoLevelException extends Exception {

  private static final long serialVersionUID = 7757616035566368034L;

  /**
   * Class constructor.
   */
  public UnsupportedInfoLevelException() {
    super();
  }

  /**
   * Class constructor.
   *
   * @param str java.lang.String
   */
  public UnsupportedInfoLevelException(String str) {
    super(str);
  }
}