package org.alfresco.jlan.server.auth.asn;

/*
 * DEROctetString.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * DER Octet String Class
 */
public class DEROctetString extends DERObject {

  // String bytes
  
  private byte[] m_string;
  
  /**
   * Default constructor
   */
  public DEROctetString() {
  }
  
  /**
   * Class constructor
   * 
   * @param byts byte[]
   */
  public DEROctetString( byte[] byts) {
    m_string = byts;
  }
  
  /**
   * Class constructor
   * 
   * @param str String
   */
  public DEROctetString(String str) {
    m_string = str.getBytes();
  }
  
  /**
   * Return the string bytes
   * 
   * @return byte[]
   */
  public byte[] getValue() {
    return m_string;
  }

  /**
   * Return as a string
   * 
   * @return String
   */
  public final String asString() {
    if ( m_string != null)
      return new String(m_string);
    return null;
  }
  
  /**
   * DER decode the object
   * 
   * @param buf DERBuffer
   */
  public void derDecode(DERBuffer buf)
    throws IOException {

    // Decode the type
    
    if ( buf.unpackType() == DER.OctetString) {
      
      // Unpack the length and bytes
      
      int len = buf.unpackLength();
      if ( len > 0) {
        
        // Get the string bytes
        
        m_string = buf.unpackBytes( len);
      }
      else
        m_string = null;
    }
    else
      throw new IOException("Wrong DER type, expected OctetString");
  }

  /**
   * DER encode the object
   * 
   * @param buf DERBuffer
   */
  public void derEncode(DERBuffer buf)
    throws IOException {

    // Get the string bytes
    
    byte[] byts = m_string;
    
    // Pack the type, length and bytes
    
    buf.packByte( DER.OctetString);

    if ( byts != null) {
      buf.packLength( byts.length);
      buf.packBytes( byts, 0, byts.length);
    }
    else
      buf.packLength( 0);
  }
  
  /**
   * Return the string details as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[OctetString:");
    str.append(m_string != null ? m_string.length : 0);
    str.append("]");
    
    return str.toString();
  }
}
