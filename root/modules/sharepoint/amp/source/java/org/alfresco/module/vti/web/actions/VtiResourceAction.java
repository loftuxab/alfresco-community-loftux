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
package org.alfresco.module.vti.web.actions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiResourceAction is used for retrieving specific resource 
* for web-view (like images, css).</p>
*
*/
public class VtiResourceAction extends HttpServlet implements VtiAction
{

    private static final long serialVersionUID = 9073113240345164795L;

    private static Map<String, byte[]> resourcesMap = new HashMap<String, byte[]>();

    private static final ReadWriteLock resourceMapLock = new ReentrantReadWriteLock();

    private final static Log logger = LogFactory.getLog(VtiResourceAction.class);

    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public VtiResourceAction()
    {
        super();
    }

    /**
     * <p>Retrieve specific resource for web-view.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String alfrescoContext = (String) request.getAttribute(VtiRequestDispatcher.VTI_ALFRESCO_CONTEXT);
        String uri = request.getRequestURI().replaceAll(alfrescoContext + "/resources", "");
        uri = uri.replaceAll("/resources", "");
        String resourceLocation = "../.." + uri;
        writeResponse(resourceLocation, response, alfrescoContext);

    }

    private void writeResponse(String resourceLocation, HttpServletResponse response, String alfrescoContext) throws IOException
    {
        byte[] resource = null;

        try
        {
            resourceMapLock.readLock().lock();
            resource = resourcesMap.get(resourceLocation);
        }
        finally
        {
            resourceMapLock.readLock().unlock();
        }

        if (resource == null)
        {
            resource = cacheResource(resourceLocation, alfrescoContext);
        }

        response.getOutputStream().write(resource);
    }

    private byte[] cacheResource(String resourceLocation, String alfrescoContext) throws IOException
    {
        InputStream input = new FileInputStream(this.getClass().getClassLoader().getResource("").getPath() + resourceLocation);

        byte[] result = new byte[input.available()];
        input.read(result);

        try
        {
            resourceMapLock.writeLock().lock();
            resourcesMap.put(resourceLocation, result);
        }
        finally
        {
            resourceMapLock.writeLock().unlock();
        }

        return result;
    }
    
    /**
     * <p>Retrieve specific resource for web-view.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            service(request, response);
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action IO exception", e);
            }
        }
        catch (ServletException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action execution exception", e);
            }
        }
    }
}