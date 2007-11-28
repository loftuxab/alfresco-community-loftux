package org.alfresco.jlan.sample;

/*
 * TestFTPSiteInterface.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

import org.alfresco.jlan.ftp.FTPRequest;
import org.alfresco.jlan.ftp.FTPSiteInterface;
import org.alfresco.jlan.ftp.FTPSrvSession;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;


/**
 * Test FTP Site Interface Class
 * 
 * <p>Implements the FTPSiteInterface to accept custom SITE commands.
 */
public class TestFTPSiteInterface implements FTPSiteInterface {

  /**
   * Initialize the FTP site interface
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   */
  public void initializeSiteInterface(ServerConfiguration config, ConfigElement params) {
  }

  /**
   * Process the FTP SITE command
   * 
   * @param sess FTPSrvSession
   * @param req FTPRequest
   */
  public void processFTPSiteCommand(FTPSrvSession sess, FTPRequest req)
    throws IOException {

    // DEBUG
    
    if ( sess.hasDebug( FTPSrvSession.DBG_INFO))
      sess.debugPrintln( "SITE command " + req.getArgument());
    
    // Echo the user request
    
    sess.sendFTPResponse( 200, "Site request : " + req.getArgument());
  }
}
