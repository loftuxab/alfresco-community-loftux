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

package org.alfresco.jlan.smb.util;

/**
 * Network Drive Mapping Class
 * 
 * <p>Contains the details of a network drive mapping that may be created using the Win32Utils MapNetworkDrive
 * method.
 *
 * @author gkspencer
 */
public class DriveMapping {

  //	Local drive name, or '*' to allocate the first available drive
  
  private String m_localDrive;
  
  //	UNC path of the remote share to map the drive to
  
  private String m_remotePath;
  
  //	Username and password to use for the drive mapping
  //
  //	If the username or password is null the default value is used (ie. the logged in users credentials)
  
  private String m_userName;
  private String m_password;
  
  //	Flags to control if the user is prompted for a username/password if the supplied
  //	values do not work or to always prompt the user.
  
  private boolean m_interactive;		//	Prompt the user if the username/password fail
  private boolean m_prompt;					//	Always prompt the user
  
  /**
   * Class constructor
   * 
   * @param localDrive String
   * @param remotePath String
   * @param userName String
   * @param password String
   * @param interactive boolean
   * @param prompt boolean 
   */
  public DriveMapping(String localDrive, String remotePath, String userName, String password, boolean interactive,
      								boolean prompt) {

    //	Save the values
    
    m_localDrive = localDrive;
    m_remotePath = remotePath;
    
    m_userName = userName;
    m_password = password;
    
    m_interactive = interactive;
    m_prompt      = prompt;
  }
  
  /**
   * Return the local drive path
   * 
   * @return String
   */
  public final String getLocalDrive() {
    return m_localDrive;
  }
  
  /**
   * Return the remote path
   * 
   * @return String
   */
  public final String getRemotePath() {
    return m_remotePath;
  }
  
  /**
   * Return the user name
   * 
   * @return String
   */
  public final String getUserName() {
    return m_userName;
  }
  
  /**
   * Return the password
   * 
   * @return String
   */
  public final String getPassword() {
    return m_password;
  }
  
  /**
   * Return the interactive flag 
   * 
   * @return boolean
   */
  public final boolean hasInteractive() {
    return m_interactive;
  }
  
  /**
   * Return the prompt flag
   * 
   * @return boolean
   */
  public final boolean hasPrompt() {
    return m_prompt;
  }
  
  /**
   * Return the drive mapping as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[");
    str.append(getLocalDrive());
    str.append(",");
    str.append(getRemotePath());
    str.append(",");
    str.append(getUserName());
    str.append(":");
    str.append(getPassword());
    
    if ( hasInteractive())
      str.append(",Interactive");
    
    if ( hasPrompt())
      str.append(",Prompt");
    
    str.append("]");
    
    return str.toString();
  }
}
