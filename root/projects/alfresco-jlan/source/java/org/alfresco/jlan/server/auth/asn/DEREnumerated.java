package org.alfresco.jlan.server.auth.asn;

/*
 * DEREnumerate.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * DER Enumerated Class
 */
public class DEREnumerated extends DERObject {

  // Enumerated value
  
  private int m_enum;
  
  /**
   * Default constructor
   */
  public DEREnumerated() {
  }
  
  /**
   * Class constructor
   * 
   * @param val int
   */
  public DEREnumerated(int val) {
    m_enum = val;
  }
  
  /**
   * Return the enumerated value
   * 
   * @return int
   */
  public final int getValue() {
    return m_enum;
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
    
    if ( buf.unpackType() == DER.Enumerated) {
      
      // Unpack the length and value
      
      int len = buf.unpackByte();
      m_enum  = buf.unpackInt( len);
    }
    else
      throw new IOException("Wrong DER type, expected Enumerated");
  }

  /**
   * Encode the object
   * 
   * @param buf
   * @throws IOException
   */
  public void derEncode(DERBuffer buf)
    throws IOException {

    // Pack the type, length and value
    
    buf.packByte( DER.Enumerated);
    if ( m_enum < 256) {
      buf.packLength( 1);
      buf.packByte( m_enum);
    }
    else {
      buf.packLength( 4);
      buf.packInt( m_enum);
    }
  }
  
  /**
   * Return the enumerated type as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[Enum:");
    str.append(getValue());
    str.append("]");
    
    return str.toString();
  }
}
