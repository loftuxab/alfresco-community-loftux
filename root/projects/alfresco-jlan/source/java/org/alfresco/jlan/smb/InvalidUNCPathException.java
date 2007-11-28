package org.alfresco.jlan.smb;

/*
 * InvalidUNCPathException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 *  Invalid UNC path exception class
 *
 *  <p>The InvalidUNCPathException indicates that a UNC path has an invalid format.
 *
 *  @see PCShare
 */
public class InvalidUNCPathException extends Exception {

  private static final long serialVersionUID = -5286647687687183134L;

  /**
   * Default invalid UNC path exception constructor.
   */

  public InvalidUNCPathException() {
  }
  
  /**
   * Invalid UNC path exception constructor, with additional details string.
   */

  public InvalidUNCPathException(String msg) {
    super(msg);
  }
}