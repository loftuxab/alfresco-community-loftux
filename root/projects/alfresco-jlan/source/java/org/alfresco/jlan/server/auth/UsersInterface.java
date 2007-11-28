package org.alfresco.jlan.server.auth;

/*
 * UsersInterface.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;


/**
 * Users Interface
 * 
 * <p>Provides the user account information for the authenticator class.
 */
public interface UsersInterface {

  /**
   * Initialize the users interface
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   * @exception InvalidConfigurationException
   */
  void initializeUsers(ServerConfiguration config, ConfigElement params)
    throws InvalidConfigurationException;
  
  /**
   * Return the specified user account details
   * 
   * @param userName String
   * @return UserAccount
   */
  UserAccount getUserAccount( String userName);
}
