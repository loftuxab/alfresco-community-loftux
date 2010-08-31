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
package org.alfresco.wcm.client.util;

import org.apache.chemistry.opencmis.client.api.Session;

/**
 * A "pool" for individual sessions authenticated with the repository.
 * @author Chris Lack
 */
public interface UserSessionPool
{	
	/**
	 * Get the session for a registered user
	 * @param user the repository user
	 * @param password the repository password
	 * @return Session a CMIS session
	 */
	Session getSession(String user, String password);
	
	/**
	 * Close the session pool and discard resources
	 */
	void close();

	/**
	 * Get the number of active session
	 * @return int number of sessions
	 */
	int getNumActive();
	
	/**
	 * Close the given session
	 * @param session session to close
	 */
	void closeSession(Session session);

	/**
	 * Returns true if the session is managed by the UserSessionPool implementation
	 * @param session the session to check
	 * @return boolean true if authenticated user session, false if an anonymous session
	 */
	boolean isUserSession(Session session);
}
