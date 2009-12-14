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
package org.alfresco.web.scripts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.surf.util.Content;
import org.alfresco.web.config.ServerProperties;

/**
 * The Class LocalWebScriptRequest.
 * 
 * @author muzquiano
 */
public class LocalWebScriptRequest extends WebScriptRequestURLImpl
{
    private Map<String, String> parameters;
    private HttpServletRequest request;
    private ServerProperties serverProperties;
    private LocalWebScriptContext context;
        
    /**
     * Instantiates a new local web script request.
     * 
     * @param runtime the runtime
     * @param scriptUrl the script url
     * @param match the match
     * @param parameters the parameters
     * @param request the request
     */
    public LocalWebScriptRequest(Runtime runtime, String scriptUrl,
            Match match, Map<String, String> parameters, ServerProperties serverProps, HttpServletRequest request, LocalWebScriptContext context)
    {
        super(runtime, splitURL(request.getContextPath().length() != 0, scriptUrl),  match);
        this.parameters = parameters;
        this.serverProperties = serverProps;
        this.request = request;
        this.context = context;
    }
    
    /**
     * Gets the http servlet request.
     * 
     * @return the http servlet request
     */
    public HttpServletRequest getHttpServletRequest()
    {
        return this.request;
    }
        
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameterNames()
     */
    public String[] getParameterNames()
    {
        return this.parameters.keySet().toArray(new String[this.parameters.size()]);            
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name)
    {
        return this.parameters.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name)
    {
        return this.parameters.values().toArray(
                new String[this.parameters.size()]);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
     */
    public String getAgent()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServerPath()
     */
    public String getServerPath()
    {
        return getServerScheme() + "://" + getServerName() + ":" + getServerPort();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
     */
    public String[] getHeaderNames()
    {
        return new String[] { };
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
     */
    public String getHeader(String name)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
     */
    public String[] getHeaderValues(String name)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContent()
     */
    public Content getContent()
    {
        return null;
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
            scheme = request.getScheme();
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
            name = request.getServerName();
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
            port = request.getServerPort();
        }
        return port;
    }
}
