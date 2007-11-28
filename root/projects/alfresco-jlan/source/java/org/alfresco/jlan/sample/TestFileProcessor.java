package org.alfresco.jlan.sample;

/*
 * TestFileProcessor.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.cache.FileState;
import org.alfresco.jlan.server.filesys.loader.FileProcessor;
import org.alfresco.jlan.server.filesys.loader.FileSegment;


/**
 * Test File Processor Class
 */
public class TestFileProcessor implements FileProcessor {

	/**
	 * Process a cached file just before it is to be stored.
	 * 
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processStoredFile(DiskDeviceContext context, FileState state, FileSegment segment) {
		try {
			Debug.println("## TestFileProcessor Storing file=" + state.getPath() + ", fid=" + state.getFileId() + ", cache=" + segment.getTemporaryFile());
		}
		catch (Exception ex) {
		}
	}

	/**
	 * Process a cached file just after being loaded.
	 *
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processLoadedFile(DiskDeviceContext context, FileState state, FileSegment segment) {
		try {
			Debug.println("## TestFileProcessor Loaded file=" + state.getPath() + ", fid=" + state.getFileId() + ", cache=" + segment.getTemporaryFile());
		}
		catch (Exception ex) {
		}
	}

}
