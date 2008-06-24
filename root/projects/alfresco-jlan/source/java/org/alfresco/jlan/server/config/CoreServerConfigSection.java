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

package org.alfresco.jlan.server.config;

import org.alfresco.jlan.server.memory.ByteBufferPool;
import org.alfresco.jlan.server.thread.ThreadRequestPool;

/**
 * Core Server Configuration section Class
 * 
 * @author gkspencer
 */
public class CoreServerConfigSection extends ConfigSection {

	// Core server configuration section name

	public static final String SectionName = "CoreServer";

	// Thread pool
	
	private ThreadRequestPool m_threadPool;
	
	// Memory pool
	
	private ByteBufferPool m_memoryPool;
	
	/**
	 * Class constructor
	 * 
	 * @param config ServerConfiguration
	 */
	public CoreServerConfigSection(ServerConfiguration config) {
		super(SectionName, config);
	}

	/**
	 * Return the global thread request pool
	 * 
	 * @return ThreadRequestPool
	 */
	public final ThreadRequestPool getThreadPool() {
		return m_threadPool;
	}
	
	/**
	 * Return the global memory pool
	 * 
	 * @return ByteBufferPool
	 */
	public final ByteBufferPool getMemoryPool() {
		return m_memoryPool;
	}
	
	/**
	 * Set the thread pool initial and maximum size
	 * 
	 * @param initSize int
	 * @param maxSize int
	 * @exception InvalidConfigurationException
	 */
	public final void setThreadPool( int initSize, int maxSize)
		throws InvalidConfigurationException {

		// Range check the initial and maximum thread counts
		
		if ( initSize <= 0 || maxSize <= 0)
			throw new InvalidConfigurationException("Invalid initial or maximum thread count, " + initSize + "/" + maxSize);
		
		if ( initSize > maxSize)
			throw new InvalidConfigurationException("Invalid initial thread count, higher than maximum count, " + initSize + "/" + maxSize);
		
		// Check if the thread pool has already been configured
		
		if ( m_threadPool != null)
			throw new InvalidConfigurationException("Thread pool already configured");
		
		// Create the thread pool
		
		m_threadPool = new ThreadRequestPool( "AlfJLANWorker", initSize);
	}
	
	/**
	 * Set the memory pool packet sizes/allocations
	 * 
	 * @param Buffer sizes int[]
	 * @param Initial allocations for each size int[]
	 * @param Maximim allocations for each size int[]
	 * @exception InvalidConfigurationException
	 */
	public final void setMemoryPool(int[] pktSizes, int[] initAlloc, int[] maxAlloc)
		throws InvalidConfigurationException {

		// Make sure the buffer size and allocation lists are the same length
		
		if (( pktSizes.length != initAlloc.length) || (pktSizes.length != maxAlloc.length))
			throw new InvalidConfigurationException("Invalid packet size/allocation lists, lengths do not match");
		
		// Make sure the packet size list is in ascending order
		
		if ( pktSizes.length > 1) {
			for ( int i = 1; i <= pktSizes.length - 1; i++)
				if ( pktSizes[i] < pktSizes[i - 1])
					throw new InvalidConfigurationException("Packet size list is not in ascending order");
		}
		
		// check if the memory pool has already been configured
		
		if ( m_memoryPool != null)
			throw new InvalidConfigurationException("Memory pool already configured");
		
		// Create the memory pool
		
		m_memoryPool = new ByteBufferPool( pktSizes, initAlloc, maxAlloc);
	}
}
