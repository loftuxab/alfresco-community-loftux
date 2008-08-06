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
package org.alfresco.extranet.webhelpdesk;

import org.alfresco.extranet.AbstractUser;

/**
 * A user who is located in the Web Helpdesk application.
 * 
 * @author muzquiano
 */
public class WebHelpdeskUser extends AbstractUser
{
    protected int id;
    protected int ldapConnectionId;
    protected String rdn;
    protected String baseDn;
    
    /**
     * Instantiates a new web helpdesk user.
     * 
     * @param userId the user id
     */
    public WebHelpdeskUser(int id, String userId)
    {
        super(userId);
    }
    
    /**
     * Instantiates a new database user.
     * 
     * @param userId the user id
     */
    public WebHelpdeskUser(String userId)
    {
        super(userId);
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
    
    public void setLdapConnectionId(int ldapConnectionId)
    {
        this.ldapConnectionId = ldapConnectionId;
    }
    
    public int getLdapConnectionId()
    {
        return this.ldapConnectionId;
    }    
    
    public String getRdn()
    {
        return this.rdn;
    }
    
    public void setRdn(String rdn)
    {
        this.rdn = rdn;
    }
    
    public String getBaseDn()
    {
        return this.baseDn;
    }
    
    public void setBaseDn(String baseDn)
    {
        this.baseDn = baseDn;
    }
}
