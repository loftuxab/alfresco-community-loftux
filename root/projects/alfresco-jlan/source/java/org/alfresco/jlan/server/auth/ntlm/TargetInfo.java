package org.alfresco.jlan.server.auth.ntlm;

/*
 * TargetInfo.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

/**
 * Target Information Class
 * 
 * <p>Contains the target information from an NTLM message.
 */
public class TargetInfo
{
  // Target type and name
  
  private int m_type;
  private String m_name;
  
  /**
   * Class constructor
   * 
   * @param type int
   * @param name String
   */
  public TargetInfo(int type, String name) {
    m_type = type;
    m_name = name;
  }
  
  /**
   * Return the target type
   * 
   * @return int
   */
  public final int isType() {
    return m_type;
  }
  
  /**
   * Return the target name
   * 
   * @return String
   */
  public final String getName() {
    return m_name;
  }
  
  /**
   * Return the target information as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[");
    str.append(getTypeAsString(isType()));
    str.append(":");
    str.append(getName());
    str.append("]");
    
    return str.toString();
  }
  
  /**
   * Return the target type as a string
   * 
   * @param typ int
   * @return String
   */
  public final static String getTypeAsString(int typ)
  {
    String typStr = null;
    
    switch ( typ) {
    case NTLM.TargetServer:
      typStr = "Server";
      break;
    case NTLM.TargetDomain:
      typStr = "Domain";
      break;
    case NTLM.TargetFullDNS:
      typStr = "DNS";
      break;
    case NTLM.TargetDNSDomain:
      typStr = "DNS Domain";
      break;
    default:
      typStr = "Unknown 0x" + Integer.toHexString(typ);
      break;
    }
    
    return typStr;
  }
}
