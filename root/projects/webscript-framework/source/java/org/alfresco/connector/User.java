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
package org.alfresco.connector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Default user profile object.
 * 
 * This is meant to be extended and a property/authentication mapping layer put
 * into place. But for now, this works well.
 * 
 * @author muzquiano
 */
public class User implements java.security.Principal, Serializable
{
    public static String PROP_ID = "id";
    public static String PROP_EMAIL = "email";
    public static String PROP_ADDRESS1 = "address1";
    public static String PROP_ADDRESS2 = "address2";
    public static String PROP_CITY = "city";
    public static String PROP_COUNTRY = "country";
    public static String PROP_FIRST_NAME = "first_name";
    public static String PROP_MIDDLE_NAME = "middle_name";
    public static String PROP_LAST_NAME = "last_name";
    public static String PROP_HOME_PHONE = "home_phone";
    public static String PROP_WORK_PHONE = "work_phonne";
    public static String PROP_MOBILE_PHONE = "mobile_phone";
    public static String PROP_STATE = "state";
    public static String PROP_ZIP_CODE = "zip_code";
    public static String PROP_JOB_TITLE = "job_title";
    public static String PROP_ORGANIZATION = "organization";
    public static String PROP_LOCATION = "location";

    protected String fullName = null;
    protected boolean isAdmin = false;
    protected Map<String, Serializable> map = null;

    /**
     * Instantiates a new user.
     * 
     * @param id the id
     */
    public User(String id)
    {
        this.map = new HashMap<String, Serializable>();
        setId(id);
    }

    /**
     * Instantiates a new user.
     * 
     * @param id the id
     * @param isAdmin the is admin
     */
    public User(String id, boolean isAdmin)
    {
        this(id);
        this.isAdmin = isAdmin;
    }

    /* (non-Javadoc)
     * @see java.security.Principal#getName()
     */
    public String getName()
    {
        return getId();
    }

    //
    // Core Properties
    //

    /**
     * Gets the address1.
     * 
     * @return the address1
     */
    public String getAddress1()
    {
        return getStringProperty(PROP_ADDRESS1);
    }

    /**
     * Sets the address1.
     * 
     * @param value the new address1
     */
    public void setAddress1(String value)
    {
        setProperty(PROP_ADDRESS1, value);
    }

    /**
     * Gets the address2.
     * 
     * @return the address2
     */
    public String getAddress2()
    {
        return getStringProperty(PROP_ADDRESS2);
    }

    /**
     * Sets the address2.
     * 
     * @param value the new address2
     */
    public void setAddress2(String value)
    {
        setProperty(PROP_ADDRESS2, value);
    }

    /**
     * Gets the city.
     * 
     * @return the city
     */
    public String getCity()
    {
        return getStringProperty(PROP_CITY);
    }

    /**
     * Sets the city.
     * 
     * @param value the new city
     */
    public void setCity(String value)
    {
        setProperty(PROP_CITY, value);
    }

    /**
     * Gets the country.
     * 
     * @return the country
     */
    public String getCountry()
    {
        return getStringProperty(PROP_COUNTRY);
    }

