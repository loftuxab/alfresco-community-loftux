package org.alfresco.jlan.oncrpc;

/*
 * TcpRpcClient.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * TCP RPC Client Connection Class
 */
public class TcpRpcClient extends RpcClient {

  //	TCP RPC client connection
  
  private TcpRpcPacketHandler m_client;
  
  /**
   * Class constructor 
   *
   * @param addr InetAddress
   * @param port int
   * @param maxRpcSize int
   * @throws IOException
   */
  public TcpRpcClient(InetAddress addr, int port, int maxRpcSize)
  	throws IOException {
    super(addr, port, Rpc.TCP, maxRpcSize);
    
    //	Connect a socket to the remote server
    
    Socket sock = new Socket(getServerAddress(), getServerPort());

    //	Create the TCP RPC packet handler for the client connection
    
    m_client = new TcpRpcPacketHandler(sock, maxRpcSize);
  }
  
  /**
   * Send an RPC request using the socket connection, and receive a response
   * 
   * @param rpc RpcPacket
   * @param rxRpc RpcPacket
   * @return RpcPacket
   * @throws IOException
   */
  public RpcPacket sendRPC(RpcPacket rpc, RpcPacket rxRpc)
  	throws IOException {

    //	Use the TCP packet handler to send the RPC
    
    m_client.sendRpc(rpc);
    
    //	Receive a response RPC
    
    RpcPacket rxPkt = rxRpc;
    if ( rxPkt == null)
      rxPkt = new RpcPacket(getMaximumRpcSize());
    
    m_client.receiveRpc(rxPkt);
    
    //	Return the RPC response
    
    return rxPkt;
  }
  
  /**
   * Close the connection to the remote RPC server 
   */
  public void closeConnection() {

    //	Close the packet handler
    
    if ( m_client != null) {
	    m_client.closePacketHandler();
	    m_client = null;
    }
  }
}
