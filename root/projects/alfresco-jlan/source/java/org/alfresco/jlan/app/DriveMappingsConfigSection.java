package org.alfresco.jlan.app;

/*
 * DriveMappingsConfigSection.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import org.alfresco.jlan.server.config.ConfigId;
import org.alfresco.jlan.server.config.ConfigSection;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.smb.util.DriveMappingList;

/**
 *  Drive Mappings Configuration Section Class
 */
public class DriveMappingsConfigSection extends ConfigSection {

  // Drive mappings configuration section name
  
  public static final String SectionName = "DriveMappings";
  
  //  Win32 local drive mappings to be added when the SMB/CIFS server has started
  
  private DriveMappingList m_mappedDrives;
  
  //  Enable debug output
  
  private boolean m_debug;
  
  /**
   * Class constructor
   * 
   * @param config ServerConfiguration
   */
  public DriveMappingsConfigSection(ServerConfiguration config) {
    super( SectionName, config);
  }

  /**
   * Check if debug output is enabled
   * 
   * @return boolean
   */
  public final boolean hasDebug() {
    return m_debug;
  }
  
  /**
   * Determine if there are mapped drives specified to be added when the SMB/CIFS server has started
   * 
   * @return boolean
   */
  public final boolean hasMappedDrives() {
    return m_mappedDrives != null ? true : false;
  }

  /**
   * Return the mapped drives list
   * 
   * @return DriveMappingList
   */
  public final DriveMappingList getMappedDrives() {
    return m_mappedDrives;
  }
  
  /**
   * Add a list of mapped drives
   *
   * @param mappedDrives DriveMappingList
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setMappedDrives(DriveMappingList mappedDrives)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.SMBMappedDrives, mappedDrives);
    m_mappedDrives = mappedDrives;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Enable/disable debug output
   * 
   * @param dbg boolean
   */
  public final void setDebug(boolean dbg) {
    m_debug = dbg;
  }
}
