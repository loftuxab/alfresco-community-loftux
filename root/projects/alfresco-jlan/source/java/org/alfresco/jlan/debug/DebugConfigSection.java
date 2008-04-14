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

package org.alfresco.jlan.debug;

import org.alfresco.jlan.server.config.ConfigId;
import org.alfresco.jlan.server.config.ConfigSection;
import org.alfresco.jlan.server.config.ConfigurationListener;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;

/**
 * Debug Configuration Section Class
 *
 * @author gkspencer
 */
public class DebugConfigSection extends ConfigSection {

  // Debug configuration section name
  
  public static final String SectionName = "Debug";
  
  //  Debugging interface to use

  private DebugInterface m_debugDev;
  private ConfigElement m_debugParams;

  /**
   * Class constructor
   * 
   * @param config ServerConfiguration
   */
  public DebugConfigSection(ServerConfiguration config) {
    super( SectionName, config);
  }

  /**
   * Return the debug interface.
   *
   * @return DebugInterface
   */
  public final DebugInterface getDebug() {
    return m_debugDev;
  }

  /**
   * Return the debug device initialization parameters
   * 
   * @return ConfigElement
   */
  public final ConfigElement getDebugParameters() {
    return m_debugParams;
  }
  
  /**
   * Detemrine if the configuration has a valid debug interface.
   *
   * @return boolean
   */
  public final boolean hasDebug() {
    return m_debugDev != null ? true : false;
  }

  /**
   * Set the debug interface to be used to output debug messages.
   *
   * @param dbgClass String
   * @param params ConfigElement
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setDebug(String dbgClass, ConfigElement params)
    throws InvalidConfigurationException {

    int sts = ConfigurationListener.StsIgnored;
    
    try {

      //  Check if the debug device is being set
      
      if ( dbgClass != null) {    

        //  Validate the debug output class
    
        Object obj = Class.forName(dbgClass).newInstance();
      
        //  Check if the debug class implements the Debug interface
      
        if ( obj instanceof DebugInterface) {
        
          //  Initialize the debug output class
        
          DebugInterface dbg = (DebugInterface) obj;
          dbg.initialize(params);
        
          //  Inform listeners of the configuration change
          
          sts = fireConfigurationChange(ConfigId.DebugDevice, dbg);
          
          //  Set the debug class and initialization parameters
        
          m_debugDev    = dbg;
          m_debugParams = params;
          
          //  Update the global debug interface
          
          Debug.setDebugInterface(dbg);
        }
        else
          throw new InvalidConfigurationException("Debugclass does not implement the Debug interface");
      }
      else {
        
        //  Clear the debug device and parameters
        
        m_debugDev    = null;
        m_debugParams = null;

        //  Inform listeners of the configuration change
          
        sts = fireConfigurationChange(ConfigId.DebugDevice, null);
      }
    }
    catch (ClassNotFoundException ex) {
      throw new InvalidConfigurationException("Debugclass not found, " + dbgClass);
    }
    catch (IllegalAccessException ex) {
      throw new InvalidConfigurationException("Cannot load debugclass " + dbgClass + ", access error");
    }
    catch (InstantiationException ex) {
      throw new InvalidConfigurationException("Cannot instantiate debugclass " + dbgClass);
    }
    catch (Exception ex) {
      throw new InvalidConfigurationException("Failed to initialize debug class, " + ex.toString());
    }
    
    //  Return the change status
    
    return sts;
  }
}
