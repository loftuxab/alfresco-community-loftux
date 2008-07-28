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
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_EMAIL = "email";
    public static final String PROP_LAST_NAME = "lastName";
    public static final String PROP_MIDDLE_NAME = "middleName";
    public static final String PROP_FIRST_NAME = "firstName";
    public static final String PROP_USER_ID = "userId";
    
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
    public String[] getPropertyNames()
    {
        return new String[] { PROP_USER_ID, PROP_FIRST_NAME, PROP_MIDDLE_NAME, PROP_LAST_NAME, PROP_EMAIL, PROP_DESCRIPTION }; 
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
        return getStringProperty(PROP_USER_ID);
    }
    
    /**
     * Sets the user id.
     * 
     * @param userId the new user id
     */
    public void setUserId(String userId)
    {
        setProperty(PROP_USER_ID, userId);
    }
    
    /**
     * Gets the first name.
     * 
     * @return the first name
     */
    public String getFirstName()
    {
        return getStringProperty(PROP_FIRST_NAME);        
    }
    
    /**
     * Sets the first name.
     * 
     * @param firstName the new first name
     */
    public void setFirstName(String firstName)
    {
        setProperty(PROP_FIRST_NAME, firstName);
    }
    
    /**
     * Gets the middle name.
     * 
     * @return the middle name
     */
    public String getMiddleName()
    {
        return getStringProperty(PROP_MIDDLE_NAME);        
    }
    
    /**
     * Sets the middle name.
     * 
     * @param middleName the new middle name
     */
    public void setMiddleName(String middleName)
    {
        setProperty(PROP_MIDDLE_NAME, middleName);
    }
    
    /**
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName()
    {
        return getStringProperty(PROP_LAST_NAME);
    }
    
    /**
     * Sets the last name.
     * 
     * @param lastName the new last name
     */
    public void setLastName(String lastName)
    {
        setProperty(PROP_LAST_NAME, lastName);
    }
    
    /**
     * Gets the email.
     * 
     * @return the email
     */
    public String getEmail()
    {
        return getStringProperty(PROP_EMAIL);
    }
    
    /**
     * Sets the email.
     * 
     * @param email the new email
     */
    public void setEmail(String email)
    {
        setProperty(PROP_EMAIL, email);
    }
    
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return getStringProperty(PROP_DESCRIPTION);
    }
    
    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description)
    {
        setProperty(PROP_DESCRIPTION, description);
    }
}
