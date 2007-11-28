package org.alfresco.jlan.smb.server;

/*
 * LanManProtocolHandler.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.FileNotFoundException;
import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.server.auth.CifsAuthenticator;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.auth.InvalidUserException;
import org.alfresco.jlan.server.config.GlobalConfigSection;
import org.alfresco.jlan.server.core.InvalidDeviceInterfaceException;
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.filesys.AccessDeniedException;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.FileAccess;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileOfflineException;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileSharingException;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.PathNotFoundException;
import org.alfresco.jlan.server.filesys.SearchContext;
import org.alfresco.jlan.server.filesys.SrvDiskInfo;
import org.alfresco.jlan.server.filesys.TooManyConnectionsException;
import org.alfresco.jlan.server.filesys.TooManyFilesException;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.jlan.server.filesys.UnsupportedInfoLevelException;
import org.alfresco.jlan.server.filesys.VolumeInfo;
import org.alfresco.jlan.smb.DataType;
import org.alfresco.jlan.smb.FileInfoLevel;
import org.alfresco.jlan.smb.FindFirstNext;
import org.alfresco.jlan.smb.InvalidUNCPathException;
import org.alfresco.jlan.smb.PCShare;
import org.alfresco.jlan.smb.PacketType;
import org.alfresco.jlan.smb.SMBDate;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.server.ntfs.NTFSStreamsInterface;
import org.alfresco.jlan.smb.server.ntfs.StreamInfoList;
import org.alfresco.jlan.util.DataBuffer;
import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.WildCard;

/**
 * LanMan SMB Protocol Handler Class.
 *
 * <p>The LanMan protocol handler processes the additional SMBs that were added to the protocol
 * in the LanMan1 and LanMan2 SMB dialects.
 */
class LanManProtocolHandler extends CoreProtocolHandler {

  //  Locking type flags

  protected static final int LockShared 				= 0x01;
  protected static final int LockOplockRelease 	= 0x02;
  protected static final int LockChangeType 		= 0x04;
  protected static final int LockCancel 				= 0x08;
  protected static final int LockLargeFiles 		= 0x10;

  // Dummy date/time for dot files
  
  public static final long DotFileDateTime = System.currentTimeMillis();
  
  /**
   * LanManProtocolHandler constructor.
   */
  protected LanManProtocolHandler() {
    super();
  }

  /**
   * LanManProtocolHandler constructor.
   *
   * @param sess SMBSrvSession
   */
  protected LanManProtocolHandler(SMBSrvSession sess) {
    super(sess);
  }

  /**
   * Return the protocol name
   *
   * @return String
   */
  public String getName() {
    return "LanMan";
  }

  /**
   * Process the chained SMB commands (AndX).
   * 
   * @param outPkt Reply packet.
   * @return New offset to the end of the reply packet
   */
  protected final int procAndXCommands(SMBSrvPacket outPkt) {

    // Use the byte offset plus length to calculate the current output packet end position

    return procAndXCommands(outPkt, outPkt.getByteOffset() + outPkt.getByteCount(), null);
  }

  /**
   * Process the chained SMB commands (AndX).
   * 
   * @param outPkt Reply packet.
   * @param endPos Current end of packet position
   * @param file Current file , or null if no file context in chain
   * @return New offset to the end of the reply packet
   */
  protected final int procAndXCommands(SMBSrvPacket outPkt, int endPos, NetworkFile file) {

    // Get the chained command and command block offset

    int andxCmd = m_smbPkt.getAndXCommand();
    int andxOff = m_smbPkt.getParameter(1) + RFCNetBIOSProtocol.HEADER_LEN;

    // Set the initial chained command and offset

    outPkt.setAndXCommand(andxCmd);
    outPkt.setParameter(1, andxOff - RFCNetBIOSProtocol.HEADER_LEN);

    // Pointer to the last parameter block, starts with the main command parameter block

    int paramBlk = SMBSrvPacket.WORDCNT;

    // Get the current end of the reply packet offset

    int endOfPkt = outPkt.getByteOffset() + outPkt.getByteCount();
    boolean andxErr = false;

    while (andxCmd != SMBSrvPacket.NO_ANDX_CMD && andxErr == false) {

      // Determine the chained command type

      int prevEndOfPkt = endOfPkt;

      switch (andxCmd) {

        // Tree connect

        case PacketType.TreeConnectAndX:
          endOfPkt = procChainedTreeConnectAndX(andxOff, outPkt, endOfPkt);
          break;

        // Close file

        case PacketType.CloseFile:
          endOfPkt = procChainedClose(andxOff, outPkt, endOfPkt);
          break;

        // Read file

        case PacketType.ReadAndX:
          endOfPkt = procChainedReadAndX(andxOff, outPkt, endOfPkt, file);
          break;
      }

      // Advance to the next chained command block

      andxCmd = m_smbPkt.getAndXParameter(andxOff, 0) & 0x00FF;
      andxOff = m_smbPkt.getAndXParameter(andxOff, 1);

      // Set the next chained command details in the current parameter block

      outPkt.setAndXCommand(prevEndOfPkt, andxCmd);
      outPkt.setAndXParameter(paramBlk, 1, prevEndOfPkt - RFCNetBIOSProtocol.HEADER_LEN);

      // Advance the current parameter block

      paramBlk = prevEndOfPkt;

      // Check if the chained command has generated an error status

      if (outPkt.getErrorCode() != SMBStatus.Success)
        andxErr = true;
    }

    // Return the offset to the end of the reply packet

    return endOfPkt;
  }

