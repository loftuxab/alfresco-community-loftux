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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author muzquiano
 * @author kevinr
 */
public class AuthenticationUtil
{
    /** cookie names */
    private static final String COOKIE_ALFLOGIN = "alfLogin";

    public static void logout(HttpServletRequest request)
    {
        // invalidate the web session - will remove all session bound objects
        // such as connector sessions, theme settings etc.
        request.getSession().invalidate();
    }

    public static void login(HttpServletRequest request, HttpServletResponse response, String userId)
    {
        // check whether there is already a user logged in
        String currentUserId = (String) request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        if (currentUserId != null)
        {
            // log out the current user
            logout(request);
        }

        // place user id onto the session
        request.getSession().setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, userId);
        
        // set login time cookie
        long timeInSeconds = System.currentTimeMillis() / 1000L;
        Cookie cookie = getLoginCookie(request);
        if (cookie == null)
        {
            cookie = new Cookie(COOKIE_ALFLOGIN, Long.toString(timeInSeconds));
        }
        else
        {
            cookie.setValue(Long.toString(timeInSeconds));
        }
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(60*60*24*7);
        response.addCookie(cookie);
    }

    /**
     * Helper to return the Alfresco cookie. The cookie saves the last login time for a username.
     * 
     * @param httpRequest
     * 
     * @return Cookie if found or null if not present
     */
    public static Cookie getLoginCookie(HttpServletRequest request)
    {
        Cookie authCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (int i=0; i<cookies.length; i++)
            {
                if (COOKIE_ALFLOGIN.equals(cookies[i].getName()))
                {
                    // found cookie
                    authCookie = cookies[i];
                    break;
                }
            }
        }
        return authCookie;
    }
}