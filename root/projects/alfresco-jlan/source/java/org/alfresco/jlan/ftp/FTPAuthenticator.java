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

package org.alfresco.jlan.ftp;

import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.springframework.extensions.config.ConfigElement;

/**
 * FTP Authenticator Interface
 *
 * @author gkspencer
 */
public interface FTPAuthenticator {

  /**
   * Initialize the authenticator
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   * @exception InvalidConfigurationException
   */
  public void initialize(ServerConfiguration config, ConfigElement params)
    throws InvalidConfigurationException;
  
  /**
   * Authenticate the user
   * 
   * @param cInfo ClientInfo
   * @param sess FTPSrvSession
   * @return boolean
   */
  public boolean authenticateUser( ClientInfo cInfo, FTPSrvSession sess);
  
  /**
   * Close the authenticator, perform any cleanup
   */
   public void closeAuthenticator();
}
