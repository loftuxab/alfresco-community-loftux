/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextFactory;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.webscripts.servlet.WebScriptServlet;


/**
 * Entry point for Feed Web Scripts.
 * 
 * Overrides the endpoint ID used to load in user-meta data. Only the basic HTTP feed
 * endpoint can be used when routing webscript calls through this servlet.
 * 
 * @author kevinr
 */
public class WebScriptFeedServlet extends WebScriptServlet
{
    private static final long serialVersionUID = 4209812354069597861L;
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        // construct the request context manually
        try
        {
            RequestContextFactory factory = WebFrameworkServiceRegistry.getInstance(getServletContext()).getRequestContextFactory();
            RequestContext context = factory.newInstance(req);
            
            // store into request attribute
            req.setAttribute(RequestContext.ATTR_REQUEST_CONTEXT, context);
        }
        catch (RequestContextException rce)
        {
            throw new ServletException("Error while building request context", rce);
        }
        
        // apply the endpoint override
        req.setAttribute(RequestContextFactory.USER_ENDPOINT, "alfresco-feed");
        
        super.service(req, res);
    }
}