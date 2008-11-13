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
package org.alfresco.web.site.servlet;

import java.util.StringTokenizer;

import javax.servlet.ServletException;

import org.alfresco.web.framework.render.PresentationUtil;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.site.exception.RequestDispatchException;

/**
 * Responsible for dispatching a region.
 * 
 * Constructs the request context as per usual construction pattern
 * so as to provide context during component execution.
 * 
 * URLs are expected to be invoked as shown:
 * 
 * /r/<regionId>/<scopeId>/<sourceId> 
 * 
 * Most commonly, these are:
 * 
 *   regionId         -> the id of the region (i.e. 'footer')
 *   scopeId        -> the scope of the region (i.e. 'page')
 *   templateId     -> the id of the template instance (i.e. 'home')
 *   
 * The region is executed, along with its chrome.
 * If a component is contained in the region, it is also executed.
 * 
 * @author muzquiano
 */
public class RegionDispatcherServlet extends DispatcherServlet
{
    public void init() throws ServletException
    {
        super.init();
    }

    /**
     * Dispatch region
     * 
     * @throws RequestDispatchException
     */
    protected void dispatch(RenderContext context)
        throws RequestDispatchException
    {
        String uri = context.getRequest().getRequestURI();
        
        // skip server context path and build the path to the resource we are looking for
        uri = uri.substring(context.getRequest().getContextPath().length());
        
        // validate and return the resource path - stripping the servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new RequestDispatchException("Invalid URL: " + uri);
        }
        
        // determine the region binding properties
        String regionId = t.nextToken();
        String regionScopeId = t.nextToken();
        String templateId = t.nextToken();
        
        // render the region
        PresentationUtil.renderRegion(context, RenderFocus.BODY, templateId,
                regionId, regionScopeId);
    }
}
