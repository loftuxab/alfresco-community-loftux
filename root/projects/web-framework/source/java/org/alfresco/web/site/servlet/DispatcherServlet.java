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
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.site.Content;
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
    private static final String ALF_REDIRECT_URL = "alfRedirectUrl";
    private static final String MIMETYPE_HTML = "text/html;charset=utf-8";
    
    /**
     * One time framework init
     */
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
    
    /**
     * Service a request and dispatch to the appropriate page
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        boolean errorOccured = false;
        
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
        
        if (isDebugEnabled())
        {
            String qs = request.getQueryString();
            debug(context, "Processing URL: ("  + request.getMethod() + ") " + request.getRequestURI() + 
                  ((qs != null && qs.length() != 0) ? ("?" + qs) : ""));
        }
        
        // stamp any theme information onto the request
        ThemeUtil.applyTheme(context, request);
        
        // dispatch to render the page
        try
        {
            if (Timer.isTimerEnabled())
                Timer.start(request, "dispatch");
            
            // a quick test to ensure that the context is set up correctly
            ensureDispatchState(context, request, response);
            
            // dispatch to page processing code
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
            
            errorOccured = true;
            
            FrameworkHelper.getLogger().error(t);
            
            // for now, we will throw back to servlet container
            if (t instanceof RuntimeException)
            {
                throw (RuntimeException)t;
            }
            else
            {
                throw new ServletException("Error during dispatch: " + t.getMessage(), t);
            }
        }
        finally
        {
            // clean up - unless an error occured as then we don't want to commit the response yet
            if (!errorOccured)
            {
                response.getWriter().flush();
                response.getWriter().close();
            }
            
            // stop the service timer and print out any timing information (if enabled)
            if (Timer.isTimerEnabled())
            {
                Timer.stop(request, "service");
                Timer.reportAll(request);
                Timer.unbindTimer(request);
            }
        }
    }
    
    /**
     * Ensure the currently available context and request state are ready for dispatch.
     * This method will set the page to a system default if no valid page is requested.
     */
    protected void ensureDispatchState(
            RequestContext context, HttpServletRequest request, HttpServletResponse response)
    {
        if (isDebugEnabled())
        {
            if (context.getSiteConfiguration() == null)
            {
                debug(context, "No site configuration - performing reset");
                
                // effectively, do a reset
                context.setPage(null);
                context.setCurrentObject(null);
            }
        }
        
        // if we have absolutely nothing to dispatch to, then check to
        // see if there is a root-page declared to which we can go
        if (context.getPage() == null && context.getCurrentObjectId() == null)
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
                    
                    context.setPage(rootPage);
                }            
            }
        }
    }
    
    /**
     * Dispatch to the page based on the given context and request.
     * 
     * @throws RequestDispatchException
     */
    protected void dispatch(RequestContext context, HttpServletRequest request, HttpServletResponse response)
        throws RequestDispatchException
    {
        String formatId = context.getFormatId();
        String objectId = context.getCurrentObjectId();
        String pageId = context.getPageId();
        Page page = context.getPage();
        
        if (page != null)
        {
            // do we need to redirect to a login page type?
            switch (page.getAuthentication())
            {
                case user:
                    User user = context.getUser();
                    if ((user == null || user.getId().equals(UserFactory.USER_GUEST)))
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
                        
                        Page loginPage = null;
                        if (loginPageId != null)
                        {
                            loginPage = context.getModel().loadPage(context, loginPageId);
                            if (loginPage != null)
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
                                        context, pageId, formatId, objectId, args);

                                // set redirect url for use on login page template
                                context.setValue(ALF_REDIRECT_URL, redirectUrl);
                                
                                dispatchPage(context, request, response, loginPage, formatId);
                                return;
                            }
                        }
                        
                        if (loginPageId == null || loginPage == null)
                        {
                            throw new AlfrescoRuntimeException("No 'login' page type configured - but page auth required it.");
                        }
                    }
                    break;
                
                // TODO: support admin/guest required auth cases
            }
        }
        
        if (isDebugEnabled())
        {
            debug(context, "Current Page ID: " + pageId);
            debug(context, "Current Format ID: " + formatId);
            debug(context, "Current Object ID: " + objectId);
        }
        
        // if at this point there really is nothing to view...
        if (page == null && objectId == null)
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
            if(pageId != null)
            {
                if (isDebugEnabled())
                    debug(context, "Dispatching to Page: " + pageId);
                
                // if there happens to be a content item specified as well,
                // it will just become part of the context
                // i.e. if the content item doesn't determine the
                // destination page if the destination page is specified
                
                // we're dispatching to the current page
                dispatchPage(context, request, response, context.getPage(), formatId);
            }
            else
            {
                // otherwise, a page wasn't specified and a content item was
                if (isDebugEnabled())
                    debug(context, "Dispatching to Content Object: " + objectId);
                
                dispatchContent(context, request, response, objectId, formatId);
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
    	// get the current object
    	Content object = context.getCurrentObject();
    	if(object == null)
    	{
    		throw new RequestDispatchException("Unable to dispatch to content page with null current object");
    	}
    	
    	// if the current object is not loaded, we should dispatch to a
    	// generic system page handler
    	if(!object.isLoaded())
    	{
    		// something wiped out while trying to load the content
    		// we want to display a friendly page to communicate this
            RenderUtil.renderSystemPage(context, request, response, 
                    WebFrameworkConstants.SYSTEM_PAGE_CONTENT_NOT_LOADED,
                    WebFrameworkConstants.DEFAULT_SYSTEM_PAGE_CONTENT_NOT_LOADED);
    	}
    	else
    	{    	
    		// otherwise, we dispatch to the associated content page
	    	String sourceId = object.getTypeId();
	    	if (isDebugEnabled())
	            debug(context, "Content - Object Source Id: " + sourceId);
	    	
	    	// Look up which page to use to display this contnet
	    	// this must also take into account the current format
	        ContentAssociation[] associations = ModelUtil.findContentAssociations(
	                context, sourceId, null, null, null);
	        if (associations != null && associations.length > 0)
	        {
	        	Page page = associations[0].getPage(context);
	            if (page != null)
	            {
	                if (isDebugEnabled())
	                    debug(context, "Content - Dispatching to Page: " + page.getId());
	
	                // dispatch to content page
	                context.setPage(page);
	                dispatchPage(context, request, response, context.getPage(), formatId);
	            }
	        }
	        else
	        {
	        	// we couldn't find a content association page
	        	// we should dispatch to a generic system page
	        	
	            // Render a friendly page to show that we could not find
	        	// a content association to a page
	            RenderUtil.renderSystemPage(context, request, response, 
	                    WebFrameworkConstants.SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING,
	                    WebFrameworkConstants.DEFAULT_SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING);
	        	
	        }
        }
    }
    
    /**
     * Dispatch to the specified page with the given option format id
     * 
     * @throws RequestDispatchException
     */
    protected void dispatchPage(
            RequestContext context, HttpServletRequest request, HttpServletResponse response,
            Page page, String formatId)
        throws RequestDispatchException
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
