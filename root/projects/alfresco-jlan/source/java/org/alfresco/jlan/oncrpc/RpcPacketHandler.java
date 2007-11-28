package org.alfresco.jlan.oncrpc;

/*
 * RpcPacketHandler.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

/**
 * RPC Packet Handler Interface
 * 
 * <p>Interface used by an RpcPacket to send a response RPC via either TCP or UDP.
 */
public interface RpcPacketHandler {

  /**
   * Send an RPC response
   * 
   * @param rpc RpcPacket
   * @exception IOException
   */
  public void sendRpcResponse(RpcPacket rpc)
  	throws IOException;
}
