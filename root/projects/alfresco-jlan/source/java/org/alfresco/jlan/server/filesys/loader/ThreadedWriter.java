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

import org.alfresco.jlan.debug.Debug;


/**
 * Threaded Writer Class
 * 
 * <p>Allows a network protocol handler to queue a write request to a thread pool for delayed writing.
 *
 * @author gkspencer
 */
public class ThreadedWriter {

	//	Default/minimum/maximum number of worker threads to use
	
	public static final int DefaultWorkerThreads			= 8;
	public static final int MinimumWorkerThreads			= 4;
	public static final int MaximumWorkerThreads			= 50;

	//	Queue of delayed write requests
	
	private WriteRequestQueue m_queue;

	//	Worker threads
	
	private ThreadWorker[] m_workers;
	
	//	Debug enable flag
	
	private static boolean m_debug = true;
		
	/**
	 * Thread Worker Inner Class
	 */
	protected class ThreadWorker implements Runnable {

		//	Worker thread
		
		private Thread mi_thread;

		//	Worker unique id
		
		private int mi_id;

		//	Shutdown flag
		
		private boolean mi_shutdown = false;
		
		/**
		 * Class constructor
		 * 
		 * @param name String
		 * @param id int
		 */
		public ThreadWorker(String name, int id) {
			mi_id     = id;
			
			mi_thread = new Thread(this);
			mi_thread.setName(name);
			mi_thread.setDaemon(true);
			mi_thread.start();
		}

		/**
		 * Request the worker thread to shutdown
		 */
		public final void shutdownRequest() {
			mi_shutdown = true;
			try {
				mi_thread.interrupt();
			}
			catch (Exception ex) {
			}
		}
				
		/**
		 * Run the thread
		 */
		public void run() {
			
			//	Loop until shutdown
			
			WriteRequest writeReq = null;
			
			while ( mi_shutdown == false) {

				try {
					
					//	Wait for a write request to be queued
					
					writeReq = m_queue.removeRequest();
				}
				catch (InterruptedException ex) {
				
					//	Check for shutdown
					
					if ( mi_shutdown == true)
						break;
				}
					
				//	If the write request is valid process it
				
				if ( writeReq != null) {

					//	DEBUG
					
					if ( Debug.EnableInfo && hasDebug())
						Debug.println("ThreadedWriter writeReq=" + writeReq);
						
					//	Perform the delayed write request
					
					writeReq.doWrite();
				}
			}
						
			//	DEBUG
			
			if ( Debug.EnableInfo && hasDebug())
				Debug.println("ThreadedWriter thread=" + mi_thread.getName() + " shutdown");
		}
	};

	/**
	 * Class constructor
	 */
	public ThreadedWriter() {
		
		//	Create the request queue
		
		m_queue = new WriteRequestQueue();
		
		//	Create the worker threads
		
		m_workers = new ThreadWorker[DefaultWorkerThreads];
		
		for ( int i = 0; i < m_workers.length; i++)
			m_workers[i] = new ThreadWorker("ThreadedWriter_" + ( i+1), i);
	}
	
	/**
	 * Check if debug output is enabled
	 * 
	 * @return boolean
	 */
	public final static boolean hasDebug() {
		return m_debug;
	}

	/**
	 * Add a delayed write request to the queue
	 *
	 * @param req WriteRequest
	 */
	public final void addWriteRequest(WriteRequest req) {
		m_queue.addRequest(req);
	}
	
	/**
	 * Shutdown the threaded writer and release all resources
	 */
	public void shutdownWriter() {

		//	Shutdown the worker threads
		
		if ( m_workers != null) {
			for ( int i = 0; i < m_workers.length; i++)
				m_workers[i].shutdownRequest();
		}
	}
}
