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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author muzquiano
 */
public class RequestUtil
{
    public static RequestContext getRequestContext(ServletRequest request)
    {
        RequestContext context = (RequestContext) request.getAttribute(ATTR_REQUEST_CONTEXT);
        if(context != null)
        {
            return context;
        }
        
        // block so only a single thread pays the price
        synchronized(RequestContext.class)
        {
            context = (RequestContext) request.getAttribute(ATTR_REQUEST_CONTEXT); 
            if(context == null)
            {
                try
                {
                    FrameworkHelper.initRequestContext(request);
                    context = (RequestContext) request.getAttribute(ATTR_REQUEST_CONTEXT);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        return context;
    }

    public static void setRequestContext(HttpServletRequest request,
            RequestContext context)
    {
        request.setAttribute(ATTR_REQUEST_CONTEXT, context);
    }

    public static void include(HttpServletRequest request,
            HttpServletResponse response, String dispatchPath)
            throws ServletException
    {
        try
        {
            request.getRequestDispatcher(dispatchPath).include(request, response);
        }
        catch (Throwable ex)
        {
            throw new ServletException(ex);
        }
    }

    public static void include(ServletContext context, ServletRequest request,
            ServletResponse response, String dispatchPath)
            throws ServletException
    {
        try
        {
            RequestDispatcher disp = context.getRequestDispatcher(dispatchPath);
            disp.include(request, response);
        }
        catch (Throwable ex)
        {
            throw new ServletException(ex);
        }
    }

    public static void forward(HttpServletRequest request,
            HttpServletResponse response, String dispatchPath)
            throws ServletException
    {
        try
        {
            request.getRequestDispatcher(dispatchPath).forward(request, response);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    public static void forward(ServletContext context, ServletRequest request,
            ServletResponse response, String dispatchPath)
            throws ServletException
    {
        try
        {
            RequestDispatcher disp = context.getRequestDispatcher(dispatchPath);
            disp.forward(request, response);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    public static String ATTR_REQUEST_CONTEXT = "requestContext";
}
