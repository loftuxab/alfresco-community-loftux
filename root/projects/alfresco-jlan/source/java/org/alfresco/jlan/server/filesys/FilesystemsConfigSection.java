package org.alfresco.jlan.server.filesys;

/*
 * FilesystemsConfigSection.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import java.util.Enumeration;

import org.alfresco.jlan.server.config.ConfigSection;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.core.SharedDeviceList;

/**
 * Filesystems Configuration Section Class
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
