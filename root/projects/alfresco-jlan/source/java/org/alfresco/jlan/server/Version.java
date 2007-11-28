package org.alfresco.jlan.server;

/*
 * Version.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

/**
 * Server Versions Class
 * 
 * <p>Holds the version strings for various server implementations.
 */
public class Version {

  // Top level version
  
  public static String ReleaseVersion = "4.0.0 [alpha]";
  
  // Server version strings
  
  public static String SMBServerVersion       = ReleaseVersion;
  public static String NetBIOSServerVersion   = ReleaseVersion;
  
  public static String NFSServerVersion       = ReleaseVersion;
  public static String MountServerVersion     = ReleaseVersion;
  public static String PortMapServerVersion   = ReleaseVersion;
  
  public static String FTPServerVersion       = ReleaseVersion;
}
