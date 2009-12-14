/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.connector.User;
import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.util.Base64;
import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.ContentAssociation;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.PageType;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.Theme;
import org.alfresco.web.framework.render.PresentationUtil;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.framework.render.RenderHelper;
import org.alfresco.web.framework.render.RenderUtil;
import org.alfresco.web.framework.render.bean.DefaultRenderContext;
import org.alfresco.web.framework.resource.ResourceContent;
import org.alfresco.web.site.AuthenticationUtil;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.Timer;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.exception.FrameworkInitializationException;
import org.alfresco.web.site.exception.RequestDispatchException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static Log logger = LogFactory.getLog(DispatcherServlet.class);
    
    private static final String ALF_REDIRECT_URL   = "alfRedirectUrl";
    private static final String ALF_LAST_USERNAME = "alfLastUsername";
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
        catch (FrameworkInitializationException fie)
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
        
        if (logger.isDebugEnabled())
        {
            String qs = request.getQueryString();
            logger.debug("Processing URL: ("  + request.getMethod() + ") " + request.getRequestURI() + 
                  ((qs != null && qs.length() != 0) ? ("?" + qs) : ""));
        }
        
        // apply language from browser locale setting
        setLanguageFromRequestHeader(request);
        
        // no caching for generated page data
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
        
        if (logger.isDebugEnabled())
            debug(context, "Context created for request: " + request.getRequestURI());
        
        // stamp any theme information onto the request
        ThemeUtil.applyTheme(context, request);
        
        // create the top render context
        RenderContext renderContext = RenderHelper.provideRenderContext(context, request, response);        
        
        // dispatch to render the page
        try
        {
            if (Timer.isTimerEnabled())
                Timer.start(request, "dispatch");

            // dispatch to page processing code
            dispatch(renderContext);
            
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
            
            logger.error(t);
            
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
            
            // release any resources associated with the request context
            context.release();
            
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
     * Dispatch to the page based on the given context and request.
     * 
     * @throws RequestDispatchException
     */
    protected void dispatch(RenderContext context)
        throws RequestDispatchException
    {
        String formatId = context.getFormatId();
        String objectId = context.getCurrentObjectId();
        String pageId = context.getPageId();
        Page page = context.getPage();
        
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        if (page != null)
        {
            // redirect to login based on page authentication required 
            boolean login = false;
            User user = context.getUser();
            switch (page.getAuthentication())
            {
                case guest:
                {
                    login = (user == null);
                    break;
                }
                
                case user:
                {
                    login = (user == null || AuthenticationUtil.isGuest(user.getId()));
                    break;
                }
                
                case admin:
                {
                    login = (user == null || !user.isAdmin());
                    if (login)
                    {
                        // special case for admin - need to clear user context before
                        // we can login again to "upgrade" our user authentication level
                        AuthenticationUtil.clearUserContext(request);
                    }
                    break;
                }
            }
            
            if (login)
            {
                String loginPageId = null;
                
                // Consider the theme first - which can override common page types
                String themeId = (String) context.getThemeId();
                Theme theme = context.getModel().getTheme(themeId);
                if (theme != null)
                {
                    loginPageId = theme.getPageId(PageType.PAGETYPE_LOGIN);
                }
                
                // Consider whether a system default has been set up
                if (loginPageId == null)
                {
                    loginPageId = FrameworkHelper.getConfig().getDefaultPageTypeInstanceId(PageType.PAGETYPE_LOGIN);
                }
                
                Page loginPage = null;
                if (loginPageId != null)
                {
                    loginPage = context.getModel().getPage(loginPageId);
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
                        String redirectUrl = request.getRequestURI() + 
                            (request.getQueryString() != null ? ("?" + request.getQueryString()) : "");
                        
                        // set redirect url for use on login page template
                        context.setValue(ALF_REDIRECT_URL, redirectUrl);
                        
                        // set last username if any
                        Cookie cookie = AuthenticationUtil.getUsernameCookie(request);
                        if (cookie != null)
                        {
                            try
                            {
                                context.setValue(ALF_LAST_USERNAME, new String(Base64.decode(cookie.getValue()), "UTF-8"));
                            }
                            catch (UnsupportedEncodingException e)
                            {
                                // should never happen
                            }
                        }
                        
                        // dispatch to the login page
                        context.setPage(loginPage);
                        dispatchPage(context, formatId);
                        
                        // no need to process further as we have dispatched
                        return;
                    }
                }
                
                // if we get here then no login page was found - the webapp is not configured correctly
                if (loginPageId == null || loginPage == null)
                {
                    throw new AlfrescoRuntimeException("No 'login' page type configured - but page auth required it.");
                }
            }
        }
        
        if (logger.isDebugEnabled())
        {
            debug(context, "Current Page ID: " + pageId);
            debug(context, "Current Format ID: " + formatId);
            debug(context, "Current Object ID: " + objectId);
        }
        
        // if at this point there really is nothing to view...
        if (page == null && objectId == null)
        {
            if (logger.isDebugEnabled())
                debug(context, "No Page or Object determined");
            
            // Go to the getting started page
            try
            {
                RenderUtil.renderSystemPage(context, 
                    WebFrameworkConstants.SYSTEM_PAGE_GETTING_STARTED);
            }
            catch (RendererExecutionException ree)
            {
                throw new RequestDispatchException(ree);
            }
        }
        else
        {
            // we know we're dispatching to something...
            // if we have a page specified, then we'll go there
            if (pageId != null)
            {
                if (logger.isDebugEnabled())
                    debug(context, "Dispatching to Page: " + pageId);
                
                // if there happens to be a content item specified as well,
                // it will just become part of the context
                // i.e. if the content item doesn't determine the
                // destination page if the destination page is specified
                
                // we're dispatching to the current page
                dispatchPage(context, formatId);
            }
            else
            {
                // otherwise, a page wasn't specified and a content item was
                if (logger.isDebugEnabled())
                    debug(context, "Dispatching to Content Object: " + objectId);
                
                dispatchContent(context, objectId, formatId);
            }
        }
    }

    protected void dispatchJsp(DefaultRenderContext renderContext, String dispatchPage) throws RequestDispatchException
    {
        PresentationUtil.renderJspPage(renderContext, dispatchPage);
    }

    protected void dispatchContent(RenderContext context,
            String contentId, String formatId) throws RequestDispatchException
    {
        // get the current object
        ResourceContent content = context.getCurrentObject();
        if (content == null)
        {
            throw new RequestDispatchException("Unable to dispatch to content page because current content object is null");
        }
        
        // ensure that we were able to load the content object
        if (!content.isLoaded())
        {
            // something wiped out while trying to load the content
            // we want to display a friendly page to communicate this
            try
            {
                RenderUtil.renderSystemPage(context, 
                    WebFrameworkConstants.SYSTEM_PAGE_CONTENT_NOT_LOADED);
            }
            catch (RendererExecutionException ree)
            {
                throw new RequestDispatchException(ree);
            }
        }
        else
        {        
            // otherwise, we dispatch to the associated content template
            String sourceId = content.getTypeId();
            if (logger.isDebugEnabled())
                debug(context, "Content - Object Source Id: " + sourceId);
            
            // Look up which template to use to display this content
            // this must also take into account the current format
            Map<String, ModelObject> objects = context.getModel().findContentAssociations(sourceId, null, null, null);
            if (objects.size() > 0)
            {
                ContentAssociation association = (ContentAssociation) objects.values().iterator().next();
                TemplateInstance templateInstance = association.getTemplate(context);
                if (templateInstance != null)
                {
                    if (logger.isDebugEnabled())
                        debug(context, "Content - Dispatching to Template Instance: " + templateInstance.getId());
    
                    // set the current template
                    context.setTemplate(templateInstance);
                    
                    // render content
                    PresentationUtil.renderContent(context, RenderFocus.BODY);
                }
                else
                {
                    // there was an associated content display template instance
                    // however, it appears to be missing or unloadable
                    try
                    {
                        RenderUtil.renderSystemPage(context, 
                            WebFrameworkConstants.SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING);
                    }
                    catch (RendererExecutionException ree)
                    {
                        throw new RequestDispatchException(ree);
                    }                    
                }
            }
            else
            {
                // we couldn't find a content association template
                
                // Render a friendly page to show that we could not find
                // a content association to a page
                try
                {
                    RenderUtil.renderSystemPage(context, 
                        WebFrameworkConstants.SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING);
                }
                catch (RendererExecutionException ree)
                {
                    throw new RequestDispatchException(ree);
                }                
            }
        }
    }
    
    /**
     * Dispatch to the specified page with the given option format id
     * 
     * @throws RequestDispatchException
     */
    protected void dispatchPage(
            RenderContext context,
            String formatId)
        throws RequestDispatchException
    {
        Page page = context.getPage();
        
        if (logger.isDebugEnabled())
            debug(context, "Template ID: " + page.getTemplateId());
        
        TemplateInstance currentTemplate = page.getTemplate(context);
        if (currentTemplate != null)
        {
            if (logger.isDebugEnabled())
                debug(context, "Rendering Page with template: " + currentTemplate.getId());
            
               PresentationUtil.renderPage(context, RenderFocus.BODY);
        }
        else
        {
            if (logger.isDebugEnabled())
                debug(context, "Unable to render Page - template was not found");
            
            try
            {
                RenderUtil.renderSystemPage(context, 
                    WebFrameworkConstants.SYSTEM_PAGE_UNCONFIGURED);
            }
            catch (RendererExecutionException ree)
            {
                throw new RequestDispatchException(ree);
            }
        }
    }
    
    protected static void debug(RequestContext context, String value)
    {
        logger.debug("[" + context.getId() + "] " + value);
    }
}
