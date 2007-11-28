package org.alfresco.jlan.server.filesys;

/*
 * SecurityDescriptorInterface.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.smb.nt.SecurityDescriptor;
import org.alfresco.jlan.smb.server.SMBSrvException;

/**
 * Security Descriptor Interface
 * 
 * <p>Optional interface that a DiskInterface driver can implement loading and saving of per file security descriptors.
 */
public interface SecurityDescriptorInterface {

  /**
   * Return the security descriptor length for the specified file
   * 
   * @param sess      Server session
   * @param tree      Tree connection
   * @param netFile   Network file
   * @return int
   * @exception SMBSrvException
   */
  public int getSecurityDescriptorLength(SrvSession sess, TreeConnection tree, NetworkFile netFile)
    throws SMBSrvException;

  /**
   * Load a security descriptor for the specified file
   * 
   * @param sess      Server session
   * @param tree      Tree connection
   * @param netFile   Network file
   * @return SecurityDescriptor
   * @exception SMBSrvException
   */
  public SecurityDescriptor loadSecurityDescriptor(SrvSession sess, TreeConnection tree, NetworkFile netFile)
    throws SMBSrvException;

  /**
   * Save the security descriptor for the specified file
   * 
   * @param sess      Server session
   * @param tree      Tree connection
   * @param netFile   Network file
   * @param secDesc   Security descriptor
   * @exception SMBSrvException
   */
  public void saveSecurityDescriptor(SrvSession sess, TreeConnection tree, NetworkFile netFile, SecurityDescriptor secDesc)
    throws SMBSrvException;
}
