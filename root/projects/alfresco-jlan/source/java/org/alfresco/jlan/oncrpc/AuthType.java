package org.alfresco.jlan.oncrpc;

/*
 * AuthType.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Authentication Types Class
 */
public final class AuthType {

  //	Authentication type contants
  
  public static final int Null		= 0;
  public static final int Unix		= 1;
  public static final int Short		= 2;
  public static final int DES			= 3;

  //	Authentication type strings
  
  private static final String[] _authTypes = { "Null", "Unix", "Short", "DES" };
  
  /**
   * Return the authentication type as string
   *
   * @param type int
   * @return String
   */
  public static final String getTypeAsString(int type) {
    if ( type < 0 || type >= _authTypes.length)
      return "" + type;
    return _authTypes[type];
  }
}
