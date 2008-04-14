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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.Template;

/**
 * @author muzquiano
 */
public class DispatcherServlet extends BaseServlet
{
    public void init() throws ServletException
    {
        super.init();
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        setNoCacheHeaders(response);

        // get the request context
        RequestContext context = RequestUtil.getRequestContext(request);

        // stamp any theme information onto the request
        ThemeUtil.applyTheme(context, request);

        // dispatch
        try
        {
            dispatch(context, request, response);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    protected void dispatch(RequestContext context, HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        // we are either navigating to a NODE
        // or to a CONTENT OBJECT (xform object)
        //
        String currentFormatId = context.getCurrentFormatId();
        String currentObjectId = context.getCurrentObjectId();
        Page currentPage = context.getCurrentPage();

        // initial state - what if nothing is set up
        if (currentPage == null && currentObjectId == null)
        {
            // go to GETTING STARTED
            dispatchJsp(request, response, "/ui/misc/getting-started.jsp");
        }
        else
        {
            // are we dispatching to a content object
            if (currentObjectId != null)
            {
                dispatchContent(context, request, response, currentObjectId,
                        currentFormatId);
            }
            else
            {
                // we're dispatching to the current page
                dispatchCurrentPage(context, request, response, currentFormatId);
            }
        }
    }

    protected void dispatchJsp(HttpServletRequest request,
            HttpServletResponse response, String dispatchPage)
            throws ServletException
    {
        try
        {
            RequestUtil.include(request, response, dispatchPage);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    protected void dispatchContent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String contentId, String formatId) throws Exception
    {
        // TODO
        // figure out the content type
        /*
         String relativeFilePath = contentId;
         Document doc = XMLHelper.getDocumentXML(context, relativeFilePath);
         String tagName = doc.getDocumentElement().getTagName();
         int i = tagName.indexOf(":");
         if(i > 0)
         tagName = tagName.substring(i+1, tagName.length());
         */

        // TODO
        String sourceId = "content type id";

        // get the content-template association
        ContentAssociation[] associations = ModelUtil.findContentAssociations(
                context, sourceId, null, null, formatId);
        if (associations != null && associations.length > 0)
        {
            Page page = associations[0].getPage(context);
            if (page != null)
            {
                context.setCurrentPage(page);
                dispatchCurrentPage(context, request, response, formatId);
            }
        }
    }

    protected void dispatchCurrentPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String formatId) throws Exception
    {
        Page page = context.getCurrentPage();
        Template currentTemplate = page.getTemplate(context);
        if (currentTemplate != null)
        {
            RenderUtil.renderTemplate(context, request, response,
                    currentTemplate.getId());
        }
        else
        {
            // no template, so dispatch to a "starter" page
            String dispatchPath = "/ui/misc/unconfigured-nav-node.jsp";
            RequestUtil.include(request, response, dispatchPath);
        }
    }
}
