/*
 * Copyright (C) 2005-2008 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jlan.server;

import java.util.LinkedList;

/**
 * Server Session Queue Class
 * 
 * @author gkspencer
 */
public class SrvSessionQueue {

	// List of sessions

	private LinkedList<SrvSession> m_queue;

	/**
	 * Class constructor
	 */
	public SrvSessionQueue() {
		m_queue = new LinkedList<SrvSession>();
	}

	/**
	 * Return the number of sessions in the queue
	 * 
	 * @return int
	 */
	public final synchronized int numberOfSessions() {
		return m_queue.size();
	}

	/**
	 * Add a session to the queue
	 * 
	 * @param sess SrvSession
	 */
	public final synchronized void addSession(SrvSession sess) {

		// Add the session to the queue

		m_queue.add( sess);

		// Notify a listener that there is a session to process

		notify();
	}

	/**
	 * Remove a session from the head of the queue
	 * 
	 * @return SrvSession
	 * @exception InterruptedException
	 */
	public final synchronized SrvSession removeSession()
		throws InterruptedException {

		// Wait until there is a session

		waitWhileEmpty();

		// Get the session from the head of the queue

		return m_queue.removeFirst();
	}

	/**
	 * Remove a session from the queue, without waiting if there are no sessions in the queue
	 * 
	 * @return SrvSession
	 */
	public final synchronized SrvSession removeSessionNoWait() {
		
		SrvSession sess = null;
		
		if ( m_queue.size() > 0)
			sess = m_queue.removeFirst();
			
		return sess;
	}
	
	/**
	 * Wait for a session to be added to the queue
	 * 
	 * @exception InterruptedException
	 */
	public final synchronized void waitWhileEmpty()
		throws InterruptedException {

		// Wait until a session arrives on the queue

		while (m_queue.size() == 0)
			wait();
	}

	/**
	 * Wait for the session queue to be emptied
	 * 
	 * @exception InterruptedException
	 */
	public final synchronized void waitUntilEmpty()
		throws InterruptedException {

		// Wait until the session queue is empty

		while (m_queue.size() != 0)
			wait();
	}
}
