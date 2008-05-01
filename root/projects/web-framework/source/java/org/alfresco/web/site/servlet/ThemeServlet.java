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

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * Listens for AJAX calls to update the theme for the current user
 * 
 * @author muzquiano
 */
public class ThemeServlet extends BaseServlet
{
    public void init() throws ServletException
    {
        super.init();
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        // get the request context
        RequestContext context = null;
        try
        {
            context = RequestUtil.getRequestContext(request);
        }
        catch(RequestContextException rce)
        {
            throw new ServletException("Unable to retrieve request context from request", rce);
        }

        // the new theme
        String themeId = (String) request.getParameter("themeId");
        if(themeId == null)
        {
            themeId = (String) request.getParameter("theme");
        }
        if (themeId != null)
        {
            ThemeUtil.setCurrentThemeId(request, themeId);
            ThemeUtil.applyTheme(context, request);
        }
    }
}
