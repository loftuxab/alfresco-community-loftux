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
package org.alfresco.web.scripts;

import java.io.Serializable;

import org.alfresco.connector.User;
import org.alfresco.web.site.RequestContext;

/**
 * Read-only root-scoped script object describing the user of the site who 
 * is currently executing current rendering process.
 * 
 * The following is equivalent:
 * 
 * var city = user.city;
 * var city = user.properties.city;
 * var city = user.properties["city"];
 * 
 * @author muzquiano
 */
public final class ScriptUser extends ScriptBase
{
    protected User user;
    
    /**
     * Instantiates a new ScriptUser object which wraps a given request
     * context and framework user object.
     * 
     * @param context the context
     * @param user the user
     */
    public ScriptUser(RequestContext context, User user)
    {
        super(context);
        
        // store a reference to the user object
        this.user = user;
    }
        
    /**
     * Provides an associative array of properties that can be accessed via
     * scripting by using the .properties accessor.
     * 
     * @return the properties
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableLinkedHashMap<String, Serializable>(user.getProperties());
        }
        
        return this.properties;
    }
    
    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return this.user.getName();
    }
    
    /**
     * Gets the full name.
     * 
     * @return the full name
     */
    public String getFullName()
    {
        return this.user.getFullName();
    }

    /**
     * Gets the address1.
     * 
     * @return the address1
     */
    public String getAddress1()
    {
        return this.user.getAddress1();
    }

    /**
     * Gets the address2.
     * 
     * @return the address2
     */
    public String getAddress2()
    {
        return this.user.getAddress2();
    }

    /**
     * Gets the city.
     * 
     * @return the city
     */
    public String getCity()
    {
        return this.user.getCity();
    }

    /**
     * Gets the country.
     * 
     * @return the country
     */
    public String getCountry()
    {
        return this.user.getCountry();
    }

    /**
     * Gets the first name.
     * 
     * @return the first name
     */
    public String getFirstName()
    {
        return this.user.getFirstName();
    }

    /**
     * Gets the home phone.
     * 
     * @return the home phone
     */
    public String getHomePhone()
    {
        return this.user.getHomePhone();
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return this.user.getId();
    }

    /**
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName()
    {
        return this.user.getLastName();
    }

    /**
     * Gets the middle name.
     * 
     * @return the middle name
     */
    public String getMiddleName()
    {
        return this.user.getMiddleName();
    }

    /**
     * Gets the mobile phone.
     * 
     * @return the mobile phone
     */
    public String getMobilePhone()
    {
        return this.user.getMobilePhone();
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public String getState()
    {
        return this.user.getState();
    }

    /**
     * Gets the work phone.
     * 
     * @return the work phone
     */
    public String getWorkPhone()
    {
        return this.user.getWorkPhone();
    }

    /**
     * Gets the zip code.
     * 
     * @return the zip code
     */
    public String getZipCode()
    {
        return this.user.getZipCode();
    }

    /**
     * Gets the email.
     * 
     * @return the email
     */
    public String getEmail()
    {
        return this.user.getEmail();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return user.getProperties().toString();
    }    
}