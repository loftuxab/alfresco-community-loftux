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

package org.alfresco.jlan.debug;

import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.alfresco.config.ConfigElement;

/**
 * JDK Logging Debug Class
 * 
 * <p>Output debug messages using the JDK logging APIs.
 *
 * @author gkspencer
 */
public class JDKLoggingDebug implements DebugInterface {

  // Buffer for debugPrint() strings
  
  private StringBuilder m_printBuf;
  
  /**
   * Class constructor
   */
  public JDKLoggingDebug() {
    super();
  }

  /**
   * Close the debug output.
   */
  public void close() {}

  /**
   * Output a debug string.
   *
   * @param str java.lang.String
   */
  public final void debugPrint(String str) {
    
    // Allocate a holding buffer
    
    if ( m_printBuf == null) {
      synchronized ( this) {
        if ( m_printBuf == null)
          m_printBuf = new StringBuilder();
      }
    }

    // Append the string to the holding buffer
    
    synchronized ( m_printBuf) {
      m_printBuf.append( str);
    }
  }

  /**
   * Output a debug string, and a newline.
   *
   * @param str java.lang.String
   */
  public final void debugPrintln(String str) {
    
    // Check if there is a holding buffer
    
    if ( m_printBuf != null) {
      
      // Append the new string
      
      m_printBuf.append( str);
      Logger.global.info( m_printBuf.toString());
      m_printBuf = null;
    }
    else
      Logger.global.info( str);
  }

  /**
   * Initialize the debug interface using the specified parameters.
   *
   * @param params ConfigElement
   */
  public void initialize( ConfigElement params) {

    //  Get the logging properities file name

    ConfigElement logProps = params.getChild( "Properties");
    
    //  Check if the log file has been specified
    
    if ( logProps.getValue() != null) {
      
      // Open the logging properties file
      
      FileInputStream logPropsFile = null;
      
      try {
        
        // Open the logging properties file
        
        logPropsFile = new FileInputStream( logProps.getValue());
      
        // Load the logging properties
        
        LogManager.getLogManager().readConfiguration( logPropsFile);
      }
      catch ( Exception ex) {
        
      }
      finally {
        
        // Close the properties file
        
        if ( logPropsFile != null) {
          try {
            logPropsFile.close();
          }
          catch (Exception ex) {
          }
        }
      }
    }
  }
}
