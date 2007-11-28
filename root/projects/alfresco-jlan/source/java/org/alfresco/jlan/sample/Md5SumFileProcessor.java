package org.alfresco.jlan.sample;

/*
 * Md5SumFileProcessor.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.security.MessageDigest;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.cache.FileState;
import org.alfresco.jlan.server.filesys.loader.FileProcessor;
import org.alfresco.jlan.server.filesys.loader.FileSegment;
import org.alfresco.jlan.util.HexDump;

/**
 * MD5 Checksum File Processor Class
 * 
 * <p>Calculate an MD5 checksum for a file before it is stored and save the result in a database field.
 */
public class Md5SumFileProcessor implements FileProcessor {

	//	Flag to indicate if the filesystem table has an MD5Sum field
	
	private boolean m_md5field = true;
	
	/**
	 * Process a cached file just before it is to be stored.
	 * 
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processStoredFile(DiskDeviceContext context, FileState state, FileSegment segment) {
		
		//	Calculate an MD5 checksum for the file
		
		String md5sum = calculateMd5Checksum(segment);
		Debug.println("## StoreFile file=" + state.getPath() + ", fid=" + state.getFileId() + ", temp=" + segment.getTemporaryFile() +
									", MD5=" + md5sum);
	} 

	/**
	 * Process a cached file just after being loaded.
	 *
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processLoadedFile(DiskDeviceContext context, FileState state, FileSegment segment) {
	}
	
	/**
	 * Calculate an MD5 checksum for a file
	 * 
	 * @param segment FileSegment
	 * @return String
	 */
	protected final String calculateMd5Checksum(FileSegment segment) {

		MessageDigest md5 = null;
		String checksum = null;
		
		try {

			//	Get an MD5 message digest
		
			md5 = MessageDigest.getInstance("MD5");
			
			//	Create a buffer for reading the file
			
			byte[] inbuf = new byte[512];
			long fileOff = 0L;
			int rdlen = segment.readBytes(inbuf, inbuf.length, 0, fileOff);
			
			//	Read the file and calculate the MD5 checksum
			
			while ( rdlen > 0) {
				
				//	Update the MD5 checksum
				
				md5.update(inbuf, 0, rdlen);
				
				//	Update the file offset
				
				fileOff += rdlen;
				
				//	Read another block of data
				
				rdlen = segment.readBytes(inbuf, inbuf.length, 0, fileOff);
			}

			//	Get the final MD5 checksum
			
			byte[] md5sum = md5.digest();
			
			//	Convert the checksum to a hex string

			checksum = HexDump.hexString(md5sum);
		}
		catch (Exception ex) {			
		}
		
		//	Return the checksum
		
		return checksum;
	}
}
