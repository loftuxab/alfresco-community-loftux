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

package org.alfresco.jlan.server.auth;

import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.SecurityConfigSection;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;

/**
 * Default Users Interface
 * 
 * <p>Use the user account list from the server configuration to provide the user details.
 *
 * @author gkspencer
 */
public class DefaultUsersInterface implements UsersInterface {

  // Security configuration containing the user list
  
  private SecurityConfigSection m_securityConfig;
  
  /**
   * Return the specified user account details
   * 
   * @param userName String
   * @return UserAccount
   */
  public UserAccount getUserAccount(String userName) {
    
    //  Get the user account list from the configuration 
    
    UserAccountList userList = m_securityConfig.getUserAccounts();
    if ( userList == null || userList.numberOfUsers() == 0)
      return null;
      
    //  Search for the required user account record
    
    return userList.findUser(userName);
  }

  /**
   * Initialize the users interface
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   * @exception InvalidConfigurationException
   */
  public void initializeUsers(ServerConfiguration config, ConfigElement params)
    throws InvalidConfigurationException {

    // Save the security configuration to access the user account list
    
    m_securityConfig = (SecurityConfigSection) config.getConfigSection( SecurityConfigSection.SectionName);
  }
}
