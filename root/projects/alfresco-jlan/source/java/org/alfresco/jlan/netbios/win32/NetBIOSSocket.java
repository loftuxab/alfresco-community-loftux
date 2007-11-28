package org.alfresco.jlan.netbios.win32;

/*
 * NetBIOSSocket.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import org.alfresco.jlan.netbios.NetBIOSName;

/**
 * NetBIOS Socket Class
 * 
 * <p>Contains the details of a Winsock NetBIOS socket that was opened using native code.
 */
public class NetBIOSSocket {
  
  // Flag to indicate if the NetBIOS socket interface has been initialized

  private static boolean _nbSocketInit;

  // NetBIOS LANA that the socket is associated with

  private int m_lana;

  // Socket pointer (Windows SOCKET)

  private int m_socket;

  // NetBIOS name, either listening name or callers name

  private NetBIOSName m_nbName;

  // Flag to indicate if this is a listener socket

  private boolean m_listenerSocket;

  /**
   * Initialize the Winsock NetBIOS interface
   */
  public static final void initializeSockets() throws WinsockNetBIOSException {

    // Check if the NetBIOS socket interface has been initialized

    if (_nbSocketInit == false) {
      
      // Initialize the NetBIOS socket interface

      Win32NetBIOS.InitializeSockets();

      // Indicate that the NetBIOS socket interface is initialized

      _nbSocketInit = true;
    }
  }

  /**
   * Shutdown the Winsock NetBIOS interface
   */
  public static final void shutdownSockets() {
    
    // Check if the NetBIOS socket interface has been initialized

    if (_nbSocketInit == true) {
      
      // Indicate that the NetBIOS socket interface is not initialized

      _nbSocketInit = false;

      // Initialize the NetBIOS socket interface

      Win32NetBIOS.ShutdownSockets();
    }
  }

  /**
   * Determine if the Winsock NetBIOS interface is initialized
   * 
   * @return boolean
   */
  public static final boolean isInitialized() {
    return _nbSocketInit;
  }

  /**
   * Create a NetBIOS socket to listen for incoming sessions on the specified LANA
   * 
   * @param lana int
   * @param nbName NetBIOSName
   * @return NetBIOSSocket
   * @exception NetBIOSSocketException
   * @exception WinsockNetBIOSException
   */
  public static final NetBIOSSocket createListenerSocket(int lana, NetBIOSName nbName) throws WinsockNetBIOSException,
      NetBIOSSocketException {
    
    // Initialize the Winsock NetBIOS interface

    initializeSockets();

    // Create a new NetBIOS socket

    int sockPtr = Win32NetBIOS.CreateSocket(lana);
    if (sockPtr == 0)
      throw new NetBIOSSocketException("Failed to create NetBIOS socket");

    // Bind the socket to a NetBIOS name

    if (Win32NetBIOS.BindSocket(sockPtr, nbName.getNetBIOSName()) != 0)
      throw new NetBIOSSocketException("Failed to bind NetBIOS socket");

    // Return the NetBIOS socket

    return new NetBIOSSocket(lana, sockPtr, nbName, true);
  }

  /**
   * Class constructor
   * 
   * @param lana int
   * @param sockPtr int
   * @param nbName NetBIOSName
   * @param listener boolean
   */
  private NetBIOSSocket(int lana, int sockPtr, NetBIOSName nbName, boolean listener) {
    m_lana = lana;
    m_nbName = nbName;
    m_socket = sockPtr;

    m_listenerSocket = listener;
  }

  /**
   * Return the NetBIOS LANA the socket is associated with
   * 
   * @return int
   */
  public final int getLana() {
    return m_lana;
  }

  /**
   * Determine if this is a listener type socket
   * 
   * @return boolean
   */
  public final boolean isListener() {
    return m_listenerSocket;
  }

  /**
   * Determine if the socket is valid
   * 
   * @return boolean
   */
  public final boolean hasSocket() {
    return m_socket != 0 ? true : false;
  }

  /**
   * Return the socket pointer
   * 
   * @return int
   */
  public final int getSocket() {
    return m_socket;
  }

  /**
   * Return the NetBIOS name. For a listening socket this is the local name, for
   * a session socket this is the remote callers name.
   * 
   * @return NetBIOSName
   */
  public final NetBIOSName getName() {
    return m_nbName;
  }

  /**
   * Write data to the session socket
   * 
   * @param buf byte[]
   * @param off int
   * @param len int
   * @return int
   * @exception WinsockNetBIOSException
   */
  public final int write(byte[] buf, int off, int len) throws WinsockNetBIOSException {
    return Win32NetBIOS.SendSocket(getSocket(), buf, off, len);
  }

  /**
   * Read data from the session socket
   * 
   * @param buf
   *          byte[]
   * @param off
   *          int
   * @param maxLen
   *          int
   * @return int
   * @exception WinsockNetBIOSException
   */
  public final int read(byte[] buf, int off, int maxLen) throws WinsockNetBIOSException {
    return Win32NetBIOS.ReceiveSocket(getSocket(), buf, off, maxLen);
  }

  /**
   * Listen for an incoming session connection and create a session socket for
   * the new session
   * 
   * @return NetBIOSSocket
   * @exception NetBIOSSocketException
   * @exception winsockNetBIOSException
   */
  public final NetBIOSSocket listen() throws WinsockNetBIOSException, NetBIOSSocketException {
    
    // Check if this socket is a listener socket, and the socket is valid

    if (isListener() == false)
      throw new NetBIOSSocketException("Not a listener type socket");

    if (hasSocket() == false)
      throw new NetBIOSSocketException("NetBIOS socket not valid");

    // Wait for an incoming session request

    byte[] callerName = new byte[NetBIOSName.NameLength];

    int sessSockPtr = Win32NetBIOS.ListenSocket(getSocket(), callerName);
    if (sessSockPtr == 0)
      throw new NetBIOSSocketException("NetBIOS socket listen failed");

    // Return the new NetBIOS socket session

    return new NetBIOSSocket(getLana(), sessSockPtr, new NetBIOSName(callerName, 0), false);
  }

  /**
   * Close the socket
   */
  public final void closeSocket() {
    
    // Close the native socket, if valid

    if (hasSocket()) {
      Win32NetBIOS.CloseSocket(getSocket());
      setSocket(0);
    }
  }

  /**
   * Set the socket pointer
   * 
   * @param sockPtr int
   */
  protected final void setSocket(int sockPtr) {
    m_socket = sockPtr;
  }

  /**
   * Return the NetBIOS socket details as a string
   * 
   * @return String
   */
  public String toString() {
    
    StringBuffer str = new StringBuffer();

    str.append("[LANA:");
    str.append(getLana());
    str.append(",Name:");
    str.append(getName());
    str.append(",Socket:");
    if (hasSocket()) {
      str.append("0x");
      str.append(Integer.toHexString(getSocket()));
    } else
      str.append("<None>");

    if (isListener())
      str.append(",Listener");

    str.append("]");

    return str.toString();
  }
}
