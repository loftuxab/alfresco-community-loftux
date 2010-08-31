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

import org.alfresco.wcm.client.util.CmisSessionPool;
import org.alfresco.wcm.client.util.UserSessionPool;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.pool.ObjectPool;

/**
 * Facade for the two types of CMIS collection pool
 * @author Chris Lack
 */
public class CmisSessionPoolImpl implements CmisSessionPool 
{
	private ObjectPool guestSessionPool;
	private UserSessionPool userSessionPool;

	public CmisSessionPoolImpl(ObjectPool guestSessionPool,
			                   UserSessionPool userSessionPool)
	{
		this.guestSessionPool = guestSessionPool;
		this.userSessionPool = userSessionPool;
	}
	
	/**
	 * @see org.alfresco.wcm.client.util.CmisSessionPool#closeSession(Session)
	 */	
	@Override
	public synchronized void closeSession(Session session) throws Exception
	{
		if (userSessionPool != null && userSessionPool.isUserSession(session)) {
			userSessionPool.closeSession(session);
		}
		else {
			guestSessionPool.returnObject(session);
		}
	}

	/**
	 * @see org.alfresco.wcm.client.util.CmisSessionPool#getGuestSession()
	 */	
	@Override
	public synchronized Session getGuestSession() throws Exception
	{
		return (Session)guestSessionPool.borrowObject();		
	}

	/**
	 * @see org.alfresco.wcm.client.util.CmisSessionPool#getSession(String, String)
	 */		
	@Override
	public synchronized Session getSession(String username, String password)
	{
		return userSessionPool.getSession(username, password);		
	}
	
	
}
