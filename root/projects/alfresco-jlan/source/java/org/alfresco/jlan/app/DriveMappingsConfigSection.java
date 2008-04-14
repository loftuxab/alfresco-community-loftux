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

package org.alfresco.jlan.app;

import org.alfresco.jlan.server.config.ConfigId;
import org.alfresco.jlan.server.config.ConfigSection;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.smb.util.DriveMappingList;

/**
 *  Drive Mappings Configuration Section Class
 *
 * @author gkspencer
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
