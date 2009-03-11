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
import java.io.UnsupportedEncodingException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.tools.FakeHttpServletResponse;
import org.alfresco.tools.WrappedHttpServletRequest;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.exception.RequestContextException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Retrieves content on behalf of the current user from the web project
 * or avm store inside of the repository.
 * 
 * The following URL formats are supported:
 * 
 *    /v/<path>?e=<endpointId>&s=<storeId>&<webappId>
 *    /v/<path>?e=<endpointId>&s=<storeId>
 *    /v/<path>?e=<endpointId>
 *    /v/<path>?s=<storeId>    
 *    /v/<path>
 *
 * In the latter cases, the web framework's selected store and webapp
 * are determined from session and reused.  This enables the case for
 * resources to be loaded as though they were local to disk (and located
 * in the /v directory). 
 *   
 * @author muzquiano
 */
public class VirtualizedContentRetrievalServlet extends BaseServlet
{
    private static Log logger = LogFactory.getLog(VirtualizedContentRetrievalServlet.class);
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // get the request context
        RequestContext context = null;
        try 
        {
            context = RequestUtil.getRequestContext(request);
        }
        catch (RequestContextException rce)
        {
            throw new ServletException("Unable to retrieve request context from request", rce);
        }

        String uri = request.getRequestURI();
        
        // trim the uri to remove the request context path
        uri = uri.substring(request.getContextPath().length());
        
        // remove the servlet path
        if (request.getServletPath() != null && request.getServletPath().length() > 0)
        {
            uri = uri.substring(request.getServletPath().length());
        }
        
        // what remains is the path to the file
        String path = uri;
        
        // endpoint id
        String endpointId = (String) request.getParameter("e");
        
        // store id
        String storeId = (String) request.getParameter("s");
        
        // webapp id
        String webappId = (String) request.getParameter("w");

        // process retrieval
        retrieve(context, request, response, path, endpointId, storeId, webappId, true);
    }

    /**
     * Performs a virtualized content retrieval and returns the result as a String
     * 
     * @param context the original request context
     * @param path the path to be included
     * @param endpointId the endpoint to utilize (optional)
     * @param storeId the store to utilize (optional)
     * @param webappId the webapp to utilize (optional)
     * 
     * @return the result as a string
     */
    public static String retrieveAsString(RequestContext context, String path, String endpointId, String storeId, String webappId)
        throws UnsupportedEncodingException, IOException, ServletException
    {
        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(context.getRequest());
// HACK FOR MERGE: Fill in the null!
//        FakeHttpServletResponse fakeResponse = new FakeHttpServletResponse(false);
        FakeHttpServletResponse fakeResponse = new FakeHttpServletResponse(null/*HACK*/, false);

        // process retrieval        
        retrieve(context, wrappedRequest, fakeResponse, path, endpointId, storeId, webappId, false);
        
        return fakeResponse.getContentAsString();
    }
    
    /**
     * Performs a virtualize content retrieval using the given dispatch objects
     * 
     * @param context the original request context
     * @param request http servlet request
     * @param response http servlet response
     * @param path the path to be included
     * @param endpointId the endpoint to utilize (optional)
     * @param storeId the store to utilize (optional)
     * @param webappId the webapp to utilize (optional)
     * @param forward whether to process as a forward rather than an include
     */
    public static void retrieve(RequestContext context, HttpServletRequest request, HttpServletResponse response, String path, String endpointId, String storeId, String webappId, boolean forward)
        throws ServletException, IOException
    {
        // use default endpoint if one not provided
        if (endpointId == null)
        {
            endpointId = FrameworkHelper.getRemoteConfig().getDefaultEndpointId();
        }
        
        // use default store if one not provided
        if (storeId == null)
        {
            storeId = (String) context.getValue(WebFrameworkConstants.STORE_ID_REQUEST_CONTEXT_NAME);
        }
        
        // use default webapp if one not provided
        if (webappId == null)
        {
            webappId = (String) context.getValue(WebFrameworkConstants.WEBAPP_ID_REQUEST_CONTEXT_NAME);
        }
        
        // build the uri to the proxy servlet
        StringBuilder fb = new StringBuilder(128);
        fb.append("/proxy/");
        fb.append(endpointId);
        fb.append("/avmstore/get/s/");
        fb.append(storeId);
        fb.append("/w/");
        fb.append(webappId);
        
        if (!path.startsWith("/"))
        {
            fb.append("/");
        }
        fb.append(path);
        
        String newUri = fb.toString();
        
        if (logger.isDebugEnabled())
            logger.debug("Formed virtual retrieval path: " + newUri);
        
        // make sure the request uri is properly established so that we can
        // flow through the proxy servlet
        if (request instanceof WrappedHttpServletRequest)
        {
            ((WrappedHttpServletRequest)request).setRequestURI(request.getContextPath() + newUri);
        }
        
        // forward now to the proxy servlet        
        RequestDispatcher dispatcher = context.getRequest().getRequestDispatcher(newUri);
        
        if (forward)
        {
            dispatcher.forward(request, response);
        }
        else
        {
            dispatcher.include(request, response);
        }
    }
}
