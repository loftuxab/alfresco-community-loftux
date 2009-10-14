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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.alfresco.connector.User;
import org.alfresco.web.site.exception.UserFactoryException;

/**
 * Factory class for producing and loading User objects. Generally this is
 * overriden on a per-application bases to build specific User instances.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public abstract class UserFactory
{
    /** Guest user name key*/
    public static final String USER_GUEST = "guest";
    
    /** User object key in the session */
    public static String SESSION_ATTRIBUTE_KEY_USER_OBJECT = "USER_OBJECT";
    
    /** User name id key in the session */
    public static String SESSION_ATTRIBUTE_KEY_USER_ID = "USER_ID";
    
    /** flag to set in the user Session when an external authentication mechanism is used
     *  this informs the framework that user cannot Change Password or Logout in the usual way */
    public static final String SESSION_ATTRIBUTE_EXTERNAL_AUTH= "_alfExternalAuth";
    
    
    /** Guest user cache (no sync required - multiple instance creation will not cause an issue) */
    private User guestUser = null;
    
    
    /**
     * Retrieve the special "Guest" user instance.
     * 
     * @param context
     * 
     * @return Guest User
     * 
     * @throws UserFactoryException
     */
    protected User getGuestUser(RequestContext context) throws UserFactoryException
    {
        if (this.guestUser == null)
        {
            User user = new User(USER_GUEST, false, true);
            user.setFirstName("Guest");
            
            this.guestUser = user;
        }
        return this.guestUser;
    }
    
    /**
     * Loads a user from the remote user store and store it into the session.
     * 
     * @param context
     * @param request
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User faultUser(RequestContext context, HttpServletRequest request)
        throws UserFactoryException
    {
        return faultUser(context, request, false);
    }
    
    /**
     * Loads a user from the remote user store and store it into the session.
     * 
     * @param context
     * @param request
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User faultUser(RequestContext context, HttpServletRequest request, String endpoint)
        throws UserFactoryException
    {
        return faultUser(context, request, endpoint, false);
    }
    
    /**
     * Loads a user from the remote user store and stores it into the session.
     * 
     * If the force flag is set, the current in-session user
     * object will be purged, forcing the user object to reload.
     * 
     * @param context
     * @param request
     * @param force
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User faultUser(RequestContext context, HttpServletRequest request, boolean force)
        throws UserFactoryException
    {
        return faultUser(context, request, null, force);
    }
    
    /**
     * Loads a user from the remote user store and stores it into the session.
     * 
     * If the force flag is set, the current in-session user
     * object will be purged, forcing the user object to reload.
     * 
     * @param context
     * @param request
     * @param force
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User faultUser(RequestContext context, HttpServletRequest request, String endpoint, boolean force)
        throws UserFactoryException
    {
        User user = null;
        HttpSession session = request.getSession();
        
        // do we want to force a user fault?
        if (force)
        {
            // remove the user object from session
            session.removeAttribute(SESSION_ATTRIBUTE_KEY_USER_OBJECT);
        }
        
        // check whether there is a "USER_ID" marker in the session
        String userId = (String)session.getAttribute(SESSION_ATTRIBUTE_KEY_USER_ID);
        
        // Support appserver-based SSO
        if (userId == null)
        {
            userId = request.getRemoteUser();
        }

        if (userId != null)
        {
            // check whether there is a user object loaded already
            user = (User)session.getAttribute(SESSION_ATTRIBUTE_KEY_USER_OBJECT);
            if (user == null)
            {
                // load the user from whatever store...
                user = loadUser(context, userId, endpoint);
                
                // if we got the user, set onto session
                if (user != null)
                {
                    session.setAttribute(SESSION_ATTRIBUTE_KEY_USER_OBJECT, user);
                    
                    // update the user ID - as the case may be different than used on the login dialog
                    session.setAttribute(SESSION_ATTRIBUTE_KEY_USER_ID, user.getId());
                }
                else
                {
                    // unable to load the user
                    session.removeAttribute(SESSION_ATTRIBUTE_KEY_USER_OBJECT);                	
                }
            }
        }
        
        // return the guest user
        if (user == null)
        {
            user = getGuestUser(context);
        }
        
        return user;
    }
    
    /**
     * Load the user from a store
     * 
     * @param context
     * @param userId
     * @param endpointId
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public abstract User loadUser(RequestContext context, String userId)
        throws UserFactoryException;
    
    /**
     * Load the user from a store
     * 
     * @param context
     * @param userId
     * @param endpointId
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public abstract User loadUser(RequestContext context, String userId, String endpointId)
        throws UserFactoryException;
    
    /**
     * Authentication the user given the supplied username/password
     * 
     * @param request
     * @param username
     * @param password
     * 
     * @return success/failure
     */
    public abstract boolean authenticate(HttpServletRequest request, String username, String password);
}
