package org.alfresco.jlan.server.auth.asn;

/*
 * DERBoolean.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * DER Boolean Class
 */
public class DERBoolean extends DERObject {

  // Object value
  
  private boolean m_bool;
  
  /**
   * Default constructor
   */
  public DERBoolean() {
  }
  
  /**
   * Class constructor
   * 
   * @param bool boolean
   */
  public DERBoolean(boolean bool) {
    m_bool = bool;
  }
  
  /**
   * Return the boolean value
   * 
   * @return boolean
   */
  public final boolean getValue() {
    return m_bool;
  }
  
  /**
   * Decode the object
   * 
   * @param buf
   * @throws IOException
   */
  public void derDecode(DERBuffer buf) throws IOException {

    // Decode the type
    
    if ( buf.unpackType() == DER.Boolean) {
      
      // Unpack the length and value
      
      buf.unpackByte();
      m_bool = buf.unpackByte() == 0xFF ? true : false;
    }
    else
      throw new IOException("Wrong DER type, expected Boolean");
  }

  /**
   * Encode the object
   * 
   * @param buf
   * @throws IOException
   */
  public void derEncode(DERBuffer buf) throws IOException {

    // Pack the type, length and value
    
    buf.packByte( DER.Boolean);
    buf.packByte( 1);
    buf.packByte( m_bool ? 0xFF : 0);
  }
  
  /**
   * Return the boolean as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[Boolean:");
    str.append(m_bool);
    str.append("]");
    
    return str.toString();
  }
}
