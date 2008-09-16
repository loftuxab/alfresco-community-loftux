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

import org.alfresco.web.framework.model.Theme;

/**
 * Request level context object that wraps a HttpServletRequest to provide
 * additional framework related general helper functions, request level object
 * containment etc.
 * 
 * @see RequestContext
 * 
 * @author muzquiano
 * @author kevinr
 */
public class HttpRequestContext extends ThreadLocalRequestContext
{
    private static final String SESSION_CURRENT_THEME    = "alfTheme";
    private static final String SESSION_CURRENT_THEME_ID = "alfThemeId";
    
    /** The request encapsulated by this context object */
    protected HttpServletRequest request;
    
    
    /**
     * Construction
     * 
     * @param request   The HttpServletRequest this context is related too
     */
    public HttpRequestContext(HttpServletRequest request)
    {
        this.request = request;
    }

    /**
     * Returns the HTTP Servlet Request bound to this request
     * 
     * @return
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    /**
     * Returns the debug mode of the current request
     * If not in debug mode, this will return null
     */
    public String getDebugMode()
    {
        String debug = request.getParameter("debug");
        return (debug != null && debug.length() != 0 ? debug : null);
    }
    
    /**
     * Returns the current Theme Id for the current user
     */
    public String getThemeId()
    {
        return (String)request.getSession().getAttribute(SESSION_CURRENT_THEME_ID);
    }
    
    /**
     * Sets the current theme id
     */
    public void setThemeId(String themeId)
    {
        if (themeId != null)
        {
            request.getSession().setAttribute(SESSION_CURRENT_THEME_ID, themeId);
        }
    }
    
    /**
     * Gets the current Theme object, or null if not set
     */
    public Theme getTheme()
    {
        Theme theme = (Theme)getValue(SESSION_CURRENT_THEME);
        if (theme == null)
        {
            String themeId = getThemeId();
            if (themeId != null)
            {
                theme = getModel().getTheme(themeId);
                if (theme != null)
                {
                    setValue(SESSION_CURRENT_THEME, theme);
                }
            }
        }
        return theme;
    }
}
