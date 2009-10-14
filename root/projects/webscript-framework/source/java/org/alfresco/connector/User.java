/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.connector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.ui.common.StringUtils;

/**
 * Default user profile object.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class User implements java.security.Principal, Serializable
{
    public static String PROP_ID = "id";
    public static String PROP_FIRST_NAME = "firstName";
    public static String PROP_MIDDLE_NAME = "middleName";
    public static String PROP_LAST_NAME = "lastName";
    public static String PROP_EMAIL = "email";
    public static String PROP_ORGANIZATION = "organization";
    public static String PROP_JOB_TITLE = "jobtitle";
    public static String PROP_LOCATION = "location";
    public static String PROP_BIOGRAPHY = "persondescription";
    public static String PROP_TELEPHONE = "telephone";
    public static String PROP_MOBILE_PHONE = "mobile";
    public static String PROP_SKYPE = "skype";
    public static String PROP_INSTANTMSG = "instantmsg";
    public static String PROP_COMPANY_ADDRESS1 = "companyaddress1";
    public static String PROP_COMPANY_ADDRESS2 = "companyaddress2";
    public static String PROP_COMPANY_ADDRESS3 = "companyaddress3";
    public static String PROP_COMPANY_POSTCODE = "companypostcode";
    public static String PROP_COMPANY_TELEPHONE = "companytelephone";
    public static String PROP_COMPANY_FAX = "companyfax";
    public static String PROP_COMPANY_EMAIL = "companyemail";
    
    protected String fullName = null;
    protected boolean isAdmin = false;
    protected boolean isGuest = false;
    protected Map<String, Serializable> map = null;
    
    /**
     * Instantiates a new user.
     * 
     * @param id the id
     */
    public User(String id)
    {
        this.map = new HashMap<String, Serializable>(32);
        setProperty(PROP_ID, id);
    }

    /**
     * Instantiates a new user.
     * 
     * @param id
     *            the id
     * @param isAdmin
     *            is this an admin user?
     * @param isGuest
     *            is this a guest user?
     */
    public User(String id, boolean isAdmin, boolean isGuest)
    {
        this(id);
        this.isAdmin = isAdmin;
        this.isGuest = isGuest;
    }

    /* (non-Javadoc)
     * @see java.security.Principal#getName()
     */
    public String getName()
    {
        return getId();
    }
    
    /**
     * Gets the id - this is usually the username.
     * 
     * @return the id
     */
    public String getId()
    {
        return getStringProperty(PROP_ID);
    }

    //
    // Core Properties
    //

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
     * Get the location
     * 
     * @return the location
     */
    public String getLocation()
    {
        return getStringProperty(PROP_LOCATION);
    }

    /**
     * Set the location
     * 
     * @param value the new location
     */
    public void setLocation(String value)
    {
        setProperty(PROP_LOCATION, value);
    }
    
    /**
     * Get the biography
     * 
     * @return the biography
     */
    public String getBiography()
    {
        return getStringProperty(PROP_BIOGRAPHY);
    }

    /**
     * Set the biography
     * 
     * @param value the new biography
     */
    public void setBiography(String value)
    {
        if (value != null)
        {
            value = StringUtils.stripUnsafeHTMLTags(value);
        }
        setProperty(PROP_BIOGRAPHY, value);
    }

    /**
     * Gets the home phone.
     * 
     * @return the home phone
     */
    public String getTelephone()
    {
        return getStringProperty(PROP_TELEPHONE);
    }

    /**
     * Sets the home phone.
     * 
     * @param value the new home phone
     */
    public void setTelephone(String value)
    {
        setProperty(PROP_TELEPHONE, value);
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
     * Gets the skype id.
     * 
     * @return the skype id
     */
    public String getSkype()
    {
        return getStringProperty(PROP_SKYPE);
    }

    /**
     * Sets the skype id
     * 
     * @param value the new skype id
     */
    public void setSkype(String value)
    {
        setProperty(PROP_SKYPE, value);
    }
    
    /**
     * Gets the instant msg id.
     * 
     * @return the instant msg id
     */
    public String getInstantMsg()
    {
        return getStringProperty(PROP_INSTANTMSG);
    }

    /**
     * Sets the instant msg id
     * 
     * @param value the new instant msg id
     */
    public void setInstantMsg(String value)
    {
        setProperty(PROP_INSTANTMSG, value);
    }
    
    /**
     * Gets Company Address1
     * 
     * @return the Company Address1
     */
    public String getCompanyAddress1()
    {
        return getStringProperty(PROP_COMPANY_ADDRESS1);
    }

    /**
     * Sets the Company Address1
     * 
     * @param value the new Company Address1
     */
    public void setCompanyAddress1(String value)
    {
        setProperty(PROP_COMPANY_ADDRESS1, value);
    }

    /**
     * Gets Company Address2
     * 
     * @return the Company Address2
     */
    public String getCompanyAddress2()
    {
        return getStringProperty(PROP_COMPANY_ADDRESS2);
    }

    /**
     * Sets the Company Address2
     * 
     * @param value the new Company Address2
     */
    public void setCompanyAddress2(String value)
    {
        setProperty(PROP_COMPANY_ADDRESS2, value);
    }
    
    /**
     * Gets Company Address3
     * 
     * @return the Company Address3
     */
    public String getCompanyAddress3()
    {
        return getStringProperty(PROP_COMPANY_ADDRESS3);
    }

    /**
     * Sets the Company Address3
     * 
     * @param value the new Company Address3
     */
    public void setCompanyAddress3(String value)
    {
        setProperty(PROP_COMPANY_ADDRESS3, value);
    }
    
    /**
     * Gets Company Postcode
     * 
     * @return the Company Postcode
     */
    public String getCompanyPostcode()
    {
        return getStringProperty(PROP_COMPANY_POSTCODE);
    }

    /**
     * Sets the Company Postcode
     * 
     * @param value the new Company Postcode
     */
    public void setCompanyPostcode(String value)
    {
        setProperty(PROP_COMPANY_POSTCODE, value);
    }
    
    /**
     * Gets Company Telephone
     * 
     * @return the Company Telephone
     */
    public String getCompanyTelephone()
    {
        return getStringProperty(PROP_COMPANY_TELEPHONE);
    }

    /**
     * Sets the Company Telephone
     * 
     * @param value the new Company Telephone
     */
    public void setCompanyTelephone(String value)
    {
        setProperty(PROP_COMPANY_TELEPHONE, value);
    }
    
    /**
     * Gets Company Fax
     * 
     * @return the Company Fax
     */
    public String getCompanyFax()
    {
        return getStringProperty(PROP_COMPANY_FAX);
    }

    /**
     * Sets the Company Fax
     * 
     * @param value the new Company Fax
     */
    public void setCompanyFax(String value)
    {
        setProperty(PROP_COMPANY_FAX, value);
    }
    
    /**
     * Gets Company Email
     * 
     * @return the Company Email
     */
    public String getCompanyEmail()
    {
        return getStringProperty(PROP_COMPANY_EMAIL);
    }

    /**
     * Sets the Company Email
     * 
     * @param value the new Company Email
     */
    public void setCompanyEmail(String value)
    {
        setProperty(PROP_COMPANY_EMAIL, value);
    }
    
    
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

    /**
     * Returns <code>true</code> if this user is a guest user
     * 
     * @return <code>true</code> if this user is a guest user
     */
    public boolean isGuest()
    {
        return this.isGuest;
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
        if (this.fullName == null)
        {
            boolean hasFirstName = (getFirstName() != null && getFirstName().length() != 0);
            boolean hasMiddleName = (getMiddleName() != null && getMiddleName().length() != 0);
            boolean hasLastName = (getLastName() != null && getLastName().length() != 0);
            
            // if they don't have a first name, then use their user id
            this.fullName = getId();
            if (hasFirstName)
            {
                this.fullName = getFirstName();
                
                if (hasMiddleName)
                {
                    this.fullName += " " + getMiddleName();
                }
                
                if (hasLastName)
                {
                    this.fullName += " " + getLastName();
                }
            }
        }
        
        return this.fullName;
    }
    
    /**
     * Persist this user
     */
    public void save()
    {
    }
}
