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

import java.util.Date;

/**
 * The Class DatabaseInvitedUser.
 * 
 * @author muzquiano
 */
public class DatabaseInvitedUser
{
    protected int id;
    protected String userId;
    protected String email;
    protected String companyId;
    protected String hash;
    protected boolean completed;
    protected String whdUserId;
    protected String alfrescoUserId;
    
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String description;
    protected Date expirationDate;
    
    public String groupIds;
    public String invitationType;
    
    protected Date subscriptionStart;
    protected Date subscriptionEnd;
    
    /**
     * Instantiates a new database invited user.
     * 
     * @param id the id
     * @param userId the user id
     */
    public DatabaseInvitedUser(int id, String userId)
    {
        this.id = id;
        this.userId = userId;
    }
    
    /**
     * Instantiates a new database invited user.
     * 
     * @param userId the user id
     */
    public DatabaseInvitedUser(String userId)
    {
        this.userId = userId;
    }
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId()
    {
        return this.id;
    }
    
    /**
     * Gets the user id.
     * 
     * @return the user id
     */
    public String getUserId()
    {
        return this.userId;
    }
    
    /**
     * Sets the user id.
     * 
     * @param userId the new user id
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    /**
     * Gets the email.
     * 
     * @return the email
     */
    public String getEmail()
    {
        return this.email;
    }
    
    /**
     * Sets the email.
     * 
     * @param email the new email
     */
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    /**
     * Gets the first name.
     * 
     * @return the first name
     */
    public String getFirstName()
    {
        return this.firstName;
    }
    
    /**
     * Sets the first name.
     * 
     * @param firstName the new first name
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    /**
     * Gets the middle name.
     * 
     * @return the middle name
     */
    public String getMiddleName()
    {
        return this.middleName;
    }
    
    /**
     * Sets the middle name.
     * 
     * @param middleName the new middle name
     */
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }
    
    /**
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName()
    {
        return this.lastName;
    }
    
    /**
     * Sets the last name.
     * 
     * @param lastName the new last name
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return this.description;
    }
    
    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * Gets the company id.
     * 
     * @return the company id
     */
    public String getCompanyId()
    {
        return this.companyId;
    }
    
    /**
     * Sets the company id.
     * 
     * @param companyId the new company id
     */
    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }
    
    /**
     * Gets the hash.
     * 
     * @return the hash
     */
    public String getHash()
    {
        return this.hash;
    }
    
    /**
     * Sets the hash.
     * 
     * @param hash the new hash
     */
    public void setHash(String hash)
    {
        this.hash = hash;
    }
    
    /**
     * Checks if is completed.
     * 
     * @return true, if is completed
     */
    public boolean isCompleted()
    {
        return this.completed;
    }
    
    /**
     * Sets the completed.
     * 
     * @param b the new completed
     */
    public void setCompleted(boolean b)
    {
        this.completed = b;
    }
    
    /**
     * Gets the web helpdesk user id.
     * 
     * @return the web helpdesk user id
     */
    public String getWebHelpdeskUserId()
    {
        return this.whdUserId;
    }
    
    /**
     * Sets the web helpdesk user id.
     * 
     * @param whdUserId the new web helpdesk user id
     */
    public void setWebHelpdeskUserId(String whdUserId)
    {
        this.whdUserId = whdUserId;
    }
    
    /**
     * Gets the alfresco user id.
     * 
     * @return the alfresco user id
     */
    public String getAlfrescoUserId()
    {
        return this.alfrescoUserId;
    }
    
    /**
     * Sets the alfresco user id.
     * 
     * @param alfrescoUserId the new alfresco user id
     */
    public void setAlfrescoUserId(String alfrescoUserId)
    {
        this.alfrescoUserId = alfrescoUserId;
    }
    
    /**
     * Gets the expiration date.
     * 
     * @return the expiration date
     */
    public Date getExpirationDate()
    {
        return this.expirationDate;
    }
    
    /**
     * Sets the expiration date.
     * 
     * @param expirationDate the new expiration date
     */
    public void setExpirationDate(Date expirationDate)
    {
        this.expirationDate = expirationDate;
    }
    
    /**
     * Gets the group ids.
     * 
     * @return the group ids
     */
    public String getGroupIds()
    {
        return this.groupIds;
    }
    
    /**
     * Sets the group ids.
     * 
     * @param groupIds the new group ids
     */
    public void setGroupIds(String groupIds)
    {
        this.groupIds = groupIds;
    }
    
    /**
     * Gets the invitation type.
     * 
     * @return the invitation type
     */
    public String getInvitationType()
    {
        return this.invitationType;
    }
    
    /**
     * Sets the invitation type.
     * 
     * @param invitationType the new invitation type
     */
    public void setInvitationType(String invitationType)
    {
        this.invitationType = invitationType;
    }
    
    /**
     * Gets the subscription start.
     * 
     * @return the subscription start
     */
    public Date getSubscriptionStart()
    {
        return this.subscriptionStart;
    }

    /**
     * Sets the subscription start.
     * 
     * @param subscriptionStart the new subscription start
     */
    public void setSubscriptionStart(Date subscriptionStart)
    {
        this.subscriptionStart = subscriptionStart;
    }
    
    /**
     * Gets the subscription end.
     * 
     * @return the subscription end
     */
    public Date getSubscriptionEnd()
    {
        return this.subscriptionEnd;
    }
    
    /**
     * Sets the subscription end.
     * 
     * @param subscriptionEnd the new subscription end
     */
    public void setSubscriptionEnd(Date subscriptionEnd)
    {
        this.subscriptionEnd = subscriptionEnd;
    }
    
}
