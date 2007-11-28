package org.alfresco.jlan.ftp;

/*
 * FTPAuthenticator.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;

/**
 * FTP Authenticator Interface
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
}
