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

import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.extranet.database.DatabaseInvitedUser;
import org.alfresco.extranet.database.DatabaseService;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.extranet.exception.InvitationAlreadyCompletedException;
import org.alfresco.extranet.exception.UserIDAlreadyExistsException;
import org.alfresco.extranet.ldap.LDAPService;
import org.alfresco.extranet.ldap.LDAPUser;
import org.alfresco.extranet.mail.MailService;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskService;
import org.alfresco.tools.ObjectGUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    protected static Log logger = LogFactory.getLog(InvitationService.class);
    
    protected ApplicationContext applicationContext;
    protected DatabaseService databaseService;
    protected LDAPService ldapService;
    protected MailService mailService;
    protected SyncService syncService;
    protected WebHelpdeskService webHelpdeskService;
    protected String alfrescoHostPort;
    
    public static int DEFAULT_INVITATION_LENGTH = 7; // a week
    
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
     * Sets the sync service.
     * 
     * @param syncService the new sync service
     */
    public void setSyncService(SyncService syncService)
    {
        this.syncService = syncService;
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
     * Invites someone to Alfresco Network.
     * 
     * This creates a record of the invitation with the provided properties.
     * Multiple invitations for the same user are allowed.
     * 
     * The default invitation length (7 days) is used.
     * 
     * @param userId the user id
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email
     * @param whdUserId the whd user id
     * @param alfrescoUserId the alfresco user id
     * @param invitationType the type of the invitation
     * @param subscriptionStartDate date when subscription starts
     * @param subscriptionEndDate date when subscription ends
     * 
     * @return the database invited user
     */
    public DatabaseInvitedUser inviteUser(String userId, String firstName, String lastName, String email, String whdUserId, String alfrescoUserId, String invitationType, Date subscriptionStartDate, Date subscriptionEndDate)
    {
        return inviteUser(userId, firstName, null, lastName, email, whdUserId, alfrescoUserId, invitationType, subscriptionStartDate, subscriptionEndDate, DEFAULT_INVITATION_LENGTH);        
    }
    
    /**
     * Invites someone to Alfresco Network.
     * 
     * This creates a record of the invitation with the provided properties.
     * Multiple invitations for the same user are allowed.
     * 
     * @param userId the user id
     * @param firstName the first name
     * @param middleName the middle name
     * @param lastName the last name
     * @param email the email
     * @param whdUserId the whd user id
     * @param alfrescoUserId the alfresco user id
     * @param invitationType the type of the invitation
     * @param subscriptionStartDate date when subscription starts
     * @param subscriptionEndDate date when subscription ends
     * @param lengthOfInvitation the length of invitation
     * 
     * @return the database invited user
     */
    public synchronized DatabaseInvitedUser inviteUser(String userId, String firstName, String middleName, String lastName, String email, String whdUserId, String alfrescoUserId, String invitationType, Date subscriptionStartDate, Date subscriptionEndDate, int lengthOfInvitation)
    {
        // clean up data
        userId = userId.trim();
        
        // check whether there are any outstanding incomplete invitations to this email address
        // if so, clean them up
        DatabaseInvitedUser invitedUser = this.databaseService.getInvitedUserByEmail(email);
        if(invitedUser != null)
        {
            this.databaseService.removeInvitedUser(invitedUser);
        }
        
        // create a new invited user
        // set properties onto it
        invitedUser = new DatabaseInvitedUser(userId);        
        if(firstName != null)
        {
            firstName = firstName.trim();
            invitedUser.setFirstName(firstName);
        }
        if(middleName != null)
        {
            middleName = middleName.trim();
            invitedUser.setMiddleName(middleName);
        }
        if(lastName != null)
        {
            lastName = lastName.trim();
            invitedUser.setLastName(lastName);
        }
        if(email != null)
        {
            email = email.trim();
            invitedUser.setEmail(email);
        }
        if(whdUserId != null)
        {
            whdUserId = whdUserId.trim();
            invitedUser.setWebHelpdeskUserId(whdUserId);
        }
        if(alfrescoUserId != null)
        {
            alfrescoUserId = alfrescoUserId.trim();
            invitedUser.setAlfrescoUserId(alfrescoUserId);
        }
        if(invitationType != null)
        {
            invitationType = invitationType.trim();
            invitedUser.setInvitationType(invitationType);
        }
        if(subscriptionStartDate != null)
        {
            invitedUser.setSubscriptionStart(subscriptionStartDate);            
        }
        if(subscriptionEndDate != null)
        {
            invitedUser.setSubscriptionEnd(subscriptionEndDate);
        }
        
        // generate a hash
        String hash = new ObjectGUID().toString();
        invitedUser.setHash(hash);
        
        // generate an invitation expiration date
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.roll(Calendar.DAY_OF_YEAR, lengthOfInvitation);        
        Date expirationDate = c.getTime();
        invitedUser.setExpirationDate(expirationDate);
        
        // invite the user
        invitedUser = this.databaseService.inviteUser(invitedUser);
        
        // send out an email
        this.mailService.inviteUser(invitedUser);

        // return the invited user
        return invitedUser;
    }
    
    /**
     * Processes the invited user and populates them into Alfresco Network
     * as a registered user according to the properties of the invited user object.
     * 
     * The process makes sure that the database user can be created.
     * 
     * It also verifies that an LDAP user can be created.
     * If an existing LDAP user is in place, the LDAP user is overwritten.
     * 
     * The user is marked as registered and is placed into the necessary groups.
     * 
     * The user is synchronized out to ACT and to Partners.
     * 
     * @param invitedUserId the invited user object id
     * @param userId the selected id for the user
     * @param password the password for the user
     * 
     * @throws DatabaseUserAlreadyExistsException the database user already exists exception
     * @throws InvitationAlreadyCompletedException the invitation already completed exception
     * @throws RemoteConfigException the remote configuration exception
     */
    public synchronized void processInvitedUser(String invitedUserId, String userId, String password)
        throws UserIDAlreadyExistsException, InvitationAlreadyCompletedException, RemoteConfigException
    {
        DatabaseInvitedUser invitedUser = this.databaseService.getInvitedUser(invitedUserId);
        if(invitedUser != null)
        {
            // check to make sure they haven't already been processed already
            if(invitedUser.isCompleted())
            {
                throw new InvitationAlreadyCompletedException("User invitation was already completed");
            }
            
            // update the user id onto the invited user object
            if(userId == null)
            {
                userId = invitedUser.getUserId();
            }
            invitedUser.setUserId(userId);

            // web helpdesk userid
            String whdUserId = invitedUser.getWebHelpdeskUserId();
            if(whdUserId != null && whdUserId.length() == 0)
            {
                whdUserId = null;
            }

            // check whether this user id is available
            //boolean available = syncService.isUserIdAvailable(userId, whdUserId);
            boolean available = syncService.isUserIdAvailable(userId);
            
            debug("Check user id (" + userId + "," + whdUserId + ") available: " + available);
            
            if(!available)
            {
                throw new UserIDAlreadyExistsException("The user id '" + userId + "' is already taken.");
            }
            
            // if this user id is available, we register the user
            if(available)
            {

                
                //
                // DATABASE USER
                //                
                debug("Creating DB User: " + userId);
                DatabaseUser dbUser = ConversionUtil.toDatabaseUser(invitedUser, null);
                dbUser = this.databaseService.insertUser(dbUser);
                
                
                
                //
                // LDAP USER
                //
                debug("Creating LDAP User: " + userId);
                LDAPUser ldapUser = ConversionUtil.toLDAPUser(dbUser, password);
                ldapUser = this.ldapService.createUser(ldapUser);
                
                
                
                //
                // WEB HELPDESK USER
                //
                if(whdUserId != null)
                {
                    debug("Sync LDAP User: " + whdUserId + " to " + userId);
                    
                    // the web helpdesk case is a special case
                    // if given a whd user id,
                    // it's either going to migrate an existing user or its going to create a new user
                    boolean sync = syncService.syncWebHelpdesk(dbUser, whdUserId, ldapUser.getPassword());
                    if(!sync)
                    {
                        debug(" -> whd sync Failed");
                    }
                }
                
                
            
                //
                // GROUPS
                //
                
                this.databaseService.addUserToGroup(userId, "community");
                this.databaseService.addUserToGroup(userId, "registered");                
            
                // if we're processing an enterprise invitation
                if("enterprise".equals(invitedUser.getInvitationType()))
                {
                    // add the user to the "enterprise" group
                    this.databaseService.addUserToGroup(userId, "enterprise");
                    
                    // add the user to the "customers" group
                    this.databaseService.addUserToGroup(userId, "customers");                
                }
                
                // TODO: Create and add users to company groups

                
                
                
                
                //
                // SYNCHRONIZE ALFRESCO WITH LDAP
                //
                syncService.syncAlfresco(ldapUser);
                /*
                if(invitedUser.getAlfrescoUserId() != null && invitedUser.getAlfrescoUserId().trim().length() > 0)
                {
                    String alfrescoUserId = invitedUser.getAlfrescoUserId();                    

                    // TODO: Nothing at this time
                }
                */

            
                // finally, mark the process as having been completed
                this.databaseService.endProcessInvitedUser(invitedUser);
            }
        }
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
        return this.databaseService.getAllInvitedUsers();
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
