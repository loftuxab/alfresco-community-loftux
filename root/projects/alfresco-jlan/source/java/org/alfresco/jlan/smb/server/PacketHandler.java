package org.alfresco.jlan.smb.server;

/*
 * PacketHandler.java
 *
 * Copyright (c) 2004-2005 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;
import java.net.InetAddress;

/**
 * Protocol Packet Handler Class
 */
public abstract class PacketHandler {

	//	Protocol type and name
	
	private int m_protoType;
	private String m_protoName;
	private String m_shortName;
	
  //	Client caller name and remote address
  
  private String m_clientName;
  private InetAddress m_remoteAddr;

	/**
	 * Class constructor
	 * 
	 * @param typ int
	 * @param name String
	 * @param shortName String
	 * @exception IOException		If a network error occurs
	 */
	public PacketHandler(int typ, String name, String shortName)
		throws IOException {

    m_protoType = typ;
		m_protoName = name;
		m_shortName = shortName;
	}
	
	/**
	 * Class constructor
	 * 
	 * @param typ int
	 * @param name String
	 * @param shortName String
	 */
	public PacketHandler(int typ, String name, String shortName, String clientName) {
		m_protoType = typ;
		m_protoName = name;
		m_shortName = shortName;
		
		m_clientName = clientName;
	}
	
	/**
	 * Return the protocol type
	 * 
	 * @return int
	 */
	public final int isProtocol() {
		return m_protoType;
	}
	
	/**
	 * Return the protocol name
	 * 
	 * @return String
	 */
	public final String isProtocolName() {
		return m_protoName;
	}

	/**
	 * Return the short protocol name
	 * 
	 * @return String
	 */
	public final String getShortName() {
		return m_shortName;
	}
	
	/**
	 * Check if there is a remote address available
	 * 
	 * @return boolean
	 */
	public final boolean hasRemoteAddress() {
	  return m_remoteAddr != null ? true : false;
	}
	
	/**
	 * Return the remote address for the connection
	 * 
	 * @return InetAddress
	 */
	public final InetAddress getRemoteAddress() {
		return m_remoteAddr;
	}

	/**
	 * Determine if the client name is available
	 * 
	 * @return boolean
	 */
	public final boolean hasClientName() {
	  return m_clientName != null ? true : false;
	}
	
	/**
	 * Return the client name
	 *  
	 * @return String
	 */
	public final String getClientName() {
	  return m_clientName;
	}
	
	/**
	 * Return the count of available bytes in the receive input stream
	 * 
	 * @return int
	 * @exception IOException		If a network error occurs.
	 */
	public abstract int availableBytes()
		throws IOException;
  
	/**
	 * Read a packet
	 * 
	 * @param pkt byte[]
	 * @param off int
	 * @param len int
	 * @return int
	 * @exception IOException		If a network error occurs.
	 */
	public abstract int readPacket(byte[] pkt, int off, int len)
		throws IOException;
	
	/**
	 * Receive an SMB request packet
	 * 
	 * @param pkt SMBSrvPacket
	 * @return int
	 * @exception IOException		If a network error occurs.
	 */
	public abstract int readPacket(SMBSrvPacket pkt)
		throws IOException;
		
	/**
	 * Send an SMB request packet
	 * 
	 * @param pkt byte[]
	 * @param off int
	 * @param len int
	 * @exception IOException		If a network error occurs.
	 */
	public abstract void writePacket(byte[] pkt, int off, int len)
		throws IOException;

	/**
	 * Send an SMB response packet
	 * 
	 * @param pkt SMBSrvPacket
	 * @param len int
	 * @exception IOException		If a network error occurs.
	 */
	public abstract void writePacket(SMBSrvPacket pkt, int len)
		throws IOException;
		
	/**
	 * Send an SMB response packet
	 * 
	 * @param pkt SMBSrvPacket
	 * @exception IOException		If a network error occurs.
	 */
	public final void writePacket(SMBSrvPacket pkt)
		throws IOException {
		writePacket(pkt, pkt.getLength());
	}

	/**
	 * Flush the output socket
	 * 
	 * @exception IOException		If a network error occurs
	 */
	public abstract void flushPacket()
		throws IOException;
	
	/**
	 * Close the protocol handler
	 */
	public void closeHandler() {
  }
  
  /**
   * Set the client name
   * 
   * @param name String
   */
  protected final void setClientName(String name) {
    m_clientName = name;
  }
  
  /**
   * Set the remote address
   * 
   * @param addr InetAddress
   */
  protected final void setRemoteAddress(InetAddress addr) {
    m_remoteAddr = addr;
  }
}
