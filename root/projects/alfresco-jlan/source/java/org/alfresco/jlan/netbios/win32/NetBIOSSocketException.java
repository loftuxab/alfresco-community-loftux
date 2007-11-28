package org.alfresco.jlan.netbios.win32;

/*
 * NetBIOSSocketException.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

/**
 * NetBIOS Socket Exception Class
 */
public class NetBIOSSocketException extends Exception {
  
  private static final long serialVersionUID = 2363178480979507007L;

  /**
   * Class constructor
   * 
   * @param msg String
   */
  public NetBIOSSocketException(String msg) {
    super(msg);
  }
}
