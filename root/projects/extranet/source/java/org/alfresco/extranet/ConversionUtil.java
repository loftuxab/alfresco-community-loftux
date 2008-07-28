package org.alfresco.extranet;

import org.alfresco.extranet.database.DatabaseInvitedUser;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.extranet.ldap.LDAPUser;
import org.alfresco.extranet.webhelpdesk.WebHelpdeskUser;

public class ConversionUtil
{
    /**
     * Creates an instance of an LDAPUser from a DatabaseUser
     * The new LDAPUser is not persisted.
     * 
     * @param dbUser the db user
     * @param password the password for the ldap record
     * 
     * @return the LDAP user
     */
    public static LDAPUser toLDAPUser(DatabaseUser dbUser, String password)
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
    
    /**
     * Creates an instance of a WebHelpdeskUser from a DatabaseUser
     * If a web helpdesk user object is provided, it is populated
     * 
     * @param dbUser the db user
     * @param whdUser the whd user
     * 
     * @return the web helpdesk user
     */
    public static WebHelpdeskUser toWebHelpdeskUser(DatabaseUser dbUser, WebHelpdeskUser whdUser)
    {
        if(whdUser == null)
        {
            whdUser = new WebHelpdeskUser(dbUser.getUserId());
        }
        
        // copy in properties
        whdUser.setDescription(dbUser.getDescription());
        whdUser.setEmail(dbUser.getEmail());
        whdUser.setFirstName(dbUser.getFirstName());
        whdUser.setMiddleName(dbUser.getMiddleName());
        whdUser.setLastName(dbUser.getLastName());
                
        return whdUser;
    }
    
    public static DatabaseUser toDatabaseUser(DatabaseInvitedUser invitedUser, DatabaseUser user)
    {
        if(user == null)
        {
            user = new DatabaseUser(invitedUser.getUserId());
        }
        
        // copy in properties
        user.setDescription(invitedUser.getDescription());
        user.setEmail(invitedUser.getEmail());
        user.setFirstName(invitedUser.getFirstName());
        user.setMiddleName(invitedUser.getMiddleName());
        user.setLastName(invitedUser.getLastName());
        
        // copy in dates
        user.setSubscriptionStart(invitedUser.getSubscriptionStart());
        user.setSubscriptionEnd(invitedUser.getSubscriptionEnd());
        
        return user;
    }
    

}
