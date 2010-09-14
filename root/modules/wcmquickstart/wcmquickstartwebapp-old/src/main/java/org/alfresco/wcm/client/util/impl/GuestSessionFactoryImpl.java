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

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.alfresco.wcm.client.exception.RepositoryUnavailableException;

/**
 * GuestSessionFactoryImpl implements a PoolableObjectFactory for use with
 * an apache commons GenericObjectPool. The class creates and destroys 
 * CMIS sessions. It uses a thread which periodically tries to reach the
 * repository. This allows for the repository not being available at 
 * application start-up without re-trying on every request.
 * @author Chris Lack
 */
public class GuestSessionFactoryImpl implements PoolableObjectFactory, Runnable 
{	
	private final static Log log = LogFactory.getLog(GuestSessionFactoryImpl.class);
	private int repositoryPollInterval;	
	private Repository repository;
	private SessionFactory sessionFactory;
	private Map<String,String> parameters;
	private volatile Thread waitForRepository;
	private Exception lastException;

	/**
	 * Create a CMIS session factory.
	 * @param repo CMIS repository URL
	 * @param user CMIS user
	 * @param password CMIS password
	 */
	public GuestSessionFactoryImpl(String repo, String user, String password)
	{
		this(repo, user, password, -1);
	}
	
	/**
	 * Create a CMIS session factory.
	 * @param repo CMIS repository URL
	 * @param user CMIS user
	 * @param password CMIS password
	 * @param repositoryPollInterval Optional. If > 0 a thread polls for the repository, otherwise the constructor
	 *                               will just issue an exception if the repository is not available. 
	 */
	public GuestSessionFactoryImpl(String repo, String user, String password, int repositoryPollInterval)
	{
		this.repositoryPollInterval = repositoryPollInterval;
		
		// Create session factory
		this.sessionFactory = SessionFactoryImpl.newInstance();
		this.parameters = new HashMap<String, String>();
		
		// user credentials
		parameters.put(SessionParameter.USER, user);
		parameters.put(SessionParameter.PASSWORD, password);

		// connection settings
		parameters.put(SessionParameter.ATOMPUB_URL, repo);
		parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		if (repositoryPollInterval > 0)
		{
			// Start thread which gets repository object
			this.waitForRepository = new Thread(this);
			waitForRepository.start();
		}
		else
		{
			// If no poll interval then just check in the current thread and throw exception if not available
			getRepository();
		}
	}

	private void getRepository()
	{
		List<Repository> repositories = sessionFactory.getRepositories(parameters);
		this.repository = repositories.get(0);		
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
	    while (waitForRepository == thisThread) {
            // See if the repository can be reached
	    	try {
	    		getRepository();
				log.info("Repository available");
				break;
	    	}
	    	catch (CmisConnectionException e) {
	    		lastException = e;
				log.error("Repository not available: "+e.getMessage());
	    	}
			
	    	// Wait a bit
            try {
                Thread.sleep(repositoryPollInterval);
            } 
            catch (InterruptedException e) {}	      
	    }
	    waitForRepository = null;
	}	
	
	public void stop()
	{
		waitForRepository = null;
	}
	
	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(Object)
	 */
	@Override
	public void activateObject(Object obj) throws Exception
	{
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(Object)
	 */
	@Override
	public void destroyObject(Object obj) throws Exception	
	{
		if (obj == null || ! (obj instanceof Session)) throw new IllegalArgumentException("Session instance expected");
		Session session = (Session)obj;
		session.cancel();
		session.clear();
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
	 */
	@Override
	public Object makeObject() throws Exception
	{
		if (repository == null) throw new RepositoryUnavailableException(lastException);
		return repository.createSession();
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(Object)
	 */	
	@Override
	public void passivateObject(Object obj) throws Exception
	{
		if (obj == null || ! (obj instanceof Session)) throw new IllegalArgumentException("Session instance expected");
		Session session = (Session)obj;
		session.cancel();
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(Object)
	 */
	@Override
	public boolean validateObject(Object obj)
	{
		return true;
	}

}
