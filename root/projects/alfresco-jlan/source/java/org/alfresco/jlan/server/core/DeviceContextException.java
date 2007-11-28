package org.alfresco.jlan.server.core;

/*
 * DeviceContextException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Device Context Exception Class
 * 
 * <p>Thrown when a device context parameter string is invalid.
 */
public class DeviceContextException extends Exception {

  private static final long serialVersionUID = -2282554065425234959L;

  /**
   * Class constructor
   */
  public DeviceContextException() {
    super();
  }
  
  /**
   * Class constructor
   * 
   * @param s java.lang.String
   */
  public DeviceContextException(String s) {
    super(s);
  }

}
