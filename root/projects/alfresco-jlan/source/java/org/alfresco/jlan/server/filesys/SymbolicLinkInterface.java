package org.alfresco.jlan.server.filesys;

/*
 * SymbolicLinkInterface.java
 *
 * Copyright (c) Starlasoft 2006. All rights reserved.
 */

import java.io.FileNotFoundException;

import org.alfresco.jlan.server.SrvSession;


/**
 * Symbolic Link Interface
 * 
 * <p>Optional interface that a filesystem driver can implement to indicate that symbolic links are supported.
 */
public interface SymbolicLinkInterface {

  /**
   * Determine if symbolic links are enabled
   * 
   * @param sess SrvSession
   * @param tree TreeConnection
   * @return boolean
   */
  public boolean hasSymbolicLinksEnabled(SrvSession sess, TreeConnection tree);
  
  /**
   * Read the link data for a symbolic link
   * 
   * @param sess SrvSession
   * @param tree TreeConnection
   * @param path String
   * @return String
   * @exception AccessDeniedException
   * @exception FileNotFoundException
   */
  public String readSymbolicLink( SrvSession sess, TreeConnection tree, String path)
    throws AccessDeniedException, FileNotFoundException;
}
