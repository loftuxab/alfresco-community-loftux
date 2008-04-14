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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author muzquiano
 */
public class RequestUtil
{

    public static RequestContext getRequestContext(HttpServletRequest request)
    {
        RequestContext context = (RequestContext) request.getAttribute(ATTR_REQUEST_CONTEXT);
        return context;
    }

    public static void setRequestContext(HttpServletRequest request,
            RequestContext context)
    {
        request.setAttribute(ATTR_REQUEST_CONTEXT, context);
    }

    /**
     * Used to include rendition of a piece of content (xform-driven)
     * @param request
     * @param response
     * @param relativePath
     * @throws ServletException
     */
    public static void includeRendition(HttpServletRequest request,
            HttpServletResponse response, String renditionRelativePath,
            String originalRelativePath) throws ServletException
    {
        RequestContext context = getRequestContext(request);

        // load the html
        String unprocessedHtml = ModelUtil.getFileStringContents(
                context, renditionRelativePath);

        // process the tags in the html
        // this executes and commits to the writer
        try
        {
            String content = FilterUtil.filterContent(context, request, response,
                    unprocessedHtml, originalRelativePath);
            response.getWriter().write(content);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    /**
     * Performs an HTML include and processes tags
     * @param request
     * @param response
     * @param renditionRelativePath
     * @throws ServletException
     */
    public static void includeHTML(HttpServletRequest request,
            HttpServletResponse response, String renditionRelativePath)
            throws ServletException
    {
        RequestContext context = getRequestContext(request);

        // load the html
        String unprocessedHtml = ModelUtil.getFileStringContents(
                context, renditionRelativePath);

        try
        {
            // process the tags in the html
            // this executes and commits to the writer
            String content = FilterUtil.filterContent(context, request, response,
                    unprocessedHtml, renditionRelativePath);
            response.getWriter().write(content);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    public static void include(HttpServletRequest request,
            HttpServletResponse response, String dispatchPath)
            throws ServletException
    {
        try
        {
            // do the include
            request.getRequestDispatcher(dispatchPath).include(request,
                    response);
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
            // do the forward
            request.getRequestDispatcher(dispatchPath).forward(request,
                    response);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    public static String ATTR_REQUEST_CONTEXT = "requestContext";
}
