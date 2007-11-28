package org.alfresco.jlan.server.filesys;

/*
 * FileType.java
 *
 * Copyright (c) Starlasoft 2006. All rights reserved.
 */

/**
 * File Type Class
 * 
 * <p>File type constants.
 */
public class FileType {

  // File types
  
  public static final int RegularFile   = 1;
  public static final int Directory     = 2;
  public static final int SymbolicLink  = 3;
  public static final int HardLink      = 4;
  public static final int Device        = 5;
  
  /**
   * Return a file type as a string
   * 
   * @param typ int
   * @return String
   */
  public final static String asString(int typ) {
    
    String typStr = "Unknown";
    
    switch ( typ) {
      case RegularFile:
        typStr = "File";
        break;
      case Directory:
        typStr = "Directory";
        break;
      case SymbolicLink:
        typStr = "SymbolicLink";
        break;
      case HardLink:
        typStr = "HardLink";
        break;
      case Device:
        typStr = "Device";
        break;
    }
    
    return typStr;
  }
}
