package org.alfresco.jlan.ftp;


/*
 * FTPSiteInterface.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;

/**
 * FTP SITE Command Interface
 * 
 * <p>Optional interface that is used to provide processing for the FTP SITE command.
 */
public interface FTPSiteInterface {

  /**
   * Initialize the site interface
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   */
  void initializeSiteInterface( ServerConfiguration config, ConfigElement params);
  
  /**
   * Process an FTP SITE specific command
   * 
   * @param sess FTPSrvSession
   * @param req FTPRequest
   */
  void processFTPSiteCommand( FTPSrvSession sess, FTPRequest req)
    throws IOException;
}
