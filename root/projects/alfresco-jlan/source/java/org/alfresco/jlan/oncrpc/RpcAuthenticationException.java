package org.alfresco.jlan.oncrpc;

/*
 * RpcAuthenticationException.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * RPC Authentication Exception Class
 */
public class RpcAuthenticationException extends Exception {

  private static final long serialVersionUID = 8169939638737905039L;
  
  //	Authentication failure error code
  
  private int m_authError;
  
  /**
   * Class constructor
   * 
   * @param authError int
   */
  public RpcAuthenticationException(int authError) {
    m_authError = authError;
  }

  /**
   * Class constructor
   * 
   * @param authError int
   * @param msg String
   */
  public RpcAuthenticationException(int authError, String msg) {
    super(msg);
    m_authError = authError;
  }
  
  /**
   * Get the authentication error code
   * 
   * @return int
   */
  public final int getAuthenticationErrorCode() {
    return m_authError;
  }
}
