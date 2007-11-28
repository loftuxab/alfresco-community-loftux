package org.alfresco.jlan.netbios.win32;

/*
 * WinsockNetBIOSException.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import java.io.IOException;

/**
 * Winsock NetBIOS Exception Class
 * 
 * <p>
 * Contains the Winsock error code from the failed Winsock call.
 */
public class WinsockNetBIOSException extends IOException {

  // Object version
  
  private static final long serialVersionUID = -5776000315712407725L;
  
  // Winsock error code

  private int m_errCode;

  /**
   * Default constructor
   */
  public WinsockNetBIOSException() {
    super();
  }

  /**
   * Class constructor
   * 
   * @param msg
   *          String
   */
  public WinsockNetBIOSException(String msg) {
    super(msg);

    // Split out the error code

    if (msg != null) {
      int pos = msg.indexOf(":");
      if (pos != -1)
        m_errCode = Integer.valueOf(msg.substring(0, pos)).intValue();
    }
  }

  /**
   * Class constructor
   * 
   * @param sts
   *          int
   */
  public WinsockNetBIOSException(int sts) {
    super();

    m_errCode = sts;
  }

  /**
   * Return the Winsock error code
   * 
   * @return int
   */
  public final int getErrorCode() {
    return m_errCode;
  }

  /**
   * Set the error code
   * 
   * @param sts
   *          int
   */
  public final void setErrorCode(int sts) {
    m_errCode = sts;
  }

  /**
   * Return the error message string
   * 
   * @return String
   */
  public String getMessage() {
    StringBuffer msg = new StringBuffer();

    msg.append(super.getMessage());
    String winsockErr = WinsockError.asString(getErrorCode());
    if (winsockErr != null) {
      msg.append(" - ");
      msg.append(winsockErr);
    }

    return msg.toString();
  }
}
