package org.alfresco.jlan.smb.dcerpc.server;

/*
 * DCEHandler.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;

import org.alfresco.jlan.smb.dcerpc.DCEBuffer;
import org.alfresco.jlan.smb.server.SMBSrvException;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * DCE Request Handler Interface
 */
public interface DCEHandler {

	/**
	 * Process a DCE/RPC request
	 * 
	 * @param sess SMBSrvSession
	 * @param inBuf DCEBuffer
	 * @param pipeFile DCEPipeFile
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	public void processRequest(SMBSrvSession sess, DCEBuffer inBuf, DCEPipeFile pipeFile)
		throws IOException, SMBSrvException;
}
