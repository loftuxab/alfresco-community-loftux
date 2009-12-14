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

import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AlfrescoUser;

/**
 * User object extended to provide persistence back to an Alfresco repo.
 * 
 * @author Kevin Roast
 */
public class SlingshotUser extends AlfrescoUser
{
    /**
     * Instantiates a new user.
     * 
     * @param id the id
     */
    public SlingshotUser(String id)
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
    public SlingshotUser(String id, boolean isAdmin, boolean isGuest)
    {
        super(id, isAdmin, isGuest);
    }
    
    /**
     * @see org.alfresco.connector.User#save()
     */
    @Override
    public void save()
    {
        try
        {
            ((SlingshotUserFactory)FrameworkUtil.getServiceRegistry().getUserFactory()).saveUser(this);
        }
        catch (UserFactoryException err)
        {
            throw new PlatformRuntimeException("Unable to save user details: " + err.getMessage(), err);
        }
    }
}
