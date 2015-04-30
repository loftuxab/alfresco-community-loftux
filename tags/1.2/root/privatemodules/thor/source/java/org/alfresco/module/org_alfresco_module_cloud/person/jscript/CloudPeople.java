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
package org.alfresco.module.org_alfresco_module_cloud.person.jscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.site.CloudSiteService;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.jscript.People;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.PermissionEvaluationMode;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.util.ScriptPagingDetails;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Override scripted People Service for cloud use-case (re: people privacy).
 *
 * TODO public users when merged (will directoryService.getHomeAccount return null or not ?)
 * 
 * @author janv
 * @since Thor
 */
public class CloudPeople extends People
{
    protected AccountService accountService;
    protected CloudSiteService cloudSiteService;
    protected DirectoryService directoryService;
	protected CloudPersonService cloudPersonService;
    
    public void setCloudPersonService(CloudPersonService cloudPersonService)
    {
		this.cloudPersonService = cloudPersonService;
	}

	public void setCloudSiteService(CloudSiteService cloudSiteService)
    {
        this.cloudSiteService = cloudSiteService;
    }
    
    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }
    
    /**
     * @param accountService the accountService to set
     */
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }

    @Override
    public Scriptable getPeoplePaging(String filter, ScriptPagingDetails pagingRequest, String sortBy, Boolean sortAsc)
    {
        // TODO post filtering here hence not accurate paging (for now get twice as many items if skipCount = 0, similar to search.lib.js)
        int maxItems = pagingRequest.getMaxItems();
        int skipCount = pagingRequest.getSkipCount();
        if (skipCount == 0)
        {
            Double newMaxItems = ((double)maxItems)*2;
            if (newMaxItems <= Integer.MAX_VALUE)
            {
                pagingRequest.setMaxItems(newMaxItems.intValue());
            }
        }
        
        if (sortBy == null)
        {
            sortBy = "firstName"; // sorts by fn -> ln -> username
        }
        
        List<PersonInfo> personInfos = getPeopleImpl(filter, pagingRequest, sortBy, sortAsc);
        
        // need to filter (maintaining sort order)
        
        Map<String, NodeRef> personMap = new HashMap<String, NodeRef>(personInfos.size());
        List<String> unfilteredUserIds = new ArrayList<String>(personInfos.size());
        
        for (PersonInfo personInfo : personInfos)
        {
            personMap.put(personInfo.getUserName(), personInfo.getNodeRef());
            unfilteredUserIds.add(personInfo.getUserName());
        }
        
        List<String> filteredUserIds = cloudSiteService.filterVisibleUsers(unfilteredUserIds, maxItems);
        
        List<NodeRef> results = new ArrayList<NodeRef>(personInfos.size());
        
        for (String filteredUserId : filteredUserIds)
        {
            results.add(personMap.get(filteredUserId));
        }

        return Context.getCurrentContext().newArray(getScope(), results.toArray());
    }
    
    // true if user is in same home tenant as the current (runAs) user
    // note: if either user is a public user then false (since public user has a null home tenant)
    public boolean isFullProfileVisible(String userName)
    {
    	return cloudPersonService.isFullProfileVisible(userName);
    }

//    public boolean isFullProfileVisible(String userName)
//    {
//        return isSameHomeTenantAsRunAsUser(directoryService, userName);
//    }
//    
//    public static boolean isSameHomeTenantAsRunAsUser(DirectoryService directoryService, String userName)
//    {
//        return isSameHomeTenant(directoryService, AuthenticationUtil.getRunAsUser(), userName);
//    }
//    
//    private static boolean isSameHomeTenant(DirectoryService directoryService, String userName1, String userName2)
//    {
//        if ((userName1 != null) && (userName1.equals(userName2)))
//        {
//            return true;
//        }
//        
//        String superAdmin = AuthenticationUtil.getAdminUserName();
//        if ((superAdmin.equals(userName1)) || (superAdmin.equals(userName2)))
//        {
//            // special case: super admin
//            return true;
//        }
//        
//        Long account1 = directoryService.getHomeAccount(userName1);;
//        if (account1 == null)
//        {
//            // eg. public user
//            return false;
//        }
//        return account1.equals(directoryService.getHomeAccount(userName2));
//    }
    
    // note: called by /api/people/<username> (people.get.js) and /webframework/metadata?user= (metadata.get.js)
    @Override
    public ScriptNode getPerson(String username)
    {
        ScriptNode person = super.getPerson(username);
        if (person != null)
        {
            List<String> unfilteredUserIds = new ArrayList<String>();
            unfilteredUserIds.add(username);
            
            List<String> filteredUserIds = cloudSiteService.filterVisibleUsers(unfilteredUserIds, 1);
            
            if (filteredUserIds.size() == 0)
            {
                // person is not visible
                person = null;
            }
        }
        return person;
    }
  
    @Override
    public boolean getExcludeTenantFilter()
    {
        return true;
    }
    
    @Override
    public PermissionEvaluationMode getPermissionEvaluationMode()
    {
        return PermissionEvaluationMode.NONE;
    }
}
