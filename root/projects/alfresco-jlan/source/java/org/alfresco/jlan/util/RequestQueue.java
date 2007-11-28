package org.alfresco.jlan.util;

/*
 * RequestQueue.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.LinkedList;

/**
 * Request Queue Base Class
 * 
 * <p>Provides a request queue for a thread pool of worker threads.
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
