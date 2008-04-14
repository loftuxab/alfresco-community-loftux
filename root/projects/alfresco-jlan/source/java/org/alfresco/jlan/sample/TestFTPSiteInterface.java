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

package org.alfresco.jlan.sample;

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
 *
 * @author gkspencer
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