  /**
   * Process a chained tree connect request.
   * 
   * @return New end of reply offset.
   * @param cmdOff int Offset to the chained command within the request packet.
   * @param outPkt SMBSrvPacket Reply packet.
   * @param endOff int Offset to the current end of the reply packet.
   */
  protected final int procChainedTreeConnectAndX(int cmdOff, SMBSrvPacket outPkt, int endOff) {

    //  Extract the parameters

    int flags = m_smbPkt.getAndXParameter(cmdOff, 2);
    int pwdLen = m_smbPkt.getAndXParameter(cmdOff, 3);

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( outPkt.getUserId());
    
    if (vc == null) {
      outPkt.setError(m_smbPkt.isLongErrorCode(), SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return endOff;
    }

    //  Get the data bytes position and length

    int dataPos = m_smbPkt.getAndXByteOffset(cmdOff);
    int dataLen = m_smbPkt.getAndXByteCount(cmdOff);
    byte[] buf = m_smbPkt.getBuffer();

    //  Extract the password string

    String pwd = null;

    if (pwdLen > 0) {
      pwd = new String(buf, dataPos, pwdLen);
      dataPos += pwdLen;
      dataLen -= pwdLen;
    }

    //  Extract the requested share name, as a UNC path

    String uncPath = DataPacker.getString(buf, dataPos, dataLen);
    if (uncPath == null) {
      outPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return endOff;
    }

    //  Extract the service type string

    dataPos += uncPath.length() + 1; // null terminated
    dataLen -= uncPath.length() + 1; // null terminated

    String service = DataPacker.getString(buf, dataPos, dataLen);
    if (service == null) {
      outPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return endOff;
    }

    //  Convert the service type to a shared device type, client may specify '?????' in which
    //  case we ignore the error.

    int servType = ShareType.ServiceAsType(service);
    if (servType == ShareType.UNKNOWN && service.compareTo("?????") != 0) {
      outPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return endOff;
    }

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
      m_sess.debugPrintln("ANDX Tree Connect AndX - " + uncPath + ", " + service);

    //  Parse the requested share name

    PCShare share = null;

    try {
      share = new PCShare(uncPath);
    }
    catch (InvalidUNCPathException ex) {
      outPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return endOff;
    }

    //  Map the IPC$ share to the admin pipe type

    if (share.getShareName().compareTo("IPC$") == 0)
      servType = ShareType.ADMINPIPE;

    //  Find the requested shared device

    SharedDevice shareDev = null;
    
		try {
    	
			//	Get/create the shared device
    	
			shareDev = m_sess.getSMBServer().findShare(share.getNodeName(), share.getShareName(), servType, getSession(), true);
		}
		catch ( InvalidUserException ex) {
    	
			//	Return a logon failure status
    	
			outPkt.setError(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return endOff;
		}
		catch ( Exception ex) {
    	
			//	Return a general status, bad network name
    	
			outPkt.setError(SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return endOff;
		}

		//	Check if the share is valid
		
    if (shareDev == null || (servType != ShareType.UNKNOWN && shareDev.getType() != servType)) {
      outPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return endOff;
    }

    //  Authenticate the share connect, if the server is using share mode security

    CifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
    int filePerm = FileAccess.Writeable;

    if (auth != null && auth.getAccessMode() == CifsAuthenticator.SHARE_MODE) {

      //  Validate the share connection

      filePerm = auth.authenticateShareConnect(m_sess.getClientInformation(), shareDev, pwd, m_sess);
      if (filePerm < 0) {

        //  Invalid share connection request

        outPkt.setError(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
        return endOff;
      }
    }

    //  Allocate a tree id for the new connection

    try {

      //  Allocate the tree id for this connection

      int treeId = vc.addConnection(shareDev);
      outPkt.setTreeId(treeId);

      //	Set the file permission that this user has been granted for this share

      TreeConnection tree = vc.findConnection(treeId);
      tree.setPermission(filePerm);

	    //	Inform the driver that a connection has been opened

			if ( tree.getInterface() != null)	    
    		tree.getInterface().treeOpened(m_sess,tree);
    
      //  Debug

      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
        m_sess.debugPrintln("ANDX Tree Connect AndX - Allocated Tree Id = " + treeId);
    }
    catch (TooManyConnectionsException ex) {

      //  Too many connections open at the moment

      outPkt.setError(SMBStatus.SRVNoResourcesAvailable, SMBStatus.ErrSrv);
      return endOff;
    }

    //  Build the tree connect response

    outPkt.setAndXParameterCount(endOff, 2);
    outPkt.setAndXParameter(endOff, 0, SMBSrvPacket.NO_ANDX_CMD);
    outPkt.setAndXParameter(endOff, 1, 0);

    //  Pack the service type

    int pos = outPkt.getAndXByteOffset(endOff);
    byte[] outBuf = outPkt.getBuffer();
    pos = DataPacker.putString(ShareType.TypeAsService(shareDev.getType()), outBuf, pos, true);
    int bytLen = pos - outPkt.getAndXByteOffset(endOff);
    outPkt.setAndXByteCount(endOff, bytLen);

    //  Return the new end of packet offset

    return pos;
  }

  /**
   * Process a chained read file request
   * 
   * @param cmdOff Offset to the chained command within the request packet.
   * @param outPkt Reply packet.
   * @param endOff Offset to the current end of the reply packet.
   * @param netFile File to be read, passed down the chained requests
   * @return New end of reply offset.
   */
  protected final int procChainedReadAndX(int cmdOff, SMBSrvPacket outPkt, int endOff, NetworkFile netFile) {

    // Get the tree id from the received packet and validate that it is a valid
    // connection id.

    TreeConnection conn = m_sess.findTreeConnection(m_smbPkt);

    if (conn == null) {
      outPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return endOff;
    }

    // Extract the read file parameters

    long offset = (long) m_smbPkt.getAndXParameterLong(cmdOff, 3); // bottom 32bits of read
    // offset
    offset &= 0xFFFFFFFFL;
    int maxCount = m_smbPkt.getAndXParameter(cmdOff, 5);

    // Check for the NT format request that has the top 32bits of the file offset

    if (m_smbPkt.getAndXParameterCount(cmdOff) == 12) {
      long topOff = (long) m_smbPkt.getAndXParameterLong(cmdOff, 10);
      offset += topOff << 32;
    }

    // Debug

    if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
      Debug.println("Chained File Read AndX : Size=" + maxCount + " ,Pos=" + offset);

    // Read data from the file

    byte[] buf = outPkt.getBuffer();
    int dataPos = 0;
    int rdlen = 0;

    try {

      // Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      // Set the returned parameter count so that the byte offset can be calculated

      outPkt.setAndXParameterCount(endOff, 12);
      dataPos = outPkt.getAndXByteOffset(endOff);
      dataPos = DataPacker.wordAlign(dataPos); // align the data buffer

      // Check if the requested data length will fit into the buffer

      int dataLen = buf.length - dataPos;
      if (dataLen < maxCount)
        maxCount = dataLen;

      // Read from the file

      rdlen = disk.readFile(m_sess, conn, netFile, buf, dataPos, maxCount, offset);

      // Return the data block

      outPkt.setAndXParameter(endOff, 0, SMBSrvPacket.NO_ANDX_CMD);
      outPkt.setAndXParameter(endOff, 1, 0);

      outPkt.setAndXParameter(endOff, 2, 0xFFFF);
      outPkt.setAndXParameter(endOff, 3, 0);
      outPkt.setAndXParameter(endOff, 4, 0);
      outPkt.setAndXParameter(endOff, 5, rdlen);
      outPkt.setAndXParameter(endOff, 6, dataPos - RFCNetBIOSProtocol.HEADER_LEN);

      // Clear the reserved parameters

      for (int i = 7; i < 12; i++)
        outPkt.setAndXParameter(endOff, i, 0);

      // Set the byte count

      outPkt.setAndXByteCount(endOff, (dataPos + rdlen) - outPkt.getAndXByteOffset(endOff));

      // Update the end offset for the new end of packet

      endOff = dataPos + rdlen;
    }
    catch (InvalidDeviceInterfaceException ex) {

      // Failed to get/initialize the disk interface

      outPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return endOff;
    }
    catch (java.io.IOException ex) {
    }

    // Return the new end of packet offset

    return endOff;
  }

  /**
   * Process a chained close file request
   * 
   * @param cmdOff int Offset to the chained command within the request packet.
   * @param outPkt SMBSrvPacket Reply packet.
   * @param endOff int Offset to the current end of the reply packet.
   * @return New end of reply offset.
   */
  protected final int procChainedClose(int cmdOff, SMBSrvPacket outPkt, int endOff) {

    // Get the tree id from the received packet and validate that it is a valid
    // connection id.

    TreeConnection conn = m_sess.findTreeConnection(m_smbPkt);

    if (conn == null) {
      outPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return endOff;
    }

    // Get the file id from the request

    int fid = m_smbPkt.getAndXParameter(cmdOff, 0);
    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      outPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return endOff;
    }

    // Debug

    if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
      Debug.println("Chained File Close [" + m_smbPkt.getTreeId() + "] fid=" + fid);

    // Close the file

    try {

      // Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      // Close the file
      //
      // The disk interface may be null if the file is a named pipe file

      if (disk != null)
        disk.closeFile(m_sess, conn, netFile);

      // Indicate that the file has been closed

      netFile.setClosed(true);
    }
    catch (InvalidDeviceInterfaceException ex) {

      // Failed to get/initialize the disk interface

      outPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return endOff;
    }
    catch (java.io.IOException ex) {
    }

    // Clear the returned parameter count and byte count

    outPkt.setAndXParameterCount(endOff, 0);
    outPkt.setAndXByteCount(endOff, 0);

    endOff = outPkt.getAndXByteOffset(endOff) - RFCNetBIOSProtocol.HEADER_LEN;

    // Remove the file from the connections list of open files

    conn.removeFile(fid, getSession());

    // Return the new end of packet offset

    return endOff;
  }

  /**
   * Close a search started via the transact2 find first/next command.
   * 
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected final void procFindClose(SMBSrvPacket outPkt) throws java.io.IOException, SMBSrvException {

    // Check that the received packet looks like a valid find close request

    if (m_smbPkt.checkPacketIsValid(1, 0) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  Get the search id

    int searchId = m_smbPkt.getParameter(0);

    //  Get the search context

    SearchContext ctx = vc.getSearchContext(searchId);

    if (ctx == null) {

      //  Invalid search handle

			m_sess.sendSuccessResponseSMB();
      return;
    }

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
      m_sess.debugPrintln("Close trans search [" + searchId + "]");

    //  Deallocate the search slot, close the search.

    vc.deallocateSearchSlot(searchId);

    //  Return a success status SMB

		m_sess.sendSuccessResponseSMB();
  }

  /**
   * Process the file lock/unlock request.
   *
   * @param outPkt SMBSrvPacket
   */
  protected final void procLockingAndX(SMBSrvPacket outPkt) throws java.io.IOException, SMBSrvException {

    //  Check that the received packet looks like a valid locking andX request

    if (m_smbPkt.checkPacketIsValid(8, 0) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  Extract the file lock/unlock parameters

    int fid = m_smbPkt.getParameter(2);
    int lockType = m_smbPkt.getParameter(3);
    long lockTmo = m_smbPkt.getParameterLong(4);
    int lockCnt = m_smbPkt.getParameter(6);
    int unlockCnt = m_smbPkt.getParameter(7);

    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_LOCK))
      m_sess.debugPrintln("File Lock [" + netFile.getFileId() + "] : type=0x" + Integer.toHexString(lockType) +
          								", tmo=" + lockTmo + ", locks=" + lockCnt + ", unlocks=" + unlockCnt);

    //  Return a success status for now

    outPkt.setParameterCount(2);
    outPkt.setAndXCommand(0xFF);
    outPkt.setParameter(1, 0);
    outPkt.setByteCount(0);

    //  Send the lock request response

    m_sess.sendResponseSMB(outPkt);
  }

  /**
   * Process the logoff request.
   *
   * @param outPkt SMBSrvPacket
   */
  protected final void procLogoffAndX(SMBSrvPacket outPkt)
    throws java.io.IOException, SMBSrvException {

    //  Check that the received packet looks like a valid logoff andX request

    if (m_smbPkt.checkPacketIsValid(2, 0) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }

    //  Get the virtual circuit for the request
    
    int uid = m_smbPkt.getUserId();
    VirtualCircuit vc = m_sess.findVirtualCircuit( uid);
    
    if (vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

    //  DEBUG
    
    if ( Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_NEGOTIATE))
      Debug.println("[SMB] Logoff vc=" + vc);

    //  Close the virtual circuit
    
    m_sess.removeVirtualCircuit( uid);
    
    //  Return a success status SMB

    m_sess.sendSuccessResponseSMB();
  }

  /**
   * Process the file open request.
   *
   * @param outPkt SMBSrvPacket
   */
  protected final void procOpenAndX(SMBSrvPacket outPkt) throws java.io.IOException, SMBSrvException {

    //  Check that the received packet looks like a valid open andX request

    if (m_smbPkt.checkPacketIsValid(15, 1) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  If the connection is to the IPC$ remote admin named pipe pass the request to the IPC handler. If the device is
    //  not a disk type device then return an error.

    if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

      //  Use the IPC$ handler to process the request

      IPCHandler.processIPCRequest(m_sess, outPkt);
      return;
    }
    else if (conn.getSharedDevice().getType() != ShareType.DISK) {

      //  Return an access denied error

      //      m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      //      m_sess.sendErrorResponseSMB(SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
      m_sess.sendErrorResponseSMB(SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
      return;
    }

    //  Extract the open file parameters

    int flags    = m_smbPkt.getParameter(2);
    int access   = m_smbPkt.getParameter(3);
    int srchAttr = m_smbPkt.getParameter(4);
    int fileAttr = m_smbPkt.getParameter(5);
    int crTime   = m_smbPkt.getParameter(6);
    int crDate   = m_smbPkt.getParameter(7);
    int openFunc = m_smbPkt.getParameter(8);
    int allocSiz = m_smbPkt.getParameterLong(9);

    //  Extract the filename string

    String fileName = m_smbPkt.unpackString(m_smbPkt.isUnicode());
    if (fileName == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

		//	Create the file open parameters

		SMBDate crDateTime = null;
		if ( crTime > 0 && crDate > 0)
			crDateTime = new SMBDate(crDate, crTime);
					
    FileOpenParams params = new FileOpenParams(fileName, openFunc, access, srchAttr, fileAttr, allocSiz, crDateTime != null ? crDateTime.getTime() : 0L);

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
      m_sess.debugPrintln("File Open AndX [" + treeId + "] params=" + params);

    //  Access the disk interface and open the requested file

    int fid;
    NetworkFile netFile = null;
    int respAction = 0;

    try {

      //  Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      //  Check if the requested file already exists

      int fileSts = disk.fileExists(m_sess, conn, fileName);

      if (fileSts == FileStatus.NotExist) {

        //  Check if the file should be created if it does not exist

        if (FileAction.createNotExists(openFunc)) {

					//	Check if the session has write access to the filesystem
					
					if (conn.hasWriteAccess() == false) {

						//	User does not have the required access rights

						m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
						return;
					}

          //  Create a new file

          netFile = disk.createFile(m_sess, conn, params);

          //  Indicate that the file did not exist and was created

          respAction = FileAction.FileCreated;
        }
        else {

          //  Check if the path is a directory

          if (fileSts == FileStatus.DirectoryExists) {

            //  Return an access denied error

            m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
          }
          else {

            //  Return a file not found error

            m_sess.sendErrorResponseSMB(SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
          }
          return;
        }
      }
      else {

        //  Open the requested file

        netFile = disk.openFile(m_sess, conn, params);

        //  Set the file action response

        if (FileAction.truncateExistingFile(openFunc))
          respAction = FileAction.FileTruncated;
        else
          respAction = FileAction.FileExisted;
      }

      //  Add the file to the list of open files for this tree connection

      fid = conn.addFile(netFile, getSession());

    }
    catch (InvalidDeviceInterfaceException ex) {

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
    catch (TooManyFilesException ex) {

      //  Too many files are open on this connection, cannot open any more files.

      m_sess.sendErrorResponseSMB(SMBStatus.DOSTooManyOpenFiles, SMBStatus.ErrDos);
      return;
    }
    catch (AccessDeniedException ex) {

      //  Return an access denied error

      m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }
		catch ( FileSharingException ex) {

			//  Return a sharing violation error

			m_sess.sendErrorResponseSMB(SMBStatus.DOSFileSharingConflict, SMBStatus.ErrDos);
			return;
		}
		catch ( FileOfflineException ex) {

			//  File data is unavailable

			m_sess.sendErrorResponseSMB(SMBStatus.NTFileOffline, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
    catch (java.io.IOException ex) {

      //  Failed to open the file

      m_sess.sendErrorResponseSMB(SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
      return;
    }

    //  Build the open file response

    outPkt.setParameterCount(15);

    outPkt.setAndXCommand(0xFF);
    outPkt.setParameter(1, 0); // AndX offset

    outPkt.setParameter(2, fid);
    outPkt.setParameter(3, netFile.getFileAttributes() & StandardAttributes);

    long modDate = 0L;

    if (netFile.hasModifyDate()) {
      GlobalConfigSection gblConfig = (GlobalConfigSection) m_sess.getServer().getConfiguration().getConfigSection( GlobalConfigSection.SectionName);
      modDate = (netFile.getModifyDate() / 1000L) + (gblConfig != null ? gblConfig.getTimeZoneOffset() : 0);
    }

    outPkt.setParameterLong(4, (int) modDate);
    outPkt.setParameterLong(6, netFile.getFileSizeInt());  				// file size
    outPkt.setParameter(8, netFile.getGrantedAccess());
    outPkt.setParameter(9, OpenAndX.FileTypeDisk);
    outPkt.setParameter(10, 0); 																	// named pipe state
    outPkt.setParameter(11, respAction);
    outPkt.setParameter(12, 0); 																	// server FID (long)
    outPkt.setParameter(13, 0);
    outPkt.setParameter(14, 0);

    outPkt.setByteCount(0);

    // Check if there is a chained command, or commands

    if (m_smbPkt.hasAndXCommand()) {

        // Process any chained commands, AndX

        int pos = procAndXCommands(outPkt, outPkt.getPacketLength(), netFile);

        // Send the read andX response

        m_sess.sendResponseSMB(outPkt, pos);
    }
    else {

        // Send the normal read andX response

        m_sess.sendResponseSMB(outPkt);
    }
  }

  /**
   * Process the file read request.
   *
   * @param outPkt SMBSrvPacket
   */
  protected final void procReadAndX(SMBSrvPacket outPkt) throws java.io.IOException, SMBSrvException {

    //  Check that the received packet looks like a valid read andX request

    if (m_smbPkt.checkPacketIsValid(10, 0) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  If the connection is to the IPC$ remote admin named pipe pass the request to the IPC handler.

    if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

      //  Use the IPC$ handler to process the request

      IPCHandler.processIPCRequest(m_sess, outPkt);
      return;
    }

    //  Extract the read file parameters

    int fid = m_smbPkt.getParameter(2);
    int offset = m_smbPkt.getParameterLong(3);
    int maxCount = m_smbPkt.getParameter(5);

    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
      m_sess.debugPrintln("File Read AndX [" + netFile.getFileId() + "] : Size=" + maxCount + " ,Pos=" + offset);

    //  Read data from the file

    byte[] buf = outPkt.getBuffer();
    int dataPos = 0;
    int rdlen = 0;

    try {

      //  Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      //  Set the returned parameter count so that the byte offset can be calculated

      outPkt.setParameterCount(12);
      dataPos = outPkt.getByteOffset();
      //    dataPos = ( dataPos + 3) & 0xFFFFFFFC;  // longword align the data

      //  Check if the requested data length will fit into the buffer

      int dataLen = buf.length - dataPos;
      if (dataLen < maxCount)
        maxCount = dataLen;

      //  Read from the file

      rdlen = disk.readFile(m_sess, conn, netFile, buf, dataPos, maxCount, offset);
    }
    catch (InvalidDeviceInterfaceException ex) {

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
    catch (AccessDeniedException ex) {
    	
    	//	No access to file, or file is a directory
			//    	
      //  Debug

      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
        m_sess.debugPrintln("File Read Error [" + netFile.getFileId() + "] : " + ex.toString());

      //  Failed to read the file

      m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }
    catch (java.io.IOException ex) {

      //  Debug

      if (Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
        m_sess.debugPrintln("File Read Error [" + netFile.getFileId() + "] : " + ex.toString());

      //  Failed to read the file

      m_sess.sendErrorResponseSMB(SMBStatus.HRDReadFault, SMBStatus.ErrHrd);
      return;
    }

    //  Return the data block

    outPkt.setAndXCommand(0xFF); // no chained command
    outPkt.setParameter(1, 0);
    outPkt.setParameter(2, 0xFFFF); // bytes remaining, for pipes only
    outPkt.setParameter(3, 0); // data compaction mode
    outPkt.setParameter(4, 0); // reserved
    outPkt.setParameter(5, rdlen); // data length
    outPkt.setParameter(6, dataPos - RFCNetBIOSProtocol.HEADER_LEN);
    // offset to data

    //  Clear the reserved parameters

    for (int i = 7; i < 12; i++)
      outPkt.setParameter(i, 0);

    //  Set the byte count

    outPkt.setByteCount((dataPos + rdlen) - outPkt.getByteOffset());

    //  Send the read andX response

    m_sess.sendResponseSMB(outPkt);
  }

  /**
   * Process the file read MPX request.
   * 
   * @param outPkt SMBSrvPacket
   */
  protected final void procReadMPX(SMBSrvPacket outPkt) throws java.io.IOException, SMBSrvException {

    // Check that the received packet looks like a valid read andX request

    if (m_smbPkt.checkPacketIsValid(8, 0) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    // Get the tree connection details

    TreeConnection conn = m_sess.findTreeConnection(m_smbPkt);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

    // Check if the user has the required access permission

    if (conn.hasReadAccess() == false) {

      // User does not have the required access rights

      m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }

    // If the connection is to the IPC$ remote admin named pipe pass the request to the IPC
    // handler.

    if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

      // Use the IPC$ handler to process the request

      IPCHandler.processIPCRequest(m_sess, outPkt);
      return;
    }

    // Extract the read file parameters

    int fid = m_smbPkt.getParameter(0);
    int offset = m_smbPkt.getParameterLong(1);
    int maxCount = m_smbPkt.getParameter(3);

    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }

    // Debug

    if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
      Debug.println("File ReadMPX [" + netFile.getFileId() + "] : Size=" + maxCount + " ,Pos=" + offset + ",MaxCount=" + maxCount);

    // Get the maximum buffer size the client allows

    int clientMaxSize = m_sess.getClientMaximumBufferSize();

    // Read data from the file

    byte[] buf = outPkt.getBuffer();
    int dataPos = 0;
    int rdlen = 0;
    int rdRemaining = maxCount;

    try {

      // Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      // Set the returned parameter count so that the byte offset can be calculated

      outPkt.setParameterCount(8);
      dataPos = outPkt.getByteOffset();

      // Calcualte the maximum read size to return

      clientMaxSize -= dataPos;

      // Loop until all required data has been read

      while (rdRemaining > 0) {

        // Check if the requested data length will fit into the buffer

        rdlen = rdRemaining;
        if (rdlen > clientMaxSize)
          rdlen = clientMaxSize;

        // Read from the file

        rdlen = disk.readFile(m_sess, conn, netFile, buf, dataPos, rdlen, offset);

        // Build the reply packet

        m_smbPkt.setParameterLong(0, offset);
        m_smbPkt.setParameter(2, maxCount);
        m_smbPkt.setParameter(3, 0xFFFF);
        m_smbPkt.setParameterLong(4, 0);
        m_smbPkt.setParameter(6, rdlen);
        m_smbPkt.setParameter(7, dataPos - RFCNetBIOSProtocol.HEADER_LEN);

        m_smbPkt.setByteCount(rdlen);

        // Update the read offset and remaining read length

        if (rdlen > 0) {
          rdRemaining -= rdlen;
          offset += rdlen;
        } else
          rdRemaining = 0;

        // Set the response command

        m_smbPkt.setCommand(PacketType.ReadMpxSecondary);

        // Debug

        if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
          Debug.println("File ReadMPX Secondary [" + netFile.getFileId() + "] : Size=" + rdlen + " ,Pos=" + offset);

        // Send the packet

        m_sess.sendResponseSMB(m_smbPkt);
      }
    }
    catch (InvalidDeviceInterfaceException ex) {

      // Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
    catch (AccessDeniedException ex) {

      // No access to file, or file is a directory
      //      
      // Debug

      if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
        Debug.println("File ReadMPX Error [" + netFile.getFileId() + "] : " + ex.toString());

      // Failed to read the file

      m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }
    catch (java.io.IOException ex) {

      // Debug

      if ( Debug.EnableError)
        Debug.println("File ReadMPX Error [" + netFile.getFileId() + "] : " + ex);

      // Failed to read the file

      m_sess.sendErrorResponseSMB(SMBStatus.HRDReadFault, SMBStatus.ErrHrd);
      return;
    }
  }

  /**
   * Rename a file.
   * 
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected void procRenameFile(SMBSrvPacket outPkt)
  	throws java.io.IOException, SMBSrvException {

    // Check that the received packet looks like a valid rename file request

    if (m_smbPkt.checkPacketIsValid(1, 4) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    // Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree id from the received packet and validate that it is a valid
    //   connection id.

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasWriteAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		//	Get the Unicode flag
		
		boolean isUni = m_smbPkt.isUnicode();
		
    //  Read the data block
    
    m_smbPkt.resetBytePointer();

    //  Extract the old file name

		if ( m_smbPkt.unpackByte() != DataType.ASCII) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
		}
		
    String oldName = m_smbPkt.unpackString(isUni);
    if (oldName == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

    //  Extract the new file name

		if ( m_smbPkt.unpackByte() != DataType.ASCII) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
		}
		
    String newName = m_smbPkt.unpackString(isUni);
    if (oldName == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
      m_sess.debugPrintln("File Rename [" + treeId + "] old name=" + oldName + ", new name=" + newName);

    //  Access the disk interface and rename the requested file

    int fid;
    NetworkFile netFile = null;

    try {

      //  Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      //  Rename the requested file

      disk.renameFile(m_sess, conn, oldName, newName);
    }
    catch (InvalidDeviceInterfaceException ex) {

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
    catch (java.io.IOException ex) {

      //  Failed to open the file

      m_sess.sendErrorResponseSMB(SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
      return;
    }

    //  Build the rename file response

    outPkt.setParameterCount(0);
    outPkt.setByteCount(0);

    //  Send the response packet

    m_sess.sendResponseSMB(outPkt);
  }

  /**
   * Process the SMB session setup request.
   *
   * @param outPkt    Response SMB packet.
   */
  protected void procSessionSetup(SMBSrvPacket outPkt) throws SMBSrvException, IOException, TooManyConnectionsException {

    //  Extract the client details from the session setup request

    int dataPos = m_smbPkt.getByteOffset();
    int dataLen = m_smbPkt.getByteCount();
    byte[] buf = m_smbPkt.getBuffer();

    //	Extract the session details

		int maxBufSize = m_smbPkt.getParameter(2);
		int maxMpx		 = m_smbPkt.getParameter(3);
    int vcNum      = m_smbPkt.getParameter(4);
		
    // Extract the password string

    byte[] pwd = null;
    int pwdLen = m_smbPkt.getParameter(7);

    if (pwdLen > 0) {
      pwd = new byte[pwdLen];
      for (int i = 0; i < pwdLen; i++)
        pwd[i] = buf[dataPos + i];
      dataPos += pwdLen;
      dataLen -= pwdLen;
    }

    //  Extract the user name string

    String user = DataPacker.getString(buf, dataPos, dataLen);
    if (user == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
    else {

      //  Update the buffer pointers

      dataLen -= user.length() + 1;
      dataPos += user.length() + 1;
    }

    //  Extract the clients primary domain name string

    String domain = "";

    if (dataLen > 0) {

      //	Extract the callers domain name

      domain = DataPacker.getString(buf, dataPos, dataLen);
      if (domain == null) {
        m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
        return;
      }
      else {

        //  Update the buffer pointers

        dataLen -= domain.length() + 1;
        dataPos += domain.length() + 1;
      }
    }

    //  Extract the clients native operating system

    String clientOS = "";

    if (dataLen > 0) {

      //	Extract the callers operating system name

      clientOS = DataPacker.getString(buf, dataPos, dataLen);
      if (clientOS == null) {
        m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
        return;
      }
    }

    //  DEBUG

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
      m_sess.debugPrintln("Session setup from user=" + user + ", password=" + pwd + ", domain="  + domain + ", os=" + clientOS
          + ", VC=" + vcNum + ", maxBuf=" + maxBufSize + ", maxMpx=" + maxMpx);

		//	Store the client maximum buffer size and maximum multiplexed requests count
		
		m_sess.setClientMaximumBufferSize(maxBufSize);
		m_sess.setClientMaximumMultiplex(maxMpx);
		
    //  Create the client information and store in the session

    ClientInfo client = ClientInfo.createInfo(user, pwd);
    client.setDomain(domain);
    client.setOperatingSystem(clientOS);
    if ( m_sess.hasRemoteAddress())
      client.setClientAddress(m_sess.getRemoteAddress().getHostAddress());

    if (m_sess.getClientInformation() == null ||
        m_sess.getClientInformation().getUserName().length() == 0) {

      //	Set the session client details

      m_sess.setClientInformation(client);
    }
    else {

      //	Get the current client details from the session

      ClientInfo curClient = m_sess.getClientInformation();
      
      if ( curClient.getUserName() == null || curClient.getUserName().length() == 0) {
      
      	//	Update the client information

				m_sess.setClientInformation(client);
      }
      else {
      	
	      //	DEBUG
	
	      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
	        m_sess.debugPrintln("Session already has client information set");
      }
    }

    //  Authenticate the user, if the server is using user mode security

    CifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
    boolean isGuest = false;

    if (auth != null && auth.getAccessMode() == CifsAuthenticator.USER_MODE) {

      //  Validate the user

      int sts = auth.authenticateUser(client, m_sess, CifsAuthenticator.LANMAN);
      if (sts > 0 && (sts & CifsAuthenticator.AUTH_GUEST) != 0)
        isGuest = true;
      else if (sts != CifsAuthenticator.AUTH_ALLOW) {

        //  Invalid user, reject the session setup request

        m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
        return;
      }
    }

		//	Set the guest flag for the client and logged on status
		
		client.setGuest(isGuest);
		getSession().setLoggedOn(true);
		
    // If the user is logged on then allocate a virtual circuit

    int uid = 0;
    
    // Create a virtual circuit for the new logon

    VirtualCircuit vc = new VirtualCircuit(vcNum, client);
    uid = m_sess.addVirtualCircuit(vc);

    if (uid == VirtualCircuit.InvalidUID) {

      // DEBUG
    
      if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
        Debug.println("Failed to allocate UID for virtual circuit, " + vc);
    
      // Failed to allocate a UID
    
      throw new SMBSrvException(SMBStatus.NTLogonFailure, SMBStatus.ErrDos, SMBStatus.DOSAccessDenied);
    }
    else if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE)) {
      
      // DEBUG
    
      Debug.println("Allocated UID=" + uid + " for VC=" + vc);
    }
    
    //  Build the session setup response SMB

    outPkt.setParameterCount(3);
    outPkt.setParameter(0, 0); //  No chained response
    outPkt.setParameter(1, 0); //  Offset to chained response
    outPkt.setParameter(2, isGuest ? 1 : 0);
    outPkt.setByteCount(0);

    outPkt.setTreeId(0);
    outPkt.setUserId(uid);

    //  Set the various flags

    //  outPkt.setFlags( SMBSrvPacket.FLG_CASELESS);
    int flags = outPkt.getFlags();
    flags &= ~SMBSrvPacket.FLG_CASELESS;
    outPkt.setFlags(flags);
    outPkt.setFlags2(SMBSrvPacket.FLG2_LONGFILENAMES);

    //  Pack the OS, dialect and domain name strings.

    int pos = outPkt.getByteOffset();
    buf = outPkt.getBuffer();

    pos = DataPacker.putString("Java", buf, pos, true);
    pos = DataPacker.putString("Alfresco AIFS Server " + m_sess.getServer().isVersion() , buf, pos, true);
    pos = DataPacker.putString(m_sess.getSMBServer().getCIFSConfiguration().getDomainName(), buf, pos, true);

    outPkt.setByteCount(pos - outPkt.getByteOffset());

    //  Check if there is a chained command, or commands

    if (m_smbPkt.hasAndXCommand() && dataPos < m_smbPkt.getReceivedLength()) {

      //  Process any chained commands, AndX

      pos = procAndXCommands(outPkt);
    }
    else {

      //  Indicate that there are no chained replies

      outPkt.setAndXCommand(SMBSrvPacket.NO_ANDX_CMD);
    }

    //  Send the negotiate response

		m_sess.sendResponseSMB(outPkt,pos);

    //  Update the session state

    m_sess.setState(SMBSrvSessionState.SMBSESSION);

    //	Notify listeners that a user has logged onto the session

    m_sess.getSMBServer().sessionLoggedOn(m_sess);
  }

  /**
   * Process a transact2 request. The transact2 can contain many different sub-requests.
   *
   * @param outPkt SMBSrvPacket
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected void procTransact2(SMBSrvPacket outPkt)
  	throws IOException, SMBSrvException {

    //  Check that we received enough parameters for a transact2 request

    if (m_smbPkt.checkPacketIsValid(15, 0) == false) {

      //  Not enough parameters for a valid transact2 request

      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree id from the received packet and validate that it is a valid
    //   connection id.

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  Create a transact packet using the received SMB packet

    SMBSrvTransPacket tranPkt = new SMBSrvTransPacket(m_smbPkt.getBuffer());
    
		//	Create a transact buffer to hold the transaction setup, parameter and data blocks

		SrvTransactBuffer transBuf = null;
    int subCmd = tranPkt.getSubFunction();
				
		if ( tranPkt.getTotalParameterCount() == tranPkt.getParameterBlockCount() &&
				 tranPkt.getTotalDataCount()      == tranPkt.getDataBlockCount()) {
				 	
			//	Create a transact buffer using the packet buffer, the entire request is contained in a single
			//	packet
			
			transBuf = new SrvTransactBuffer(tranPkt);
		}
		else {
		
			//	Create a transact buffer to hold the multiple transact request parameter/data blocks
			
			transBuf = new SrvTransactBuffer(tranPkt.getSetupCount(), tranPkt.getTotalParameterCount(), tranPkt.getTotalDataCount());
			transBuf.setType(tranPkt.getCommand());
			transBuf.setFunction(subCmd);
			
			//	Append the setup, parameter and data blocks to the transaction data

			byte[] buf = tranPkt.getBuffer();
						
			transBuf.appendSetup(buf,tranPkt.getSetupOffset(), tranPkt.getSetupCount() * 2);
			transBuf.appendParameter(buf,tranPkt.getParameterBlockOffset(), tranPkt.getParameterBlockCount());
			transBuf.appendData(buf,tranPkt.getDataBlockOffset(),tranPkt.getDataBlockCount());
		}

		//	Set the return data limits for the transaction

		transBuf.setReturnLimits(tranPkt.getMaximumReturnSetupCount(), tranPkt.getMaximumReturnParameterCount(),
														 tranPkt.getMaximumReturnDataCount());
														 
		//	Check for a multi-packet transaction, for a multi-packet transaction we just acknowledge the receive with
		//	an empty response SMB
		
		if ( transBuf.isMultiPacket()) {
			
			//	Save the partial transaction data
			
			vc.setTransaction(transBuf);
			
			//	Send an intermediate acknowedgement response

			m_sess.sendSuccessResponseSMB();		
			return;	
		}
		
		//	Check if the transaction is on the IPC$ named pipe, the request requires special processing
				
    if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
      IPCHandler.procTransaction(vc, transBuf, m_sess, outPkt);
      return;
    }
    
		//	DEBUG
		
    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
      m_sess.debugPrintln("Transaction [" + treeId + "] tbuf=" + transBuf);
 
		//	Process the transaction buffer
		
		processTransactionBuffer(transBuf, outPkt);
  }

  /**
   * Process a transact2 secondary request.
   *
   * @param outPkt SMBSrvPacket
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected void procTransact2Secondary(SMBSrvPacket outPkt)
  	throws IOException, SMBSrvException {

    //  Check that we received enough parameters for a transact2 request

    if (m_smbPkt.checkPacketIsValid(8, 0) == false) {

      //  Not enough parameters for a valid transact2 request

      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree id from the received packet and validate that it is a valid
    //   connection id.

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		//	Check if there is an active transaction, and it is an NT transaction
		
		if ( vc.hasTransaction() == false ||
				 ( vc.getTransaction().isType() == PacketType.Transaction  && m_smbPkt.getCommand() != PacketType.TransactionSecond) ||
				 ( vc.getTransaction().isType() == PacketType.Transaction2 && m_smbPkt.getCommand() != PacketType.Transaction2Second)) {
			
			//	No transaction to continue, or packet does not match the existing transaction, return an error
		
			m_sess.sendErrorResponseSMB(SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}
		
    //  Create an NT transaction using the received packet

		SMBSrvTransPacket tpkt = new SMBSrvTransPacket(m_smbPkt.getBuffer());
		byte[] buf = tpkt.getBuffer();
		SrvTransactBuffer transBuf = vc.getTransaction();
		
		//	Append the parameter data to the transaction buffer, if any
		
		int plen = tpkt.getSecondaryParameterBlockCount();
		if ( plen > 0) {
			
			//	Append the data to the parameter buffer
			
			DataBuffer paramBuf = transBuf.getParameterBuffer();
			paramBuf.appendData(buf,tpkt.getSecondaryParameterBlockOffset(),plen);
		}
		
		//	Append the data block to the transaction buffer, if any
		
		int dlen = tpkt.getSecondaryDataBlockCount();
		if ( dlen > 0) {
			
			//	Append the data to the data buffer
			
			DataBuffer dataBuf = transBuf.getDataBuffer();
			dataBuf.appendData(buf,tpkt.getSecondaryDataBlockOffset(),dlen);
		}
		
    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
      m_sess.debugPrintln("Transaction Secondary [" + treeId + "] paramLen=" + plen + ", dataLen=" + dlen);

		//	Check if the transaction has been received or there are more sections to be received
		
		int totParam = tpkt.getTotalParameterCount();
		int totData  = tpkt.getTotalDataCount();
		
		int paramDisp = tpkt.getParameterBlockDisplacement();
		int dataDisp  = tpkt.getDataBlockDisplacement();
		
		if (( paramDisp + plen) == totParam &&
				( dataDisp + dlen)  == totData) {
		
	    //  Debug
	
	    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
	      m_sess.debugPrintln("Transaction complete, processing ...");
	      
			//	Clear the in progress transaction
			
			vc.setTransaction(null);
			
			//	Check if the transaction is on the IPC$ named pipe, the request requires special processing
					
	    if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
	      IPCHandler.procTransaction(vc, transBuf, m_sess, outPkt);
	      return;
	    }
    
			//	DEBUG
			
	    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
	      m_sess.debugPrintln("Transaction second [" + treeId + "] tbuf=" + transBuf);
 
			//	Process the transaction
		
			processTransactionBuffer(transBuf, outPkt);
		}
		else {
			
			//	There are more transaction parameter/data sections to be received, return an intermediate response
			
			m_sess.sendSuccessResponseSMB();
		}
  }

	/**
	 * Process a transaction buffer
	 * 
	 * @param tbuf TransactBuffer
	 * @param outPkt SMBSrvPacket
	 * @exception IOException				If a network error occurs
	 * @exception SMBSrvException 	If an SMB error occurs
	 */
	private final void processTransactionBuffer(SrvTransactBuffer tbuf, SMBSrvPacket outPkt)
		throws IOException, SMBSrvException {

    //  Get the transaction sub-command code and validate

    switch (tbuf.getFunction()) {

      //  Start a file search

      case PacketType.Trans2FindFirst :
        procTrans2FindFirst(tbuf, outPkt);
        break;

        //  Continue a file search

      case PacketType.Trans2FindNext :
        procTrans2FindNext(tbuf, outPkt);
        break;

        //  Query file system information

      case PacketType.Trans2QueryFileSys :
        procTrans2QueryFileSys(tbuf, outPkt);
        break;

        //	Query path

      case PacketType.Trans2QueryPath :
        procTrans2QueryPath(tbuf, outPkt);
        break;

        // Query file information via handle

      case PacketType.Trans2QueryFile:
        procTrans2QueryFile(tbuf, outPkt);
        break;
        
        //  Unknown transact2 command

      default :

        //  Return an unrecognized command error

				if ( Debug.EnableError)
        	m_sess.debugPrintln("Error Transact2 Command = 0x" + Integer.toHexString(tbuf.getFunction()));
        m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
        break;
    }
	}

  /**
   * Process a transact2 file search request.
   *
   * @param tbuf 			 Transaction request details
   * @param outPkt     Packet to use for the reply.
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected final void procTrans2FindFirst(SrvTransactBuffer tbuf, SMBSrvPacket outPkt)
  	throws java.io.IOException, SMBSrvException {

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  Get the search parameters

		DataBuffer paramBuf = tbuf.getParameterBuffer();
		
    int srchAttr = paramBuf.getShort();
    int maxFiles = paramBuf.getShort();
    int srchFlag = paramBuf.getShort();
    int infoLevl = paramBuf.getShort();
    paramBuf.skipBytes(4);
    
    String srchPath = paramBuf.getString(tbuf.isUnicode());

    //  Check if the search path is valid

    if (srchPath == null || srchPath.length() == 0) {

      //  Invalid search request

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

    //  Access the shared device disk interface

    SearchContext ctx = null;
    DiskInterface disk = null;
    int searchId = -1;

    try {

      //  Access the disk interface

      disk = (DiskInterface) conn.getSharedDevice().getInterface();

      //  Allocate a search slot for the new search

      searchId = vc.allocateSearchSlot();
      if (searchId == -1) {

        //  Failed to allocate a slot for the new search

        m_sess.sendErrorResponseSMB(SMBStatus.SRVNoResourcesAvailable, SMBStatus.ErrSrv);
        return;
      }

      //  Debug

      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
        m_sess.debugPrintln("Start trans search [" + searchId + "] - " + srchPath + ", attr=0x" + Integer.toHexString(srchAttr)
            + ", maxFiles=" + maxFiles  + ", infoLevel="  + infoLevl + ", flags=0x" + Integer.toHexString(srchFlag));

      //  Start a new search

      ctx = disk.startSearch(m_sess, conn, srchPath, srchAttr);
      if (ctx != null) {

        //  Store details of the search in the context

        ctx.setTreeId(treeId);
        ctx.setMaximumFiles(maxFiles);
      }
      else {

        //  Failed to start the search, return a no more files error

        m_sess.sendErrorResponseSMB(SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
        return;
      }

      //  Save the search context

      vc.setSearchContext(searchId, ctx);

			//	Create the reply transact buffer
			
			SrvTransactBuffer replyBuf = new SrvTransactBuffer(tbuf);
			DataBuffer dataBuf = replyBuf.getDataBuffer();

      //	Determine the maximum return data length

      int maxLen = replyBuf.getReturnDataLimit();

			//	Check if resume keys are required
			
			boolean resumeReq = (srchFlag & FindFirstNext.ReturnResumeKey) != 0 ? true : false;
			
      //	Loop until we have filled the return buffer or there are no more files to return

      int fileCnt = 0;
      int packLen = 0;
      int lastNameOff = 0;
      
      boolean pktDone    = false;
      boolean searchDone = false;
      
      FileInfo info = new FileInfo();

      // If this is a wildcard search then add the '.' and '..' entries

      if (WildCard.containsWildcards(srchPath)) {

        // Pack the '.' file information

        if (resumeReq == true) {
          dataBuf.putInt(-1);
          maxLen -= 4;
        }

        lastNameOff = dataBuf.getPosition();
        FileInfo dotInfo = new FileInfo(".", 0, FileAttribute.Directory);
        dotInfo.setFileId(dotInfo.getFileName().hashCode());
        dotInfo.setCreationDateTime(DotFileDateTime);
        dotInfo.setModifyDateTime(DotFileDateTime);
        dotInfo.setAccessDateTime(DotFileDateTime);

        packLen = FindInfoPacker.packInfo(dotInfo, dataBuf, infoLevl, tbuf.isUnicode());

        // Update the file count for this packet, update the remaining buffer length

        fileCnt++;
        maxLen -= packLen;

        // Pack the '..' file information

        if (resumeReq == true) {
          dataBuf.putInt(-2);
          maxLen -= 4;
        }

        lastNameOff = dataBuf.getPosition();
        dotInfo.setFileName("..");
        dotInfo.setFileId(dotInfo.getFileName().hashCode());

        packLen = FindInfoPacker.packInfo(dotInfo, dataBuf, infoLevl, tbuf.isUnicode());

        // Update the file count for this packet, update the remaining buffer length

        fileCnt++;
        maxLen -= packLen;
      }
      
      // Pack the file information records
      
      while (pktDone == false && fileCnt < maxFiles) {

        // Get file information from the search

        if (ctx.nextFileInfo(info) == false) {

          //  No more files

          pktDone    = true;
          searchDone = true;
        }

        //  Check if the file information will fit into the return buffer

        else if (FindInfoPacker.calcInfoSize(info, infoLevl, false, true) <= maxLen) {

					//	Pack a dummy resume key, if required
					
					if ( resumeReq) {
						dataBuf.putZeros(4);
						maxLen -= 4;
					}
					
					//	Save the offset to the last file information structure
					
					lastNameOff = dataBuf.getPosition();
					
          // Mask the file attributes
          
          info.setFileAttributes( info.getFileAttributes() & StandardAttributes);
          
          //  Pack the file information

          packLen = FindInfoPacker.packInfo(info, dataBuf, infoLevl, tbuf.isUnicode());

          //  Update the file count for this packet

          fileCnt++;

          //  Recalculate the remaining buffer space

          maxLen -= packLen;
        }
        else {

          //  Set the search restart point

          ctx.restartAt(info);

          //  No more buffer space

          pktDone = true;
        }
      }

      //  Pack the parameter block

			paramBuf = replyBuf.getParameterBuffer();
			
			paramBuf.putShort(searchId);
			paramBuf.putShort(fileCnt);
			paramBuf.putShort(ctx.hasMoreFiles() ? 0 : 1);
			paramBuf.putShort(0);
			paramBuf.putShort(lastNameOff);

			//	Send the transaction response
			
			SMBSrvTransPacket tpkt = new SMBSrvTransPacket(outPkt.getBuffer());
			tpkt.doTransactionResponse(m_sess, replyBuf);
			
      //  Debug

      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
        m_sess.debugPrintln("Search [" + searchId + "] Returned " + fileCnt + " files, moreFiles=" + ctx.hasMoreFiles());

      //  Check if the search is complete

      if (searchDone == true) {

        //  Debug

        if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
          m_sess.debugPrintln("End start search [" + searchId + "] (Search complete)");

        //  Release the search context

        vc.deallocateSearchSlot(searchId);
      }
    }
    catch (FileNotFoundException ex) {

      //	Deallocate the search

      if (searchId != -1)
        vc.deallocateSearchSlot(searchId);

      //  Search path does not exist

      m_sess.sendErrorResponseSMB(SMBStatus.DOSNoMoreFiles, SMBStatus.ErrDos);
    }
    catch (InvalidDeviceInterfaceException ex) {

      //	Deallocate the search

      if (searchId != -1)
        vc.deallocateSearchSlot(searchId);

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
    }
    catch (UnsupportedInfoLevelException ex) {

      //	Deallocate the search

      if (searchId != -1)
        vc.deallocateSearchSlot(searchId);

      //  Requested information level is not supported

      m_sess.sendErrorResponseSMB(SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
    }
  }

  /**
   * Process a transact2 file search continue request.
   *
   * @param tbuf 			 Transaction request details
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected final void procTrans2FindNext(SrvTransactBuffer tbuf, SMBSrvPacket outPkt)
  	throws java.io.IOException, SMBSrvException {

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  Get the search parameters

		DataBuffer paramBuf = tbuf.getParameterBuffer();
		
    int searchId = paramBuf.getShort();
    int maxFiles = paramBuf.getShort();
    int infoLevl = paramBuf.getShort();
    int reskey   = paramBuf.getInt();
    int srchFlag = paramBuf.getShort();

    String resumeName = paramBuf.getString(tbuf.isUnicode());

    //  Access the shared device disk interface

    SearchContext ctx = null;
    DiskInterface disk = null;

    try {

      //  Access the disk interface

      disk = (DiskInterface) conn.getSharedDevice().getInterface();

      //  Retrieve the search context

      ctx = vc.getSearchContext(searchId);
      if (ctx == null) {

        //	DEBUG

				if ( Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
        	m_sess.debugPrintln("Search context null - [" + searchId + "]");

        //  Invalid search handle

        m_sess.sendErrorResponseSMB(SMBStatus.DOSNoMoreFiles, SMBStatus.ErrDos);
        return;
      }

      //  Debug

      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
        m_sess.debugPrintln("Continue search [" + searchId + "] - " + resumeName + ", maxFiles=" + maxFiles + ", infoLevel="
            + infoLevl + ", flags=0x" + Integer.toHexString(srchFlag));

			//	Create the reply transaction buffer
			
			SrvTransactBuffer replyBuf = new SrvTransactBuffer(tbuf);
			DataBuffer dataBuf = replyBuf.getDataBuffer();
			
			//	Determine the maximum return data length
			
			int maxLen = replyBuf.getReturnDataLimit();
			
			//	Check if resume keys are required
			
			boolean resumeReq = (srchFlag & FindFirstNext.ReturnResumeKey) != 0 ? true : false;
			
      //	Loop until we have filled the return buffer or there are no more files to return

      int fileCnt = 0;
      int packLen = 0;
      int lastNameOff = 0;
      
      boolean pktDone    = false;
      boolean searchDone = false;
      
      FileInfo info = new FileInfo();

      while (pktDone == false && fileCnt < maxFiles) {

        //  Get file information from the search

        if (ctx.nextFileInfo(info) == false) {

          //  No more files

          pktDone    = true;
          searchDone = true;
        }

        //  Check if the file information will fit into the return buffer

        else if (FindInfoPacker.calcInfoSize(info, infoLevl, false, true) <= maxLen) {

					//	Pack a dummy resume key, if required
					
					if ( resumeReq)
						dataBuf.putZeros(4);
					
					//	Save the offset to the last file information structure
					
					lastNameOff = dataBuf.getPosition();
					
          // Mask the file attributes
          
          info.setFileAttributes( info.getFileAttributes() & StandardAttributes);
          
          //  Pack the file information

          packLen = FindInfoPacker.packInfo(info, dataBuf, infoLevl, tbuf.isUnicode());

          //  Update the file count for this packet

          fileCnt++;

          //  Recalculate the remaining buffer space

          maxLen -= packLen;
        }
        else {

          //  Set the search restart point

          ctx.restartAt(info);

          //  No more buffer space

          pktDone = true;
        }
      }

      //  Pack the parameter block

			paramBuf = replyBuf.getParameterBuffer();
			
			paramBuf.putShort(fileCnt);
			paramBuf.putShort(ctx.hasMoreFiles() ? 0 : 1);
			paramBuf.putShort(0);
			paramBuf.putShort(lastNameOff);

			//	Send the transaction response
			
			SMBSrvTransPacket tpkt = new SMBSrvTransPacket(outPkt.getBuffer());
			tpkt.doTransactionResponse(m_sess, replyBuf);
			
      //  Debug

      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
        m_sess.debugPrintln("Search [" + searchId + "] Returned " + fileCnt + " files, moreFiles=" + ctx.hasMoreFiles());

      //  Check if the search is complete

      if (searchDone == true) {

        //  Debug

        if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
          m_sess.debugPrintln("End start search [" + searchId + "] (Search complete)");

        //  Release the search context

        vc.deallocateSearchSlot(searchId);
      }
    }
    catch (FileNotFoundException ex) {

      //	Deallocate the search

      if (searchId != -1)
        vc.deallocateSearchSlot(searchId);

      //  Search path does not exist

      m_sess.sendErrorResponseSMB(SMBStatus.DOSNoMoreFiles, SMBStatus.ErrDos);
    }
    catch (InvalidDeviceInterfaceException ex) {

      //	Deallocate the search

      if (searchId != -1)
        vc.deallocateSearchSlot(searchId);

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
    }
    catch (UnsupportedInfoLevelException ex) {

      //	Deallocate the search

      if (searchId != -1)
        vc.deallocateSearchSlot(searchId);

      //  Requested information level is not supported

      m_sess.sendErrorResponseSMB(SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
    }
  }

  /**
   * Process a transact2 query file information (via handle) request.
   * 
   * @param tbuf Transaction request details
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException If an I/O error occurs
   * @exception SMBSrvException SMB protocol exception
   */
  protected final void procTrans2QueryFile(SrvTransactBuffer tbuf, SMBSrvPacket outPkt) throws java.io.IOException,
      SMBSrvException {

    // Get the virtual circuit for the request

    VirtualCircuit vc = m_sess.findVirtualCircuit(m_smbPkt.getUserId());

    if (vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

    // Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

    // Check if the user has the required access permission

    if (conn.hasReadAccess() == false) {

      // User does not have the required access rights

      m_sess.sendErrorResponseSMB(SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }

    // Get the file id and query path information level

    DataBuffer paramBuf = tbuf.getParameterBuffer();

    int fid = paramBuf.getShort();
    int infoLevl = paramBuf.getShort();

    // Get the file details via the file id

    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }

    // Debug

    if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
      Debug.println("Query File - level=0x" + Integer.toHexString(infoLevl) + ", fid=" + fid + ", stream=" + netFile.getStreamId()
          + ", name=" + netFile.getFullName());

    // Access the shared device disk interface

    try {

      // Access the disk interface

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      // Set the return parameter count, so that the data area position can be calculated.

      outPkt.setParameterCount(10);

      // Pack the file information into the data area of the transaction reply

      byte[] buf = outPkt.getBuffer();
      int prmPos = DataPacker.longwordAlign(outPkt.getByteOffset());
      int dataPos = prmPos + 4;

      // Pack the return parametes, EA error offset

      outPkt.setPosition(prmPos);
      outPkt.packWord(0);

      // Create a data buffer using the SMB packet. The response should always fit into a
      // single
      // reply packet.

      DataBuffer replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);

      // Check if the virtual filesystem supports streams, and streams are enabled

      boolean streams = false;

      if (disk instanceof NTFSStreamsInterface) {

        // Check if NTFS streams are enabled

        NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
        streams = ntfsStreams.hasStreamsEnabled(m_sess, conn);
      }

      // Check for the file streams information level

      int dataLen = 0;

      if (streams == true && (infoLevl == FileInfoLevel.PathFileStreamInfo || infoLevl == FileInfoLevel.NTFileStreamInfo)) {

        // Debug

        if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_STREAMS))
          Debug.println("Get NTFS streams list fid=" + fid + ", name=" + netFile.getFullName());

        // Get the list of streams from the share driver

        NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
        StreamInfoList streamList = ntfsStreams.getStreamList(m_sess, conn, netFile.getFullName());

        if (streamList == null) {
          m_sess.sendErrorResponseSMB(SMBStatus.NTObjectNotFound, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
          return;
        }

        // Pack the file streams information into the return data packet

        dataLen = QueryInfoPacker.packStreamFileInfo(streamList, replyBuf, true);
      } else {

        // Get the file information

        FileInfo fileInfo = disk.getFileInformation(m_sess, conn, netFile.getFullNameStream());

        if (fileInfo == null) {
          m_sess.sendErrorResponseSMB(SMBStatus.NTObjectNotFound, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
          return;
        }

        // Mask the file attributes

        fileInfo.setFileAttributes(fileInfo.getFileAttributes() & StandardAttributes);

        // Pack the file information into the return data packet

        dataLen = QueryInfoPacker.packInfo(fileInfo, replyBuf, infoLevl, true);
      }

      // Check if any data was packed, if not then the information level is not supported

      if (dataLen == 0) {
        m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
        return;
      }

      SMBSrvTransPacket.initTransactReply(outPkt, 2, prmPos, dataLen, dataPos);
      outPkt.setByteCount(replyBuf.getPosition() - outPkt.getByteOffset());

      // Send the transact reply

      m_sess.sendResponseSMB(outPkt);
    }
    catch (AccessDeniedException ex) {

      // Not allowed to access the file/folder

      m_sess.sendErrorResponseSMB(SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }
    catch (FileNotFoundException ex) {

      // Requested file does not exist

      m_sess.sendErrorResponseSMB(SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
      return;
    }
    catch (PathNotFoundException ex) {

      // Requested path does not exist

      m_sess.sendErrorResponseSMB(SMBStatus.NTObjectPathNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
      return;
    }
    catch (InvalidDeviceInterfaceException ex) {

      // Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    catch (UnsupportedInfoLevelException ex) {

      // Requested information level is not supported

      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
  }
  
  /**
   * Process a transact2 file system query request.
   * 
   * @param tbuf Transaction request details
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected final void procTrans2QueryFileSys(SrvTransactBuffer tbuf, SMBSrvPacket outPkt)
  	throws java.io.IOException, SMBSrvException {

    // Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    // Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  Get the query file system required information level

		DataBuffer paramBuf = tbuf.getParameterBuffer();
		
    int infoLevl = paramBuf.getShort();

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
      m_sess.debugPrintln("Query File System Info - level = 0x" + Integer.toHexString(infoLevl));

    //  Access the shared device disk interface

    try {

      //  Access the disk interface and context

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();
      DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();

      //  Set the return parameter count, so that the data area position can be calculated.

      outPkt.setParameterCount(10);

      //  Pack the disk information into the data area of the transaction reply

      byte[] buf = outPkt.getBuffer();
      int prmPos = DataPacker.longwordAlign(outPkt.getByteOffset());
      int dataPos = prmPos; // no parameters returned

			//	Create a data buffer using the SMB packet. The response should always fit into a single
			//	reply packet.
			
			DataBuffer replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);
			
      //  Determine the information level requested

			SrvDiskInfo diskInfo = null;
			VolumeInfo volInfo = null;
			
      switch (infoLevl) {

        //  Standard disk information

        case DiskInfoPacker.InfoStandard :

          //  Get the disk information

					diskInfo = getDiskInformation(disk, diskCtx);

          //  Pack the disk information into the return data packet

          DiskInfoPacker.packStandardInfo(diskInfo, replyBuf);
          break;

        //  Volume label information

        case DiskInfoPacker.InfoVolume :

          //  Get the volume label information

					volInfo = getVolumeInformation(disk, diskCtx);

          //  Pack the volume label information

          DiskInfoPacker.packVolumeInfo(volInfo, replyBuf, tbuf.isUnicode());
          break;
          
        //	Full volume information
         
        case DiskInfoPacker.InfoFsVolume:

          //  Get the volume information

					volInfo = getVolumeInformation(disk, diskCtx);

          //  Pack the volume information

          DiskInfoPacker.packFsVolumeInformation(volInfo, replyBuf, tbuf.isUnicode());
         	break;
         	
        //	Filesystem size information
         
        case DiskInfoPacker.InfoFsSize:

          //  Get the disk information

					diskInfo = getDiskInformation(disk, diskCtx);

          //  Pack the disk information into the return data packet

          DiskInfoPacker.packFsSizeInformation(diskInfo, replyBuf);
         	break;
         	
        //	Filesystem device information
         
        case DiskInfoPacker.InfoFsDevice:
         	DiskInfoPacker.packFsDevice(0, 0, replyBuf);
         	break;
         	
        //	Filesystem attribute information
         
        case DiskInfoPacker.InfoFsAttribute:
         	DiskInfoPacker.packFsAttribute(0, 255, "JLAN", tbuf.isUnicode(), replyBuf);
         	break;
      }

      //  Check if any data was packed, if not then the information level is not supported

      if (replyBuf.getPosition() == dataPos) {
        m_sess.sendErrorResponseSMB(SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
        return;
      }

      int bytCnt = replyBuf.getPosition() - outPkt.getByteOffset();
      replyBuf.setEndOfBuffer();
      int dataLen = replyBuf.getLength();
      SMBSrvTransPacket.initTransactReply(outPkt, 0, prmPos, dataLen, dataPos);
      outPkt.setByteCount(bytCnt);

      //  Send the transact reply

			m_sess.sendResponseSMB(outPkt);
    }
    catch (InvalidDeviceInterfaceException ex) {

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
  }

  /**
   * Process a transact2 query path information request.
   *
   * @param tbuf 			 Transaction request details
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   */
  protected final void procTrans2QueryPath(SrvTransactBuffer tbuf, SMBSrvPacket outPkt)
  	throws java.io.IOException, SMBSrvException {

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }

		//	Check if the user has the required access permission

		if (conn.hasReadAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  Get the query path information level and file/directory name

		DataBuffer paramBuf = tbuf.getParameterBuffer();

    int infoLevl = paramBuf.getShort();
    paramBuf.skipBytes(4);
    
    String path = paramBuf.getString(tbuf.isUnicode());

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
      m_sess.debugPrintln("Query Path - level = 0x" + Integer.toHexString(infoLevl) + ", path = " + path);

    //  Access the shared device disk interface

    try {

      //  Access the disk interface

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      //  Set the return parameter count, so that the data area position can be calculated.

      outPkt.setParameterCount(10);

      //  Pack the file information into the data area of the transaction reply

      byte[] buf = outPkt.getBuffer();
      int prmPos = DataPacker.longwordAlign(outPkt.getByteOffset());
      int dataPos = prmPos; // no parameters returned

			//	Create a data buffer using the SMB packet. The response should always fit into a single
			//	reply packet.
			
			DataBuffer replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);
			
      //  Get the file information

      FileInfo fileInfo = disk.getFileInformation(m_sess, conn, path);

      if (fileInfo == null) {
        m_sess.sendErrorResponseSMB(SMBStatus.NTObjectNotFound, SMBStatus.NTErr);
        return;
      }

      // Mask the file attributes
      
      fileInfo.setFileAttributes( fileInfo.getFileAttributes() & StandardAttributes);
      
      //  Pack the file information into the return data packet

			int dataLen = QueryInfoPacker.packInfo(fileInfo, replyBuf, infoLevl, true);			

      //  Check if any data was packed, if not then the information level is not supported

      if (dataLen == 0) {
        m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
        return;
      }

      SMBSrvTransPacket.initTransactReply(outPkt, 0, prmPos, dataLen, dataPos);
      outPkt.setByteCount(replyBuf.getPosition() - outPkt.getByteOffset());

      //  Send the transact reply

			m_sess.sendResponseSMB(outPkt);
    }
    catch (FileNotFoundException ex) {

      //  Requested file does not exist

      m_sess.sendErrorResponseSMB(SMBStatus.NTObjectNotFound, SMBStatus.NTErr);
      return;
    }
    catch (InvalidDeviceInterfaceException ex) {

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }
    catch (UnsupportedInfoLevelException ex) {

      //  Requested information level is not supported

      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }
  }

  /**
   * Process the SMB tree connect request.
   *
   * @param outPkt  Response SMB packet.
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
   * @exception org.alfresco.aifs.smb.TooManyConnectionsException    Too many concurrent connections on this session.
   */

  protected void procTreeConnectAndX(SMBSrvPacket outPkt)
    throws SMBSrvException, TooManyConnectionsException, java.io.IOException {

    //  Check that the received packet looks like a valid tree connect request

    if (m_smbPkt.checkPacketIsValid(4, 3) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Extract the parameters

    int flags = m_smbPkt.getParameter(2);
    int pwdLen = m_smbPkt.getParameter(3);

    //  Get the data bytes position and length

    int dataPos = m_smbPkt.getByteOffset();
    int dataLen = m_smbPkt.getByteCount();
    byte[] buf = m_smbPkt.getBuffer();

    //  Extract the password string

    String pwd = null;

    if (pwdLen > 0) {
      pwd = new String(buf, dataPos, pwdLen);
      dataPos += pwdLen;
      dataLen -= pwdLen;
    }

    //  Extract the requested share name, as a UNC path

    String uncPath = DataPacker.getString(buf, dataPos, dataLen);
    if (uncPath == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

    //  Extract the service type string

    dataPos += uncPath.length() + 1; // null terminated
    dataLen -= uncPath.length() + 1; // null terminated

    String service = DataPacker.getString(buf, dataPos, dataLen);
    if (service == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

    //  Convert the service type to a shared device type, client may specify '?????' in which
    //  case we ignore the error.

    int servType = ShareType.ServiceAsType(service);
    if (servType == ShareType.UNKNOWN && service.compareTo("?????") != 0) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
      m_sess.debugPrintln("Tree Connect AndX - " + uncPath + ", " + service);

    //  Parse the requested share name

    PCShare share = null;

    try {
      share = new PCShare(uncPath);
    }
    catch (InvalidUNCPathException ex) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }

    //  Map the IPC$ share to the admin pipe type

    if (servType == ShareType.NAMEDPIPE && share.getShareName().compareTo("IPC$") == 0)
      servType = ShareType.ADMINPIPE;

    //  Find the requested shared device

    SharedDevice shareDev = null; 

		try {
    	
			//	Get/create the shared device
    	
			shareDev = m_sess.getSMBServer().findShare(share.getNodeName(), share.getShareName(), servType, getSession(), true);
		}
		catch ( InvalidUserException ex) {
    	
			//	Return a logon failure status
    	
			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch ( Exception ex) {
    	
			//	Return a general status, bad network name
    	
			m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return;
		}

		//	Check if the share is valid
		
    if (shareDev == null || (servType != ShareType.UNKNOWN && shareDev.getType() != servType)) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

    //  Authenticate the share connection depending upon the security mode the server is running under

    CifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
    int filePerm = FileAccess.Writeable;

    if (auth != null) {

      //  Validate the share connection

      filePerm = auth.authenticateShareConnect(m_sess.getClientInformation(), shareDev, pwd, m_sess);
      if (filePerm < 0) {

        //  Invalid share connection request

        m_sess.sendErrorResponseSMB(SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
        return;
      }
    }

    //  Allocate a tree id for the new connection

    int treeId = vc.addConnection(shareDev);
    outPkt.setTreeId(treeId);

    //	Set the file permission that this user has been granted for this share

    TreeConnection tree = vc.findConnection(treeId);
    tree.setPermission(filePerm);

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
      m_sess.debugPrintln("Tree Connect AndX - Allocated Tree Id = " + treeId + ", Permission = " + FileAccess.asString(filePerm));

    //  Build the tree connect response

    outPkt.setParameterCount(3);
    outPkt.setAndXCommand(0xFF); // no chained reply
    outPkt.setParameter(1, 0);
    outPkt.setParameter(2, 0);

    //  Pack the service type

    int pos = outPkt.getByteOffset();
    pos = DataPacker.putString(ShareType.TypeAsService(shareDev.getType()), buf, pos, true);
    outPkt.setByteCount(pos - outPkt.getByteOffset());

    //  Send the response

    m_sess.sendResponseSMB(outPkt);

    //	Inform the driver that a connection has been opened
    
    if ( tree.getInterface() != null)
   		tree.getInterface().treeOpened(m_sess,tree);
  }

  /**
   * Process the file write request.
   *
   * @param outPkt SMBSrvPacket
   */
  protected final void procWriteAndX(SMBSrvPacket outPkt) throws java.io.IOException, SMBSrvException {

    //  Check that the received packet looks like a valid write andX request

    if (m_smbPkt.checkPacketIsValid(12, 0) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    //  Get the virtual circuit for the request
    
    VirtualCircuit vc = m_sess.findVirtualCircuit( m_smbPkt.getUserId());
    if ( vc == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    
    //  Get the tree connection details

    int treeId = m_smbPkt.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

		//	Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			//	User does not have the required access rights

			m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

    //  If the connection is to the IPC$ remote admin named pipe pass the request to the IPC handler.

    if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

      //  Use the IPC$ handler to process the request

      IPCHandler.processIPCRequest(m_sess, outPkt);
      return;
    }

    //  Extract the write file parameters

    int fid = m_smbPkt.getParameter(2);
    int offset = m_smbPkt.getParameterLong(3);
    int dataLen = m_smbPkt.getParameter(10);
    int dataPos = m_smbPkt.getParameter(11) + RFCNetBIOSProtocol.HEADER_LEN;

    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
      m_sess.debugPrintln("File Write AndX [" + netFile.getFileId() + "] : Size=" + dataLen + " ,Pos=" + offset);

    //  Write data to the file

    byte[] buf = m_smbPkt.getBuffer();
    int wrtlen = 0;

    //  Access the disk interface and write to the file

    try {

      //  Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      //  Write to the file

      wrtlen = disk.writeFile(m_sess, conn, netFile, buf, dataPos, dataLen, offset);
    }
    catch (InvalidDeviceInterfaceException ex) {

      //  Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
    catch (java.io.IOException ex) {

      //  Debug

      if (Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
        m_sess.debugPrintln("File Write Error [" + netFile.getFileId() + "] : " + ex.toString());

      //  Failed to read the file

      m_sess.sendErrorResponseSMB(SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
      return;
    }

    //  Return the count of bytes actually written

    outPkt.setParameterCount(6);
    outPkt.setAndXCommand(0xFF);
    outPkt.setParameter(1, 0);
    outPkt.setParameter(2, wrtlen);
    outPkt.setParameter(3, 0); // remaining byte count for pipes only
    outPkt.setParameter(4, 0); // reserved
    outPkt.setParameter(5, 0); //    "
    outPkt.setByteCount(0);

    //  Send the write response

    m_sess.sendResponseSMB(outPkt);
  }

  /**
   * Process the file write MPX request.
   * 
   * @param outPkt SMBSrvPacket
   */
  protected final void procWriteMPX(SMBSrvPacket outPkt) throws java.io.IOException, SMBSrvException {

    // Check that the received packet looks like a valid write andX request

    if (m_smbPkt.checkPacketIsValid(12, 0) == false) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    // Get the tree connection details

    TreeConnection conn = m_sess.findTreeConnection(m_smbPkt);

    if (conn == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }

    // Check if the user has the required access permission

    if (conn.hasWriteAccess() == false) {

      // User does not have the required access rights

      m_sess.sendErrorResponseSMB(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }

    // If the connection is to the IPC$ remote admin named pipe pass the request to the IPC
    // handler.

    if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

      // Use the IPC$ handler to process the request

      IPCHandler.processIPCRequest(m_sess, outPkt);
      return;
    }

    // Extract the write file parameters

    int fid = m_smbPkt.getParameter(0);
    int totLen = m_smbPkt.getParameter(1);
    int offset = m_smbPkt.getParameterLong(3);
    int dataLen = m_smbPkt.getParameter(10);
    int dataPos = m_smbPkt.getParameter(11) + RFCNetBIOSProtocol.HEADER_LEN;

    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }

    // Debug

    if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
      Debug.println("File WriteMPX [" + netFile.getFileId() + "] : Size=" + dataLen + " ,Pos=" + offset + ", TotLen=" + totLen);

    // Write data to the file

    byte[] buf = m_smbPkt.getBuffer();
    int wrtlen = 0;

    // Access the disk interface and write to the file

    try {
      // Access the disk interface that is associated with the shared device

      DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

      // Write to the file

      wrtlen = disk.writeFile(m_sess, conn, netFile, buf, dataPos, dataLen, offset);

      // Return the initial MPX response

      outPkt.setParameterCount(1);
      outPkt.setAndXCommand(0xFF);
      outPkt.setParameter(1, 0xFFFF);
      outPkt.setByteCount(0);

      // Send the write response

      m_sess.sendResponseSMB(outPkt);

      // Update the remaining data length and write offset

      totLen -= wrtlen;
      offset += wrtlen;

      int rxlen = 0;

      while (totLen > 0) {

        // Receive the next write packet

        rxlen = m_sess.getPacketHandler().readPacket(m_smbPkt);
        m_smbPkt.setReceivedLength(rxlen);

        // Make sure it is a secondary WriteMPX type packet

        if (m_smbPkt.getCommand() != PacketType.WriteMpxSecondary)
          throw new IOException("Write MPX invalid packet type received");

        // Get the write length and buffer offset

        dataLen = m_smbPkt.getParameter(6);
        dataPos = m_smbPkt.getParameter(7) + RFCNetBIOSProtocol.HEADER_LEN;

        // Debug

        if (Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
          Debug.println("File WriteMPX Secondary [" + netFile.getFileId() + "] : Size=" + dataLen + " ,Pos=" + offset);

        // Write the block of data

        wrtlen = disk.writeFile(m_sess, conn, netFile, buf, dataPos, dataLen, offset);

        // Update the remaining data length and write offset

        totLen -= wrtlen;
        offset += wrtlen;
      }
    }
    catch (InvalidDeviceInterfaceException ex) {

      // Failed to get/initialize the disk interface

      m_sess.sendErrorResponseSMB(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
      return;
    }
    catch (java.io.IOException ex) {

      // Debug

      if ( Debug.EnableError)
        Debug.println("File WriteMPX Error [" + netFile.getFileId() + "] : " + ex);

      // Failed to read the file

      m_sess.sendErrorResponseSMB(SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
      return;
    }
  }

  /**
   * Run the LanMan protocol handler
   */
  public boolean runProtocol()
    throws java.io.IOException, SMBSrvException, TooManyConnectionsException {

    // Check if the SMB packet is initialized

    if (m_smbPkt == null)
      m_smbPkt = m_sess.getReceivePacket();

		// Check if the received packet has a valid SMB signature
		
		if ( m_smbPkt.checkPacketSignature() == false)
			throw new IOException("Invalid SMB signature");
			
    // Determine if the request has a chained command, if so then we will copy the incoming request
    // so that
    //  a chained reply can be built.

    SMBSrvPacket outPkt = m_smbPkt;
    boolean chainedCmd = hasChainedCommand(m_smbPkt);

    if (chainedCmd) {

      //  Debug

      if (Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_STATE))
        m_sess.debugPrintln("AndX Command = 0x" + Integer.toHexString(m_smbPkt.getAndXCommand()));

      //  Copy the request packet into a new packet for the reply

      outPkt = new SMBSrvPacket(m_smbPkt);
    }

		//	Reset the byte unpack offset
		
		m_smbPkt.resetBytePointer();

    //  Determine the SMB command type

    boolean handledOK = true;

    switch (m_smbPkt.getCommand()) {

      //  Session setup

      case PacketType.SessionSetupAndX :
        procSessionSetup(outPkt);
        break;

      //  Tree connect

      case PacketType.TreeConnectAndX :
        procTreeConnectAndX(outPkt);
        break;

      //  Transaction2

      case PacketType.Transaction2:
      case PacketType.Transaction:
        procTransact2(outPkt);
        break;

			//	Transaction/transaction2 secondary
			
			case PacketType.TransactionSecond:
			case PacketType.Transaction2Second:
				procTransact2Secondary(outPkt);
				break;
				
      //  Close a search started via the FindFirst transaction2 command

      case PacketType.FindClose2 :
        procFindClose(outPkt);
        break;

      //  Open a file

      case PacketType.OpenAndX :
        procOpenAndX(outPkt);
        break;

      //  Read a file

      case PacketType.ReadAndX :
        procReadAndX(outPkt);
        break;

        // Read MPX

      case PacketType.ReadMpx:
          procReadMPX(outPkt);
          break;

      // Write to a file

      case PacketType.WriteAndX:
          procWriteAndX(outPkt);
          break;

      // Write MPX

      case PacketType.WriteMpx:
          procWriteMPX(outPkt);
          break;

      //  Tree disconnect

      case PacketType.TreeDisconnect :
        procTreeDisconnect(outPkt);
        break;

      //  Lock/unlock regions of a file

      case PacketType.LockingAndX :
        procLockingAndX(outPkt);
        break;

      //	Logoff a user

      case PacketType.LogoffAndX :
        procLogoffAndX(outPkt);
        break;

			//	Tree connection (without AndX batching)
			
			case PacketType.TreeConnect:
				super.runProtocol();
				break;

			//	Rename file
			
			case PacketType.RenameFile:
				procRenameFile(outPkt);
				break;
				
      //  Echo request

      case PacketType.Echo :
        super.procEcho(outPkt);
        break;

      //  Default

      default :

        //  Get the tree connection details, if it is a disk or printer type connection then pass the request to the
        //  core protocol handler

        int treeId = m_smbPkt.getTreeId();
        TreeConnection conn = null;
        if ( treeId != -1)
        	conn = m_sess.findTreeConnection(m_smbPkt);

        if (conn != null) {

          //  Check if this is a disk or print connection, if so then send the request to the core protocol handler

          if (conn.getSharedDevice().getType() == ShareType.DISK || conn.getSharedDevice().getType() == ShareType.PRINTER) {

            //  Chain to the core protocol handler

            handledOK = super.runProtocol();
          }
          else if (conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

            //  Send the request to IPC$ remote admin handler

            IPCHandler.processIPCRequest(m_sess, outPkt);
            handledOK = true;
          }
        }
        break;
    }

    //  Return the handled status

    return handledOK;
  }
}