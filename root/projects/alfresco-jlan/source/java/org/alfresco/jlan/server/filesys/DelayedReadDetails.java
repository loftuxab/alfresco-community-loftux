package org.alfresco.jlan.server.filesys;

/*
 * DelayedReadDetails.java
 *
 * Copyright (c) Starlasoft 2005. All rights reserved.
 */

import java.io.IOException;

import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 *	Delayed Read Details Class
 *
 * <p>Contains the details of a file read request that is to be returned out of sequence by the filesystem
 * driver.
 * 
 * <p>The filesystem driver throws a FilesystemPendingException to indicate that the read request will be
 * handled later. This allows other requests to be handled by the session so that the client remains
 * responsive.
 */
public class DelayedReadDetails {

  //	SMB session
  
  private SMBSrvSession m_sess;
  
  //	Read response packet
  
  private SMBSrvPacket m_smbPkt;
  
  //	Start of read data offset in SMB packet
  
  private int m_dataPos;

  /**
   * Class constructor
   *
   * @param sess SrvSession
   */
  public DelayedReadDetails( SrvSession sess, int dataPos) {
   
    //	Only SMB/CIFS delayed reads are supported at present
    
    m_sess    = (SMBSrvSession) sess;
    m_smbPkt  = m_sess.getReceivePacket();
    m_dataPos = dataPos;
  }
  
  /**
   * Send the read response via the sessions asynchronous packet queue
   *
   * @param rdlen int
   * @exception IOException
   */
  public final void sendReadResponse( int rdlen)
  	throws IOException {
    
    //  Build the ReadAndX response

    m_smbPkt.setAndXCommand(0xFF); // no chained command
    m_smbPkt.setParameter(1, 0);
    m_smbPkt.setParameter(2, 0); // bytes remaining, for pipes only
    m_smbPkt.setParameter(3, 0); // data compaction mode
    m_smbPkt.setParameter(4, 0); // reserved
    m_smbPkt.setParameter(5, rdlen); // data length
    m_smbPkt.setParameter(6, m_dataPos - RFCNetBIOSProtocol.HEADER_LEN);    // offset to data

    //  Clear the reserved parameters

    for (int i = 7; i < 12; i++)
      m_smbPkt.setParameter(i, 0);

    //  Set the byte count

    m_smbPkt.setByteCount((m_dataPos + rdlen) - m_smbPkt.getByteOffset());
    
    //	Send the read response
    
    m_sess.sendAsynchResponseSMB( m_smbPkt, m_smbPkt.getLength());
  }

  /**
   * Send an error response for the read
   *
   * @param errClass int
   * @param errCode int
   * @exception IOException
   */
  public final void sendReadErrorResponse( int errClass, int errCode)
  	throws IOException {

		//	Make sure the response flag is set
		
		if ( m_smbPkt.isResponse() == false)
			m_smbPkt.setFlags(m_smbPkt.getFlags() + SMBSrvPacket.FLG_RESPONSE);
			
    //  Set the error code and error class in the response packet

    m_smbPkt.setParameterCount(0);
    m_smbPkt.setByteCount(0);

		//	Add default flags/flags2 values
		
		m_smbPkt.setFlags( m_smbPkt.getFlags() | m_sess.getDefaultFlags());
		m_smbPkt.setFlags2( m_smbPkt.getFlags2() | m_sess.getDefaultFlags2());
		
		//	Check if the error is a NT 32bit error status
		
		if ( errClass == SMBStatus.NTErr) {
			
			//	Enable the long error status flag
			
			if ( m_smbPkt.isLongErrorCode() == false)
				m_smbPkt.setFlags2(m_smbPkt.getFlags2() + SMBSrvPacket.FLG2_LONGERRORCODE);

			//	Set the NT status code
			
			m_smbPkt.setLongErrorCode(errCode);
		}
		else {
			
			//	Disable the long error status flag
			
			if ( m_smbPkt.isLongErrorCode() == true)
				m_smbPkt.setFlags2(m_smbPkt.getFlags2() - SMBSrvPacket.FLG2_LONGERRORCODE);
				
			//	Set the error status/class
			
    	m_smbPkt.setErrorCode(errCode);
    	m_smbPkt.setErrorClass(errClass);
		}

    //  Return the error response to the client via the asynchronous packet queue

    m_sess.sendAsynchResponseSMB( m_smbPkt, m_smbPkt.getLength());
  }
}
