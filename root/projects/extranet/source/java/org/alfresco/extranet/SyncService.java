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

import org.alfresco.connector.Connector;
import org.alfresco.connector.Response;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.extranet.database.DatabaseService;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.extranet.ldap.LDAPService;
import org.alfresco.extranet.ldap.LDAPUser;
import org.alfresco.extranet.mail.MailService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskUser;
import org.alfresco.web.site.FrameworkHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Class SyncService
 * 
 * @author muzquiano
 */
public class SyncService 
    implements ApplicationContextAware
{
    protected static Log logger = LogFactory.getLog(SyncService.class);
    
    protected ApplicationContext applicationContext;
    protected DatabaseService databaseService;
    protected LDAPService ldapService;
    protected MailService mailService;
    protected WebHelpdeskService webHelpdeskService;

    protected static void debug(String value)
    {
        if(logger.isDebugEnabled())
            logger.debug(value);
        else
            System.out.println(value);
    }
    
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
     * Instantiates a new invitation service impl.
     */
    public SyncService()
    {
    }
    
    /**
     * Ensures that a user exists in web helpdesk for the given database user.
     * 
     * Begins by checking to see if a web helpdesk id exists for the given whd user id.
     * If a row exists, it is migrated to the new user id (database user id).
     * If no row exists, a new whd user object is created and persisted.
     * 
     * If whdUserId is null, the id will be assumed to be the same as the database user.
     * 
     * @param dbUser
     * @param whdUserId
     *  
     * @return whether the sync was successful
     */
    public boolean syncWebHelpdesk(DatabaseUser dbUser, String whdUserId, String password)
    {
        boolean success = false;
        
        debug("Synchronizing ACT, whdUserId = " + whdUserId + " and userId = " + dbUser.getUserId());
        
        // check whether a web helpdesk user already exists
        WebHelpdeskUser whdUser = this.webHelpdeskService.getUser(whdUserId);
        if(whdUser != null)
        {
            debug(" -> whd user already exists");
            debug(" -> migrating '" + whdUserId + "' to '" + dbUser.getUserId() + "'");
            
            // migrate the existing whd user id to our user's id
            boolean migrate = this.webHelpdeskService.migrateUser(whdUserId, dbUser.getUserId());
            if(migrate)
            {
                debug(" -> migration successful");
                
                whdUser = this.webHelpdeskService.getUser(dbUser.getUserId());
                
                debug(" -> whd user: " + whdUser);
            
                // map db user into whd user instance
                whdUser = ConversionUtil.toWebHelpdeskUser(dbUser, whdUser);
                
                // update the whd user instance
                this.webHelpdeskService.updateUser(whdUser);
                
                debug(" -> updated user");
                
                success = true;
            }
        }
        else
        {
            /*
            debug(" -> no existing whd user, creating new one");
            
            // create a new whd user id
            whdUser = ConversionUtil.toWebHelpdeskUser(dbUser, null);
            
            // insert into web helpdesk
            this.webHelpdeskService.insertUser(whdUser);
            
            debug(" -> insert was successful");
            
            success = true;
            */
            
            System.out.println("Skipping creation of web helpdesk user - not implemented");
        }
        
        if(success)
        {
            // push password into web helpdesk
            debug(" -> pushing password");
            this.webHelpdeskService.updatePassword(whdUser, password);
        }
        
        return success;
    }
    
    /**
     * Determines whether a given user id is available across
     * all of the systems that make up Alfresco Network
     * 
     * This method ignores Web Helpdesk
     * 
     * @param userId
     * 
     * @return whether the user id is available
     */
    public synchronized boolean isUserIdAvailable(String userId)
    {
        return isUserIdAvailable(userId, null);
    }
    
    /**
     * Returns whether a given user id is available within the
     * various infrastructure stores for Network
     * 
     * @param userId
     * @param whdUserId
     * @return
     */
    public synchronized boolean isUserIdAvailable(String userId, String whdUserId)
    {
        if(userId != null && userId.trim().length() == 0)
        {
            userId = null;
        }
        
        if(whdUserId != null && whdUserId.trim().length() == 0)
        {
            whdUserId = null;
        }
        
        // database user
        DatabaseUser dbUser = null;
        if(userId != null)
        {
            dbUser = this.databaseService.getUser(userId);
        }
        
        // ldap user
        LDAPUser ldapUser = null;
        if(userId != null)
        {
            ldapUser = this.ldapService.getUser(userId);
        }
        
        // whd user
        WebHelpdeskUser whdUser = null;
        if(whdUserId != null)
        {
            whdUser = this.webHelpdeskService.getUser(whdUserId);
        }
        
        debug("isUserIdAvailable.dbUser = " + dbUser);
        debug("isUserIdAvailable.ldapUser = " + ldapUser);
        debug("isUserIdAvailable.whdUser = " + whdUser);
        
        return ((dbUser == null) && (ldapUser == null) && (whdUser == null));
    }
    
    /**
     * Calls over to Alfresco and syncs a single user
     * 
     * @param ldapUser
     * @return
     * @throws RemoteConfigException
     */
    public synchronized boolean syncAlfresco(LDAPUser ldapUser)
        throws RemoteConfigException
    {
        boolean success = false;
        
        Connector connector = FrameworkHelper.getConnector("alfresco-system");                
        String uri = "/network/ldapsync?command=user&id=" + ldapUser.getEntityId();
        Response r = connector.call(uri);
        if(r.getStatus().getCode() == 200)
        {
            success = true;
        }
        if(r.getStatus().getCode() != 200)
        {
            System.out.println("Sync command for user: " + ldapUser.getEntityId() + " failed with code: " + r.getStatus().getCode());
            if(r.getStatus().getException() != null)
            {
                r.getStatus().getException().printStackTrace();
            }
        }
        
        return success;
    }
    
    /**
     * Calls Alfresco and ensures group membership is set for a single user
     * 
     * @param databaseUser
     * @return
     */
    public synchronized boolean syncAlfrescoGroupsForUser(String userId, String userType)
        throws RemoteConfigException
    {
        boolean success = false;
        
        Connector connector = FrameworkHelper.getConnector("alfresco-system");                
        String uri = "/network/login/sync?userId=" + userId + "&userType=" + userType;
        Response r = connector.call(uri);
        if(r.getStatus().getCode() == 200)
        {
            success = true;
        }
        if(r.getStatus().getCode() != 200)
        {
            System.out.println("Sync command for user: " + userId + " failed with code: " + r.getStatus().getCode());
            if(r.getStatus().getException() != null)
            {
                r.getStatus().getException().printStackTrace();
            }
        }
        
        return success;
        
    }
    
}
