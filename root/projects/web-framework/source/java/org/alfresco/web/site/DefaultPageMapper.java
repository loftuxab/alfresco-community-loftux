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

import javax.servlet.ServletRequest;

import org.alfresco.web.site.exception.PageMapperException;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.Theme;

/**
 * The default page mapper instance.
 * 
 * An out-of-the-box page mapper instance which can either be used straight
 * away or can be looked upon as a reference for how to build your own.
 * 
 * This provides a very simple, request parameter based way of identifying
 * pages, looking them up and mapping them into the request context.
 * 
 * @author muzquiano
 */
public class DefaultPageMapper extends AbstractPageMapper
{
    /**
     * Empty constructor - for instantiation via reflection 
     */
    public DefaultPageMapper()
    {
        super();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.AbstractPageMapper#execute(org.alfresco.web.site.RequestContext, javax.servlet.ServletRequest)
     */
    public void execute(RequestContext context, ServletRequest request)
        throws PageMapperException
    {
        /**
         * For this default instance, we assume that requests arrive
         * via servlet invocation and that all elements to be mapped into
         * the request context are described by request parameters.
         * 
         * The following request parameter keys are defined:
         * 
         * f    The format id
         * p    The page id
         * o    The object id
         * pt   The page type id
         * 
         * Thus, requests may arrive in the following example patterns:
         * 
         * A simple request for a page:
         *      ?p=<pageId>
         * 
         * A request for a page in a given format:
         *      ?p=<pageId>&f=<formatId>
         *      
         * A request for an object:
         *      ?o=<objectId>
         * 
         * A request for a page with an object bound into context:
         *      ?p=<pageId>&o=<objectId>
         *      
         * A request for a page of a specific type:
         *      ?pt=<pageTypeId>
         *      
         * A request for a page of a specific type, with format and object binding:
         *      ?pt=<pageTypeId>&f=<formatId>&o=<objectId>
         *      
         * For an empty request, a best effort is made to determine the root
         * page for the site.
         * 
         */

        /**
         * Extract the format id and set onto the request context
         */
        String formatId = (String) request.getParameter("f");
        if (formatId == null || formatId.length() == 0)
        {
            formatId = context.getConfig().getDefaultFormatId();
        }
        if (formatId != null)
        {
            context.setCurrentFormatId(formatId);
        }

        /**
         * Extract the object id and set onto the request context
         */
        String objectId = (String) request.getParameter("o");
        if (objectId != null && objectId.length() != 0)
        {
            context.setCurrentObjectId(objectId);
        }

        /**
         * Extract the page type id and determine which page to bind
         * into the request context.
         * 
         * This checks to see if a theme is defined and whether said theme
         * describes an override for this page type.
         * 
         * Otherwise, a check is made to see if a system default page has
         * been specified for this page type.
         * 
         * Finally, if nothing can be determined, a generic page is
         * bound into the request context.
         */
        String pageTypeId = (String) request.getParameter("pt");
        if(pageTypeId != null && pageTypeId.length() != 0)
        {
            String pageId = null;

            /**
             * Consider the theme
             */
            String themeId = (String) context.getThemeId();
            Theme theme = context.getModel().loadTheme(context, themeId);
            if(theme != null)
            {
                pageId = theme.getDefaultPageId(pageTypeId);
            }
            
            /**
             * Consider whether a system default has been set up
             */
            if(pageId == null)
            {
                pageId = (String) getDefaultPageId(context, pageTypeId);
            }
            
            /**
             * Worst case, pick a generic page
             */           
            if(pageId == null)
            {
                pageId = (String) getDefaultPageId(context, WebFrameworkConstants.GENERIC_PAGE_TYPE_DEFAULT_PAGE_ID);
            }
            
            /**
             * Bind the page into the context
             */
            if(pageId != null)
            {
                Page page = context.getModel().loadPage(context, pageId);
                if(page != null)
                {
                    context.setCurrentPage(page);
                }
            }
        }
        
        /**
         * Extract the page id and set it onto the context
         */
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
