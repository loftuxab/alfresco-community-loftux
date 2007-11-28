package org.alfresco.jlan.smb.server.win32;

/*
 * LanaListener.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

/**
 * LANA Listener Class
 * 
 * <p>Receive status change events for a particular NetBIOS LANA.
 */
public interface LanaListener {
  
  /**
   * LANA status change callback
   * 
   * @param lana int
   * @param online boolean
   */
  public void lanaStatusChange(int lana, boolean online);
}
