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

package org.alfresco.jlan.server.config;

/**
 * Configuration Section Abstract Class
 *
 * @author gkspencer
 */
public class ConfigSection {

  // Configuration section name
  
  private String m_name;
  
  // Server configuration that this section is associated with
  
  private ServerConfiguration m_config;

  // Flag to indicate that this section has been updated
  
  private boolean m_updated;
  
  /**
   * Class constructor
   * 
   * @param name String
   * @param config ServerConfiguration
   */
  protected ConfigSection(String name, ServerConfiguration config) {
    m_name   = name;
    m_config = config;
    
    if ( m_config != null)
      m_config.addConfigSection( this);
  }
  
  /**
   * Return the configuration section name, used to identify the configuration
   * 
   * @return String
   */
  public String getSectionName() {
    return m_name;
  }

  /**
   * Check if this configuration section has been updated
   * 
   * @return boolean
   */
  public final boolean isUpdated() {
    return m_updated;
  }
  
  /**
   * Return the server configuration that this section is associated with
   * 
   * @return ServerConfiguration
   */
  protected final ServerConfiguration getServerConfiguration() {
    return m_config;
  }
  
  /**
   * Notify all registered configuration change listeners of a configuration change
   * 
   * @param id int
   * @param newVal Object
   * @return int
   * @throws InvalidConfigurationException
   */
  protected final int fireConfigurationChange(int id, Object newVal)
    throws InvalidConfigurationException {

    // Listeners are registered with the main server configuration container
    
    int sts = -1;
    
    if ( m_config != null)
      sts = m_config.fireConfigurationChange(id, newVal);
    
    // Check if the configuration change was accepted
    
    if ( sts >= ConfigurationListener.StsAccepted)
      setUpdated( true);
    
    //  Return the status
    
    return sts;
  }
  
  /**
   * Set/clear the configuration section updated flag
   * 
   * @param upd boolean
   */
  protected final void setUpdated( boolean upd) {
    m_updated = upd;
  }
  
  /**
   * Close the configuration section, perform any cleanup
   */
  public void closeConfig() {
  }
}
