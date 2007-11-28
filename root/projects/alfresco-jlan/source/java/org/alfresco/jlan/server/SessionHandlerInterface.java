package org.alfresco.jlan.server;

/*
 * SessionHandlerInterface.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

/**
 * Session Handler Interface
 * 
 * <p>Implemented by classes that wait for an incoming session request.
 */
public interface SessionHandlerInterface {

  /**
   * Return the protocol name
   * 
   * @return String
   */
  public String getHandlerName();
  
  /**
   * Initialize the session handler
   * 
   * @param server NetworkServer
   * @exception IOException
   */
  public void initializeSessionHandler(NetworkServer server)
  	throws IOException;
  
  /**
   * Close the session handler
   */
  public void closeSessionHandler(NetworkServer server);
}
