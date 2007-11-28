package org.alfresco.jlan.server;

/*
 * PacketHandlerInterface.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;

/**
 * Packet Handler Interface
 * 
 * <p>Implemented by classes that read/write request packets to a network connection.
 */
public interface PacketHandlerInterface {

  /**
   * Return the protocol name
   *
   * @return String
   */
  public String getProtocolName();
  
  /**
   * Return the number of bytes available for reading without blocking
   * 
   * @return int
   * @exception IOException
   */
  public int availableBytes()
  	throws IOException;
  
  /**
   * Read a packet of data
   * 
   * @param pkt byte[]
   * @param offset int
   * @param maxLen int
   * @return int
   * @exception IOException
   */
  public int readPacket(byte[] pkt, int offset, int maxLen)
  	throws IOException;
  
  /**
   * Write a packet of data
   * 
   * @param pkt byte[]
   * @param offset int
   * @param len int
   * @exception IOException
   */
  public void writePacket(byte[] pkt, int offset, int len)
  	throws IOException;
  
  /**
   * Close the packet handler
   */
  public void closePacketHandler();
}
