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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.alfresco.connector.Connector;
import org.alfresco.connector.Response;
import org.alfresco.extranet.database.DatabaseInvitedUser;
import org.alfresco.extranet.database.DatabaseService;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.extranet.ldap.LDAPService;
import org.alfresco.extranet.ldap.LDAPUser;
import org.alfresco.extranet.mail.MailService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskUser;
import org.alfresco.tools.ObjectGUID;
import org.alfresco.web.site.FrameworkHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Class InvitationServiceImpl.
 * 
 * @author muzquiano
 */
public class InvitationService 
    implements ApplicationContextAware, EntityService
{
    protected ApplicationContext applicationContext;
    protected DatabaseService databaseService;
    protected LDAPService ldapService;
    protected MailService mailService;
    protected WebHelpdeskService webHelpdeskService;
    protected String alfrescoHostPort;
    
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
     * Sets the alfresco host port.
     * 
     * @param alfrescoHostPort the new alfresco host port
     */
    public void setAlfrescoHostPort(String alfrescoHostPort)
    {
        this.alfrescoHostPort = alfrescoHostPort;
    }
    
    /**
     * Instantiates a new invitation service impl.
     */
    public InvitationService()
    {
    }
    
    /**
     * Gets the invited user from hash.
     * 
     * @param hash the hash
     * 
     * @return the invited user from hash
     */
    public DatabaseInvitedUser getInvitedUserFromHash(String hash)
    {
        return this.databaseService.getInvitedUserFromHash(hash);
    }
    
    /**
     * Process invited user.
     * 
     * @param userId the user id
     * 
     * @throws Exception the exception
     */
    public void processInvitedUser(String invitedUserId, String userId, String password)
        throws Exception
    {
        DatabaseInvitedUser invitedUser = this.databaseService.getInvitedUser(invitedUserId);
        if(invitedUser != null)
        {
            // check to make sure they haven't already been processed already
            if(invitedUser.isCompleted())
            {
                throw new Exception("User invitation was already completed");
            }
            
            // create the db user
            DatabaseUser dbUser = this.databaseService.startProcessInvitedUser(invitedUser, userId);
            
            // map the db user into an lpda user
            LDAPUser ldapUser = mapToLDAPUser(dbUser, password);
            
            // create the ldap user 
            System.out.println("Creating LDAP User: " + ldapUser.getEntityId());
            ldapUser = this.ldapService.createUser(ldapUser);
            if(ldapUser != null)
            {
                // sync the ldap user into alfresco
                // this calls over to a web script to pull in the ldap user
                Connector connector = FrameworkHelper.getConnector("alfresco-system");                
                String uri = "/network/ldapsync?command=user&id=" + ldapUser.getEntityId();
                Response r = connector.call(uri);
                if(r.getStatus().getCode() != 200)
                {
                    System.out.println("Sync command for user: " + dbUser.getEntityId() + " failed with code: " + r.getStatus().getCode());
                    if(r.getStatus().getException() != null)
                    {
                        r.getStatus().getException().printStackTrace();
                    }
                }
            }

////////////////////////////////////////////////////////////////////////
// Set up groups
////////////////////////////////////////////////////////////////////////
            
            // add all users to the community group
            this.databaseService.addUserToGroup(userId, "community");
            
            // if we're processing an enterprise invitation
            if("enterprise".equals(invitedUser.getInvitationType()))
            {
                // add the user to the "enterprise" group
                this.databaseService.addUserToGroup(userId, "enterprise");
                
                // add the user to the "customers" group
                this.databaseService.addUserToGroup(userId, "customers");
                
                // add the user to the "registered" group
                this.databaseService.addUserToGroup(userId, "registered");                
            }
            

////////////////////////////////////////////////////////////////////////
// Synchronize Partners
////////////////////////////////////////////////////////////////////////
            if(invitedUser.getAlfrescoUserId() != null && invitedUser.getAlfrescoUserId().trim().length() > 0)
            {
                String alfrescoUserId = invitedUser.getAlfrescoUserId();
                
                // TODO
                // Nothing at this time
            }

            
////////////////////////////////////////////////////////////////////////
// Synchronize ACT
////////////////////////////////////////////////////////////////////////
            if(invitedUser.getWebHelpdeskUserId() != null && invitedUser.getWebHelpdeskUserId().trim().length() > 0)
            {
                String whdUserId = invitedUser.getWebHelpdeskUserId();
                
                // migrate the old ACT user id to the new ACT user id
                // this will also set the LDAP flag
                boolean migrate = this.webHelpdeskService.migrateUser(whdUserId, dbUser.getUserId());
                if(migrate)
                {
                    // migration completed
                    // load the user
                    WebHelpdeskUser whdUser = this.webHelpdeskService.getUser(dbUser.getUserId());
                    if(whdUser != null)
                    {
                        // map properties in
                        mapToWebHelpdeskUser(whdUser, dbUser);
                        
                        // save the user
                        boolean update = this.webHelpdeskService.updateUser(whdUser);
                        if(!update)
                        {
                            System.out.println("Update of user: " + whdUser + " failed");
                        }
                        
                        // TODO: This ought to sync from LDAP
                        // WHD doesn't do real time auth?
                        // This is a temporary way to alleviate the issue
                        // Write the password directly into WHD tables
                        this.webHelpdeskService.updatePassword(whdUser, ldapUser.getPassword());
                    }
                    else
                    {
                        System.out.println("Unable to find migrated whd user: " + dbUser.getUserId());
                    }
                }
                else
                {
                    System.out.println("Migration of whd user: " + whdUserId + " to new id: " + dbUser.getUserId() + " failed"); 
                }
                
                // TODO: query for the WHD user with this id
                // and update the WHD user properties
            }
            

            
            
            
            
            
            
            
            // finally, mark the user as having been invited            
            this.databaseService.endProcessInvitedUser(invitedUser);            
        }
    }
    
    /**
     * Map a db user to an ldap user.
     * 
     * @param dbUser the db user
     * 
     * @return the lDAP user
     */
    public LDAPUser mapToLDAPUser(DatabaseUser dbUser, String password)
    {
        // create the ldap user
        LDAPUser user = new LDAPUser(dbUser.getUserId());
        
        // copy in properties
        user.setDescription(dbUser.getDescription());
        user.setEmail(dbUser.getEmail());
        user.setFirstName(dbUser.getFirstName());
        user.setMiddleName(dbUser.getMiddleName());
        user.setLastName(dbUser.getLastName());
        
        // set password
        user.setPassword(password);
        
        return user;        
    }
    
    public WebHelpdeskUser mapToWebHelpdeskUser(WebHelpdeskUser user, DatabaseUser dbUser)
    {
        if(user == null)
        {
            user = new WebHelpdeskUser(dbUser.getUserId());
        }
        
        // copy in properties
        user.setDescription(dbUser.getDescription());
        user.setEmail(dbUser.getEmail());
        user.setFirstName(dbUser.getFirstName());
        user.setMiddleName(dbUser.getMiddleName());
        user.setLastName(dbUser.getLastName());
                
        return user;
    }

    /**
     * Invite user.
     * 
     * @param userId the user id
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email
     * @param whdUserId the whd user id
     * @param alfrescoUserId the alfresco user id
     * @param invitationType the type of the invitation
     * 
     * @return the database invited user
     */
    public DatabaseInvitedUser inviteUser(String userId, String firstName, String lastName, String email, String whdUserId, String alfrescoUserId, String invitationType, Date subscriptionStartDate, Date subscriptionEndDate)
    {
        return inviteUser(userId, firstName, null, lastName, email, whdUserId, alfrescoUserId, DEFAULT_INVITATION_LENGTH, invitationType, subscriptionStartDate, subscriptionEndDate);        
    }
    
    /**
     * Invite user.
     * 
     * @param userId the user id
     * @param firstName the first name
     * @param middleName the middle name
     * @param lastName the last name
     * @param email the email
     * @param whdUserId the whd user id
     * @param alfrescoUserId the alfresco user id
     * @param lengthOfInvitation the length of invitation
     * @param invitationType the type of the invitation
     * @param subscriptionStartDate date when subscription starts
     * @param subscriptionEndDate date when subscription ends
     * 
     * @return the database invited user
     */
    public DatabaseInvitedUser inviteUser(String userId, String firstName, String middleName, String lastName, String email, String whdUserId, String alfrescoUserId, int lengthOfInvitation, String invitationType, Date subscriptionStartDate, Date subscriptionEndDate)
    {
        // clean up data
        userId = userId.trim();
        
        DatabaseInvitedUser user = new DatabaseInvitedUser(userId);
        
        if(firstName != null)
        {
            firstName = firstName.trim();
            user.setFirstName(firstName);
        }
        
        if(middleName != null)
        {
            middleName = middleName.trim();
            user.setMiddleName(middleName);
        }
        
        if(lastName != null)
        {
            lastName = lastName.trim();
            user.setLastName(lastName);
        }
        
        if(email != null)
        {
            email = email.trim();
            user.setEmail(email);
        }
        
        if(whdUserId != null)
        {
            whdUserId = whdUserId.trim();
            user.setWebHelpdeskUserId(whdUserId);
        }
        
        if(alfrescoUserId != null)
        {
            alfrescoUserId = alfrescoUserId.trim();
            user.setAlfrescoUserId(alfrescoUserId);
        }
        
        if(invitationType != null)
        {
            invitationType = invitationType.trim();
            user.setInvitationType(invitationType);
        }
        
        if(subscriptionStartDate != null)
        {
            user.setSubscriptionStart(subscriptionStartDate);            
        }
        
        if(subscriptionEndDate != null)
        {
            user.setSubscriptionEnd(subscriptionEndDate);
        }
        
        // generate a hash
        String hash = new ObjectGUID().toString();
        user.setHash(hash);
        
        // generate an invitation expiration date
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.roll(Calendar.DAY_OF_YEAR, lengthOfInvitation);        
        Date expirationDate = c.getTime();
        user.setExpirationDate(expirationDate);
        
        // create the invitation record
        DatabaseInvitedUser invitedUser = this.databaseService.inviteUser(user);
        
        // send out an email
        this.mailService.inviteUser(invitedUser);

        // return the invited user
        return invitedUser;
    }
        

    /**
     * Gets the invited user by email.
     * 
     * @param email the email
     * 
     * @return the invited user by email
     */
    public DatabaseInvitedUser getInvitedUserByEmail(String email)
    {
        return this.databaseService.getInvitedUserByEmail(email);
    }
    
    /**
     * Gets the invited user.
     * 
     * @param userId the user id
     * 
     * @return the invited user
     */
    public DatabaseInvitedUser getInvitedUser(String userId)
    {
        return this.databaseService.getInvitedUser(userId);
    }

    
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
