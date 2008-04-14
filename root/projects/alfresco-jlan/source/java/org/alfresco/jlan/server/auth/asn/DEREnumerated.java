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
 * DER Enumerated Class
 *
 * @author gkspencer
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
