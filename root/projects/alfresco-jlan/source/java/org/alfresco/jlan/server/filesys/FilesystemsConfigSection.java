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

package org.alfresco.jlan.server.filesys;

import java.util.Enumeration;

import org.alfresco.jlan.server.config.ConfigSection;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.core.SharedDeviceList;

/**
 * Filesystems Configuration Section Class
 *
 * @author gkspencer
 */
public class FilesystemsConfigSection extends ConfigSection {

  // Filesystems configuration section name
  
  public static final String SectionName = "Filesystems";
  
  //  List of shared devices

  private SharedDeviceList m_shareList;

  /**
   * Class constructor
   * 
   * @param config ServerConfiguration
   */
  public FilesystemsConfigSection(ServerConfiguration config) {
    super( SectionName, config);
    
    // Allocate the share list
    
    m_shareList = new SharedDeviceList();
  }

  /**
   * Return the shared device list.
   *
   * @return SharedDeviceList
   */
  public final SharedDeviceList getShares() {
    return m_shareList;
  }

  /**
   * Add a shared device to the server configuration.
   *
   * @param shr SharedDevice
   * @return boolean
   */
  public final boolean addShare(SharedDevice shr) {
    return m_shareList.addShare(shr);
  }
  
  /**
   * Close the configuration section
   */
  public final void closeConfig() {

    // Close the shared filesystems
    
    if ( getShares() != null && getShares().numberOfShares() > 0) {
      
      // Close the shared filesystems
        
      Enumeration<SharedDevice> shareEnum = getShares().enumerateShares();
        
      while ( shareEnum.hasMoreElements()) {
          
        SharedDevice share = shareEnum.nextElement();
        DeviceContext devCtx = share.getContext();
            
        if ( devCtx != null)
          devCtx.CloseContext();
      }
    }
  }
}
