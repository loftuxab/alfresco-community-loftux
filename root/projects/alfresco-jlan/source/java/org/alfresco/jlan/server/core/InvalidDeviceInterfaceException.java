package org.alfresco.jlan.server.core;

/*
 * InvalidDeviceInterfaceException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>This exception may be thrown by a SharedDevice when the device interface has not been specified,
 * the device interface does not match the shared device type, or the device interface driver class
 * cannot be loaded.
 */
public class InvalidDeviceInterfaceException extends Exception {

  private static final long serialVersionUID = -3497495092231515500L;

  /**
   * InvalidDeviceInterfaceException constructor.
   */
  public InvalidDeviceInterfaceException() {
    super();
  }

  /**
   * InvalidDeviceInterfaceException constructor.
   *
   * @param s java.lang.String
   */
  public InvalidDeviceInterfaceException(String s) {
    super(s);
  }
}