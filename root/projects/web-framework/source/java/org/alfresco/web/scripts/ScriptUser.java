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

import org.alfresco.connector.User;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.exception.UserFactoryException;

/**
 * Read-only root-scoped script object wrapping the current user for
 * the current thread of execution.
 * 
 * The following is equivalent:
 * 
 * var organization = user.organization;
 * var organization = user.properties.organization;
 * var organization = user.properties["organization"];
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class ScriptUser extends ScriptBase
{
    private final User user;
    
    /**
     * Instantiates a new ScriptUser object which wraps a given request
     * context and framework user object.
     * 
     * @param context the render context
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
            this.properties = new ScriptableWrappedMap(user.getProperties());
        }
        
        return this.properties;
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
     * Gets the name (generally this is the username - i.e. same as id)
     * 
     * @return the name
     */
    public String getName()
    {
        return this.user.getName();
    }
    
    public String getFullName()
    {
        return this.user.getFullName();
    }
    
    public String getFirstName()
    {
        return this.user.getFirstName();
    }
    
    public void setFirstName(String value)
    {
        this.user.setFirstName(value);
    }
    
    public String getLastName()
    {
        return this.user.getLastName();
    }
    
    public void setLastName(String value)
    {
        this.user.setLastName(value);
    }

    public String getMiddleName()
    {
        return this.user.getMiddleName();
    }
    
    public void setMiddleName(String value)
    {
        this.user.setMiddleName(value);
    }
    
    public String getEmail()
    {
        return this.user.getEmail();
    }
    
    public void setEmail(String value)
    {
        this.user.setEmail(value);
    }
    
    public String getOrganization()
    {
        return this.user.getOrganization();
    }
    
    public void setOrganization(String value)
    {
        this.user.setEmail(value);
    }
    
    public String getJobTitle()
    {
        return this.user.getJobTitle();
    }
    
    public void setJobTitle(String value)
    {
        this.user.setJobTitle(value);
    }
    
    public String getLocation()
    {
        return this.user.getLocation();
    }
    
    public void setLocation(String value)
    {
        this.user.setLocation(value);
    }
    
    public String getBiography()
    {
        return this.user.getBiography();
    }
    
    public void setBiography(String value)
    {
        this.user.setBiography(value);
    }
    
    public String getTelephone()
    {
        return this.user.getTelephone();
    }
    
    public void setTelephone(String value)
    {
        this.user.setTelephone(value);
    }
    
    public String getMobilePhone()
    {
        return this.user.getMobilePhone();
    }
    
    public void setMobilePhone(String value)
    {
        this.user.setMobilePhone(value);
    }
    
    public String getSkype()
    {
        return this.user.getSkype();
    }
    
    public void setSkype(String value)
    {
        this.user.setSkype(value);
    }
    
    public String getInstantMsg()
    {
        return this.user.getInstantMsg();
    }
    
    public void setInstantMsg(String value)
    {
        this.user.setInstantMsg(value);
    }
    
    public String getCompanyPostcode()
    {
        return this.user.getCompanyPostcode();
    }
    
    public void setCompanyPostcode(String value)
    {
        this.user.setCompanyPostcode(value);
    }
    
    public String getCompanyTelephone()
    {
        return this.user.getCompanyTelephone();
    }
    
    public void setCompanyTelephone(String value)
    {
        this.user.setCompanyTelephone(value);
    }
    
    public String getCompanyFax()
    {
        return this.user.getCompanyFax();
    }
    
    public void setCompanyFax(String value)
    {
        this.user.setCompanyFax(value);
    }
    
    public String getCompanyEmail()
    {
        return this.user.getCompanyEmail();
    }
    
    public void setCompanyEmail(String value)
    {
        this.user.setCompanyEmail(value);
    }
    
    public String getCompanyAddress1()
    {
        return this.user.getCompanyAddress1();
    }
    
    public void setCompanyAddress1(String value)
    {
        this.user.setCompanyAddress1(value);
    }

    public String getCompanyAddress2()
    {
        return this.user.getCompanyAddress2();
    }
    
    public void setCompanyAddress2(String value)
    {
        this.user.setCompanyAddress2(value);
    }
    
    public String getCompanyAddress3()
    {
        return this.user.getCompanyAddress3();
    }
    
    public void setCompanyAddress3(String value)
    {
        this.user.setCompanyAddress3(value);
    }
    
    public boolean getIsAdmin()
    {
        return this.user.isAdmin();
    }
        
    public boolean getIsGuest()
    {
        return this.user.isGuest();
    }

    /**
     * Persist user changes
     */
    public void save()
    {
        this.user.save();
    }
    
    /**
     * Retrieve a user object with populated details for the given user Id
     * 
     * @param userId
     * 
     * @return ScriptUser
     */
    public ScriptUser getUser(String userId)
    {
        try
        {
            User user = FrameworkHelper.getUserFactory().loadUser(this.context, userId);
            return new ScriptUser(this.context, user);
        }
        catch (UserFactoryException err)
        {
            // unable to load user details - so cannot return a user to the caller
            return null;
        }
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