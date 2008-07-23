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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.User;
import org.alfresco.extranet.UserService;
import org.alfresco.extranet.database.DatabaseUser;
import org.alfresco.web.site.exception.UserFactoryException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This factory loads users from Alfresco, fetching their properties
 * and so forth.
 * 
 * @author muzquiano
 */
public class ExtranetUserFactory extends AlfrescoUserFactory
{
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#authenticate(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        boolean authenticated = false;

        // check to see if we have this user in our db
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
        if(appContext != null)
        {
            // get the user service
            UserService userService = (UserService) appContext.getBean("extranet.service.user");
            if(userService != null)
            {
                DatabaseUser dbUser = userService.getUser(username);
                if(dbUser != null)
                {
                    boolean checkSubscription = true;
                    boolean subscriber = false;
                    
                    // if they are admin user, let them in
                    if("admin".equalsIgnoreCase(dbUser.getLevel()))
                    {
                        checkSubscription = false;
                    }
                    
                    if(checkSubscription)
                    {
                        long now = new Date().getTime();
                        
                        if(dbUser.getSubscriptionStart() != null && dbUser.getSubscriptionEnd() != null)
                        {
                            long t1 = dbUser.getSubscriptionStart().getTime();
                            long t2 = dbUser.getSubscriptionEnd().getTime();
                            
                            System.out.println("user: " + dbUser.getUserId() + " - t1: " + t1);
                            System.out.println("user: " + dbUser.getUserId() + " - now: " + now);
                            System.out.println("user: " + dbUser.getUserId() + " - t2: " + t2);
                            
                            subscriber = ((t1 < now) && (now < t2));
                        }
                        else
                        {
                            System.out.println("subscription start: " + dbUser.getSubscriptionStart() + " and end: " + dbUser.getSubscriptionEnd() + " so skipping");
                        }
                        
                        System.out.println("user: " + dbUser.getUserId() + " - subscriber: " + subscriber);                        
                    }
                    else
                    {
                        subscriber = true;
                    }
                    
                    if(subscriber)
                    {
                        authenticated = super.authenticate(request, username, password);
                    }
                }
                else
                {
                    System.out.println("User: " + username + " does not exist in application database");
                }
            }
        }
        
        return authenticated;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public User loadUser(RequestContext context, HttpServletRequest request, String userId)
        throws UserFactoryException
    {
        User user = null;
        
        // get the Spring application context
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
        if(appContext != null)
        {
            // get the user service
            UserService userService = (UserService) appContext.getBean("extranet.service.user");
            if(userService != null)
            {
                // get the user
                DatabaseUser databaseUser = userService.getUser(userId);
                if(databaseUser != null)
                {
                    boolean isAdmin = ("admin".equalsIgnoreCase(databaseUser.getLevel())); 
                    user = new User(userId, isAdmin);

                    user.setFirstName(databaseUser.getFirstName());
                    user.setLastName(databaseUser.getLastName());
                    user.setMiddleName(databaseUser.getMiddleName());
                    user.setEmail(databaseUser.getEmail());
                    
                    user.setProperty("subscription_start", databaseUser.getSubscriptionStart());
                    user.setProperty("subscription_end", databaseUser.getSubscriptionEnd());
                    user.setProperty("level", databaseUser.getLevel());
                }
            }
        }
        
        return user;
    }
    
}
