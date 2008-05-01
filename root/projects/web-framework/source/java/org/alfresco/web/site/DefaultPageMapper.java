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

import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.Theme;

/**
 * @author muzquiano
 */
public class DefaultPageMapper extends AbstractPageMapper
{
    public DefaultPageMapper()
    {
        super();
    }
    
    protected String getDefaultPageId(RequestContext context, String pageTypeId)
    {
        if(pageTypeId == null)
        {
            return null;
        }
        
        return context.getConfig().getDefaultPageTypeInstanceId(pageTypeId);
    }

    public void execute(RequestContext context, HttpServletRequest request)
    {
        // init
        // requests come in the form
        // ?f=formatId&p=pageId
        // ?f=formatId&p=pageId&o=objectId
        // ?p=pageId
        // ?p=pageId&o=objectId
        // ?pt=pageTypeId
        // ?pt=pageTypeId&f=formatId
        // ?pt=pageTypeId&f=formatId&o=objectId
        // ? (node id assumed to be site root node)

        // format id
        String formatId = (String) request.getParameter("f");
        if (formatId == null || formatId.length() == 0)
        {
            formatId = context.getConfig().getDefaultFormatId();
        }
        if (formatId != null)
        {
            context.setCurrentFormatId(formatId);
        }

        // object id
        String objectId = (String) request.getParameter("o");
        if (objectId != null && objectId.length() != 0)
        {
            context.setCurrentObjectId(objectId);
        }

        // page type id
        String pageTypeId = (String) request.getParameter("pt");
        if(pageTypeId != null && pageTypeId.length() != 0)
        {
            // the page id to which we will resolve
            String pageId = null;
            
            // see if the current theme has defined an "override"
            String themeId = (String) context.getThemeId();
            Theme theme = context.getModel().loadTheme(context, themeId);
            if(theme != null)
            {
                pageId = theme.getDefaultPageId(pageTypeId);
            }
            
            // otherwise, look up the system default
            if(pageId == null)
            {
                pageId = (String) getDefaultPageId(context, pageTypeId);
            }
            
            // if we still couldn't resolve, we can use a generic page            
            if(pageId == null)
            {
                pageId = (String) getDefaultPageId(context, WebFrameworkConstants.GENERIC_PAGE_TYPE_DEFAULT_PAGE_ID);
            }
            
            // load page into context (if we can)
            if(pageId != null)
            {
                Page page = context.getModel().loadPage(context, pageId);
                if(page != null)
                {
                    context.setCurrentPage(page);
                }
            }
        }
        
        // page id
        String pageId = (String) request.getParameter("p");
        if(pageId != null && pageId.length() != 0)
        {
            Page page = context.getModel().loadPage(context, pageId);
            if (page != null)
            {
                context.setCurrentPage(page);
            }
        }
    }
}
