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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.webscripts.servlet.WebScriptServlet;
import org.springframework.web.context.support.WebApplicationContextUtils;


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
    
    private ServletContext servletContext;
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config)
    	throws ServletException
    {
    	super.init(config);
    	
    	this.servletContext = config.getServletContext();
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        // apply the endpoint override
        req.setAttribute(RequestContext.USER_ENDPOINT, "alfresco-feed");

        // construct the request context manually
        try
        {
            RequestContextUtil.initRequestContext(getApplicationContext(), (HttpServletRequest)req);
        }
        catch (RequestContextException rce)
        {
            throw new ServletException("Error while building request context", rce);
        }
        
        super.service(req, res);
    }
    
    /**
     * Retrieves the root application context
     * 
     * @return application context
     */
    private ApplicationContext getApplicationContext()
    {
    	return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }    
}