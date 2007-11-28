package org.alfresco.jlan.server.filesys.loader;

/*
 * FileProcessor.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.cache.FileState;

/**
 * File Processor Interface
 * 
 * <p>Allows post-processing of a cached file before being stored by the FileLoader or when a file load has just
 * completed.
 */
public interface FileProcessor {

	/**
	 * Process a cached file just before it is to be stored.
	 * 
	 * @param context DiskDeviceContext
	 * @param state FileState
	 * @param segment FileSegment
	 */
	void processStoredFile(DiskDeviceContext context, FileState state, FileSegment segment);
	
	/**
	 * Process a cached file just after being loaded.
	 *
	 * @param context DiskDeviceContext
	 * @param state FileState
	 * @param segment FileSegment
	 */
	void processLoadedFile(DiskDeviceContext context, FileState state, FileSegment segment);
}
