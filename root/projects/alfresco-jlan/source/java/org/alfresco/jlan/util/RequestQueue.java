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

package org.alfresco.jlan.util;

import java.util.LinkedList;

/**
 * Request Queue Base Class
 * 
 * <p>Provides a request queue for a thread pool of worker threads.
 *
 * @author gkspencer
 */
public class RequestQueue {

	//	List of file requests
	
	private LinkedList m_queue;
	
	/**
	 * Class constructor
	 */
	public RequestQueue() {
		m_queue = new LinkedList();
	}
	
	/**
	 * Return the number of requests in the queue
	 * 
	 * @return int
	 */
	public final synchronized int numberOfRequests() {
		return m_queue.size();
	}
	
	/**
	 * Add a request to the queue
	 * 
	 * @param req Object
	 */
	public final synchronized void addRequest(Object req) {
		
		//	Add the request to the queue
		
		m_queue.add(req);
		
		//	Notify workers that there is a request to process
		
		notifyAll();
	}
	
	/**
	 * Remove a request from the head of the queue
	 * 
	 * @return Object
	 * @exception InterruptedException
	 */
	public final synchronized Object removeRequest()
		throws InterruptedException {
		
		//	Wait until there is a request
		
		waitWhileEmpty();
		
		//	Get the request from the head of the queue
		
		return m_queue.removeFirst();
	}
	
	/**
	 * Wait for a write request to be added to the queue
	 * 
	 * @exception InterruptedException
	 */
	public final synchronized void waitWhileEmpty()
		throws InterruptedException {
			
		//	Wait until some work arrives on the queue
		
		while ( m_queue.size() == 0)
			wait();
	}
	
	/**
	 * Wait for the file request queue to be emptied
	 * 
	 * @exception InterruptedException
	 */
	public final synchronized void waitUntilEmpty()
		throws InterruptedException {
			
		//	Wait until the request queue is empty
		
		while ( m_queue.size() != 0)
			wait();
	}
}
