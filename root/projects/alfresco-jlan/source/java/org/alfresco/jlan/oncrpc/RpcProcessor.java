package org.alfresco.jlan.oncrpc;

/*
 * RpcProcessor.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

/**
 * RPC Processor Interface
 */
public interface RpcProcessor {

  /**
   * Process an RPC request
   * 
   * @param rpc RpcPacket
   * @return RpcPacket
   * @throws IOException
   */
  public RpcPacket processRpc(RpcPacket rpc)
  	throws IOException;
}
