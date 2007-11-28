package org.alfresco.jlan.server.auth;

/*
 * AuthenticatorException.java
 *
 * Copyright (c) Starlasoft 2006. All rights reserved.
 */

/**
 * Authenticator Exception Class
 */
public class AuthenticatorException extends Exception
{
  private static final long serialVersionUID = 7816213724352083486L;

  /**
   * Default constructor.
   */
  public AuthenticatorException() {
    super();
  }

  /**
   * Class constructor.
   * 
   * @param s String
   */
  public AuthenticatorException(String s) {
    super(s);
  }
}
