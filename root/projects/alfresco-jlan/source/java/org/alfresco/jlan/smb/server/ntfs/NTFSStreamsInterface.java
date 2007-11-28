package org.alfresco.jlan.smb.server.ntfs;

/*
 * NTFSStreamsInterface.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.IOException;

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.TreeConnection;

/**
 * NTFS Streams Interface
 * 
 * <p>Optional interface that a DiskInterface driver can implement to provide file streams support.
 */
public interface NTFSStreamsInterface {

	/**
	 * Determine if NTFS streams are enabled
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @return boolean
	 */
	public boolean hasStreamsEnabled(SrvSession sess, TreeConnection tree);
	
	/**
	 * Return stream information for the specified stream
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param streamInfo StreamInfo
	 * @return StreamInfo
	 * @exception IOException		I/O error occurred
	 */
	public StreamInfo getStreamInformation(SrvSession sess, TreeConnection tree, StreamInfo streamInfo)
		throws IOException;
		
	/**
	 * Return a list of the streams for the specified file
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param fileName String
	 * @return StreamInfoList
	 * @exception IOException 	I/O error occurred
	 */
	public StreamInfoList getStreamList(SrvSession sess, TreeConnection tree, String fileName)
		throws IOException;
		
	/**
	 * Rename a stream
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param oldName String
	 * @param newName String
	 * @param overWrite boolean
	 * @exception IOException
	 */
	public void renameStream(SrvSession sess, TreeConnection tree, String oldName, String newName, boolean overWrite)
		throws IOException;
}
