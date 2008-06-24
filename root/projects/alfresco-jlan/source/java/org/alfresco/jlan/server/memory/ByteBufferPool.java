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

package org.alfresco.jlan.server.memory;

/**
 * Byte buffer Pool Class
 * 
 * <p>Memory pool of different sized byte buffers.
 * 
 * @author gkspencer
 */
public class ByteBufferPool {

	// List of byte buffer pools
	
	private ByteBufferList[] m_bufferLists;
	
	// Buffer sizes, initial allocation and maximum allocation
	
	private int[] m_bufSizes;
	private int[] m_initAlloc;
	private int[] m_maxAlloc;
	
	/**
	 * Class constuctor
	 * 
	 * @param Buffer sizes int[]
	 * @param Initial allocations for each size int[]
	 * @param Maximim allocations for each size int[]
	 */
	public ByteBufferPool( int[] bufSizes, int[] initAlloc, int[] maxAlloc) {
		
		// Validate the buffer size, initial allocation and max allocation arrays
		
		if (( bufSizes.length != initAlloc.length) && (bufSizes.length != maxAlloc.length))
			throw new RuntimeException("Invalid ByteBufferPool parameters");
		
		// Save the buffer details
		
		m_bufSizes  = bufSizes;
		m_initAlloc = initAlloc;
		m_maxAlloc  = maxAlloc;
		
		// Allocate the buffer list
		
		m_bufferLists = new ByteBufferList[ m_bufSizes.length];
		
		// Allocate the byte buffer lists
		
		for ( int i = 0; i < m_bufSizes.length; i++) {
			
			// Allocate a byte buffer list for the current buffer size
			
			ByteBufferList bufList = new ByteBufferList( m_bufSizes[ i], m_initAlloc[ i], m_maxAlloc[ i]);
			m_bufferLists[ i] = bufList;
		}
	}
	
	/**
	 * Allocate a byte buffer from the appropriate buffer list, if there are buffers available then do not wait
	 * 
	 *  @param siz int
	 *  @return byte[]
	 */
	public final byte[] allocateBuffer( int siz) {

		// Allocate a buffer, do not wait if there are no buffers available
		
		return allocateBuffer( siz, 0);
	}
	
	/**
	 * Allocate a byte buffer from the appropriate buffer list
	 * 
	 *  @param siz int
	 *  @param waitTime long
	 *  @return byte[]
	 */
	public final byte[] allocateBuffer( int siz, long waitTime) {
		
		// Allocate the buffer from the appropriate list
		
		int idx = 0;
		
		while ( idx < m_bufSizes.length && siz > m_bufSizes[ idx])
			idx++;
		
		if ( idx == m_bufSizes.length)
			throw new RuntimeException("Requested allocation size too long for pool, " + siz);
		
		// Allocate a buffer
		
		return m_bufferLists[ idx].allocateBuffer( waitTime);
	}

	/**
	 * Release a buffer
	 * 
	 * @param buf byte[]
	 */
	public final void releaseBuffer( byte[] buf) {
		
		// Find the buffer list the buffer was allocated from
		
		int idx = 0;
		
		while ( idx < m_bufSizes.length && buf.length != m_bufSizes[ idx])
			idx++;
		
		if ( idx == m_bufSizes.length)
			throw new RuntimeException("Released buffer does not match any buffer sizes, " + buf.length);
		
		// Release the buffer
		
		m_bufferLists[ idx].releaseBuffer( buf);
	}
	
	/**
	 * Shrink the buffer lists back to their initial allocation sizes
	 */
	public final void shrinkLists() {
		for ( int i = 0; i < m_bufferLists.length; i++)
			m_bufferLists[ i].shrinkList();
	}
	
	/**
	 * Return the length of the smallest packet size available
	 * 
	 * @return int
	 */
	public final int getSmallestSize() {
		return m_bufSizes[ 0];
	}
	
	/**
	 * Return the length of the largest packet size available
	 * 
	 * @return int
	 */
	public final int getLargestSize() {
		return m_bufSizes[ m_bufSizes.length - 1];
	}
	
	/**
	 * Return the byte buffer pool details as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[BufferLists: ");
		
		for ( int i = 0; i < m_bufferLists.length; i++) {
			str.append( m_bufferLists[ i].toString());
			str.append(" ");
		}
		
		str.append("]");
		
		return str.toString();
	}
}
