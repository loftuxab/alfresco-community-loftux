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

package org.alfresco.jlan.smb.util;

import java.util.Vector;

/**
 * Network Drive Mapping List Class
 *
 * @author gkspencer
 */
public class DriveMappingList {

  //	List of network drive mappings
  
  private Vector m_mappings;
  
  /**
   * Default constructor
   */
  public DriveMappingList() {
    m_mappings = new Vector();
  }
  
  /**
   * Add a drive mapping to the list
   *
   * @param mapping DriveMapping
   */
  public final void addMapping(DriveMapping mapping) {
    m_mappings.addElement(mapping);
  }
  
  /**
   * Return the count of mappings in the list
   * 
   * @return int
   */
  public final int numberOfMappings() {
    return m_mappings.size();
  }
  
  /**
   * Return the required drive mapping
   *
   * @param idx int
   * @return DriveMapping
   */
  public final DriveMapping getMappingAt(int idx) {
    if ( idx < 0 || idx >= m_mappings.size())
      return null;
    return (DriveMapping) m_mappings.elementAt(idx);
  }

  /**
   * Find the mapping for the specified local drive
   * 
   * @param localDrive String
   * @return DriveMapping
   */
  public final DriveMapping findMapping(String localDrive) {
    
    //	Search the drive mappings list
    
    for ( int i = 0; i < m_mappings.size(); i++) {
      
      //	Get the current drive mapping
      
      DriveMapping driveMap = (DriveMapping) m_mappings.elementAt(i);
      
      if ( driveMap.getLocalDrive().equalsIgnoreCase(localDrive))
        return driveMap;
    }
    
    //	Drive mapping not found
    
    return null;
  }
  
  /**
   * Remove a drive mapping from the list
   * 
   * @param idx int
   */
  public final void removeMapping(int idx) {
    if ( idx < 0 || idx >= m_mappings.size())
      return;
    m_mappings.removeElementAt(idx);
  }
  
  /**
   * Remove all mappings from the list
   */
  public final void removeAllMappings() {
    m_mappings.removeAllElements();
  }
}
