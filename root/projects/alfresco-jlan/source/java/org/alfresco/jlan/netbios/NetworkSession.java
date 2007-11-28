package org.alfresco.jlan.netbios;

/*
 * NetworkSession.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Network Session Abstract Class
 * 
 * <p>Base class for client network sessions.
 */
public abstract class NetworkSession {

  //  Default socket timeout value
  
  private static int _defTimeout = 30000;   //  30 seconds, in milliseconds
  
  // Protocol name
  
  private String m_protoName;
  
  // Session send/receive timeout, in milliseconds
  
  private int m_tmo = _defTimeout;
  
  /**
   * Class constructor
   * 
   * @param protoName String
   */
  public NetworkSession(String protoName) {
    m_protoName = protoName;
  }
  
	/**
	 * Return the protocol name
	 * 
	 * @return String
	 */
	public final String getProtocolName() {
	  return m_protoName; 
  }
	
	/**
	 * Open a connection to a remote host
	 * 
	 * @param toName		Host name/address being called
	 * @param fromName	Local host name/address
	 * @param toAddr		Optional address of the remote host
	 * @exception		IOException
	 * @exception UnknownHostException
	 */
	public abstract void Open(String toName, String fromName, String toAddr)
		throws IOException, UnknownHostException;
	
	/**
	 * Determine if the session is connected to a remote host
	 * 
	 * @return boolean
	 */
	public abstract boolean isConnected();
	
	/**
	 * Check if the network session has data available
	 * 
	 * @return boolean
	 * @exception IOException
	 */
	public abstract boolean hasData()
		throws IOException;
	
  /**
   * Return the send/receive timeout, in milliseconds
   * 
   * @return int
   */
  public final int getTimeout() {
    return m_tmo;
  }
  
	/**
	 * Receive a data packet from the remote host.
	 *
	 * @param buf      Byte buffer to receive the data into.
	 * @return         Length of the received data.
	 * @exception      java.io.IOException   I/O error occurred.
	 */
	public abstract int Receive ( byte [] buf)
		throws IOException;

	/**
	 * Send a data packet to the remote host.
	 *
	 * @param data     Byte array containing the data to be sent.
	 * @param siz      Length of the data to send.
	 * @return         true if the data was sent successfully, else false.
	 * @exception      java.io.IOException   I/O error occurred.
	 */
	public abstract boolean Send ( byte [] data, int siz)
	  throws IOException;
	  
	/**
	 * Close the network session
	 * 
	 * @exception 		java.io.IOException		I/O error occurred
	 */
	public abstract void Close()
		throws IOException;
  
  /**
   * Set the send/receive timeout, in milliseconds
   * 
   * @param tmo int
   */
  public void setTimeout(int tmo) {
    m_tmo = tmo;
  }
  
  
  /**
   * Return the default socket timeout value
   * 
   * @return int
   */
  public static final int getDefaultTimeout() {
    return _defTimeout;
  }
  
  /**
   * Set the default socket timeout for new sessions
   * 
   * @param tmo int
   */
  public static final void setDefaultTimeout(int tmo) {
    _defTimeout = tmo;
  }
}
