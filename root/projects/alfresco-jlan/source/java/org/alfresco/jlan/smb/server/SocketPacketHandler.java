package org.alfresco.jlan.smb.server;

/*
 * SocketPacketHandler.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Socket Packet Handler Class
 * 
 * <p>Provides the base class for Java Socket based packet handler implementations.
 */
public abstract class SocketPacketHandler extends PacketHandler {

  // Socket that this session is using.

  private Socket m_socket;

  // Input/output streams for receiving/sending SMB requests.

  private DataInputStream m_in;
  private DataOutputStream m_out;

  /**
   * Class constructor
   * 
   * @param sock Socket
   * @param typ int
   * @param name String
   * @param shortName String
   * @exception IOException   If a network error occurs
   */
  public SocketPacketHandler(Socket sock, int typ, String name, String shortName)
    throws IOException {
    
    super(typ, name, shortName);
    
    m_socket = sock;

    //  Set socket options
    
    sock.setTcpNoDelay(true);
    
    //  Open the input/output streams
    
    m_in  = new DataInputStream(m_socket.getInputStream());
    m_out = new DataOutputStream(m_socket.getOutputStream());
    
    // Set the remote address
    
    setRemoteAddress(m_socket.getInetAddress());
  }
  
  /**
   * Return the count of available bytes in the receive input stream
   * 
   * @return int
   * @exception IOException   If a network error occurs.
   */
  public int availableBytes()
    throws IOException {
    if ( m_in != null)
      return m_in.available();
    return 0;
  }
  
  /**
   * Read a packet
   * 
   * @param pkt byte[]
   * @param off int
   * @param len int
   * @return int
   * @exception IOException   If a network error occurs.
   */
  public int readPacket(byte[] pkt, int off, int len)
    throws IOException {
      
    //  Read a packet of data
    
    if ( m_in != null)
      return m_in.read(pkt,off,len);
    return 0;
  }
  
  /**
   * Send an SMB request packet
   * 
   * @param pkt byte[]
   * @param off int
   * @param len int
   * @exception IOException   If a network error occurs.
   */
  public void writePacket(byte[] pkt, int off, int len)
    throws IOException {

    //  Output the raw packet
    
    if ( m_out != null)
      m_out.write(pkt, off, len);
  }

  /**
   * Flush the output socket
   * 
   * @exception IOException   If a network error occurs
   */
  public void flushPacket()
    throws IOException {
    if ( m_out != null)
      m_out.flush();    
  }
  
  /**
   * Close the protocol handler
   */
  public void closeHandler() {

    //  Close the input stream
    
    if ( m_in != null) {
      try {
        m_in.close();
      }
      catch (Exception ex) {
      }
      m_in = null;
    }
    
    //  Close the output stream
    
    if ( m_out != null) {
      try {
        m_out.close();
      }
      catch (Exception ex) {
      }
      m_out = null;
    }

    //  Close the socket
    
    if (m_socket != null) {
      try {
        m_socket.close();
      }
      catch (Exception ex) {
      }
      m_socket = null;
    }
  }
}
