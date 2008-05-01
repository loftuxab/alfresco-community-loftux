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

import org.alfresco.web.site.exception.UserFactoryException;

/**
 * This factory is responsible for loading user objects from a user
 * repository and making them available to the framework.
 * 
 * By implementing this class, User derived objects are available to
 * all downstream components and templates.  These components and
 * templates can then consult the user profile as they execute.
 * 
 * The user is stored on the request context and can be fetched
 * using context.getUser()
 * 
 * Within Slingshot, this factory takes a user id and then calls over
 * to Alfresco to load the user object.
 * 
 * @author muzquiano
 */
public class SlingshotUserFactory extends UserFactory
{
	/**
	 * For the moment, this just produces Slingshot Users that are
	 * guests. 
	 * 
	 * Ultimately, this method should call to Alfresco, JSON the data
	 * for the user object and construct the Slingshot user.
	 * 
	 * The returned User object is then placed onto the session.  This
	 * is done automatically by the UserFactory class.  Thus, the
	 * User object faulting should only occur once.
	 */
    public User loadUser(RequestContext context, HttpServletRequest request,
            String user_id) throws UserFactoryException
    {
    	return getGuestUser(context, request);
    }
    
    protected User getGuestUser(RequestContext context,
            HttpServletRequest request) throws UserFactoryException
    {
		if(this.guestUser == null)
		{
			this.guestUser = new SlingshotUser("guest");
			this.guestUser.setFirstName("Guest");
			this.guestUser.setLastName("User");
		}
		return this.guestUser;
    }
	
	protected SlingshotUser guestUser;
}
