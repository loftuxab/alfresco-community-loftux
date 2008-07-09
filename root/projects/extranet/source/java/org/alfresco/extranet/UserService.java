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

import org.alfresco.extranet.database.DatabaseService;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.extranet.ldap.LDAPService;
import org.alfresco.extranet.mail.MailService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Class ExtranetService.
 * 
 * @author muzquiano
 */
public class UserService implements ApplicationContextAware, EntityService
{
    protected ApplicationContext applicationContext;
    protected DatabaseService databaseService;
    protected LDAPService ldapService;
    protected MailService mailService;
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
     * Instantiates a new user service impl.
     */
    public UserService()
    {
    }
    
    /**
     * Gets the user.
     * 
     * @param userId the user id
     * 
     * @return the user
     */
    public DatabaseUser getUser(String userId)
    {
        return this.databaseService.getUser(userId);
    }
    
    /**
     * Gets the user by email.
     * 
     * @param email the email
     * 
     * @return the user by email
     */
    public DatabaseUser getUserByEmail(String email)
    {
        return this.databaseService.getUserByEmail(email);
    } 

    
    ///////////////////////////////////////////////////////////////////
    // CRUD Operations
    ///////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#get(java.lang.String)
     */
    public Entity get(String entityId)
    {
        return this.databaseService.getUser(entityId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#list()
     */
    public List<Entity> list()
    {
        return this.databaseService.getAllUsers();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#insert(org.alfresco.extranet.Entity)
     */
    public Entity insert(Entity entity)
    {
        return this.databaseService.insertUser((DatabaseUser)entity);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#update(org.alfresco.extranet.Entity)
     */
    public boolean update(Entity entity)
    {
        return this.databaseService.updateUser((DatabaseUser)entity);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.EntityService#remove(org.alfresco.extranet.Entity)
     */
    public boolean remove(Entity entity)
    {
        return this.databaseService.removeUser((DatabaseUser)entity);
    }
    
}
