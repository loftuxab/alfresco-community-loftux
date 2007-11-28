package org.alfresco.jlan.oncrpc.nfs;

/*
 * ShareDetails.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * Share Details Class
 * 
 * <p>Contains the file id cache, active search cache and tree connection details of a shared
 * filesystem.
 */
public class ShareDetails {

	//	Share name
	
	private String m_name;
	
	//	File id to path conversion cache
	
	private FileIdCache m_idCache;
	
	//	Flag to indicate if the filesystem driver for this share supports file id lookups
	//	via the FileIdInterface
	
	private boolean m_fileIdLookup;
	
	/**
	 * Class constructor
	 *
	 * @param name String
	 * @param fileIdSupport boolean
	 */
	public ShareDetails(String name, boolean fileIdSupport) {
		
		//	Save the share name
		
		m_name = name;
		
		//	Set the file id support flag
		
		m_fileIdLookup = fileIdSupport;
		
		//	Create the file id and search caches
		
		m_idCache = new FileIdCache();
	}

	/**
	 * Return the share name
	 * 
	 * @return String
	 */
	public final String getName() {
		return m_name;	
	}
	
	/**
	 * Return the file id cache
	 * 
	 * @return FileIdCache
	 */
	public final FileIdCache getFileIdCache() {
		return m_idCache;
	}
	
	/**
	 * Determine if the filesystem driver for this share has file id support
	 * 
	 * @return boolean
	 */
	public final boolean hasFileIdSupport() {
	  return m_fileIdLookup;
	}
}
