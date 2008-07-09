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

/**
 * The Class AbstractUser.
 * 
 * @author muzquiano
 */
public abstract class AbstractUser extends AbstractEntity
{
    public static String ENTITY_TYPE = "user";
    
    /**
     * Builds a new abstract user.
     * 
     * @param userId the user id
     */
    public AbstractUser(String userId)
    {
        setUserId(userId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.AbstractEntity#getEntityType()
     */
    public String getEntityType()
    {
        return AbstractCompany.ENTITY_TYPE;
    }        
    
    /**
     * Gets the property names.
     * 
     * @return the property names
     */
    public static String[] getPropertyNames()
    {
        return new String[] { "userId", "firstName", "middleName", "lastName", "email", "description" }; 
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.extranet.AbstractEntity#getEntityId()
     */
    public String getEntityId()
    {
        return getUserId();
    }    
    
    /**
     * Gets the user id.
     * 
     * @return the user id
     */
    public String getUserId()
    {
        return getStringProperty("userId");
    }
    
    /**
     * Sets the user id.
     * 
     * @param userId the new user id
     */
    public void setUserId(String userId)
    {
        setProperty("userId", userId);
    }
    
    /**
     * Gets the first name.
     * 
     * @return the first name
     */
    public String getFirstName()
    {
        return getStringProperty("firstName");        
    }
    
    /**
     * Sets the first name.
     * 
     * @param firstName the new first name
     */
    public void setFirstName(String firstName)
    {
        setProperty("firstName", firstName);
    }
    
    /**
     * Gets the middle name.
     * 
     * @return the middle name
     */
    public String getMiddleName()
    {
        return getStringProperty("middleName");        
    }
    
    /**
     * Sets the middle name.
     * 
     * @param middleName the new middle name
     */
    public void setMiddleName(String middleName)
    {
        setProperty("middleName", middleName);
    }
    
    /**
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName()
    {
        return getStringProperty("lastName");
    }
    
    /**
     * Sets the last name.
     * 
     * @param lastName the new last name
     */
    public void setLastName(String lastName)
    {
        setProperty("lastName", lastName);
    }
    
    /**
     * Gets the email.
     * 
     * @return the email
     */
    public String getEmail()
    {
        return getStringProperty("email");
    }
    
    /**
     * Sets the email.
     * 
     * @param email the new email
     */
    public void setEmail(String email)
    {
        setProperty("email", email);
    }
    
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return getStringProperty("description");
    }
    
    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description)
    {
        setProperty("description", description);
    }
}
