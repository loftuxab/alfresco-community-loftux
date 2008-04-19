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

import org.alfresco.web.site.Framework;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.PresentationUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.Timer;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.TemplateInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author muzquiano
 */
public class DispatcherServlet extends BaseServlet
{
    public void init() throws ServletException
    {
        super.init();
        
        // make sure the default framework is loaded
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        FrameworkHelper.initFramework(getServletContext(), context);
    }
    
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        // bind a timer for reporting of dispatches
        Timer.bindTimer(request);
        Timer.start(request, "service");
        
        setNoCacheHeaders(response);
        
        // initialize the request context
        RequestContext context = null;
        try
        {
            Timer.start(request, "initRequestContext");
            FrameworkHelper.initRequestContext(request);
            context = RequestUtil.getRequestContext(request);
            Timer.stop(request, "initRequestContext");
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
        
        // stamp any theme information onto the request
        ThemeUtil.applyTheme(context, request);

        // dispatch
        try
        {
            Timer.start(request, "dispatch");
            dispatch(context, request, response);
            Timer.stop(request, "dispatch");
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
        
        // stop the service timer
        Timer.stop(request, "service");
        
        // print out any timing information (if enabled)
        Timer.reportAll(request);
        Timer.unbindTimer(request);
    }

    protected void dispatch(RequestContext context, HttpServletRequest request,
            HttpServletResponse response)
    {
        // we are either navigating to a NODE
        // or to a CONTENT OBJECT (xform object)
        //
        String currentFormatId = context.getCurrentFormatId();
        String currentObjectId = context.getCurrentObjectId();
        String currentPageId = context.getCurrentPageId();
        Page currentPage = context.getCurrentPage();
        
        debug(context, "Current Page ID: " + currentPageId);
        debug(context, "Current Format ID: " + currentFormatId);
        debug(context, "Current Object ID: " + currentObjectId);
        
        // reset case - if the site config is not available, assume
        // the entire site is not available
        boolean siteReset = false;
        if(context.getSiteConfiguration() == null)
        {
            currentPage = null;
            currentPageId = null;
            currentObjectId = null;
            siteReset = true;
        }

        // if we have absolutely nothing to dispatch to, then check to
        // see if there is a root-page declared to which we can go
        if (currentPage == null && currentObjectId == null && !siteReset)
        {
            // check if a root page exists to which we can forward
            Page rootPage = ModelUtil.getRootPage(context);
            if (rootPage != null)
            {
                debug(context, "Set root page as current page");
                currentPage = rootPage;
                currentPageId = currentPage.getId();
                context.setCurrentPage(rootPage);
            }            
        }
        
        // if at this point there really is nothing to view...
        if (currentPage == null && currentObjectId == null)
        {
            debug(context, "No Page or Object determined");
            
            // go to getting started page
            String gettingStartedPageUri = context.getConfig().getPresentationPageURI(WebFrameworkConstants.PRESENTATION_PAGE_GETTING_STARTED);
            debug(context, "Dispatching to Getting Started: " + gettingStartedPageUri);
            dispatchJsp(context, request, response, gettingStartedPageUri);
        }
        else
        {
            // we know we're dispatching to something...
            
            // if we have a page specified, then we'll go there
            if(currentPageId != null)
            {
                debug(context, "Dispatching to Page: " + currentPageId);
                
                // if there happens to be a content item specified as well, it will just become part of the context
                // in other words, the content item doesn't determine the
                // destination page if the destination page is specified
                
                // we're dispatching to the current page
                dispatchCurrentPage(context, request, response, currentFormatId);                
            }
            else
            {
                // otherwise, a page wasn't specified and a content item was
                debug(context, "Dispatching to Content Object: " + currentObjectId);
                
                dispatchContent(context, request, response, currentObjectId,
                        currentFormatId);
            }
        }
    }

    protected void dispatchJsp(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String dispatchPage)
    {
        PresentationUtil.renderJspPage(context, request, response, dispatchPage);
    }

    protected void dispatchContent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String contentId, String formatId)
    {
        // TODO
        // Load the Content Item into a Content wrapper
        // This should go through a ContentFactory pattern
        // Objects that are XML serialized on disk are trivial
        // Objects that must be remoted should go through a caching layer
        // and then be present in the wrapped object
        //
        // An example - loading an object: workspace://SpacesStore/ABCDEF
        // content.getId();
        // content.getSource();
        // content.getMetadata();
        //

        // TODO
        String sourceId = "content type id";
        debug(context, "Content - Object Source Id: " + sourceId); 

        // Once we determine the "sourceId", we can do the following

        // get the content-template association
        ContentAssociation[] associations = ModelUtil.findContentAssociations(
                context, sourceId, null, null, formatId);
        if (associations != null && associations.length > 0)
        {
            Page page = associations[0].getPage(context);
            if (page != null)
            {
                debug(context, "Content - Dispatching to Page: " + page.getId());
                
                // dispatch to content page
                context.setCurrentPage(page);
                dispatchCurrentPage(context, request, response, formatId);
            }
        }
    }

    protected void dispatchCurrentPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String formatId)
    {
        Page page = context.getCurrentPage();
        debug(context, "Template ID: " +page.getTemplateId()); 
        TemplateInstance currentTemplate = page.getTemplate(context);
        if (currentTemplate != null)
        {
            debug(context, "Rendering Page with template: " + currentTemplate.getId()); 
            PresentationUtil.renderPage(context, request, response);
        }
        else
        {
            debug(context, "Unable to render Page - template was not found");
            
            // go to unconfigured page display
            String dispatchPage = context.getConfig().getPresentationPageURI(WebFrameworkConstants.PRESENTATION_PAGE_UNCONFIGURED);
            if (dispatchPage == null || "".equals(dispatchPage))
                dispatchPage = "/ui/core/page-unconfigured.jsp";

            // dispatch
            debug(context, "Rendering Unconfigured Page: " + dispatchPage);
            PresentationUtil.renderJspPage(context, request, response,
                    dispatchPage);
        }
    }
    
    protected static void debug(RequestContext context, String value)
    {
        Framework.getLogger().debug("[" + context.getId() + "] " + value);
    }
}
