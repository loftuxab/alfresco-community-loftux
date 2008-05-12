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

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.exception.PageMapperException;
import org.alfresco.web.site.exception.RequestContextException;
import org.alfresco.web.site.exception.UserFactoryException;
import org.alfresco.web.site.filesystem.FileSystemManager;
import org.alfresco.web.site.filesystem.IFileSystem;

/**
 * Produces HttpRequestContext instances for HttpServletRequest request
 * inputs.  The HttpRequestContext type has an additional convenience
 * accessor method.
 *  
 * @author muzquiano
 */
public class HttpRequestContextFactory implements RequestContextFactory
{
    /**
     * Produces a new RequestContext instance for a given request
     * 
     * @return The RequestContext instance
     * @throws RequestContextException
     */    
    public RequestContext newInstance(ServletRequest request)
        throws RequestContextException
    {
        if(!(request instanceof HttpServletRequest))
        {
            throw new RequestContextException("HttpRequestContextFactory can only produce HttpRequestContext instances for HttpServletRequest requests");
        }

        HttpRequestContext context = null;
        
        /**
         * Load the user and place the user onto the RequestContext
         */
        try
        {
            /**
             * Construct the HttpRequestContext instance
             */
            context = new HttpRequestContext((HttpServletRequest)request);

            /**
             * Construct/load the user and place them onto the instance
             */
            UserFactory userFactory = UserFactoryBuilder.newFactory();
            if(userFactory != null)
            {
                User user = userFactory.getUser(context, (HttpServletRequest)request);
                context.setUser(user);
            }
            
            /**
             * Determine the store id and set it onto the request context
             */
            initStoreId(context);
            
            /**
             * Initialize the file system
             */
            String rootPath = context.getConfig().getFileSystemDescriptor("local").getRootPath();
            initFileSystem(context, (HttpServletRequest)request, rootPath);
            
            /**
             * Execute the configured page mapper
             * 
             * This will populate request context with information about
             * how to render, based on the incoming URL
             */
            PageMapper pageMapper = PageMapperFactory.newInstance(context);
            pageMapper.execute(context, (HttpServletRequest)request);
        }
        catch(UserFactoryException ufe)
        {
            throw new RequestContextException("Exception running UserFactory in HttpRequestContextFactory", ufe);
        }
        catch(PageMapperException pme)
        {
            throw new RequestContextException("Exception running PageMapper in HttpRequestContextFactory", pme);
        }
        
        return context;
    }

    /**
     * Creates a FileSystem that points to the local web application root.
     * 
     * @param context
     * @param request
     * @param rootPath
     */
    public void initFileSystem(RequestContext context,
            HttpServletRequest request, String rootPath)
    {
        ServletContext servletContext = request.getSession().getServletContext();
        String realPath = servletContext.getRealPath(rootPath);

        // the file system manager takes care to make sure that if this
        // file system has already been loaded, it will be reused
        File dir = new File(realPath);
        IFileSystem fileSystem = FileSystemManager.getLocalFileSystem(dir);
        context.setFileSystem(fileSystem);
    }

    /**
     * Sets the AVM store id onto the request.
     * 
     * This method purely copies the default value as described in the
     * configuration into the request context.
     * 
     * @param context The request context instance
     * @param request
     */
    public void initStoreId(RequestContext context)
    {
        String defId = context.getConfig().getDefaultFileSystemId();
        String storeId = context.getConfig().getFileSystemDescriptor(defId).getStore();
        context.setStoreId(storeId);
    }
}
