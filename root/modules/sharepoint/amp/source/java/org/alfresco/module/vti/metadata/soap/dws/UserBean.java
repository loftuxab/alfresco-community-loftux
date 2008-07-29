/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.module.vti.metadata.soap.dws;

import java.io.Serializable;

import org.alfresco.module.vti.metadata.soap.SoapUtils;

/**
 * represent user of Sharepoint Site
 * 
 * @author PavelYur
 *
 */
public class UserBean implements Serializable
{

    private static final long serialVersionUID = 1404078639183811405L;
    
    private String id;
    private String name;
    private String loginName;
    private String email;
    private boolean isDomainGroup;
    private boolean isSiteAdmin;
    
    
    
    /**
     * 
     */
    public UserBean()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param id
     * @param name
     * @param loginName
     * @param email
     * @param isDomainGroup
     * @param isSiteAdmin
     */
    public UserBean(String id, String name, String loginName, String email, boolean isDomainGroup, boolean isSiteAdmin)
    {
        super();
        this.id = id;
        this.name = name;
        this.loginName = loginName;
        this.email = email;
        this.isDomainGroup = isDomainGroup;
        this.isSiteAdmin = isSiteAdmin;
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isDomainGroup()
    {
        return isDomainGroup;
    }

    public void setDomainGroup(boolean isDomainGroup)
    {
        this.isDomainGroup = isDomainGroup;
    }

    public boolean isSiteAdmin()
    {
        return isSiteAdmin;
    }

    public void setSiteAdmin(boolean isSiteAdmin)
    {
        this.isSiteAdmin = isSiteAdmin;
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        result.append(SoapUtils.startTag("User"))
              .append(SoapUtils.proccesTag("ID", id))
              .append(SoapUtils.proccesTag("Name", name))
              .append(SoapUtils.proccesTag("LoginName", loginName))
              .append(SoapUtils.proccesTag("Email", email))
              .append(SoapUtils.proccesTag("IsDomainGroup", isDomainGroup))
              .append(SoapUtils.proccesTag("IsSiteAdmin", isSiteAdmin))
              .append(SoapUtils.endTag("User"));
        return result.toString();
    }

}
