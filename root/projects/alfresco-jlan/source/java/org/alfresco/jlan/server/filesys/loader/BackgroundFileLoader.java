package org.alfresco.jlan.server.filesys.loader;

/*
 * BackgroundFileLoader.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Background File Loader Interface
 * 
 * <p>Provides methods called by the multi-threaded background loader to load/save file data to
 * a temporary file.
 */
public interface BackgroundFileLoader {

  /**
	 * Load a file
	 * 
	 * @param loadReq FileRequest
	 * @return int
	 * @exception Exception
	 */
	public int loadFile(FileRequest loadReq)
		throws Exception;
	
	/**
 	 * Store a file
 	 * 
	 * @param saveReq FileRequest
	 * @return int
	 * @exception Exception
	 */
	public int storeFile(FileRequest saveReq)
		throws Exception;
}
