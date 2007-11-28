package org.alfresco.jlan.smb.util;

/*
 * DriveMappingList.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.Vector;

/**
 * Network Drive Mapping List Class
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
