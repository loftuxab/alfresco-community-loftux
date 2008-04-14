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

package org.alfresco.jlan.server.filesys;

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.smb.nt.SecurityDescriptor;
import org.alfresco.jlan.smb.server.SMBSrvException;

/**
 * Security Descriptor Interface
 * 
 * <p>Optional interface that a DiskInterface driver can implement loading and saving of per file security descriptors.
 *
 * @author gkspencer
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
