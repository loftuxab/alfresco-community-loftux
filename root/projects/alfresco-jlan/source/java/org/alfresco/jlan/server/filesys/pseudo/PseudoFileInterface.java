package org.alfresco.jlan.server.filesys.pseudo;

/*
 * PseudoFileInterface.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.TreeConnection;

/**
 * Pseudo File Interface
 * 
 * <p>
 * Provides the ability to add files into the file listing of a folder.
 * 
 * @author gkspencer
 */
public interface PseudoFileInterface {
  
  /**
   * Check if the specified path refers to a pseudo file
   * 
   * @param sess SrvSession
   * @param tree TreeConnection
   * @param path String
   * @return boolean
   */
  public boolean isPseudoFile(SrvSession sess, TreeConnection tree, String path);

  /**
   * Return the pseudo file for the specified path, or null if the path is not a pseudo file
   * 
   * @param sess SrvSession
   * @param tree TreeConnection
   * @param path String
   * @return PseudoFile
   */
  public PseudoFile getPseudoFile(SrvSession sess, TreeConnection tree, String path);

  /**
   * Add pseudo files to a folder so that they appear in a folder search
   * 
   * @param sess SrvSession
   * @param tree TreeConnection
   * @param path String
   * @return int
   */
  public int addPseudoFilesToFolder(SrvSession sess, TreeConnection tree, String path);

  /**
   * Delete a pseudo file
   * 
   * @param sess SrvSession
   * @param tree TreeConnection
   * @param path String
   */
  public void deletePseudoFile(SrvSession sess, TreeConnection tree, String path);
}
