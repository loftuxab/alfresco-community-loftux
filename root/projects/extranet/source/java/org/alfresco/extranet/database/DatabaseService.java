/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.extranet.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Class DatabaseService.
 * 
 * @author muzquiano
 */
public class DatabaseService implements ApplicationContextAware
{
    protected ApplicationContext applicationContext;
    protected DatabaseUserBean userBean;
    protected DatabaseGroupBean groupBean;
    protected DatabaseCompanyBean companyBean;
    protected DatabaseInvitedUserBean invitedUserBean;
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Sets the user bean.
     * 
     * @param userBean the new user bean
     */
    public void setUserBean(DatabaseUserBean userBean)
    {
        this.userBean = userBean;
    }

    public void setGroupBean(DatabaseGroupBean groupBean)
    {
        this.groupBean = groupBean;
    }

    public void setCompanyBean(DatabaseCompanyBean companyBean)
    {
        this.companyBean = companyBean;
    }
    
    public void setInvitedUserBean(DatabaseInvitedUserBean invitedUserBean)
    {
        this.invitedUserBean = invitedUserBean;
    }
    
    public DatabaseService()
    {
    }

    
    ///////////////////////////////////////////////////////////////////////
    // Invited User Functions
    ///////////////////////////////////////////////////////////////////////
    
    public Map<String, DatabaseInvitedUser> getInvitedUsers()
    {
        // return map
        Map<String, DatabaseInvitedUser> map = new HashMap<String, DatabaseInvitedUser>(1024, 1.0f);
        
        // get all of the migrating users
        List users = invitedUserBean.list();
        for(int i = 0; i < users.size(); i++)
        {
            DatabaseInvitedUser user = (DatabaseInvitedUser) users.get(i);
            if(!user.isCompleted())
            {
                map.put(user.getUserId(), user);
            }
        }
        
        return map;        
    }
        
    public DatabaseInvitedUser getInvitedUser(String userId)
    {
        DatabaseInvitedUser user = null;
        if(userId != null)
        {
            userId = userId.trim();
            user = invitedUserBean.get(userId);
        }
        return user;
    }
    
    public DatabaseInvitedUser getInvitedUserFromHash(String hash)
    {
        DatabaseInvitedUser user = null;
        if(hash != null)
        {
            hash = hash.trim();
            user = invitedUserBean.getFromHash(hash);
        }
        return user;
    } 

    public synchronized void endProcessInvitedUser(DatabaseInvitedUser invitedUser)
    {
        invitedUser.setCompleted(true);
        invitedUserBean.update(invitedUser);
    }
    
    public synchronized DatabaseInvitedUser inviteUser(DatabaseInvitedUser invitedUser)
    {
        return invitedUserBean.insert(invitedUser);
    }
    
    public synchronized DatabaseInvitedUser getInvitedUserByEmail(String email)
    {
        DatabaseInvitedUser user = null;
        if(email != null)
        {
            email = email.trim();
            user = invitedUserBean.getByEmail(email);
        }
        return user;
    }    
    
    public boolean removeInvitedUser(DatabaseInvitedUser invitedUser)
    {
        return this.invitedUserBean.delete(invitedUser);
    }
    
    public synchronized List getAllInvitedUsers()
    {
        return this.invitedUserBean.list();
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    // User Functions
    ///////////////////////////////////////////////////////////////////////
    
    public synchronized List getAllUsers()
    {
        return this.userBean.list();
    }
    
    public synchronized DatabaseUser getUser(String userId)
    {
        DatabaseUser user = null;
        if(userId != null)
        {
            userId = userId.trim();
            user = userBean.get(userId);
        }
        return user;
    }
    
    public synchronized DatabaseUser getUserByEmail(String email)
    {
        DatabaseUser user = null;
        if(email != null)
        {
            email = email.trim();
            user = userBean.getByEmail(email);
        }
        return user;
    }
    
    public boolean updateUser(DatabaseUser user)
    {
        return this.userBean.update(user);        
    }
    
    public DatabaseUser insertUser(DatabaseUser user)
    {
        return this.userBean.insert(user);
    }
    
    public boolean removeUser(DatabaseUser user)
    {
        return this.userBean.delete(user);
    }
    
    
    
    
    
    ///////////////////////////////////////////////////////////////////////
    // Group Functions
    ///////////////////////////////////////////////////////////////////////
    
    
    public synchronized boolean addUserToGroup(String userId, String groupId)
    {
        return this.groupBean.addUserToGroup(groupId, userId);
    }
    
    public synchronized boolean removeUserFromGroup(String userId, String groupId)
    {
        return this.groupBean.removeUserFromGroup(groupId, userId);
    }

    public DatabaseGroup getGroup(String groupId)
    {
        return this.groupBean.get(groupId);        
    }
    
    public synchronized List getAllGroups()
    {
        return this.groupBean.list();        
    }
        
    public synchronized List getGroupsForUser(String userId)
    {
        List groupIds = this.groupBean.getGroupIdsForUser(userId);
        
        List groupList = new ArrayList();
        for(int i = 0; i < groupIds.size(); i++)
        {
            String groupId = (String) groupIds.get(i);
            
            DatabaseGroup group = (DatabaseGroup) this.groupBean.get(groupId);
            if(group != null)
            {
                groupList.add(group);
            }
        }
        
        return groupList;
    }
        
    public boolean updateGroup(DatabaseGroup group)
    {
        return this.groupBean.update(group);        
    }
    
    public DatabaseGroup insertGroup(DatabaseGroup group)
    {
        return this.groupBean.insert(group);
    }
    
    public boolean removeGroup(DatabaseGroup group)
    {
        return this.groupBean.delete(group);
    }

    
    
    
    
    
    
    ///////////////////////////////////////////////////////////////////////
    // Company Functions
    ///////////////////////////////////////////////////////////////////////    
    
    public DatabaseCompany getCompany(String companyId)
    {
        return this.companyBean.get(companyId);       
    }
    
    public synchronized List getAllCompanies()
    {
        return this.companyBean.list();        
    }
                
    public boolean updateCompany(DatabaseCompany company)
    {
        return this.companyBean.update(company);        
    }
    
    public DatabaseCompany insertCompany(DatabaseCompany company)
    {
        return this.companyBean.insert(company);
    }
    
    public boolean removeCompany(DatabaseCompany company)
    {
        return this.companyBean.delete(company);
    }
    
    public static SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
}
