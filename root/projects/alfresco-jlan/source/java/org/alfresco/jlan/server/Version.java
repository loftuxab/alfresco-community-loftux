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

package org.alfresco.jlan.server;

/**
 * Server Versions Class
 * 
 * <p>Holds the version strings for various server implementations.
 *
 * @author gkspencer
 */
public class Version {

  // Top level version
  
  public static String ReleaseVersion = "5.0.0";
  
  // Server version strings
  
  public static String SMBServerVersion       = ReleaseVersion;
  public static String NetBIOSServerVersion   = ReleaseVersion;
  
  public static String NFSServerVersion       = ReleaseVersion;
  public static String MountServerVersion     = ReleaseVersion;
  public static String PortMapServerVersion   = ReleaseVersion;
  
  public static String FTPServerVersion       = ReleaseVersion;
}
