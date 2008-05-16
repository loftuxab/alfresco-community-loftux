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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.connector.User;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.PresentationUtil;
import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.Timer;
import org.alfresco.web.site.UserFactory;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.exception.FrameworkInitializationException;
import org.alfresco.web.site.exception.RequestDispatchException;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageType;
import org.alfresco.web.site.model.TemplateInstance;
import org.alfresco.web.site.model.Theme;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Central dispatching servlet for the Web Framework page rendering
 * processor.
 * 
 * The role of this servlet is to serve back fully renditioned pages
 * including all downstream templates and components, renderered
 * in their entirety and in the proper markup.
 * 
 * @author muzquiano
 * @author kroast
 */
public class DispatcherServlet extends BaseServlet
{
    private static final String MIMETYPE_HTML = "text/html;charset=utf-8";
    
    public void init() throws ServletException
    {
        super.init();
        
        // make sure the web framework is loaded        
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        try
        {
        	FrameworkHelper.initFramework(getServletContext(), context);
        }
        catch(FrameworkInitializationException fie)
        {
        	throw new ServletException("Unable to initialize the Web Framework: " + fie);
        }
    }
    
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        // bind a timer for reporting of dispatches
        if (Timer.isTimerEnabled())
        {
            Timer.bindTimer(request);
            Timer.start(request, "service");
        }
        
        setNoCacheHeaders(response);
        
        // set response content type and charset
        response.setContentType(MIMETYPE_HTML);
        
        // initialize the request context
        RequestContext context = null;
        try
        {
            context = FrameworkHelper.initRequestContext(request);
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
        
        // stamp any theme information onto the request
        ThemeUtil.applyTheme(context, request);

        // dispatch to render the page
        try
        {
            if (Timer.isTimerEnabled())
                Timer.start(request, "dispatch");
            
            dispatch(context, request, response);
            
            if (Timer.isTimerEnabled())
                Timer.stop(request, "dispatch");
        }
        catch (Throwable t)
        {
            /**
             * The framework should naturally catch exceptions and handle
             * them gracefully using the system pages defined in the
             * configuration file.  For instance, if a component fails
             * to render, it should resort to displaying itself with
             * a friendly message.
             * 
             * On the other hand, it could be the case that something
             * really nasty came this way - for instance an Error.
             * 
             * Or it may be the case that a system page was unavailable
             * to handle the error.  In that case, the exception will fall
             * back here as a RequestDispatchException.
             * 
             * It may also be the case that the system administrator
             * wishes the errors to trickle back.  This would be the
             * typical setup if the administrator wishes the servlet
             * container to handle the error.
             * 
             * Finally, they may have opted to throw a Runtime Exception
             * back.  We must handle that case as well.
             */
            
            // Either way, print stuff out to log
            FrameworkHelper.getLogger().error(t);
            
            // If it is a runtime exception, we should handle
            if (t instanceof RuntimeException)
            {
                // TODO: How?
            }
            else
            {
                // otherwise, we will throw back to servlet container
                throw new ServletException(t);
            }
        }
        finally
        {
            // clean up
            response.getWriter().flush();
            response.getWriter().close();
            
            // stop the service timer and print out any timing information (if enabled)
            if (Timer.isTimerEnabled())
            {
                Timer.stop(request, "service");
                Timer.reportAll(request);
                Timer.unbindTimer(request);
            }
        }
    }

    protected void dispatch(RequestContext context, HttpServletRequest request,
            HttpServletResponse response) throws RequestDispatchException
    {
        // a quick test to ensure that the context is set up correctly
        ensureDispatchState(context, request, response);
        
        // dispatch to page processing code
        doDispatch(context, request, response);
    }
    
    protected void ensureDispatchState(RequestContext context, HttpServletRequest request,
            HttpServletResponse response)
    {
        if (isDebugEnabled())
        {
            if (context.getSiteConfiguration() == null)
            {
                debug(context, "No site configuration - performing reset");
                
                // effectively, do a reset
                context.setCurrentPage(null);
                context.setCurrentObjectId(null);
            }
        }
        
        // if we have absolutely nothing to dispatch to, then check to
        // see if there is a root-page declared to which we can go
        if (context.getCurrentPage() == null && context.getCurrentObjectId() == null)
        {
            // if the site configuration exists...
            if (context.getSiteConfiguration() != null)
            {
                // check if a root page exists to which we can forward
                Page rootPage = ModelUtil.getRootPage(context);
                if (rootPage != null)
                {
                    if (isDebugEnabled())
                        debug(context, "Set root page as current page");
                    
                    context.setCurrentPage(rootPage);
                }            
            }
        }
    }
    
