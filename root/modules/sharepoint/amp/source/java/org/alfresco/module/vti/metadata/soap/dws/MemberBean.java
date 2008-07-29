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
 * @author AndreyAk
 *
 */
public class MemberBean implements Serializable
{

    private static final long serialVersionUID = -7459386981434580654L;
    
    private String id;
    private String name;
    private String loginName;
    private String email;
    private boolean isDomainGroup;
    
    /**
     * Default constructor
     */
    public MemberBean()
    {
    }

    /**
     * @param id
     * @param name
     * @param loginName
     * @param email
     * @param isDomainGroup
     */
    public MemberBean(String id, String name, String loginName, String email, boolean isDomainGroup)
    {
        super();
        this.id = id;
        this.name = name;
        this.loginName = loginName;
        this.email = email;
        this.isDomainGroup = isDomainGroup;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the loginName
     */
    public String getLoginName()
    {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * @return the isDomainGroup
     */
    public boolean isDomainGroup()
    {
        return isDomainGroup;
    }

    /**
     * @param isDomainGroup the isDomainGroup to set
     */
    public void setDomainGroup(boolean isDomainGroup)
    {
        this.isDomainGroup = isDomainGroup;
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        result.append(SoapUtils.startTag("Member"))
              .append(SoapUtils.proccesTag("ID", id))
              .append(SoapUtils.proccesTag("Name", name))
              .append(SoapUtils.proccesTag("LoginName", loginName))
              .append(SoapUtils.proccesTag("Email", email))
              .append(SoapUtils.proccesTag("IsDomainGroup", isDomainGroup))
              .append(SoapUtils.endTag("Member"));
        return result.toString();
    }
}
