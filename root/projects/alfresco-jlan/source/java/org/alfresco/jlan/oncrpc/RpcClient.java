package org.alfresco.jlan.oncrpc;

/*
 * RpcClient.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * RPC Client Class
 * 
 * <p>Provides either a socket or datagram connection to an RPC server.
 */
public abstract class RpcClient {

  //	Network address and port to connect to on the remote RPC server
  
  private InetAddress m_server;
  private int m_port;
  
  //	Protocol type
  
  private int m_protocol;
  
  //	Maximum RPC size to send/receive
  
  private int m_maxRpcSize;
  
  /**
   * Class constructor
   *
   * @param addr InetAddress
   * @param port int
   * @param proto int
   * @param maxRpcSize int
   * @throws IOException
   * @throws SocketException
   */
  protected RpcClient(InetAddress addr, int port, int proto, int maxRpcSize)
  	throws IOException, SocketException {

    //	Save the server address, port and the protocol type
    
    m_server = addr;
    m_port   = port;

    m_protocol = proto;
    
    //	Set the maximum RPC size to send/recieve
    
    m_maxRpcSize = maxRpcSize;
  }

  /**
   * Return the maximum RPC size
   * 
   * @return int
   */
  public final int getMaximumRpcSize() {
    return m_maxRpcSize;
  }
  
  /**
   * Return the server address
   * 
   * @return InetAddress
   */
  public final InetAddress getServerAddress() {
    return m_server;
  }
  
  /**
   * Return the server port
   * 
   * @return int
   */
  public final int getServerPort() {
    return m_port;
  }

  /**
   * Return the protocol type
   * 
   * @return int
   */
  public final int isProtocol() {
    return m_protocol;
  }
  
  /**
   * Send an RPC request to the server
   *
   * @param rpc RpcPacket
   * @param rxRpc RpcPacket
   * @return RpcPacket
   * @throws IOException
   */
  public abstract RpcPacket sendRPC(RpcPacket rpc, RpcPacket rxRpc)
  	throws IOException;

  /**
   * Close the connection to the remote RPC server
   */
  public abstract void closeConnection();
  
  /**
   * Return the RPC connection details as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[");
    str.append(isProtocol() == Rpc.TCP ? "TCP:" : "UDP:");
    str.append(getServerAddress().getHostAddress());
    str.append(":");
    str.append(getServerPort());
    
    str.append(",");
    str.append(getMaximumRpcSize());
    str.append("]");
    
    return str.toString();
  }
}
