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
 * DER Application Specific Class
 *
 * @author gkspencer
 */
public class DERApplicationSpecific extends DERObject {

  // Application specific blob bytes
  
  private byte[] m_bytes;
  
  /**
   * Default constructor
   */
  public DERApplicationSpecific() {
  }
  
  /**
   * Class constructor
   * 
   * @param byts byte[]
   */
  public DERApplicationSpecific( byte[] byts) {
    m_bytes = byts;
  }
  
  /**
   * Class constructor
   *
   * @param tagId int
   * @param byts byte[]
   */
  public DERApplicationSpecific( int tagId, byte[] byts) {
	setTagNo( tagId);
    m_bytes = byts;
  }
  
  /**
   * Return the bytes
   * 
   * @return byte[]
   */
  public byte[] getValue() {
    return m_bytes;
  }

  /**
   * DER decode the object
   * 
   * @param buf DERBuffer
   */
  public void derDecode(DERBuffer buf)
    throws IOException {

    // Decode the type
    
    if (( buf.unpackType() & DER.Application) != 0) {
      
      // Unpack the length and bytes
      
      int len = buf.unpackLength();
      if ( len > 0) {
        
        // Get the string bytes
        
        m_bytes = buf.unpackBytes( len);
      }
      else
        m_bytes = null;
    }
    else
      throw new IOException("Wrong DER type, expected ApplicationSpecific");
  }

  /**
   * DER encode the object
   * 
   * @param buf DERBuffer
   */
  public void derEncode(DERBuffer buf)
    throws IOException {

    // Pack the type, length and bytes

	int tagNo = 0;
	if ( isTagged())
		tagNo = getTagNo();
	
    buf.packByte( DER.Application + DER.Constructed + (tagNo & 0xFF));

    if ( m_bytes != null) {
      buf.packLength( m_bytes.length);
      buf.packBytes( m_bytes, 0, m_bytes.length);
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
    
    str.append("[ApplicationSpecific:");
    str.append(m_bytes != null ? m_bytes.length : 0);
    str.append("]");
    
    return str.toString();
  }
}
