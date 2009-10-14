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
package org.alfresco.web.site;

import org.alfresco.connector.User;

/**
 * User object extended to add avatar reference property.
 * 
 * @author Kevin Roast
 */
public class AlfrescoUser extends User
{
    public static String PROP_AVATARREF = "avatar";
    
    /**
     * Instantiates a new user.
     * 
     * @param id the id
     */
    public AlfrescoUser(String id)
    {
        super(id);
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
    public AlfrescoUser(String id, boolean isAdmin, boolean isGuest)
    {
        super(id, isAdmin, isGuest);
    }
    
    /**
     * @return  the avatarRef
     */
    public String getAvatarRef()
    {
        return getStringProperty(PROP_AVATARREF);
    }

    /**
     * @param avatarRef the avatarRef to set
     */
    public void setAvatarRef(String avatarRef)
    {
        setProperty(PROP_AVATARREF, avatarRef);
    }
}
