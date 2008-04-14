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
 * DER General String Class
 *
 * @author gkspencer
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
