package org.alfresco.jlan.util;

/*
 * ConsoleIO.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

/**
 * Console IO Class
 * 
 * <p>Provides a wrapper class for conole I/O functions to allow Java and J#/.NET versions.
 */
public class ConsoleIO {

  /**
   * Check if the console input is connected to a valid stream
   * 
   * @return boolean
   */
  public final static boolean isValid() {
    try {
      System.in.available();
      return true;
    }
    catch (IOException ex) {
    }
    return false;
  }
  
  /**
   * Check if there is input available
   *
   * @return int
   */
  public final static int available() {
    try {
      return System.in.available();
    }
    catch (Exception ex) {
    }
    return -1;
  }
  
  /**
   * Read a character from the console
   *
   * @return int
   * @exception IOException
   */
  public final static int readCharacter() {
    try {
      return System.in.read();
    }
    catch (Exception ex) {
    }
    return -1;
  }
}
