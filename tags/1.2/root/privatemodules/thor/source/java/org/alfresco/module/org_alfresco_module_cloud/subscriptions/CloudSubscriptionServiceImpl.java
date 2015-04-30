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
package org.alfresco.module.org_alfresco_module_cloud.subscriptions;

import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.subscriptions.SubscriptionServiceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.subscriptions.PrivateSubscriptionListException;

/**
 * Extend/override Subscription Service (within Subscriptions subsystem - see custom-subscriptions-service-context.xml)
 * 
 * @author janv
 * @since Alfresco Cloud Module (Thor)
 */
public class CloudSubscriptionServiceImpl extends SubscriptionServiceImpl
{
    private DirectoryService directoryService;
    
    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    @Override
    protected void checkRead(String userId, boolean checkPrivate)
    {
        if (AuthenticationUtil.isRunAsUserTheSystemUser() || isSameHomeTenant(AuthenticationUtil.getRunAsUser(), userId, true))
        {
            super.checkRead(userId, checkPrivate);
        }
        else
        {
            throw new PrivateSubscriptionListException("subscription_service.err.private-list");
        }
    }
    
    @Override
    public void subscribe(String userId, NodeRef node)
    {
        throw new UnsupportedOperationException("CloudSubscriptionServiceImpl.subscribe");
    }
    
    @Override
    public void follow(String userId, String userToFollow)
    {
        if ((userId != null) && (userId.equals(userToFollow)))
        {
            return;
        }
        
        if (isSameHomeTenant(userId, userToFollow, false))
        {
            super.follow(userId, userToFollow);
        }
        else
        {
            throw new PrivateSubscriptionListException("subscription_service.err.write-denied");
        }
    }
    
    // is same home tenant
    private boolean isSameHomeTenant(String userName1, String userName2, boolean allowSuperAdmin)
    {
        if ((userName1 != null) && (userName1.equals(userName2)))
        {
            return true;
        }
        
        // special case for super admin (should we remove completely ?) - eg. allow read (but not follow)
        if (allowSuperAdmin)
        {
            String superAdmin = AuthenticationUtil.getAdminUserName();
            if ((superAdmin.equals(userName1)) || (superAdmin.equals(userName2)))
            {
                return true;
            }
        }
        
        Long account1 = directoryService.getHomeAccount(userName1);
        if (account1 == null)
        {
            // eg. public user
            return false;
        }
        return account1.equals(directoryService.getHomeAccount(userName2));
    }
    
    @Override
    public int getFollowersCount(String userId)
    {
        if (isSameHomeTenant(AuthenticationUtil.getRunAsUser(), userId, true))
        {
            return super.getFollowersCount(userId);
        }
        // return 0 rather than error for now - to allow for UI (Thor extension)
        return 0;
    }
    
    @Override
    public int getFollowingCount(String userId)
    {
        if (isSameHomeTenant(AuthenticationUtil.getRunAsUser(), userId, true))
        {
            return super.getFollowingCount(userId);
        }
        // return 0 rather than error for now - to allow for UI (Thor extension)
        return 0;
    }
}
