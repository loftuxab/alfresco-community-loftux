/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.util;

import java.io.IOException;

/**
 * Console IO Class
 * 
 * <p>Provides a wrapper class for conole I/O functions to allow Java and J#/.NET versions.
 *
 * @author gkspencer
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
