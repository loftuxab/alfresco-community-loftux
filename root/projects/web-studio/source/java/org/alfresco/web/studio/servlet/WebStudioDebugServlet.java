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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.studio.servlet;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.servlet.BaseServlet;
import org.alfresco.web.studio.OverlayUtil;
import org.alfresco.web.studio.WebStudioUtil;

/**
 * Helpful servlet that lets you switch various request-side features
 * on for the current Web Studio session.
 * 
 * Generally, these are invoked by the web studio client but they
 * could also be invoked by hand.
 * 
 * The general format of incoming requests will be:
 *  - /studio/debug/<command>/<value>
 * 
 * The following is supported:
 * 
 * /studio/debug/overlay/enable /studio/debug/overlay/disable
 * /studio/debug/refresh
 * 
 * @author muzquiano
 */
public class WebStudioDebugServlet extends BaseServlet
{
    public void init() throws ServletException
    {
        super.init();
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        String uri = request.getRequestURI();

        // skip server context path and build the path to the resource
        // we are looking for
        uri = uri.substring(request.getContextPath().length());

        // validate and return the resource path - stripping the
        // servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }
        String command = (String) t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }
        String value = (String) t.nextToken();

        if (command != null)
        {
            if (command.equals("overlay"))
            {
                if ("enable".equals(value))
                {
                    // enable the overlays
                    WebStudioUtil.setOverlayEnabled(request, true);
                }

                if ("disable".equals(value))
                {
                    // disable the overlays
                    WebStudioUtil.setOverlayEnabled(request, false);
                }
            }

            if (command.equals("refresh"))
            {
                // refresh the js and css cache
                OverlayUtil.removeCachedResources(request, "JS_");
                OverlayUtil.removeCachedResources(request, "CSS_");
            }
        }
    }
}
