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
package org.alfresco.web.studio;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.tools.FakeHttpServletResponse;
import org.alfresco.tools.WrappedHttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Convenience functions for managing overlay state
 * 
 * @author muzquiano
 */
public class OverlayUtil
{
    private static Log logger = LogFactory.getLog(OverlayUtil.class);

    /**
     * Performs a wrapped include of a resource and writes results to a buffer
     * 
     * @param request
     * @param realResponse
     * @param buffer
     * @param path
     */
    public static void include(HttpServletRequest request, HttpServletResponse realResponse, StringBuilder buffer,
            String path)
    {
        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(
                request);
        FakeHttpServletResponse fakeResponse = new FakeHttpServletResponse(realResponse, 
                false);

        try
        {
            // do the include
            request.getRequestDispatcher(path).include(wrappedRequest,
                    fakeResponse);

            // write to buffer
            buffer.append(fakeResponse.getContentAsString());

            // append a line feed / carriage return
            buffer.append("\r\n");
        }
        catch (Exception ex)
        {
            logger.warn("Unable to include '" + path + "', " + ex.getMessage());
        }
    }

    /**
     * Gets a cached resource from the user session
     * 
     * @param request
     * @param key
     * @return
     */
    public static StringBuilder getCachedResource(HttpServletRequest request,
            String key)
    {
        return (StringBuilder) request.getSession().getAttribute(
                "CACHED_RESOURCE_" + key);
    }

    /**
     * Caches a resource into the user session
     * 
     * @param request
     * @param key
     * @param buffer
     */
    public static void setCachedResource(HttpServletRequest request,
            String key, StringBuilder buffer)
    {
        request.getSession().setAttribute("CACHED_RESOURCE_" + key, buffer);
    }

    /**
     * Removes a cached resource from the user session
     * 
     * @param request
     *            the request
     * @param key
     *            the key
     */
    public static void removeCachedResource(HttpServletRequest request,
            String key)
    {
        request.getSession().removeAttribute("CACHED_RESOURCE_" + key);
    }

    /**
     * Removes cached resources that whose start with the given string
     * 
     * @param request
     * @param car
     */
    public static void removeCachedResources(HttpServletRequest request,
            String key)
    {
        Enumeration en = request.getSession().getAttributeNames();
        while (en.hasMoreElements())
        {
            String attributeName = (String) en.nextElement();
            if (attributeName.startsWith("CACHED_RESOURCE_" + key))
            {
                request.getSession().removeAttribute(attributeName);
            }
        }
    }

    /**
     * Returns the host port for the web studio application
     * 
     * @return
     */
    public static String getWebStudioHostPort(HttpServletRequest request)
    {
        String url = request.getScheme();
        url += "://";
        url += request.getServerName();
        if (request.getServerPort() != 80)
        {
            url += ":" + request.getServerPort();
        }
        url += request.getContextPath();

        return url;
    }

    /**
     * Constructs a browser-friendly path to the web studio relative path
     * 
     * @param request
     * @param relativePath
     * @return
     */
    public static String getWebStudioURL(HttpServletRequest request,
            String relativePath)
    {
        return getWebStudioHostPort(request) + relativePath;
    }

    /**
     * Returns the context path of the original web application
     * 
     * @param request
     * @return
     */
    public static String getOriginalContextPath(HttpServletRequest request)
    {
        String contextPath = request.getParameter("contextPath");
        if (contextPath == null)
        {
            contextPath = "/";
        }

        return contextPath;
    }

    /**
     * Constructs a browser-friendly path to the original webapp relative path
     * 
     * @param request
     * @param relativePath
     * @return
     */
    public static String getOriginalURL(HttpServletRequest request,
            String relativePath)
    {
        return getOriginalContextPath(request) + relativePath;
    }

}
