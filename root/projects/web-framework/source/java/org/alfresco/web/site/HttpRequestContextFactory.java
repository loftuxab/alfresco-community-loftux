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
import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.filesystem.FileSystemManager;
import org.alfresco.web.site.filesystem.IFileSystem;

/**
 * Produces HttpRequestContext objects that have access to the servlet context
 * file system.  These use the standard &f=formatId&o=objectId object linking
 * behavior that is out of the box with ADS.
 *  
 * @author muzquiano
 */
public class HttpRequestContextFactory extends RequestContextFactory
{
    public RequestContext newInstance() throws Exception
    {
        throw new Exception(
                "Unable to instantiate HttpRequestContext without request");
    }

    public HttpRequestContext newInstance(HttpServletRequest request)
            throws Exception
    {
        HttpRequestContext context = new HttpRequestContext(request);
        
        // load the user onto the context
        // TODO: Make User Factory pluggable
        UserFactory userFactory = new AlfrescoUserFactory();
        User user = userFactory.getUser(context, request);
        context.setUser(user);

        // load properties from the request context properties block
        // the store to run against (standalone mode)
        initStoreId(context, request);

        // initialize the file system
        String rootPath = context.getConfig().getFileSystemRootPath("local");
        initFileSystem(context, request, rootPath);

        // populate the request context
        PageMapper pageMapper = PageMapperFactory.newInstance(context);
        pageMapper.execute(context, request);

        return context;
    }

    public void initFileSystem(RequestContext context,
            HttpServletRequest request, String rootPath)
    {
        ServletContext servletContext = request.getSession().getServletContext();
        String realPath = servletContext.getRealPath(rootPath);
        
        // System.out.println("INITFILESYSTEM rootPath: " + rootPath);        
        // System.out.println("INITFILESYSTEM: " + realPath);
        
        //System.out.println("GETFILESYSTEM.realPath = " + realPath);
        // D:\tomcat-test\webapps\green

        //System.out.println("LocalFileSystem");
        //System.out.println(" -> rootPath: " + rootPath);
        //System.out.println(" -> realPath: " + realPath);

        // the file system manager takes care to make sure that if this
        // file system has already been loaded, it will be reused
        File dir = new File(realPath);
        IFileSystem fileSystem = FileSystemManager.getLocalFileSystem(dir);
        context.setFileSystem(fileSystem);
    }

    /**
     * When using the HttpRequestContext servlet, we're going directly against
     * the disk and there is NO virtual server / no virtualization.
     * 
     * Thus, we have to be explicitly told ahead of time which store we're
     * acting against.
     * 
     * This is configured via the properties bundle.
     * 
     * @param context
     * @param request
     */
    public void initStoreId(RequestContext context, HttpServletRequest request)
    {
        String defId = context.getConfig().getDefaultRequestContextId();
        String storeId = context.getConfig().getRequestContextSetting(defId,
                "store");
        context.setStoreId(storeId);
    }

}
