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
 * DER Integer Class
 *
 * @author gkspencer
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
