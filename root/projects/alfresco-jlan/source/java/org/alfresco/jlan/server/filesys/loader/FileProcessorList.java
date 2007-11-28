package org.alfresco.jlan.server.filesys.loader;

/*
 * FileProcessorList.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * File Processor List Class
 */
public class FileProcessorList {

	//	File processor list
	
	private List<FileProcessor> m_list;
	
	/**
	 * Default constructor
	 */
	public FileProcessorList() {
		m_list = new ArrayList<FileProcessor>();
	}
	
	/**
	 * Add a file processor to the list
	 * 
	 * @param proc FileProcessor
	 */
	public final void addProcessor(FileProcessor proc) {
		m_list.add(proc);
	}
	
	/**
	 * Return the number of file processors in the list
	 * 
	 * @return int
	 */
	public final int numberOfProcessors() {
		return m_list.size();
	}
	
	/**
	 * Return the required file processor
	 * 
	 * @param idx int
	 * @return FileProcessor
	 */
	public final FileProcessor getProcessorAt(int idx) {
		
		//	Check the index
		
		if ( idx < 0 || idx >= m_list.size())
			return null;
			
		//	Return the required file processor
		
		return m_list.get(idx);
	}
	
	/**
	 * Remove a file processor from the list
	 * 
	 * @param idx int
	 * @return FileProcessor
	 */
	public final FileProcessor removeProcessorAt(int idx) {		

		//	Check the index
			
		if ( idx < 0 || idx >= m_list.size())
			return null;
				
		//	Remove the required file processor
			
    return m_list.remove(idx);
	}
	
	/**
	 * Remove all file processors from the list
	 */
	public final void removeAllProcessors() {
		m_list.clear();
	}
}
