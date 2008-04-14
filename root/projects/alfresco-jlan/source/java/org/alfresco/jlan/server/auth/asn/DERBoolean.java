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
 * DER Boolean Class
 *
 * @author gkspencer
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
