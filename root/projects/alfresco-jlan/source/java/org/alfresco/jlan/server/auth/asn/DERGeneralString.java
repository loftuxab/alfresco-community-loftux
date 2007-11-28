package org.alfresco.jlan.server.auth.asn;

/*
 * DERGeneralString.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * DER General String Class
 */
public class DERGeneralString extends DERObject {

  // String value
  
  private String m_string;
  
  /**
   * Default constructor
   */
  public DERGeneralString() {
  }
  
  /**
   * Class constructor
   * 
   * @param str String
   */
  public DERGeneralString(String str) {
    m_string = str;
  }

  /**
   * Return the string value
   * 
   * @return String
   */
  public final String getValue() {
    return m_string;
  }
  
  /**
   * Decode the object
   * 
   * @param buf
   * @throws IOException
   */
  public void derDecode(DERBuffer buf)
    throws IOException {

    // Decode the type
    
    if ( buf.unpackType() == DER.GeneralString) {
      
      // Unpack the length and bytes
      
      int len = buf.unpackLength();
      if ( len > 0) {
        
        // Get the string bytes
        
        byte[] byts = buf.unpackBytes( len);
        m_string = new String( byts);
      }
      else
        m_string = null;
    }
    else
      throw new IOException("Wrong DER type, expected GeneralString");
  }

  /**
   * Encode the object
   * 
   * @param buf
   * @throws IOException
   */
  public void derEncode(DERBuffer buf)
    throws IOException {

    // Pack the type, length and bytes
    
    buf.packByte( DER.GeneralString);

    if ( m_string != null) {
      byte[] byts = m_string.getBytes();
      buf.packLength( byts.length);
      buf.packBytes( byts, 0, byts.length);
    }
    else
      buf.packLength( 0);
  }
  
  /**
   * Return as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[GeneralString:");
    str.append(m_string);
    str.append("]");
    
    return str.toString();
  }
}
