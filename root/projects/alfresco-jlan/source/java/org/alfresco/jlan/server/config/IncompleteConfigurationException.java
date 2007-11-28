package org.alfresco.jlan.server.config;

/*
 * IncompleteConfigurationException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>Indicates that the server configuration is incomplete, and the server cannot be started.
 *
 * <p>The server name, domain name and network broadcast mask are the minimum parameters that must be specified
 * for a server configuration.
 */
public class IncompleteConfigurationException extends Exception {

  private static final long serialVersionUID = 6805142016306543355L;

  /**
   * IncompleteConfigurationException constructor.
   */
  public IncompleteConfigurationException() {
    super();
  }

  /**
   * IncompleteConfigurationException constructor.
   * 
   * @param s java.lang.String
   */
  public IncompleteConfigurationException(String s) {
    super(s);
  }
}