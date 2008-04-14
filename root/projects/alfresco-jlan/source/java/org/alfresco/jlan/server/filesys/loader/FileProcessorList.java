/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.server.filesys.loader;

import java.util.ArrayList;
import java.util.List;

/**
 * File Processor List Class
 *
 * @author gkspencer
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
