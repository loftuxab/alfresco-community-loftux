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
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.CacheUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * A general control servlet for administering and controlling
 * the application framework.
 * 
 * Services include:
 * 
 * Cache invalidation:
 * 
 *   /<application>/control/cache/invalidate
 *     -> invalidates cached data for the current session
 *   
 * @author muzquiano
 */
public class FrameworkControlServlet extends BaseServlet
{
    private static final String MODE_CACHE_COMMAND_INVALIDATE = "invalidate";
    private static final String MODE_CACHE = "cache";

    public void init() throws ServletException
    {
        super.init();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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

        String uri = request.getRequestURI();
        
        // skip server context path and build the path to the resource we are looking for
        uri = uri.substring(request.getContextPath().length());
        
        // validate and return the resource path - stripping the servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }
        String mode = t.nextToken();
        if( !t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }
        String command = t.nextToken();
        
        // load additional arguments, if any
        ArrayList<String> args = new ArrayList<String>();
        if(t.hasMoreTokens())
        {
            args.add(t.nextToken());            
        }
                
        // CACHE
        if(MODE_CACHE.equalsIgnoreCase(mode))
        {
            if(MODE_CACHE_COMMAND_INVALIDATE.equalsIgnoreCase(command))
            {
                // invalidate the model service object cache
                CacheUtil.invalidateModelObjectServiceCache(context);
            }
        }
    }
}