    protected void doDispatch(RequestContext context, HttpServletRequest request,
            HttpServletResponse response) throws RequestDispatchException
    {
        String currentFormatId = context.getCurrentFormatId();
        String currentObjectId = context.getCurrentObjectId();
        String currentPageId = context.getCurrentPageId();
        Page currentPage = context.getCurrentPage();
        
        if (currentPage != null)
        {
            // do we need to redirect to a login page type?
            switch (currentPage.getAuthentication())
            {
                case user:
                    User user = context.getUser();
                    if ((user == null || user.getId().equals(UserFactory.USER_GUEST)) ||
                        (user != null && context.getCredentialVault().hasCredentials(user) == false))
                    {
                        // no valid user found - login required
                        String loginPageId = null;
                        
                        // Consider the theme first - which can override common page types
                        String themeId = (String) context.getThemeId();
                        Theme theme = context.getModel().loadTheme(context, themeId);
                        if (theme != null)
                        {
                            loginPageId = theme.getPageId(PageType.PAGETYPE_LOGIN);
                        }
                        
                        // Consider whether a system default has been set up
                        if (loginPageId == null)
                        {
                            loginPageId = context.getConfig().getDefaultPageTypeInstanceId(PageType.PAGETYPE_LOGIN);
                        }
                        
                        Page page = null;
                        if (loginPageId != null)
                        {
                            page = context.getModel().loadPage(context, loginPageId);
                            if (page != null)
                            {
                                // get URL arguments as a map ready for rebuilding the request params
                                Map<String, String> args = new HashMap<String, String>(
                                        request.getParameterMap().size(), 1.0f);
                                Enumeration names = request.getParameterNames();
                                while (names.hasMoreElements())
                                {
                                    String name = (String)names.nextElement();
                                    args.put(name, request.getParameter(name));
                                }
                                // construct redirection url
                                String redirectUrl = context.getLinkBuilder().page(
                                        context, currentPageId, currentFormatId, currentObjectId, args);
                                // set redirect url for use on login page template
                                page.setCustomProperty("alfRedirectUrl", redirectUrl);
                                dispatchPage(context, request, response, page, currentFormatId);
                                return;
                            }
                        }
                        
                        if (loginPageId == null || page == null)
                        {
                            FrameworkHelper.getLogger().warn("No 'login' page type found - but page auth required it.");
                        }
                    }
                    break;
                
                // TODO: support admin/guest required auth cases
            }
        }
        
        if (isDebugEnabled())
        {
            debug(context, "Current Page ID: " + currentPageId);
            debug(context, "Current Format ID: " + currentFormatId);
            debug(context, "Current Object ID: " + currentObjectId);
        }
        
        // if at this point there really is nothing to view...
        if (currentPage == null && currentObjectId == null)
        {
            if (isDebugEnabled())
                debug(context, "No Page or Object determined");
            
            // Go to the getting started page
            RenderUtil.renderSystemPage(context, request, response, 
                    WebFrameworkConstants.SYSTEM_PAGE_GETTING_STARTED,
                    WebFrameworkConstants.DEFAULT_SYSTEM_PAGE_GETTING_STARTED);
        }
        else
        {
            // we know we're dispatching to something...
            // if we have a page specified, then we'll go there
            if(currentPageId != null)
            {
                if (isDebugEnabled())
                    debug(context, "Dispatching to Page: " + currentPageId);
                
                // if there happens to be a content item specified as well,
                // it will just become part of the context
                // i.e. if the content item doesn't determine the
                // destination page if the destination page is specified
                
                // we're dispatching to the current page
                dispatchCurrentPage(context, request, response, currentFormatId);                
            }
            else
            {
                // otherwise, a page wasn't specified and a content item was
                if (isDebugEnabled())
                    debug(context, "Dispatching to Content Object: " + currentObjectId);
                
                dispatchContent(context, request, response, currentObjectId, currentFormatId);
            }
        }
    }

    protected void dispatchJsp(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String dispatchPage) throws RequestDispatchException
    {
        PresentationUtil.renderJspPage(context, request, response, dispatchPage);
    }

    protected void dispatchContent(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String contentId, String formatId) throws RequestDispatchException
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
        if (isDebugEnabled())
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
                if (isDebugEnabled())
                    debug(context, "Content - Dispatching to Page: " + page.getId());
                
                // dispatch to content page
                context.setCurrentPage(page);
                dispatchCurrentPage(context, request, response, formatId);
            }
        }
    }

    protected void dispatchCurrentPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String formatId) throws RequestDispatchException
    {
        Page page = context.getCurrentPage();
        dispatchPage(context, request, response, page, formatId);
    }
    
    protected void dispatchPage(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            Page page, String formatId) throws RequestDispatchException
    {
        if (isDebugEnabled())
            debug(context, "Template ID: " + page.getTemplateId());
        
        TemplateInstance currentTemplate = page.getTemplate(context);
        if (currentTemplate != null)
        {
            if (isDebugEnabled())
                debug(context, "Rendering Page with template: " + currentTemplate.getId());
            
            PresentationUtil.renderPage(context, request, response, page.getId());
        }
        else
        {
            if (isDebugEnabled())
                debug(context, "Unable to render Page - template was not found");
            
            RenderUtil.renderSystemPage(context, request, response, 
                    WebFrameworkConstants.SYSTEM_PAGE_UNCONFIGURED,
                    WebFrameworkConstants.DEFAULT_SYSTEM_PAGE_UNCONFIGURED);
        }
    }
    
    protected static boolean isDebugEnabled()
    {
        return FrameworkHelper.getLogger().isDebugEnabled();
    }
    
    protected static void debug(RequestContext context, String value)
    {
        FrameworkHelper.getLogger().debug("[" + context.getId() + "] " + value);
    }
}
