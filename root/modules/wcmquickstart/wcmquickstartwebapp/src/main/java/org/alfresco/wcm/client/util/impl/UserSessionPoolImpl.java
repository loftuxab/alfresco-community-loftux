/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.util.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.wcm.client.util.UserSessionPool;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

/**
 * A list for individual sessions authenticated with the repository.
 * @author Chris Lack
 */
public class UserSessionPoolImpl implements UserSessionPool, Runnable
{	
	private static final long IDLE_THREAD_INTERVAL = 60000; //ms
	
	private String repoUrl;
	private long timeout; //ms
	private Map<Session,SessionMetaData> sessionMap = new  HashMap<Session,SessionMetaData>();
	private Thread idleMonitor;
	
	public UserSessionPoolImpl(String repoUrl, int timeoutMins) // min
	{
		this.repoUrl = repoUrl;
		this.timeout = timeout * 60000; // convert to ms
		this.idleMonitor = new Thread(this);
		idleMonitor.start();
	}
	
	private class SessionMetaData
	{		
		private Session session;
		private long creationTime = System.currentTimeMillis();
		
		SessionMetaData(Session session) 
		{
			this.session = session;
		}
		
		public Session getSession() 
		{
			return session;
		}
		
		public long getCreationTime() 
		{
			return creationTime;
		}
	}
	
	/**
	 * @see org.alfresco.wcm.client.util.UserSessionPool#getSession(String, String)
	 */
	@Override
	public Session getSession(String user, String password)
	{
		// Create session factory
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameters = new HashMap<String, String>();
		
		// user credentials
		parameters.put(SessionParameter.USER, user);
		parameters.put(SessionParameter.PASSWORD, password);

		// connection settings
		parameters.put(SessionParameter.ATOMPUB_URL, repoUrl);
		parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		List<Repository> repositories = sessionFactory.getRepositories(parameters);
		Repository repository = repositories.get(0);
		Session session = repository.createSession();
		sessionMap.put(session, new SessionMetaData(session));
		return session;
	}

	/**
	 * @see org.alfresco.wcm.client.util.UserSessionPool#close()
	 */	
	@Override
	public void close()
	{
		idleMonitor = null;
		for (Session session : sessionMap.keySet())
		{
			closeSession(session);
		}
	}

	/**
	 * @see org.alfresco.wcm.client.util.UserSessionPool#getNumActive()
	 */
	@Override
	public int getNumActive()
	{
		return sessionMap.size();
	}

	/**
	 * @see org.alfresco.wcm.client.util.UserSessionPool#closeSession(Session)
	 */	
	@Override
	public void closeSession(Session session)
	{
		if (session == null) throw new IllegalArgumentException("Session instance expected");
		session.cancel();
		session.clear();
		sessionMap.remove(session);
		//TODO close also needed?!
	}

	/**
	 * @see Runnable#run()
	 */
	@Override
	public void run()
	{
		try 
		{
			while (idleMonitor != null) 
			{
				long timeoutThreshold = System.currentTimeMillis() - timeout;
				for (SessionMetaData sessionMeta : sessionMap.values())
				{
					if (sessionMeta.getCreationTime() < timeoutThreshold) 
					{
						closeSession(sessionMeta.getSession());
					}
				}
				Thread.sleep(IDLE_THREAD_INTERVAL);
			}
		}
		catch (InterruptedException e) {}
		idleMonitor = null;
	}

	/**
	 * @see org.alfresco.wcm.client.util.UserSessionPool#isUserSession(Session)
	 */
	@Override
	public boolean isUserSession(Session session)
	{
		return sessionMap.containsKey(session);
	}
}
