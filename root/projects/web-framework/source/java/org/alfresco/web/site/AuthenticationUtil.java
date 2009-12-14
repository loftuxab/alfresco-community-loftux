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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.util.Base64;

/**
 * @author muzquiano
 * @author kevinr
 */
public class AuthenticationUtil
{
    /** cookie names */
    private static final String COOKIE_ALFLOGIN = "alfLogin";
    private static final String COOKIE_ALFUSER = "alfUsername2";
    private static final int TIMEOUT = 60*60*24*7;
    
    private static final String MT_GUEST_PREFIX = UserFactory.USER_GUEST + "@"; // eg. for MT Share
    
    
    public static void logout(HttpServletRequest request, HttpServletResponse response)
    {
        // invalidate the web session - will remove all session bound objects
        // such as connector sessions, theme settings etc.
        request.getSession().invalidate();
        
        // remove cookie
        if (response != null)
        {
            Cookie userCookie = new Cookie(COOKIE_ALFUSER, "");
            userCookie.setPath(request.getContextPath());
            userCookie.setMaxAge(0);
            response.addCookie(userCookie);
        }
    }
    
    public static void login(HttpServletRequest request, String userId)
    {
        login(request, null, userId);
    }
    
    public static void login(HttpServletRequest request, HttpServletResponse response, String userId)
    {
        // check whether there is already a user logged in
        String currentUserId = (String) request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        if (currentUserId != null)
        {
            // log out the current user
            logout(request, response);
        }
        
        // place user id onto the session
        request.getSession().setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, userId);
        
        // set login and last username cookies
        if (response != null)
        {
            long timeInSeconds = System.currentTimeMillis() / 1000L;
            Cookie loginCookie = new Cookie(COOKIE_ALFLOGIN, Long.toString(timeInSeconds));
            loginCookie.setPath(request.getContextPath());
            loginCookie.setMaxAge(TIMEOUT);
            response.addCookie(loginCookie);
            
            if (isGuest(userId) == false)
            {
                Cookie userCookie;
                try
                {
                    userCookie = new Cookie(COOKIE_ALFUSER, Base64.encodeBytes(userId.getBytes("UTF-8")));
                    userCookie.setPath(request.getContextPath());
                    userCookie.setMaxAge(TIMEOUT);
                    response.addCookie(userCookie);
                }
                catch (UnsupportedEncodingException e)
                {
                    // should never happen
                }
            }
        }
    }
    
    public static void clearUserContext(HttpServletRequest request)
    {
        request.getSession().removeAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        request.getSession().removeAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
    }
    
    public static boolean isAuthenticated(HttpServletRequest request)
    {
        // get user id from the session
        String userId = (String)request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        
        // return whether is non-null and not 'guest'
        return (userId != null && !isGuest(userId));
    }
    
    public static boolean isGuest(String userId)
    {
        // return whether 'guest' (or 'guest@tenant')
        return (userId != null && (UserFactory.USER_GUEST.equals(userId) || userId.startsWith(MT_GUEST_PREFIX)));
    }
    
    public static boolean isExternalAuthentication(HttpServletRequest request)
    {
        return (request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH) != null);
    }
    
    public static String getUserId(HttpServletRequest request)
    {
        return (String)request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
    }
    
    /**
     * Helper to return cookie that saves the last login time for the current user.
     * 
     * @param httpRequest
     * 
     * @return Cookie if found or null if not present
     */
    public static Cookie getLastLoginCookie(HttpServletRequest request)
    {
        return getCookie(request, COOKIE_ALFLOGIN);
    }

    /**
     * Helper to return cookie that saves the last login time for the current user.
     * 
     * @param httpRequest
     * 
     * @return Cookie if found or null if not present
     */
    public static Cookie getUsernameCookie(HttpServletRequest request)
    {
        return getCookie(request, COOKIE_ALFUSER);
    }
    
    private static Cookie getCookie(HttpServletRequest request, String name)
    {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (int i=0; i<cookies.length; i++)
            {
                if (name.equals(cookies[i].getName()))
                {
                    // found cookie
                    cookie = cookies[i];
                    break;
                }
            }
        }
        return cookie;
    }
}