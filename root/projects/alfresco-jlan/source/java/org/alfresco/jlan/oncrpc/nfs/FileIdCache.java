package org.alfresco.jlan.oncrpc.nfs;

/*
 * FileIdCache.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.Hashtable;

/**
 * File Id Cache Class
 * 
 * <p>Converts a file/directory id to a share relative path.
 */
public class FileIdCache {

	//	File id to path cache
	
	private Hashtable<Integer, String> m_idCache;
	
	/**
	 * Default constructor
	 */
	public FileIdCache() {
		m_idCache = new Hashtable<Integer, String>();
	}
	
	/**
	 * Add an entry to the cache
	 * 
	 * @param fid int
	 * @param path String
	 */
	public final void addPath(int fid, String path) {
		m_idCache.put(new Integer(fid), path);
	}
	
	/**
	 * Convert a file id to a path
	 * 
	 * @param fid int
	 * @return String
	 */
	public final String findPath(int fid) {
		return m_idCache.get(new Integer(fid));
	}
	
	/**
	 * Delete an entry from the cache
	 * 
	 * @param fid int
	 */
	public final void deletePath(int fid) {
		m_idCache.remove(new Integer(fid));
	}
}
