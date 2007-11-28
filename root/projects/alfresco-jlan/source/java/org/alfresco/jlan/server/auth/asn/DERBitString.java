package org.alfresco.jlan.server.auth.asn;

/*
 * DERBitString.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * DER Bit String Class 
 */
public class DERBitString extends DERObject {

  // Bit flags value
  
  private int m_bits;
  
  /**
   * Default constructor
   */
  public DERBitString() {
  }

  /**
   * Class constructor
   * 
   * @param bits int
   */
  public DERBitString( int bits) {
    m_bits = bits;
  }

  /**
   * Return the value
   * 
   * @return int
   */
  public final int getValue() {
    return m_bits;
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
    
    if ( buf.unpackType() == DER.BitString) {
      
      // Unpack the length and bytes
      
      int len = buf.unpackLength();
      m_bits = 0;
      
      while ( len-- > 0) {
        
        // Get the value bytes

        m_bits = (m_bits << 8) + buf.unpackByte();
      }
    }
    else
      throw new IOException("Wrong DER type, expected BitString");
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
    
    buf.packByte( DER.BitString);

    buf.packLength( 4);
    buf.packInt( m_bits);;
  }
  
  /**
   * Return the bit string as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[BitString:0x");
    str.append( Integer.toHexString( m_bits));
    str.append("]");
    
    return str.toString();
  }
}
