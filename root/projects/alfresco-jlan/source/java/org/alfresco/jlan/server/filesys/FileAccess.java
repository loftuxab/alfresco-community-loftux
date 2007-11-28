package org.alfresco.jlan.server.filesys;

/*
 * FileAccess.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * File Access Class
 *
 * <p>Contains a list of the available file permissions that may be applied to a share, directory or file.
 */
public final class FileAccess {

  //	Permissions

  public static final int NoAccess 	= 0;
  public static final int ReadOnly 	= 1;
  public static final int Writeable = 2;

  /**
   * Return the file permission as a string.
   *
   * @param perm int
   * @return java.lang.String
   */
  public final static String asString(int perm) {
    String permStr = "";

    switch (perm) {
      case NoAccess :
        permStr = "NoAccess";
        break;
      case ReadOnly :
        permStr = "ReadOnly";
        break;
      case Writeable :
        permStr = "Writeable";
        break;
    }
    return permStr;
  }
}