package org.alfresco.jlan.smb;

/*
 * UnsupportedDeviceTypeException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 *  Unsupported device type exception class
 *
 *  <p>The UnsupportedDeviceTypeException is thrown when an attempt to connect to
 *  a remote device that is not supported by the remote server.
 */

public class UnsupportedDeviceTypeException extends Exception {

  private static final long serialVersionUID = -8150979721279343472L;

  /**
   * Class constructor
   */
  public UnsupportedDeviceTypeException() {
  }

  /**
   * Class constructor
   */

  public UnsupportedDeviceTypeException(String msg) {
    super(msg);
  }
}