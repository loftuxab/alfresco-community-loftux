/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.users;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Cloud-specific person services (note: currently wrap rather than extend PersonService)
 */
public interface CloudPersonService
{
    public static enum TYPE
    {
    	INTERNAL, EXTERNAL, ALL;
    };
    
	/**
	 * Lists users in the current tenant/domain, optionally filtered by name, internal, networkAdmin
	 * and optionally sorted.
	 * 
	 * @param nameFilter
	 * @param sortBy
	 * @param skipCount
	 * @param maxItems
	 * @param internal
	 * @param networkAdmin
	 * 
	 * @return list of users
	 */
	public PagingResults<NodeRef> getPeople(final String nameFilter, final String sortBy, final int skipCount,
			final int maxItems, final TYPE type, final Boolean networkAdmin);
	
	public void removeExternalUser(final Account account, final String email);
	
    public void removePersonProfile(final String email, final String tenantDomain);
    
    public NodeRef getPerson(final String userName, final boolean autoCreateHomeFolderAndMissingPersonIfAllowed);
    
    public NodeRef getPersonOrNull(String userName);
    
    public NodeRef createPerson(Map<QName, Serializable> properties);
	
    public boolean isFullProfileVisible(String userName);
}
