package org.alfresco.jlan.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.netbios.NetBIOSNameList;
import org.alfresco.jlan.netbios.NetBIOSSession;
import org.alfresco.jlan.netbios.win32.Win32NetBIOS;

/*
 * LocalServer.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

/**
 * Local Server Class
 * 
 * @author gkspencer
 */
public class LocalServer {

  // Local server name and domain
  
  private static String m_localName;
  private static String m_localDomain;
  
  /**
   * Get the local server name and optionally trim the domain name
   * 
   * @param trimDomain boolean
   * @return String
   */
  public static final String getLocalServerName(boolean trimDomain) {

    // Check if the name has already been set

    if (m_localName != null)
      return m_localName;

    // Find the local server name

    String srvName = null;

    if (Platform.isPlatformType() == Platform.Type.WINDOWS) {
      
      // Get the local name via JNI

      srvName = Win32NetBIOS.GetLocalNetBIOSName();
    }
    else {
      
      // Get the DNS name of the local system

      try {
        srvName = InetAddress.getLocalHost().getHostName();
      }
      catch (UnknownHostException ex) {
      }
    }

    // Strip the domain name

    if (trimDomain && srvName != null) {
      int pos = srvName.indexOf(".");
      if (pos != -1)
        srvName = srvName.substring(0, pos);
    }

    // Save the local server name

    m_localName = srvName;

    // Return the local server name

    return srvName;
  }

  /**
   * Get the local domain/workgroup name
   * 
   * @return String
   */
  public static final String getLocalDomainName() {

    // Check if the local domain has been set

    if (m_localDomain != null)
      return m_localDomain;

    // Find the local domain name

    String domainName = null;

    if (Platform.isPlatformType() == Platform.Type.WINDOWS) {
      
      // Get the local domain/workgroup name via JNI

      domainName = Win32NetBIOS.GetLocalDomainName();
    }
    else {
      
      NetBIOSName nbName = null;

      try {
        // Try and find the browse master on the local network

        nbName = NetBIOSSession.FindName(NetBIOSName.BrowseMasterName, NetBIOSName.BrowseMasterGroup, 5000);

        // Get the NetBIOS name list from the browse master

        NetBIOSNameList nbNameList = NetBIOSSession.FindNamesForAddress(nbName.getIPAddressString(0));
        if (nbNameList != null) {
          nbName = nbNameList.findName(NetBIOSName.MasterBrowser, false);
          
          // Set the domain/workgroup name
          
          if (nbName != null)
            domainName = nbName.getName();
        }
      }
      catch (IOException ex) {
      }
    }

    // Save the local domain name

    m_localDomain = domainName;

    // Return the local domain/workgroup name

    return domainName;
  }
}