    /**
     * Sets the country.
     * 
     * @param value the new country
     */
    public void setCountry(String value)
    {
        setProperty(PROP_COUNTRY, value);
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
     * @param value the new first name
     */
    public void setFirstName(String value)
    {
        setProperty(PROP_FIRST_NAME, value);
    }

    /**
     * Gets the home phone.
     * 
     * @return the home phone
     */
    public String getHomePhone()
    {
        return getStringProperty(PROP_HOME_PHONE);
    }

    /**
     * Sets the home phone.
     * 
     * @param value the new home phone
     */
    public void setHomePhone(String value)
    {
        setProperty(PROP_HOME_PHONE, value);
    }
    
    /**
     * Gets the job title.
     * 
     * @return the job title
     */
    public String getJobTitle()
    {
        return getStringProperty(PROP_JOB_TITLE);
    }

    /**
     * Sets the job title.
     * 
     * @param value the new job title
     */
    public void setJobTitle(String value)
    {
        setProperty(PROP_JOB_TITLE, value);
    }
    
    /**
     * Gets the organization.
     * 
     * @return the organization
     */
    public String getOrganization()
    {
        return getStringProperty(PROP_ORGANIZATION);
    }

    /**
     * Sets the organization.
     * 
     * @param value the new organization
     */
    public void setOrganization(String value)
    {
        setProperty(PROP_ORGANIZATION, value);
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return getStringProperty(PROP_ID);
    }

    /**
     * Sets the id.
     * 
     * @param value the new id
     */
    public void setId(String value)
    {
        setProperty(PROP_ID, value);
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
     * @param value the new last name
     */
    public void setLastName(String value)
    {
        setProperty(PROP_LAST_NAME, value);
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
     * @param value the new middle name
     */
    public void setMiddleName(String value)
    {
        setProperty(PROP_MIDDLE_NAME, value);
    }

    /**
     * Gets the mobile phone.
     * 
     * @return the mobile phone
     */
    public String getMobilePhone()
    {
        return getStringProperty(PROP_MOBILE_PHONE);
    }

    /**
     * Sets the mobile phone.
     * 
     * @param value the new mobile phone
     */
    public void setMobilePhone(String value)
    {
        setProperty(PROP_MOBILE_PHONE, value);
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public String getState()
    {
        return getStringProperty(PROP_STATE);
    }

    /**
     * Sets the state.
     * 
     * @param value the new state
     */
    public void setState(String value)
    {
        setProperty(PROP_STATE, value);
    }

    /**
     * Gets the work phone.
     * 
     * @return the work phone
     */
    public String getWorkPhone()
    {
        return (String) map.get(PROP_WORK_PHONE);
    }

    /**
     * Sets the work phone.
     * 
     * @param value the new work phone
     */
    public void setWorkPhone(String value)
    {
        setProperty(PROP_WORK_PHONE, value);
    }

    /**
     * Gets the zip code.
     * 
     * @return the zip code
     */
    public String getZipCode()
    {
        return getStringProperty(PROP_ZIP_CODE);
    }

    /**
     * Sets the zip code.
     * 
     * @param value the new zip code
     */
    public void setZipCode(String value)
    {
        setProperty(PROP_ZIP_CODE, value);
    }
    
    public String getLocation()
    {
        return getStringProperty(PROP_LOCATION);
    }

    public void setLocation(String value)
    {
        setProperty(PROP_LOCATION, value);
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
     * @param value the new email
     */
    public void setEmail(String value)
    {
        setProperty(PROP_EMAIL, value);
    }
    

    //
    // general accessors
    //


    /**
     * Gets the property.
     * 
     * @param key the key
     * 
     * @return the property
     */
    public Object getProperty(String key)
    {
        return (Object) map.get(key);
    }

    /**
     * Gets the string property.
     * 
     * @param key the key
     * 
     * @return the string property
     */
    public String getStringProperty(String key)
    {
        return (String) map.get(key);
    }

    /**
     * Sets the property.
     * 
     * @param key the key
     * @param value the value
     */
    public void setProperty(String key, Serializable value)
    {
        map.put(key, value);
    }

    /**
     * Gets the properties.
     * 
     * @return the properties
     */
    public Map<String, Serializable> getProperties()
    {
        return map;
    }

    /**
     * Checks if is admin.
     * 
     * @return the isAdmin
     */
    public boolean isAdmin()
    {
        return this.isAdmin;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return map.toString();
    }
    
    /**
     * Provides the full name for the user.  This makes a best attempt at
     * building the full name based on what it knows about the user.
     * 
     * If a first name is not known, the returned name will be the user id
     * of the user.
     * 
     * If a first name is known, then the first name will be returned.
     * If a first and middle name are known, then the first and middle name
     * will be returned.
     * 
     * Valid full names are therefore:
     * 
     *      jsmith
     *      Joe
     *      Joe D
     *      Joe Smith
     *      Joe D Smith
     * 
     * @return A valid full name
     */
    public String getFullName()
    {
        if(this.fullName == null)
        {
            boolean hasFirstName = (getFirstName() != null && getFirstName().length() > 0);
            boolean hasMiddleName = (getMiddleName() != null && getMiddleName().length() > 0);
            boolean hasLastName = (getLastName() != null && getLastName().length() > 0);
            
            // if they don't have a first name, then use their user id
            this.fullName = getId();
            if(hasFirstName)
            {
                this.fullName = getFirstName();
                
                if(hasMiddleName)
                {
                    this.fullName += " " + getMiddleName();
                }
                
                if(hasLastName)
                {
                    this.fullName += " " + getLastName();
                }
            }
        }
        
        return this.fullName;
    }
}
