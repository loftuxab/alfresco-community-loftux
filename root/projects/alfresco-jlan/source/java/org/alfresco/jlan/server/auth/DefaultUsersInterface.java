package org.alfresco.jlan.server.auth;

/*
 * DefaultUsersInterface.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.SecurityConfigSection;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;

/**
 * Default Users Interface
 * 
 * <p>Use the user account list from the server configuration to provide the user details.
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
