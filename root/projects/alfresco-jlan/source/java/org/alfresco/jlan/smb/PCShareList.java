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

package org.alfresco.jlan.smb;

import java.util.Vector;

/**
 * PC share list class.
 *
 * <p>The PCShareList class contains a list of PCShare objects.
 *
 * @author gkspencer
 */
public class PCShareList implements java.io.Serializable {

  private static final long serialVersionUID = 6318830926098970791L;

  //	Vector used to store the PCShare objects

  private Vector<PCShare> m_list;
  
  /**
   * Class constructor
   */
  public PCShareList() {
    m_list = new Vector<PCShare>();
  }
  
  /**
   * Add a PCShare to the list
   * @param shr PCShare object to be added to the list
   */
  public final void addPCShare(PCShare shr) {
    m_list.add(shr);
  }
  
  /**
   * Clear the list of PCShare objects
   */
  public final void clearList() {
    m_list.removeAllElements();
  }
  
  /**
   * Return the required PCShare object from the list.
   *
   * @return PCShare
   * @param idx Index of the PCShare to be returned
   * @exception java.lang.ArrayIndexOutOfBoundsException  If the index is not valid
   */
  public final PCShare getPCShare(int idx)
    throws ArrayIndexOutOfBoundsException {

    //  Bounds check the index

    if (idx >= m_list.size())
      throw new ArrayIndexOutOfBoundsException();

    //  Return the required share information

    return m_list.get(idx);
  }
  
  /**
   * Return the number of PCShare objects that are in this list.
   *
   * @return Number of PCShare objects in the list.
   */
  public final int NumberOfPCShares() {
    return m_list.size();
  }
}
