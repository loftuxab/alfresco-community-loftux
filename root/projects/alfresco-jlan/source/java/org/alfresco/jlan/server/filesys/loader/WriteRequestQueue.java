package org.alfresco.jlan.server.filesys.loader;

/*
 * WriteRequestQueue.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.LinkedList;

/**
 * Write Request Queue Class
 */
public class WriteRequestQueue {

	//	List of file requests
	
	private LinkedList<WriteRequest> m_queue;
	
	/**
	 * Class constructor
	 */
	public WriteRequestQueue() {
		m_queue = new LinkedList<WriteRequest>();
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
	 * @param req WriteRequest
	 */
	public final synchronized void addRequest(WriteRequest req) {
		
		//	Add the request to the queue
		
		m_queue.add(req);
		
		//	Notify workers that there is a request to process
		
		notifyAll();
	}
	
	/**
	 * Remove a request from the head of the queue
	 * 
	 * @return WriteRequest
	 * @exception InterruptedException
	 */
	public final synchronized WriteRequest removeRequest()
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
