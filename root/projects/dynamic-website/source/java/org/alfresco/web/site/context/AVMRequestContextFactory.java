/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.context;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.jndi.AVMFileDirContext;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.HttpRequestContextFactory;
import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.filesystem.AVMFileSystemManager;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.Page;

/**
 * This produces an instance of HttpRequestContext that has an AVM powered file
 * system behind it. This is useful within the virtual server.
 * 
 * @author Uzquiano
 * 
 */
public class AVMRequestContextFactory extends HttpRequestContextFactory
{
    public HttpRequestContext newInstance(HttpServletRequest request)
    {
        HttpRequestContext context = new HttpRequestContext(request);

        // pre init
        initStoreId(context, request);
        initFileSystem(context, request); // this requires the store id to be
                                            // set

        // bootstrap the ADS Configuration object to this filesystem
        if (!context.getConfig().isInitialized())
        {
            System.out.println("- Initializing ADSConfiguration to AVM FileSystem");
            context.getConfig().reset(context);
        }

        // init
        // requests come in the form
        // ?f=formatId&n=nodeId
        // ?f=formatId&n=nodeId&o=objectId
        // ?n=nodeId
        // ?n=nodeId&o=objectId
        // ? (node id assumed to be site root node)

        // format id
        String formatId = (String) request.getParameter("f");
        if (formatId == null || "".equals(formatId))
            formatId = context.getConfig().getDefaultFormatId();
        if (formatId != null)
            context.setCurrentFormatId(formatId);

        // page id
        String pageId = (String) request.getParameter("p");
        if (pageId == null || "".equals(pageId))
        {
            // no page was provided, so load the root page
            Page[] rootPages = ModelUtil.findPages(context, null, "true", null);
            if (rootPages != null && rootPages.length > 0)
                pageId = rootPages[0].getId();
        }
        if (pageId != null)
        {
            Page _page = context.getModel().loadPage(context, pageId);
            if (_page != null)
                context.setCurrentPage(_page);

        }

        // object id
        String objectId = (String) request.getParameter("o");
        if (objectId != null && !"".equals(objectId))
        {
            context.setCurrentObjectId(objectId);
        }

        // post-init

        return context;
    }

    public void initFileSystem(RequestContext context,
            HttpServletRequest request)
    {
        // set up avm file system
        String avmStoreId = context.getStoreId();
        AVMRemote avmRemote = AVMFileDirContext.getAVMRemote();
        String avmWebappPath = "/www/avm_webapps/ROOT";

        // the file system manager takes care to make sure that if this
        // file system has already been loaded, it will be reused
        IFileSystem fileSystem = AVMFileSystemManager.getAVMFileSystem(
                avmRemote, avmStoreId, avmWebappPath);
        context.setFileSystem(fileSystem);
    }

    /**
     * When using the AVMRequestContextFactory, all of our file access occurs
     * through the AVMRemote interface. Thus, we're running entirely
     * virtualized. We can work with multiple stores at the same time.
     * 
     * Thus, we can find out from the AVMFileDirContext which store we're
     * currently running against. There are many ways to do this.
     * 
     * This is kind of a quick and dirty way. We can figure out the "real path"
     * to the root (which will be the web application root).
     * 
     * This will map to a drive and we can pick the store off the path.
     */
    public void initStoreId(RequestContext context, HttpServletRequest request)
    {
        // TODO: A better way to do this?
        // This is pretty fast but should be a more "forward-compatible" way
        String avmStoreId = "";
        String realPath = request.getSession().getServletContext().getRealPath(
                "/");
        if (realPath != null)
        {
            // WINDOWS
            // v:\ads--admin\VERSION\v-1\DATA\www\avm_webapps\ROOT

            // LINUX
            // /media/alfresco/cifs/v/ads--admin/VERSION/v-1/DATA/www/avm_webapps/ROOT

            realPath = realPath.replace("\\", "/");
            int x = realPath.indexOf("/VERSION");
            if (x > -1)
            {
                String splinter = realPath.substring(0, x);
                int y = splinter.lastIndexOf("/");
                if (y > -1)
                {
                    avmStoreId = splinter.substring(y + 1, splinter.length());
                    // System.out.println("Initialized for store id: " +
                    // avmStoreId);
                }
            }
        }
        context.setStoreId(avmStoreId);
    }

}
