package org.alfresco.jlan.server.filesys.loader;

/*
 * FileRequestQueue.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.LinkedList;

/**
 * File Request Queue Class
 * 
 * <p>Synchronized queue of FileRequest objects.
 */
public class FileRequestQueue {

	//	List of file requests
	
	private LinkedList<FileRequest> m_queue;
	
	/**
	 * Class constructor
	 */
	public FileRequestQueue() {
		m_queue = new LinkedList<FileRequest>();
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
	 * @param req FileRequest
	 */
	public final synchronized void addRequest(FileRequest req) {
		
		//	Add the request to the queue
		
		m_queue.add(req);
		
		//	Notify workers that there is a request to process
		
		notifyAll();
	}
	
	/**
	 * Remove a request from the head of the queue
	 * 
	 * @return FileRequest
	 * @exception InterruptedException
	 */
	public final synchronized FileRequest removeRequest()
		throws InterruptedException {
		
		//	Wait until there is a request
		
		waitWhileEmpty();
		
		//	Get the request from the head of the queue
		
		return m_queue.removeFirst();
	}
	
	/**
	 * Remove a request from the head of the queue without waiting
	 * 
	 * @return FileRequest
	 */
	public final synchronized FileRequest removeRequestNoWait() {
		
		//	Get the request from the head of the queue
		
		return m_queue.removeFirst();
	}
	
	/**
	 * Remove all requests from the queue
	 */
	public final void removeAllRequests() {
	  m_queue.clear();
	}
	
	/**
	 * Wait for a file request to be added to the queue
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
