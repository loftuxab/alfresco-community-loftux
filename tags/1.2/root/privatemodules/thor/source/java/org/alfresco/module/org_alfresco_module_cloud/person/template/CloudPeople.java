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
package org.alfresco.module.org_alfresco_module_cloud.person.template;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.tenant.jscript.UserTenant;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.template.People;

/**
 * Override People Template Extension for cloud use-case (re: profile privacy).
 *
 * @author janv
 * @since Thor
 */
public class CloudPeople extends People
{
	private CloudPersonService cloudPersonService;
    private DirectoryService directoryService;
    private AccountService accountService;
    
    public void setCloudPersonService(CloudPersonService cloudPersonService)
    {
		this.cloudPersonService = cloudPersonService;
	}

	public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    // true if user is in same home tenant as the current (runAs) user
    // note: if either user is a public user then false (since public user has a null home tenant)
    public boolean isFullProfileVisible(String userName)
    {
        return cloudPersonService.isFullProfileVisible(userName);
    }
    
    // note: can be null (eg. public user has no home tenant)
    public String getHomeTenant(String userName)
    {
        return UserTenant.getHomeTenant(directoryService, accountService, userName);
    }
}
