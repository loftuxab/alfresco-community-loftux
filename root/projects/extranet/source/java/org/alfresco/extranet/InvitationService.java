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

import org.alfresco.extranet.database.DatabaseInvitedUser;
import org.alfresco.extranet.database.DatabaseService;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.extranet.ldap.LDAPService;
import org.alfresco.extranet.mail.MailService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskService;
import org.alfresco.tools.ObjectGUID;
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
    public void processInvitedUser(String userId)
        throws Exception
    {
        DatabaseInvitedUser invitedUser = this.databaseService.getInvitedUser(userId);
        if(invitedUser != null)
        {
            // check to make sure they haven't already been processed already
            if(invitedUser.isCompleted())
            {
                throw new Exception("User invitation was already completed");
            }
            
            // create the db user
            DatabaseUser dbUser = this.databaseService.startProcessInvitedUser(invitedUser);
            
            // create the ldap user (this is what partners will now use)
            //LDAPUser ldapUser = this.ldapService.createUser(dbUser);
            
            // if it has a legacy alfresco partners entry...
            // do additional touch up on partners
            // move folders, who knows...
            if(invitedUser.getAlfrescoUserId() != null && invitedUser.getAlfrescoUserId().trim().length() > 0)
            {
                String alfrescoUserId = invitedUser.getAlfrescoUserId();
                
                // TODO
            }
            
            // if it has an whd entry, do additional touch up on whd
            if(invitedUser.getWebHelpdeskUserId() != null && invitedUser.getWebHelpdeskUserId().trim().length() > 0)
            {
                String whdUserId = invitedUser.getWebHelpdeskUserId();
                
                // TODO
            }
            
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
            
            // add all users to the community group
            this.databaseService.addUserToGroup(userId, "community");

            // finally, mark the user as having been invited            
            this.databaseService.endProcessInvitedUser(invitedUser);            
        }
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
    public DatabaseInvitedUser inviteUser(String userId, String firstName, String lastName, String email, String whdUserId, String alfrescoUserId, String invitationType)
    {
        return inviteUser(userId, firstName, null, lastName, email, whdUserId, alfrescoUserId, DEFAULT_INVITATION_LENGTH, invitationType);        
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
     * 
     * @return the database invited user
     */
    public DatabaseInvitedUser inviteUser(String userId, String firstName, String middleName, String lastName, String email, String whdUserId, String alfrescoUserId, int lengthOfInvitation, String invitationType)
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
