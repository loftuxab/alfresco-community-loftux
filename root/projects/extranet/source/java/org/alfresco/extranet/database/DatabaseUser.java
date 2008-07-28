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

import org.alfresco.extranet.AbstractUser;

/**
 * @author muzquiano
 */
public class DatabaseUser extends AbstractUser
{
    public static final String PROP_LEVEL = "level";
    public static final String PROP_SUBSCRIPTION_START = "subscriptionStart";
    public static final String PROP_SUBSCRIPTION_END = "subscriptionEnd";
    
    protected int id;
    //protected Date subscriptionStart;
    //protected Date subscriptionEnd;
    //protected String level;
    
    /**
     * Instantiates a new extranet user.
     * 
     * @param id the id
     * @param userId the user id
     */
    public DatabaseUser(int id, String userId)
    {
        super(userId);
        
        this.id = id;
    }
    
    /**
     * Instantiates a new database user.
     * 
     * @param userId the user id
     */
    public DatabaseUser(String userId)
    {
        super(userId);
    }
    
    /**
     * Gets the property names.
     * 
     * @return the property names
     */
    public String[] getPropertyNames()
    {
        return new String[] { PROP_USER_ID, PROP_FIRST_NAME, PROP_MIDDLE_NAME, PROP_LAST_NAME, PROP_EMAIL, PROP_DESCRIPTION, PROP_LEVEL, PROP_SUBSCRIPTION_START, PROP_SUBSCRIPTION_END}; 
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
    
    public String getLevel()
    {
        return getStringProperty(PROP_LEVEL);
    }
    
    public void setLevel(String level)
    {
        setProperty(PROP_LEVEL, level);
    }
    
    public Date getSubscriptionStart()
    {
        return (Date) getProperty(PROP_SUBSCRIPTION_START);
    }

    public void setSubscriptionStart(Date subscriptionStart)
    {
        setProperty(PROP_SUBSCRIPTION_START, subscriptionStart);
    }
    
    public Date getSubscriptionEnd()
    {
        return (Date) getProperty(PROP_SUBSCRIPTION_END);
    }
    
    public void setSubscriptionEnd(Date subscriptionEnd)
    {
        setProperty(PROP_SUBSCRIPTION_END, subscriptionEnd);
    }
}
