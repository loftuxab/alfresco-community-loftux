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
package org.alfresco.extranet;

import java.util.List;

import org.alfresco.extranet.database.DatabaseGroup;
import org.alfresco.extranet.database.DatabaseService;
import org.alfresco.extranet.ldap.LDAPService;
import org.alfresco.extranet.mail.MailService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Class GroupServiceImpl.
 * 
 * @author muzquiano
 */
public class GroupService implements ApplicationContextAware, EntityService
{
    protected ApplicationContext applicationContext;
    protected DatabaseService databaseService;
    protected LDAPService ldapService;
    protected MailService mailService;
    protected SyncService syncService;
    protected WebHelpdeskService webHelpdeskService;
    
    public static int DEFAULT_INVITATION_LENGTH = 7; // a week
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Sets the database service.
     * 
     * @param databaseService the new database service
     */
    public void setDatabaseService(DatabaseService databaseService)
    {
        this.databaseService = databaseService;
    }
    
    /**
     * Sets the ldap service.
     * 
     * @param ldapService the new ldap service
     */
    public void setLdapService(LDAPService ldapService)
    {
        this.ldapService = ldapService;
    }
    
    /**
     * Sets the web helpdesk service.
     * 
     * @param webHelpdeskService the new web helpdesk service
     */
    public void setWebHelpdeskService(WebHelpdeskService webHelpdeskService)
    {
        this.webHelpdeskService = webHelpdeskService;
    }
    
    /**
     * Sets the mail service.
     * 
     * @param mailService the new mail service
     */
    public void setMailService(MailService mailService)
    {
        this.mailService = mailService;
    }
    
    /**
     * Sets the sync service.
     * 
     * @param syncService the new sync service
     */
    public void setSyncService(SyncService syncService)
    {
        this.syncService = syncService;
    }    
    
    /**
     * Instantiates a new group service impl.
     */
    public GroupService()
    {
    }
    
    /**
     * Gets the group.
     * 
     * @param groupId the group id
     * 
     * @return the group
     */
    public DatabaseGroup getGroup(String groupId)
    {
        return (DatabaseGroup) get(groupId);
    }
           
    /**
     * Adds the user to group.
     * 
     * @param userId the user id
     * @param groupId the group id
     * 
     * @return true, if successful
     */
    public boolean addUserToGroup(String userId, String groupId)
    {
        return this.databaseService.addUserToGroup(userId, groupId);
    }
    
    /**
     * Removes the user from group.
     * 
     * @param userId the user id
     * @param groupId the group id
     * 
     * @return true, if successful
     */
    public boolean removeUserFromGroup(String userId, String groupId)
    {
        return this.databaseService.removeUserFromGroup(userId, groupId);
    }
    
    /**
     * Gets the groups for user.
     * 
     * @param userId the user id
     * 
     * @return the groups for user
     */
    public List getGroupsForUser(String userId)
    {
        return this.databaseService.getGroupsForUser(userId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#get(java.lang.String)
     */
    public Entity get(String entityId)
    {
        return this.databaseService.getGroup(entityId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#list()
     */
    public List<Entity> list()
    {
        return this.databaseService.getAllGroups();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#insert(org.alfresco.extranet.Entity)
     */
    public Entity insert(Entity entity)
    {
        return this.databaseService.insertGroup((DatabaseGroup)entity);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#update(org.alfresco.extranet.Entity)
     */
    public boolean update(Entity entity)
    {
        return this.databaseService.updateGroup((DatabaseGroup)entity);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#remove(org.alfresco.extranet.Entity)
     */
    public boolean remove(Entity entity)
    {
        return this.databaseService.removeGroup((DatabaseGroup)entity);
    }
}
