package org.alfresco.jlan.server.filesys;

/*
 * FileIdInterface.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.FileNotFoundException;

import org.alfresco.jlan.server.SrvSession;


/**
 * File Id Interface
 * 
 * <p>Optional interface that a DiskInterface driver can implement to provide file id to path conversion.
 */
public interface FileIdInterface {

	/**
	 * Convert a file id to a share relative path
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param dirid int
	 * @param fileid
	 * @return String
	 * @exception FileNotFoundException 
	 */
	public String buildPathForFileId(SrvSession sess, TreeConnection tree, int dirid, int fileid)
		throws FileNotFoundException;
}
