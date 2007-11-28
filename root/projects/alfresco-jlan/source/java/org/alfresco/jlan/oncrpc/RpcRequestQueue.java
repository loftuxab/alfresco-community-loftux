package org.alfresco.jlan.oncrpc;

/*
 * RpcRequestQueue.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.LinkedList;

/**
 * RPC Request Queue Class
 * 
 * <p>Provides a request queue for a thread pool of worker threads.
 */
public class RpcRequestQueue {

	//	List of RPC requests
	
	private LinkedList<RpcPacket> m_queue;
	
	/**
	 * Class constructor
	 */
	public RpcRequestQueue() {
		m_queue = new LinkedList<RpcPacket>();
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
	 * @param req RpcPacket
	 */
	public final synchronized void addRequest(RpcPacket req) {
		
		//	Add the request to the queue
		
		m_queue.add(req);
		
		//	Notify workers that there is a request to process
		
		notifyAll();
	}
	
	/**
	 * Remove a request from the head of the queue
	 * 
	 * @return RpcPacket
	 * @exception InterruptedException
	 */
	public final synchronized RpcPacket removeRequest()
		throws InterruptedException {
		
		//	Wait until there is a request
		
		waitWhileEmpty();
		
		//	Get the request from the head of the queue
		
		return m_queue.removeFirst();
	}
	
	/**
	 * Wait for a request to be added to the queue
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
	 * Wait for the request queue to be emptied
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
