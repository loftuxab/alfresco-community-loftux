package org.alfresco.jlan.util;

/*
 * X64.java
 *
 * Copyright (c) Starlasoft 2006. All rights reserved.
 */

/**
 * X64 Class
 * 
 * <p>
 * Check if the platform is a 64bit operating system.
 */
public class X64 {
  
  /**
   * Check if we are running on a Windows 64bit system
   * 
   * @return boolean
   */
  public static boolean isWindows64() {

    // Check for Windows

    String prop = System.getProperty("os.name");
    if (prop == null || prop.startsWith("Windows") == false)
      return false;

    // Check the OS architecture

    prop = System.getProperty("os.arch");
    if (prop != null && prop.equalsIgnoreCase("amd64"))
      return true;

    // Check the VM name

    prop = System.getProperty("java.vm.name");
    if (prop != null && prop.indexOf("64-Bit") != -1)
      return true;

    // Check the data model

    prop = System.getProperty("sun.arch.data.model");
    if (prop != null && prop.equals("64"))
      return true;

    // Not 64 bit Windows

    return false;
  }
}
