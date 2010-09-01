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
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * GuestSessionFactoryImpl implements a PoolableObjectFactory for use with
 * an apache commons GenericObjectPool. The class creates and destroys 
 * CMIS sessions.
 * @author Chris Lack
 */
public class GuestSessionFactoryImpl implements PoolableObjectFactory 
{	
	private Repository repository;
	
	public GuestSessionFactoryImpl(String repo, String user, String password)
	{
		// Create session factory
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameters = new HashMap<String, String>();
		
		// user credentials
		parameters.put(SessionParameter.USER, user);
		parameters.put(SessionParameter.PASSWORD, password);

		// connection settings
		parameters.put(SessionParameter.ATOMPUB_URL, repo);
		parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		List<Repository> repositories = sessionFactory.getRepositories(parameters);
		this.repository = repositories.get(0);		
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
