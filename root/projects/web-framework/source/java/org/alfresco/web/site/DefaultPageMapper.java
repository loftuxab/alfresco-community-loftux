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

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.Page;

/**
 * @author muzquiano
 */
public class DefaultPageMapper extends PageMapper
{
    protected DefaultPageMapper()
    {
        super();
    }

    public void execute(RequestContext context, HttpServletRequest request)
    {
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
            Page rootPage = ModelUtil.getRootPage(context);
            if (rootPage != null)
                pageId = rootPage.getId();
        }
        if (pageId != null)
        {
            Page _page = context.getModelManager().loadPage(context, pageId);
            if (_page != null)
                context.setCurrentPage(_page);
        }

        // object id
        String objectId = (String) request.getParameter("o");
        if (objectId != null && !"".equals(objectId))
        {
            context.setCurrentObjectId(objectId);
        }

        // check to make sure we have a site configuration
        Configuration siteConfiguration = ModelUtil.getSiteConfiguration(context);
        if (siteConfiguration == null)
        {
            // if we don't, then lets null everything
            // this forces the framework to "restart"
            context.setCurrentPage(null);
            context.setCurrentObjectId(null);
            context.setCurrentFormatId(null);
        }

    }
}
