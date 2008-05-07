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
package org.alfresco.web.site.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.UserFactory;
import org.alfresco.web.site.UserFactoryBuilder;
import org.alfresco.web.site.exception.UserFactoryException;

/**
 * Responds to Login POSTs to allow the user to authenticate to the
 * web site.
 * 
 * @author muzquiano
 */
public class LoginServlet extends BaseServlet
{
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
    	String username = (String) request.getParameter("username");
    	String password = (String) request.getParameter("password");
    	
    	String successPage = (String) request.getParameter("success");
    	String failurePage = (String) request.getParameter("failure");
    	
    	/**
    	 * See if we can load the user with this identity
    	 */
    	boolean authenticated = true;
    	try
    	{
	    	UserFactory userFactory = UserFactoryBuilder.newFactory();
	    	if(userFactory != null)
	    	{
	    		authenticated = userFactory.authenticate(request, username, password);
	    	}
    	}
    	catch(UserFactoryException ufe)
    	{
    		throw new ServletException(ufe);
    	}
    	
    	
    	/**
    	 * If they authenticated, "log in" to the framework
    	 */
    	boolean loggedIn = false;
    	if(authenticated)
    	{
    		String userId = username;
    	
    		// set this onto the session
    		request.getSession().setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, userId);
    	
    		loggedIn = true;
    	}
        
        
        /**
         * If they succeeded in logging in, redirect to the success page
         * Otherwise, redirect to the failure page
         */
        if(loggedIn)
        {
        	if(successPage != null)
        	{
        		response.sendRedirect(successPage);
        	}
        	else
        	{
        		response.sendRedirect("/");
        	}
        }
        else
        {
        	if(failurePage != null)
        	{
        		response.sendRedirect(failurePage);
        	}
        	else
        	{
        		response.sendRedirect("/");
        	}
        }        
    }
}
