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
package org.alfresco.web.scripts.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.util.Content;
import org.alfresco.util.InputStreamContent;
import org.alfresco.web.config.ServerProperties;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.Runtime;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequestImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * HTTP Servlet Web Script Request
 * 
 * @author davidc
 */
public class WebScriptServletRequest extends WebScriptRequestImpl
{
    // Logger
    private static final Log logger = LogFactory.getLog(WebScriptServletRequest.class);

    /** HTTP Request */
    private ServerProperties serverProperties;
    private HttpServletRequest req;
    
    /** Service bound to this request */
    private Match serviceMatch;
    
    /** Form data associated with multipart/form-data */
    private FormData formData;

    /**
     * Construct
     *
     * @param container  request generator
     * @param req
     * @param serviceMatch
     */
    public WebScriptServletRequest(Runtime container, HttpServletRequest req, Match serviceMatch, ServerProperties serverProperties)
    {
        super(container);
        this.serverProperties = serverProperties;
        this.req = req;
        this.serviceMatch = serviceMatch;
        
        String contentType = req.getContentType();
        if (logger.isDebugEnabled())
            logger.debug("Content Type: " + contentType);
        
        if (contentType != null && contentType.startsWith("multipart/form-data"))
        {
            formData = new FormData(req);
        }
    }

    /**
     * Gets the HTTP Servlet Request
     * 
     * @return  HTTP Servlet Request
     */
    public HttpServletRequest getHttpServletRequest()
    {
        return req;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServiceMatch()
     */
    public Match getServiceMatch()
    {
        return serviceMatch;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServerPath()
     */
    public String getServerPath()
    {
        return getServerScheme() + "://" + getServerName() + ":" + getServerPort();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContextPath()
     */
    public String getContextPath()
    {
        return req.getContextPath();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServiceContextPath()
     */
    public String getServiceContextPath()
    {
        return req.getContextPath() + req.getServletPath();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServicePath()
     */
    public String getServicePath()
    {
        String pathInfo = getPathInfo();
        return getServiceContextPath() + ((pathInfo == null) ? "" : pathInfo);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getURL()
     */
    public String getURL()
    {
        return getServicePath() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getPathInfo()
     */
    public String getPathInfo()
    {
        // NOTE: Don't use req.getPathInfo() - it truncates the path at first semi-colon in Tomcat
        String requestURI = req.getRequestURI();
        String pathInfo = requestURI.substring(getServiceContextPath().length());
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
     * @see org.alfresco.web.scripts.WebScriptRequest#getQueryString()
     */
    public String getQueryString()
    {
        return req.getQueryString();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameterNames()
     */
    public String[] getParameterNames()
    {
        if (formData == null)
        {
            Set<String> keys = req.getParameterMap().keySet();
            String[] names = new String[keys.size()];
            keys.toArray(names);
            return names;
        }
        else
        {
            Set<String> keys = formData.getParameters().keySet();
            String[] names = new String[keys.size()];
            keys.toArray(names);
            return names;
        }        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name)
    {
        if (formData == null)
        {
            return req.getParameter(name);
        }
        else
        {
            String[] vals = formData.getParameters().get(name);
            return (vals == null) ? null : vals[0];
        }        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getArrayParameter(java.lang.String)
     */
    public String[] getParameterValues(String name)
    {
        if (formData == null)
        {           
            return req.getParameterValues(name);
        }
        else
        {
            return formData.getParameters().get(name);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
     */
    @SuppressWarnings("unchecked")
    public String[] getHeaderNames()
    {
        List<String> headersList = new ArrayList<String>();
        Enumeration<String> enumNames = req.getHeaderNames();
        while(enumNames.hasMoreElements())
        {
            headersList.add(enumNames.nextElement());
        }
        String[] headers = new String[headersList.size()];
        headersList.toArray(headers);
        return headers;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
     */
    public String getHeader(String name)
    {
        return req.getHeader(name);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public String[] getHeaderValues(String name)
    {
        String[] values = null;
        Enumeration<String> enumValues = req.getHeaders(name);
        if (enumValues.hasMoreElements())
        {
            List<String> valuesList = new ArrayList<String>(2);
            do
            {
                valuesList.add(enumValues.nextElement());
            } 
            while (enumValues.hasMoreElements());
            values = new String[valuesList.size()];
            valuesList.toArray(values);
        }
        return values;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
     */
    public String getAgent()
    {
        String userAgent = req.getHeader("user-agent");
        if (userAgent != null)
        {
            if (userAgent.indexOf("Firefox/") != -1)
            {
                return "Firefox";
            }
            else if (userAgent.indexOf("MSIE") != -1)
            {
                return "MSIE";
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContent()
     */
    public Content getContent()
    {
        try
        {
            return new InputStreamContent(req.getInputStream(), req.getContentType(), req.getCharacterEncoding());
        }
        catch(IOException e)
        {
            throw new WebScriptException("Failed to retrieve request content", e);
        }
    }

    /**
     * Get Server Scheme
     * 
     * @return  server scheme
     */
    private String getServerScheme()
    {
        String scheme = null;
        if (serverProperties != null)
        {
            scheme = serverProperties.getScheme();
        }
        if (scheme == null)
        {
            scheme = req.getScheme();
        }
        return scheme;
    }

    /**
     * Get Server Name
     * 
     * @return  server name
     */
    private String getServerName()
    {
        String name = null;
        if (serverProperties != null)
        {
            name = serverProperties.getHostName();
        }
        if (name == null)
        {
            name = req.getServerName();
        }
        return name;
    }

    /**
     * Get Server Port
     * 
     * @return  server name
     */
    private int getServerPort()
    {
        Integer port = null;
        if (serverProperties != null)
        {
            port = serverProperties.getPort();
        }
        if (port == null)
        {
            port = req.getServerPort();
        }
        return port;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequestImpl#forceSuccessStatus()
     */
    @Override
    public boolean forceSuccessStatus()
    {
        String forceSuccess = req.getHeader("alf-force-success-response");
        return Boolean.valueOf(forceSuccess);
    }

    /**
     * Gets the Form data associated with this request
     * 
     * @return form data, or null if request is not multipart/form-data encoded
     */
    public FormData getFormData()
    {
        return formData;
    }

}
