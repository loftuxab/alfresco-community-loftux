package org.alfresco.jlan.oncrpc;

/*
 * UdpRpcDatagramHandler.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import org.alfresco.jlan.server.DatagramSessionHandler;
import org.alfresco.jlan.server.NetworkServer;


/**
 * UDP RPC Datagram Handler Class
 * 
 * <p>Receives RPC requests via a datagram and passes the request to the registered RPC server.
 */
public class UdpRpcDatagramHandler extends DatagramSessionHandler {

  //	RPC server implementation that handles the RPC processing
  
  private RpcProcessor m_rpcProcessor;
  
  /**
   * Class constructor
   * 
   * @param name String
   * @param protocol String
   * @param rpcServer RpcProcessor
   * @param server NetworkServer
   * @param addr InetAddress
   * @param port int
   * @param maxSize int
   */
  public UdpRpcDatagramHandler(String name, String protocol, RpcProcessor rpcServer, NetworkServer server,
      												 InetAddress addr, int port, int maxSize) {
    super(name, protocol, server, addr, port);

    //	Set the RPC server implementation that will handle the actual requests
    
    m_rpcProcessor = rpcServer;
    
    //	Set the maximum RPC request size allowed
    
    setMaximumDatagramSize(maxSize);
  }
  
  /**
   * Return the RPC server used to process the requests
   * 
   * @return RpcProcessor
   */
  protected final RpcProcessor getRpcProcessor() {
    return m_rpcProcessor;
  }
  
  /**
   * Process the RPC datagram
   * 
   * @param pkt DatagramPacket
   * @return boolean
   * @throws IOException
   */
  protected boolean processDatagram(DatagramPacket pkt)
  	throws IOException {
    
    //	The default implementation processes the RPC immediately then returns to the main datagram handler
    //	to wait for the next datagram to be received. In this case the datagram packet can be re-used as
    //	processing is done sequentially.
    
    //	Wrap the datagram data up as an RPC request
    
    RpcPacket rpcPkt = new RpcPacket(pkt.getData(), 0, pkt.getLength());
    
    //	Set the client details
    
    rpcPkt.setClientDetails(pkt.getAddress(), pkt.getPort(), Rpc.UDP);
    
    //	Validate the RPC header
    
    if ( rpcPkt.getRpcVersion() != Rpc.RpcVersion) {
    
      //	Build/send an error response
      
      rpcPkt.buildRpcMismatchResponse();
      pkt.setData(rpcPkt.getBuffer(), rpcPkt.getOffset(), RpcPacket.ResponseMismatchLen);
      
      sendDatagram(pkt);
    }
    else {
      
	    //	Pass the request to the registered RPC server to process
	    
	    RpcPacket response = m_rpcProcessor.processRpc(rpcPkt);
	    
	    //	Send the RPC response
	    
	    if ( response != null) {
		    pkt.setData(response.getBuffer(), response.getOffset(), response.getLength());
		    sendDatagram(pkt);
	    }
    }
    
    //	Indicate that the existing datagram packet can be re-used for the next request
    
    return true;
  }
}
