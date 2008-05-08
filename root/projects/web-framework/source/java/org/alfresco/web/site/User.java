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
package org.alfresco.web.site;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Default user profile object.
 * 
 * This is meant to be extended and a property/authentication mapping
 * layer put into place.  But for now, this works well.
 * 
 * @author muzquiano
 */
public class User
    implements java.security.Principal, Serializable
{
    public static String PROP_ID = "id";
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
    
    protected Map<String, Serializable> map = null;
    
    public User(String id)
    {
        map = new HashMap<String,Serializable>();
        setId(id);
    }
    
    public String getName()
    {
        return getId();
    }

    //
    // Core Properties
    //
        
    public String getAddress1()
    {
        return getStringProperty(PROP_ADDRESS1);
    }
    
    protected void setAddress1(String value)
    {
        setProperty(PROP_ADDRESS1, value);
    }
    
    public String getAddress2()
    {
        return getStringProperty(PROP_ADDRESS2);
    }
    
    protected void setAddress2(String value)
    {
        setProperty(PROP_ADDRESS2, value);
    }
    
    public String getCity()
    {
        return getStringProperty(PROP_CITY);      
    }
    
    protected void setCity(String value)
    {
        setProperty(PROP_CITY, value);
    }
    
    public String getCountry()
    {
        return getStringProperty(PROP_COUNTRY);
    }
    
    protected void setCountry(String value)
    {
        setProperty(PROP_COUNTRY, value);
    }
    
    public String getFirstName()
    {
        return getStringProperty(PROP_FIRST_NAME);
    }
    
    protected void setFirstName(String value)
    {
        setProperty(PROP_FIRST_NAME, value);
    }
    
    public String getHomePhone()
    {
        return getStringProperty(PROP_HOME_PHONE);
    }

    protected void setHomePhone(String value)
    {
        setProperty(PROP_HOME_PHONE, value);
    }
    
    public String getId()
    {
        return getStringProperty(PROP_ID);
    }
    
    protected void setId(String value)
    {
        setProperty(PROP_ID, value);
    }
    
    public String getLastName()
    {
        return getStringProperty(PROP_LAST_NAME);
    }
    
    protected void setLastName(String value)
    {
        setProperty(PROP_LAST_NAME, value);
    }
    
    public String getMiddleName()
    {
        return getStringProperty(PROP_MIDDLE_NAME);
    }
    
    protected void setMiddleName(String value)
    {
        setProperty(PROP_MIDDLE_NAME, value);
    }
    
    public String getMobilePhone()
    {
        return getStringProperty(PROP_MOBILE_PHONE);
    }
    
    protected void setMobilePhone(String value)
    {
        setProperty(PROP_MOBILE_PHONE, value);
    }
    
    public String getState()
    {
        return getStringProperty(PROP_STATE);
    }
    
    protected void setState(String value)
    {
        setProperty(PROP_STATE, value);
    }
    
    public String getWorkPhone()
    {
        return (String) map.get(PROP_WORK_PHONE);
    }
    
    protected void setWorkPhone(String value)
    {
        setProperty(PROP_WORK_PHONE, value);
    }
    
    public String getZipCode()
    {
        return getStringProperty(PROP_ZIP_CODE);
    }
    
    protected void setZipCode(String value)
    {
        setProperty(PROP_ZIP_CODE, value);
    }
    
    //
    // general accessors
    //
    
    public Object getProperty(String key)
    {
        return (Object) map.get(key);
    }
    
    public String getStringProperty(String key)
    {
        return (String) map.get(key);
    }
    
    public void setProperty(String key, Serializable value)
    {
        map.put(key, value);
    }
    
    public Map<String, Serializable> getProperties()
    {
        return map;
    }

    @Override
    public String toString()
    {
        return map.toString();
    }
}
