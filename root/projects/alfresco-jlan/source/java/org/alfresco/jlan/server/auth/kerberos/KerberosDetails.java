package org.alfresco.jlan.server.auth.kerberos;

/*
 * KerberosDetails.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import org.ietf.jgss.GSSName;

/**
 * Kerberos Details Class
 * 
 * <p>Holds the Kerberos response token and session details about the user.
 */
public class KerberosDetails {

  // Source and target details

  private String m_krbSource;

  private String m_krbTarget;

  // Kerberos response token

  private byte[] m_krbResponse;

  /**
   * Class constructor
   * 
   * @param source GSSName
   * @param target GSSName
   * @param response byte[]
   */
  public KerberosDetails(GSSName source, GSSName target, byte[] response) {

    m_krbSource = source.toString();
    m_krbTarget = target.toString();

    m_krbResponse = response;
  }

  /**
   * Return the context initiator for the Kerberos authentication
   * 
   * @return String
   */
  public final String getSourceName() {

    return m_krbSource;
  }

  /**
   * Return the context acceptor for the Kerberos authentication
   * 
   * @return String
   */
  public final String getTargetName() {

    return m_krbTarget;
  }

  /**
   * Return the Kerberos response token
   * 
   * @return byte[]
   */
  public final byte[] getResponseToken() {

    return m_krbResponse;
  }

  /**
   * Parse the source name to return the user name part only
   * 
   * @return String
   */
  public final String getUserName() {

    String userName = m_krbSource;

    if (m_krbSource != null) {
      int pos = m_krbSource.indexOf('@');
      if (pos != -1) {
        userName = m_krbSource.substring(0, pos);
      }
    }

    return userName;
  }

  /**
   * Return the response token length
   * 
   * @return int
   */
  public final int getResponseLength() {

    return m_krbResponse != null ? m_krbResponse.length : 0;
  }

  /**
   * Return the Kerberos authentication details as a string
   * 
   * @return String
   */
  public String toString() {

    StringBuffer str = new StringBuffer();

    str.append("[Source=");
    str.append(getSourceName());
    str.append(",Target=");
    str.append(getTargetName());
    str.append(":Response=");
    str.append(getResponseLength());
    str.append(" bytes]");

    return str.toString();
  }
}
