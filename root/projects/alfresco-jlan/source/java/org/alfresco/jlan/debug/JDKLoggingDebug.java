package org.alfresco.jlan.debug;

/*
 * JDKLoggingDebug.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.alfresco.config.ConfigElement;

/**
 * JDK Logging Debug Class
 * 
 * <p>Output debug messages using the JDK logging APIs.
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
