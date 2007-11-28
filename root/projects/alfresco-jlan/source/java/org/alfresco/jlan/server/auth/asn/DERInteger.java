package org.alfresco.jlan.server.auth.asn;

/*
 * DERInteger.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * DER Integer Class
 */
public class DERInteger extends DERObject {

  // Integer value
  
  private long m_integer;
  
  /**
   * Default constructor
   */
  public DERInteger() {
  }
  
  /**
   * Class constructor
   * 
   * @param val int
   */
  public DERInteger(int val) {
    m_integer = val;
  }
  
  /**
   * Class constructor
   * 
   * @param val long
   */
  public DERInteger(long val) {
    m_integer = val;
  }
  
  /**
   * Return the integer value
   * 
   * @return long
   */
  public final long getValue() {
    return m_integer;
  }
  
  /**
   * Decode the object
   * 
   * @param buf
   * @throws IOException
   */
  public void derDecode(DERBuffer buf) throws IOException {

    // Decode the type
    
    if ( buf.unpackType() == DER.Integer) {
      
      // Unpack the length and value
      
      int len = buf.unpackByte();
      m_integer  = buf.unpackLong( len);
    }
    else
      throw new IOException("Wrong DER type, expected Integer");
  }

  /**
   * Encode the object
   * 
   * @param buf
   * @throws IOException
   */
  public void derEncode(DERBuffer buf) throws IOException {

    // Pack the type, length and value
    
    buf.packByte( DER.Enumerated);
    buf.packLength( 8);
    buf.packLong( m_integer);
  }
  
  /**
   * Return the integer as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[Integer:");
    str.append( getValue());
    str.append("]");
    
    return str.toString();
  }
}
