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
package org.alfresco.extranet.ldap;

import org.alfresco.extranet.database.DatabaseUser;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Wraps functions around users in the LDAP.
 * 
 * @author muzquiano
 */
public class LDAPService implements ApplicationContextAware
{
    protected ApplicationContext applicationContext;
    protected LDAPUserBean userBean;
    protected LDAPGroupBean groupBean;
    protected LDAPCompanyBean companyBean;
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Sets the user bean.
     * 
     * @param userBean the new user bean
     */
    public void setUserBean(LDAPUserBean userBean)
    {
        this.userBean = userBean;
    }
    
    /**
     * Sets the group bean.
     * 
     * @param groupBean the new group bean
     */
    public void setGroupBean(LDAPGroupBean groupBean)
    {
        this.groupBean = groupBean;
    }
    
    /**
     * Sets the company bean.
     * 
     * @param companyBean the new company bean
     */
    public void setCompanyBean(LDAPCompanyBean companyBean)
    {
        this.companyBean = companyBean;
    }
    
    /**
     * Instantiates a new lDAP service.
     */
    public LDAPService()
    {
    }
    
    /**
     * Creates the user.
     * 
     * @param ldapUser the LDAPUser
     * 
     * @return the lDAP user
     */
    public LDAPUser createUser(LDAPUser ldapUser)
    {
        return userBean.insert(ldapUser);
    }
    
    /**
     * Update user.
     * 
     * @param ldapUser the ldap user
     * 
     * @return true, if successful
     */
    public boolean updateUser(LDAPUser ldapUser)
    {
        return userBean.update(ldapUser);
    }
    
    
    
}
