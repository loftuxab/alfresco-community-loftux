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

import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.tools.DataUtil;
import org.alfresco.web.site.exception.RequestContextException;
import org.alfresco.web.site.filesystem.IFile;

/**
 * The Class HTMLUtil.
 */
public class HTMLUtil
{
    
    /**
     * Used to include rendition of a piece of content (xform-driven).
     * 
     * @param request the request
     * @param response the response
     * @param renditionRelativePath the rendition relative path
     * @param originalRelativePath the original relative path
     * 
     * @throws ServletException the servlet exception
     */
    public static void includeRendition(HttpServletRequest request,
            HttpServletResponse response, String renditionRelativePath,
            String originalRelativePath) throws ServletException
    {
        RequestContext context = null;
        try
        {
        	context = RequestUtil.getRequestContext(request);
        }
        catch(RequestContextException rce)
        {
        	throw new ServletException("Unable to locate request context in the request", rce);
        }

        // process the tags in the html
        // this executes and commits to the writer
        try
        {
            // load the html
            IFile file = context.getFileSystem().getFile(renditionRelativePath);        
            InputStream input = file.getInputStream();
            String unprocessedHtml = DataUtil.copyToString(input, true);

            // process the html
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
     * Performs an HTML include and processes tags.
     * 
     * @param request the request
     * @param response the response
     * @param renditionRelativePath the rendition relative path
     * 
     * @throws ServletException the servlet exception
     */
    public static void includeHTML(HttpServletRequest request,
            HttpServletResponse response, String renditionRelativePath)
            throws ServletException
    {
        RequestContext context = null;
        try
        {
        	context = RequestUtil.getRequestContext(request);
        }
        catch(RequestContextException rce)
        {
        	throw new ServletException("Unable to locate request context in the request", rce);
        }

        try
        {
            // load the html
            IFile file = context.getFileSystem().getFile(renditionRelativePath);        
            InputStream input = file.getInputStream();
            String unprocessedHtml = DataUtil.copyToString(input, true);
            
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
	
}