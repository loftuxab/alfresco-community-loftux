/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.scripts.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.config.ServerProperties;
import org.alfresco.web.scripts.Authenticator;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.alfresco.web.scripts.AbstractRuntime;
import org.alfresco.web.scripts.RuntimeContainer;


/**
 * HTTP Servlet Web Script Runtime
 * 
 * @author davidc
 */
public class WebScriptServletRuntime extends AbstractRuntime
{
    protected ServletAuthenticatorFactory authFactory;
    protected HttpServletRequest req;
    protected HttpServletResponse res;
    protected ServerProperties serverProperties;
    
    private WebScriptServletRequest servletReq;
    private WebScriptServletResponse servletRes;
    

    /**
     * Construct
     * 
     * @param registry
     * @param serviceRegistry
     * @param authenticator
     * @param req
     * @param res
     */
    public WebScriptServletRuntime(RuntimeContainer container, ServletAuthenticatorFactory authFactory, HttpServletRequest req, HttpServletResponse res, ServerProperties serverProperties)
    {
        super(container);
        this.authFactory = authFactory;
        this.req = req;
        this.res = res;
        this.serverProperties = serverProperties;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#getScriptMethod()
     */
    @Override
    protected String getScriptMethod()
    {
        // Is this an overloaded POST request?
        String method = req.getMethod();
        if (method.equalsIgnoreCase("post"))
        {
            boolean overloadParam = false;
            String overload = req.getHeader("X-HTTP-Method-Override");
            if (overload == null || overload.length() == 0)
            {
                overload = req.getParameter("alf_method");
                overloadParam = true;
            }
            if (overload != null && overload.length() > 0)
            {
                if (logger.isDebugEnabled())
                    logger.debug("POST is tunnelling method '" + overload + "' as specified by " + (overloadParam ? "alf_method parameter" : "X-HTTP-Method-Override header"));
                    
                method = overload;
            }
        }
        
        return method;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#getScriptUrl()
     */
    @Override
    protected String getScriptUrl()
    {
        // NOTE: Don't use req.getPathInfo() - it truncates the path at first semi-colon in Tomcat
        String requestURI = req.getRequestURI();
        String pathInfo = requestURI.substring((req.getContextPath() + req.getServletPath()).length());
        try
        {
            return URLDecoder.decode(pathInfo, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new WebScriptException("Failed to retrieve path info", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#createRequest(org.alfresco.web.scripts.WebScriptMatch)
     */
    @Override
    protected WebScriptRequest createRequest(Match match)
    {
        servletReq = new WebScriptServletRequest(this, req, match, serverProperties);
        return servletReq;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#createResponse()
     */
    @Override
    protected WebScriptResponse createResponse()
    {
        servletRes = new WebScriptServletResponse(this, res);
        return servletRes;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractRuntime#createAuthenticator()
     */
    @Override
    protected Authenticator createAuthenticator()
    {
        if (authFactory == null)
        {
            return null;
        }
        return authFactory.create(servletReq, servletRes);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptContainer#getName()
     */
    public String getName()
    {
        return "ServletRuntime";
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptContainer#getScriptParameters()
     */
    public Map<String, Object> getScriptParameters()
    {
        FormData formData = servletReq.getFormData();
        if (formData != null)
        {
            Map<String, Object> model = new HashMap<String, Object>(1);
            model.put("formdata", formData);
            return model;
        }
        else
        {
            return Collections.emptyMap();
        }
    }

}
