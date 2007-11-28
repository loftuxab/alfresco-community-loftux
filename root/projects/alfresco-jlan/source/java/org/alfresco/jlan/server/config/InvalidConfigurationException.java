package org.alfresco.jlan.server.config;

/*
 * InvalidConfigurationException.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * <p>Indicates that one or more parameters in the server configuration are not valid.
 */
public class InvalidConfigurationException extends Exception {

  private static final long serialVersionUID = 4660972667850041322L;

  //	Chained exception details
	
  private Exception m_exception;
	
  /**
   * InvalidConfigurationException constructor.
   */
  public InvalidConfigurationException() {
    super();
  }

  /**
   * InvalidConfigurationException constructor.
   * 
   * @param s java.lang.String
   */
  public InvalidConfigurationException(String s) {
    super(s);
  }

	/**
	 * InvalidConfigurationException constructor.
	 * 
	 * @param s java.lang.String
	 * @param ex Exception
	 */
	public InvalidConfigurationException(String s, Exception ex) {
		super(s, ex);
		m_exception = ex;
	}
	
	/**
	 * Check if there is a chained exception
	 * 
	 * @return boolean
	 */
	public final boolean hasChainedException() {
		return m_exception != null ? true : false;
	}
	
	/**
	 * Return the chained exception details
	 * 
	 * @return Exception
	 */
	public final Exception getChainedException() {
		return m_exception;
	}
}