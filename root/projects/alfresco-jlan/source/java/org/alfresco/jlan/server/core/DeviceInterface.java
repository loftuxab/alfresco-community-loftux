package org.alfresco.jlan.server.core;

/*
 * DeviceInterface.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.config.ConfigElement;


/**
 * The device interface is the base of the shared device interfaces that are used by
 * shared devices on the SMB server.
 */
public interface DeviceInterface {
  
  /**
   * Parse and validate the parameter string and create a device context object for this instance
   * of the shared device. The same DeviceInterface implementation may be used for multiple shares.
   * 
   * @param shareName String
   * @param args ConfigElement
   * @return DeviceContext
   * @exception DeviceContextException
   */
	public DeviceContext createContext(String shareName, ConfigElement args)
		throws DeviceContextException;
    
  /**
   * Connection opened to this disk device
   * 
   * @param sess					Server session
   * @param tree         	Tree connection
   */
  public void treeOpened(SrvSession sess, TreeConnection tree);
  
  /**
   * Connection closed to this device
   * 
   * @param sess					Server session
   * @param tree         	Tree connection
   */
  public void treeClosed(SrvSession sess, TreeConnection tree);
}