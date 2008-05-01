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

import org.alfresco.web.site.exception.RequestContextException;

/**
 * @author muzquiano
 */
public class RequestUtil
{
    /**
     * A lightweight method for returning the request context that is bound
     * to the current request.
     * 
     * If the request context is found, it is returned.  This is very quick
     * and pays no performance penalty due to synchronized handling.
     * 
     * If the request context is not found, then the method calls into
     * the protected _getRequestContext which is synchronized and pays the
     * penalty of constructing the actual request context and binding it to
     * the request context.
     * 
     * Thus, subsequent requests are isolated from this penalty and only the
     * first player pays the penalty.
     * 
     * @param request
     * @return
     * @throws RequestContextException
     */
    public static RequestContext getRequestContext(ServletRequest request)
        throws RequestContextException
    {
        RequestContext context = (RequestContext) request.getAttribute(ATTR_REQUEST_CONTEXT);
        if(context != null)
        {
            return context;
        }
        
        return _getRequestContext(request, false);
    }
    
    /**
     * Synchronized method that checks to see if a request context already
     * exists as bound to the current request.  If not, one is created.
     * 
     * @param request
     * @param forceNew
     * @return
     * @throws RequestContextException
     */
    protected static synchronized RequestContext _getRequestContext(ServletRequest request, boolean forceNew)
        throws RequestContextException
    {
        /**
         * If we already have a request context on the request, then we
         * will simply return that.
         */
        RequestContext context = (RequestContext) request.getAttribute(ATTR_REQUEST_CONTEXT); 
        if(context != null)
        {
            return context;
        }
        
        /**
         * Create a new request context
         * 
         * This will be bound to the current request by the initRequestContext
         * method.  Thus, we can just hand it back.
         */
        context = FrameworkHelper.initRequestContext(request);

        return context;
    }

    /**
     * Binds the given request context to the given servlet request.
     * 
     * @param request
     * @param context
     */
    public static void setRequestContext(ServletRequest request,
            RequestContext context)
    {
        request.setAttribute(ATTR_REQUEST_CONTEXT, context);
    }

    /**
     * Performs a servlet include.  This is the principal means
     * for handling any kind of JSP include that occurs within the framework.
     * 
     * With this method, all dispatch path referencing is relative 
     * to the request.
     * 
     * @param request
     * @param response
     * @param dispatchPath
     * @throws ServletException
     */
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

    /**
     * Performs a servlet include.  This is the principal means
     * for handling any kind of JSP include that occurs within the framework.
     * 
     * With this method, all dispatch path referencing is relative 
     * to the servlet context.
     * 
     * @param request
     * @param response
     * @param dispatchPath
     * @throws ServletException
     */    
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

    /**
     * Performs a servlet forward.  This is the principal means
     * for handling any kind of JSP include that occurs within the framework.
     * 
     * With this method, all dispatch path referencing is relative 
     * to the request.
     * 
     * @param request
     * @param response
     * @param dispatchPath
     * @throws ServletException
     */    
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

    /**
     * Performs a servlet forward.  This is the principal means
     * for handling any kind of JSP include that occurs within the framework.
     * 
     * With this method, all dispatch path referencing is relative 
     * to the servlet context.
     * 
     * @param request
     * @param response
     * @param dispatchPath
     * @throws ServletException
     */    
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

    public static final String ATTR_REQUEST_CONTEXT = "requestContext";
}
