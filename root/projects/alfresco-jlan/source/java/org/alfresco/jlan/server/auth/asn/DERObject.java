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
 * DER Object Class
 * 
 * <p>Base class for ASN.1 DER encoded objects.
 *
 * @author gkspencer
 */
public abstract class DERObject {

  // Value to indicate that the object is not tagged
  
  public static final int NotTagged = -1;
  
  // Tag number, or -1 if not tagged
  
  private int m_tagNo = NotTagged;
  
  /**
   * Check if the object is tagged
   * 
   * @return boolean
   */
  public final boolean isTagged() {
    return m_tagNo != -1 ? true : false;
  }
  
  /**
   * Return the tag number
   * 
   * @return int
   */
  public final int getTagNo() {
    return m_tagNo;
  }
  
  /**
   * Set the tag number
   * 
   * @param tagNo int
   */
  public final void setTagNo( int tagNo) {
    m_tagNo = tagNo;
  }
  
  /**
   * DER encode the object
   * 
   * @param buf DERBuffer
   */
  public abstract void derEncode( DERBuffer buf)
    throws IOException;
  
  /**
   * DER decode the object
   * 
   * @param buf DERBuffer
   */
  public abstract void derDecode( DERBuffer buf)
    throws IOException;
}
