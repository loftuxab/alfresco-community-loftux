package org.alfresco.jlan.smb.server;

/*
 * IPCHandler.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.FileNotFoundException;
import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.core.InvalidDeviceInterfaceException;
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.DiskOfflineException;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.PathNotFoundException;
import org.alfresco.jlan.server.filesys.TooManyFilesException;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.jlan.server.filesys.UnsupportedInfoLevelException;
import org.alfresco.jlan.smb.FileInfoLevel;
import org.alfresco.jlan.smb.PacketType;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.TransactionNames;
import org.alfresco.jlan.smb.dcerpc.DCEPipeType;
import org.alfresco.jlan.smb.dcerpc.server.DCEPipeFile;
import org.alfresco.jlan.smb.dcerpc.server.DCEPipeHandler;
import org.alfresco.jlan.smb.server.ntfs.NTFSStreamsInterface;
import org.alfresco.jlan.smb.server.ntfs.StreamInfoList;
import org.alfresco.jlan.util.DataBuffer;
import org.alfresco.jlan.util.DataPacker;

/**
 * <p>The IPCHandler class processes requests made on the IPC$ remote admin pipe. The code is shared
 * amongst different SMB protocol handlers.
 */
class IPCHandler {

  /**
   * Process a request made on the IPC$ remote admin named pipe.
   *
   * @param sess SMBSrvSession
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.server.SMBSrvException The exception description.
   */
  public static void processIPCRequest(SMBSrvSession sess, SMBSrvPacket outPkt)
    throws java.io.IOException, SMBSrvException {

    //  Get the received packet from the session and verify that the connection is valid

    SMBSrvPacket smbPkt = sess.getReceivePacket();

    //  Get the tree id from the received packet and validate that it is a valid
    //  connection id.

    TreeConnection conn = sess.findTreeConnection( smbPkt);

    if (conn == null) {
      sess.sendErrorResponseSMB(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ Request [" + smbPkt.getTreeId() + "] - cmd = " + smbPkt.getPacketTypeString());

    //  Determine the SMB command

    switch (smbPkt.getCommand()) {

      //  Open file request

      case PacketType.OpenAndX :
      case PacketType.OpenFile :
      	procIPCFileOpen(sess,smbPkt,outPkt);
        break;

      //  Read file request

      case PacketType.ReadFile :
      	procIPCFileRead(sess,smbPkt,outPkt);
        break;

			//	Read AndX file request
			
      case PacketType.ReadAndX :
      	procIPCFileReadAndX(sess,smbPkt,outPkt);
        break;
      
			//	Write file request
			
			case PacketType.WriteFile:
				procIPCFileWrite(sess,smbPkt,outPkt);
				break;

			//	Write AndX file request
							
			case PacketType.WriteAndX:
				procIPCFileWriteAndX(sess,smbPkt,outPkt);
				break;
			
      //  Close file request

      case PacketType.CloseFile :
        procIPCFileClose(sess,smbPkt,outPkt);
        break;

			//	NT create andX request
			
			case PacketType.NTCreateAndX:
				procNTCreateAndX(sess,smbPkt,outPkt);
				break;
				
      //  Default, respond with an unsupported function error.

      default :
        sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
        break;
    }
  }
  
  /**
   * Process an IPC$ transaction request.
   *
   * @param vc VirtualCircuit
   * @param tbuf SrvTransactBuffer
   * @param sess SMBSrvSession
   * @param outPkt SMBSrvPacket
   */
  protected static void procTransaction(VirtualCircuit vc, SrvTransactBuffer tbuf, SMBSrvSession sess, SMBSrvPacket outPkt)
    throws IOException, SMBSrvException {

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ Transaction  pipe=" + tbuf.getName() + ", subCmd=" + NamedPipeTransaction.getSubCommand(tbuf.getFunction()));

    //  Call the required transaction handler

    if (tbuf.getName().compareTo(TransactionNames.PipeLanman) == 0) {

      //  Call the \PIPE\LANMAN transaction handler to process the request

      if ( PipeLanmanHandler.processRequest(tbuf, sess, outPkt))
      	return;
    }
    
    //	Process the pipe command
    
    switch ( tbuf.getFunction()) {
      
      //	Set named pipe handle state
      
      case NamedPipeTransaction.SetNmPHandState:
      	procSetNamedPipeHandleState(sess, vc, tbuf, outPkt);
      	break;
      	
      //	Named pipe transation request, pass the request to the DCE/RPC handler
      
      case NamedPipeTransaction.TransactNmPipe:
      	DCERPCHandler.processDCERPCRequest(sess, vc, tbuf, outPkt);
				break;
				
	      //  Query file information via handle
	      
      case PacketType.Trans2QueryFile:
        procTrans2QueryFile(sess, vc, tbuf, outPkt);
        break;
        
			//	Unknown command

			default:			
      	sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      	break;
    }
  }
  
  /**
   * Process a special IPC$ file open request.
   * 
   * @param sess SMBSrvSession
   * @param rxPkt SMBSrvPacket
   * @param outPkt SMBSrvPacket
   */
  protected static void procIPCFileOpen(SMBSrvSession sess, SMBSrvPacket rxPkt, SMBSrvPacket outPkt)
  	throws IOException, SMBSrvException {

    //  Get the data bytes position and length

    int dataPos = rxPkt.getByteOffset();
    int dataLen = rxPkt.getByteCount();
    byte[] buf  = rxPkt.getBuffer();

    //  Extract the filename string

    String fileName = DataPacker.getString(buf, dataPos, dataLen);
		
		//	Debug
		
		if ( Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
			sess.debugPrintln("IPC$ Open file = " + fileName);
		  
		//	Check if the requested IPC$ file is valid
		
		int pipeType = DCEPipeType.getNameAsType(fileName);
		if ( pipeType == -1) {
		  sess.sendErrorResponseSMB(SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
		  return;
		}

    //  Get the tree connection details

    TreeConnection conn = sess.findTreeConnection( rxPkt);

    if (conn == null) {
      sess.sendErrorResponseSMB(SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
      return;
    }
    
		//	Create a network file for the special pipe
		
		DCEPipeFile pipeFile = new DCEPipeFile(pipeType);
		pipeFile.setGrantedAccess(NetworkFile.READWRITE);

    //  Add the file to the list of open files for this tree connection

		int fid = -1;
		
		try {
    	fid = conn.addFile(pipeFile, sess);
		}
		catch ( TooManyFilesException ex) {

      //  Too many files are open on this connection, cannot open any more files.

      sess.sendErrorResponseSMB(SMBStatus.DOSTooManyOpenFiles, SMBStatus.ErrDos);
      return;
		}

    //  Build the open file response

    outPkt.setParameterCount(15);

    outPkt.setAndXCommand(0xFF);
    outPkt.setParameter(1, 0); // AndX offset

    outPkt.setParameter(2, fid);
    outPkt.setParameter(3, 0); 	// file attributes
    outPkt.setParameter(4, 0); 	// last write time
    outPkt.setParameter(5, 0); 	// last write date
    outPkt.setParameterLong(6, 0); 	// file size
    outPkt.setParameter(8, 0);
    outPkt.setParameter(9, 0);
    outPkt.setParameter(10, 0); // named pipe state
    outPkt.setParameter(11, 0);
    outPkt.setParameter(12, 0); // server FID (long)
    outPkt.setParameter(13, 0);
    outPkt.setParameter(14, 0);

    outPkt.setByteCount(0);

    //  Send the response packet

		sess.sendResponseSMB(outPkt);
  }

	/**
	 * Process an IPC pipe file read request
	 * 
   * @param sess SMBSrvSession
   * @param rxPkt SMBSrvPacket
   * @param outPkt SMBSrvPacket
	 */
	protected static void procIPCFileRead(SMBSrvSession sess, SMBSrvPacket rxPkt, SMBSrvPacket outPkt)
		throws IOException, SMBSrvException {
		  
	  //	Check if the received packet is a valid read file request
	  
	  if ( rxPkt.checkPacketIsValid(5, 0) == false) {
		  
		  //	Invalid request
		  
      sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
		}

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ File Read");

		//	Pass the read request the DCE/RPC handler
		
		DCERPCHandler.processDCERPCRead(sess,rxPkt,outPkt);
	}
	
	/**
	 * Process an IPC pipe file read andX request
	 * 
   * @param sess SMBSrvSession
   * @param rxPkt SMBSrvPacket
   * @param outPkt SMBSrvPacket
	 */
	protected static void procIPCFileReadAndX(SMBSrvSession sess, SMBSrvPacket rxPkt, SMBSrvPacket outPkt)
		throws IOException, SMBSrvException {
		  
	  //	Check if the received packet is a valid read andX file request
	  
	  if ( rxPkt.checkPacketIsValid(10, 0) == false) {
		  
		  //	Invalid request
		  
      sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
		}

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ File Read AndX");

		//	Pass the read request the DCE/RPC handler
		
		DCERPCHandler.processDCERPCRead(sess,rxPkt,outPkt);
	}
	
	/**
	 * Process an IPC pipe file write request
	 * 
   * @param sess SMBSrvSession
   * @param rxPkt SMBSrvPacket
   * @param outPkt SMBSrvPacket
   */
	protected static void procIPCFileWrite(SMBSrvSession sess, SMBSrvPacket rxPkt, SMBSrvPacket outPkt)
		throws IOException, SMBSrvException {

	  //	Check if the received packet is a valid write file request
	  
	  if ( rxPkt.checkPacketIsValid(5, 0) == false) {
		  
		  //	Invalid request
		  
      sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
		}

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ File Write");

		//	Pass the write request the DCE/RPC handler
		
		DCERPCHandler.processDCERPCRequest(sess,rxPkt,outPkt);
	}
		
	/**
	 * Process an IPC pipe file write andX request
	 * 
   * @param sess SMBSrvSession
   * @param rxPkt SMBSrvPacket
   * @param outPkt SMBSrvPacket
   */
	protected static void procIPCFileWriteAndX(SMBSrvSession sess, SMBSrvPacket rxPkt, SMBSrvPacket outPkt)
		throws IOException, SMBSrvException {

	  //	Check if the received packet is a valid write andX request
	 
	  if ( rxPkt.checkPacketIsValid(12, 0) == false) {
		  
		  //	Invalid request
		  
      sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
		}

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ File Write AndX");

		//	Pass the write request the DCE/RPC handler
		
		DCERPCHandler.processDCERPCRequest(sess,rxPkt,outPkt);
	}
		
  /**
   * Process a special IPC$ file close request.
   * 
   * @param sess SMBSrvSession
   * @param rxPkt SMBSrvPacket
   * @param outPkt SMBSrvPacket
   */
  protected static void procIPCFileClose(SMBSrvSession sess, SMBSrvPacket rxPkt, SMBSrvPacket outPkt)
  	throws IOException, SMBSrvException {

    //  Check that the received packet looks like a valid file close request

    if (rxPkt.checkPacketIsValid(3, 0) == false) {
      sess.sendErrorResponseSMB(SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
      return;
    }

    //  Get the tree id from the received packet and validate that it is a valid
    //   connection id.

    TreeConnection conn = sess.findTreeConnection( rxPkt);

    if (conn == null) {
      sess.sendErrorResponseSMB(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

    //  Get the file id from the request

    int fid = rxPkt.getParameter(0);
    DCEPipeFile netFile = (DCEPipeFile) conn.findFile(fid);

    if (netFile == null) {
      sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ File close [" + rxPkt.getTreeId() + "] fid=" + fid);

    //  Remove the file from the connections list of open files

    conn.removeFile(fid, sess);

    //  Build the close file response

    outPkt.setParameterCount(0);
    outPkt.setByteCount(0);

    //  Send the response packet

		sess.sendResponseSMB(outPkt);
  }
  
  /**
   * Process a set named pipe handle state request
   * 
   * @param sess SMBSrvSession
   * @param vc VirtualCircuit
   * @param tbuf SrvTransactBuffer
   * @param outPkt SMBSrvPacket
   */
	protected static void procSetNamedPipeHandleState(SMBSrvSession sess, VirtualCircuit vc, SrvTransactBuffer tbuf, SMBSrvPacket outPkt)
  	throws IOException, SMBSrvException {
	  
  	//	Get the request parameters

		DataBuffer setupBuf = tbuf.getSetupBuffer();
		setupBuf.skipBytes(2);
  	int fid = setupBuf.getShort();
  	
  	DataBuffer paramBuf = tbuf.getParameterBuffer();
  	int state = paramBuf.getShort();

		//	Get the connection for the request
		  	
    TreeConnection conn = vc.findConnection( tbuf.getTreeId());
    
  	//	Get the IPC pipe file for the specified file id
  	
    DCEPipeFile netFile = (DCEPipeFile) conn.findFile(fid);
    if (netFile == null) {
      sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }
    
  	//	Debug
  	
    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("  SetNmPHandState pipe=" + netFile.getName() + ", fid=" + fid + ", state=0x" + Integer.toHexString(state));

		//	Store the named pipe state
		
		netFile.setPipeState(state);
		
  	//	Setup the response packet
  
  	SMBSrvTransPacket.initTransactReply(outPkt,0,0,0,0);
  
    //  Send the response packet
  
  	sess.sendResponseSMB(outPkt);
	}
	
	/**
	 * Process an NT create andX request
	 * 
   * @param sess SMBSrvSession
   * @param rxPkt SMBSrvPacket
   * @param outPkt SMBSrvPacket
	 */   
	protected static void procNTCreateAndX(SMBSrvSession sess, SMBSrvPacket rxPkt, SMBSrvPacket outPkt)
  	throws IOException, SMBSrvException {
  		
    //  Get the tree id from the received packet and validate that it is a valid
    //  connection id.

    TreeConnection conn = sess.findTreeConnection( rxPkt);

    if (conn == null) {
      sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }

    //  Extract the NT create andX parameters

		NTParameterPacker prms = new NTParameterPacker(rxPkt.getBuffer(),SMBSrvPacket.PARAMWORDS + 5);
		
		int nameLen 	 = prms.unpackWord();
		int flags   	 = prms.unpackInt();
		int rootFID 	 = prms.unpackInt();
		int accessMask = prms.unpackInt();
		long allocSize = prms.unpackLong();
		int attrib     = prms.unpackInt();
		int shrAccess  = prms.unpackInt();
		int createDisp = prms.unpackInt();
		int createOptn = prms.unpackInt();
		int impersonLev= prms.unpackInt();
		int secFlags   = prms.unpackByte();

    //  Extract the filename string

		int pos = DataPacker.wordAlign(rxPkt.getByteOffset());
    String fileName = DataPacker.getUnicodeString(rxPkt.getBuffer(), pos, nameLen);
    if (fileName == null) {
      sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
      return;
    }

    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("NT Create AndX [" + rxPkt.getTreeId() + "] name=" + fileName + ", flags=0x" + Integer.toHexString(flags) + ", attr=0x" +
          							Integer.toHexString(attrib) + ", allocSize=" + allocSize);

		//	Check if the pipe name is a short or long name
		
		if ( fileName.startsWith("\\PIPE") == false)
			fileName = "\\PIPE" + fileName;
			
		//	Check if the requested IPC$ file is valid
		
		int pipeType = DCEPipeType.getNameAsType(fileName);
		if ( pipeType == -1) {
		  sess.sendErrorResponseSMB(SMBStatus.NTObjectNotFound, SMBStatus.NTErr);
		  return;
		}

		//	Check if there is a handler for the pipe file
		
		if ( DCEPipeHandler.getHandlerForType(pipeType) == null) {
		  sess.sendErrorResponseSMB(SMBStatus.NTAccessDenied, SMBStatus.NTErr);
		  return;
		}
		
		//	Create a network file for the special pipe
		
		DCEPipeFile pipeFile = new DCEPipeFile(pipeType);
		pipeFile.setGrantedAccess(NetworkFile.READWRITE);

    //  Add the file to the list of open files for this tree connection

		int fid = -1;
		
		try {
    	fid = conn.addFile(pipeFile, sess);
		}
		catch ( TooManyFilesException ex) {

      //  Too many files are open on this connection, cannot open any more files.

      sess.sendErrorResponseSMB(SMBStatus.Win32InvalidHandle, SMBStatus.NTErr);
      return;
		}

    //  Build the NT create andX response

    outPkt.setParameterCount(34);

		prms.reset(outPkt.getBuffer(), SMBSrvPacket.PARAMWORDS + 4);

		prms.packByte(0);
		prms.packWord(fid);
		prms.packInt(0x0001);		//	File existed and was opened

		prms.packLong(0);				// 	Creation time
		prms.packLong(0);				//	Last access time
		prms.packLong(0);				//	Last write time
		prms.packLong(0);				//	Change time

		prms.packInt(0x0080);		//	File attributes
		prms.packLong(4096);		//	Allocation size
		prms.packLong(0);				//	End of file
		prms.packWord(2);				//	File type - named pipe, message mode
		prms.packByte(0xFF);		//	Pipe instancing count
		prms.packByte(0x05);		//	IPC state bits
		
		prms.packByte(0);				//	directory flag
		
		outPkt.setByteCount(0);
		
    outPkt.setAndXCommand(0xFF);
		outPkt.setParameter(1, outPkt.getLength());	//	AndX offset

    //  Send the response packet

		sess.sendResponseSMB(outPkt);
  }
	
  /**
   * Process a transact2 query file information (via handle) request.
   *
   * @param sess SMBSrvSession
   * @param vc VirtualCircuit
   * @param tbuf       Transaction request details
   * @param outPkt SMBSrvPacket
   * @exception java.io.IOException The exception description.
   * @exception org.alfresco.aifs.smb.SMBSrvException SMB protocol exception
   */
  protected static final void procTrans2QueryFile(SMBSrvSession sess, VirtualCircuit vc, SrvTransactBuffer tbuf, SMBSrvPacket outPkt)
    throws java.io.IOException, SMBSrvException {

    //  Get the tree connection details

    int treeId = tbuf.getTreeId();
    TreeConnection conn = vc.findConnection(treeId);

    if (conn == null) {
      sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
      return;
    }

    //  Check if the user has the required access permission

    if (conn.hasReadAccess() == false) {

      //  User does not have the required access rights

      sess.sendErrorResponseSMB(SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
      return;
    }

    //  Get the file id and query path information level

    DataBuffer paramBuf = tbuf.getParameterBuffer();

    int fid = paramBuf.getShort();
    int infoLevl = paramBuf.getShort();

    //  Get the file details via the file id
    
    NetworkFile netFile = conn.findFile(fid);

    if (netFile == null) {
      sess.sendErrorResponseSMB(SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
      return;
    }
    
    //  Debug

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_IPC))
      sess.debugPrintln("IPC$ Query File - level=0x" + Integer.toHexString(infoLevl) + ", fid=" + fid + ", name=" + netFile.getFullName());

    //  Access the shared device disk interface

    try {

      //  Set the return parameter count, so that the data area position can be calculated.

      outPkt.setParameterCount(10);

      //  Pack the file information into the data area of the transaction reply

      byte[] buf = outPkt.getBuffer();
      int prmPos = DataPacker.longwordAlign(outPkt.getByteOffset());
      int dataPos = prmPos + 4;

      //  Pack the return parametes, EA error offset
      
      outPkt.setPosition(prmPos);
      outPkt.packWord(0);
      
      //  Create a data buffer using the SMB packet. The response should always fit into a single
      //  reply packet.
      
      DataBuffer replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);

      //  Build the file information from the network file details

      FileInfo fileInfo = new FileInfo( netFile.getName(), netFile.getFileSize(), netFile.getFileAttributes());
      
      fileInfo.setAccessDateTime( netFile.getAccessDate());
      fileInfo.setCreationDateTime( netFile.getCreationDate());
      fileInfo.setModifyDateTime( netFile.getModifyDate());
      fileInfo.setChangeDateTime( netFile.getModifyDate());
      
      fileInfo.setFileId( netFile.getFileId());
      
      //  Pack the file information into the return data packet

      int dataLen = QueryInfoPacker.packInfo(fileInfo, replyBuf, infoLevl, true);

      //  Check if any data was packed, if not then the information level is not supported

      if (dataLen == 0) {
        sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
        return;
      }

      SMBSrvTransPacket.initTransactReply(outPkt, 2, prmPos, dataLen, dataPos);
      outPkt.setByteCount(replyBuf.getPosition() - outPkt.getByteOffset());

      //  Send the transact reply

      sess.sendResponseSMB(outPkt);
    }
    catch (FileNotFoundException ex) {

      //  Requested file does not exist

      sess.sendErrorResponseSMB(SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
      return;
    }
    catch (PathNotFoundException ex) {

      //  Requested path does not exist

      sess.sendErrorResponseSMB(SMBStatus.NTObjectPathNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
      return;
    }
    catch (UnsupportedInfoLevelException ex) {

      //  Requested information level is not supported

      sess.sendErrorResponseSMB(SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
      return;
    }
    catch ( DiskOfflineException ex) {
      
      // Filesystem is offline
      
      sess.sendErrorResponseSMB( SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
    }
  }
}