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

import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.extranet.database.DatabaseService;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.extranet.ldap.LDAPService;
import org.alfresco.extranet.ldap.LDAPUser;
import org.alfresco.extranet.mail.MailService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskService;
import org.alfresco.tools.ObjectGUID;
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
    
    /**
     * Resets the user password for a user and then send an email
     * to notify them of the password reset
     * 
     * @param userId
     */
    public void resetUserPassword(String userId)
    {
        // generate a user password
        String newPassword = (new ObjectGUID()).toString();

        // get the LDAP user, update and save
        LDAPUser ldapUser = this.ldapService.getUser(userId);;
        ldapUser.setPassword(newPassword);
        this.ldapService.updateUser(ldapUser);
        
        // load the database user
        DatabaseUser dbUser = this.databaseService.getUser(userId);
        
        // send an email
        this.mailService.resetUserPassword(dbUser, newPassword);
    }
    
    /**
     * Bulk sets user properties onto an existing user object
     * 
     * @param userId
     * @param newUser
     * @return
     */
    public synchronized boolean setUserProperties(String userId, DatabaseUser newUser)
    {
        boolean success = false;
        
        // load the user
        DatabaseUser dbUser = this.databaseService.getUser(userId);
        if(dbUser != null)
        {
            newUser.copyPropertiesInto(dbUser);
            
            // don't allow the user id to get renamed
            // TODO: At some point in the future, change this
            dbUser.setUserId(userId);
            
            // update the user
            this.databaseService.updateUser(dbUser);
            
            // sync to ldap
            LDAPUser ldapUser = this.ldapService.getUser(userId);
            if(ldapUser != null)
            {
                // update ldap user
                LDAPUser mappedLdapUser = ConversionUtil.toLDAPUser(newUser, ldapUser.getPassword());
                mappedLdapUser.copyPropertiesInto(ldapUser);
                this.ldapService.updateUser(ldapUser);
                
                // sync to alfresco
                try
                {
                    this.syncService.syncAlfresco(ldapUser);
                }
                catch(RemoteConfigException rce)
                {
                    rce.printStackTrace();
                }
                
                // sync web helpdesk
                this.syncWebHelpdeskUser(userId, userId);
            }
            
            // flag success
            success = true;
        }
        
        return success;
    }
    
    public boolean syncWebHelpdeskUser(String userId, String whdUserId)
    {
        boolean success = false;
        
        DatabaseUser dbUser = this.databaseService.getUser(userId);
        if(dbUser != null)
        {
            LDAPUser ldapUser = this.ldapService.getUser(userId);
            if(ldapUser != null)
            {
                success = syncService.syncWebHelpdesk(dbUser, whdUserId, ldapUser.getPassword());
            }
        }
        
        return success;
    }
    
    public boolean changePassword(String userId, String originalPassword, String newPassword1, String newPassword2)
        throws Exception
    {
        boolean success = false;
        
        LDAPUser ldapUser = this.ldapService.getUser(userId);
        if(ldapUser != null)
        {
            String currentPassword = ldapUser.getPassword();
            if(currentPassword != null && currentPassword.equalsIgnoreCase(originalPassword))
            {
                if(newPassword1 != null && newPassword1.equalsIgnoreCase(newPassword2))
                {
                    ldapUser.setPassword(newPassword1);
                    success = this.ldapService.updateUser(ldapUser);                    
                }
                else
                {
                    throw new Exception("The new passwords did not match.");
                }
            }
            else
            {
                throw new Exception("The current password is incorrect.");
            }
        }
        else
        {
            throw new Exception("The user with ID '" + userId + "' was not found.");
        }
        
        return success;        
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
