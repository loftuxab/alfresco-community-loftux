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

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.User;
import org.alfresco.web.site.exception.UserFactoryException;

/**
 * The world's simplest user factory.
 * This just returns guest users.  NOP users, essentially.
 * 
 * @author muzquiano
 */
public class DefaultUserFactory extends UserFactory
{
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#authenticate(javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
    	return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String)
     */
    public User loadUser(RequestContext context, String userId) throws UserFactoryException
    {
        return this.getGuestUser(context);
    }
}