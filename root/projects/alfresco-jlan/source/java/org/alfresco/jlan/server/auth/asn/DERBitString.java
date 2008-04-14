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

package org.alfresco.jlan.server.auth.asn;

import java.io.IOException;

/**
 * DER Bit String Class 
 *
 * @author gkspencer
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
