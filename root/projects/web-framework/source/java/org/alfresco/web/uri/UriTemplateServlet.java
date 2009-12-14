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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.uri;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * URI template remapping servlet.
 * 
 * Using the facilities of the UriTemplateIndex and a configured list of URI Template mappings.
 * @see UriTemplateIndex
 * 
 * Each URI Template maps to one a page resource urls. The page resource URL is then forwarded
 * to the PageRendererServlet. 
 * 
 * @author Kevin Roast
 */
public class UriTemplateServlet extends HttpServlet
{
    private static Log logger = LogFactory.getLog(UriTemplateServlet.class);

    public static final String CONFIG_ELEMENT = "UriTemplate";

    /** URI Template index - Application url mappings */
    private UriTemplateMappingIndex uriTemplateIndex;


    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException
    {
        super.init();

        // init required beans
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        ConfigService configService = (ConfigService)context.getBean("web.config");
        initUriIndex(configService);
    }

    /**
     * Initialise the list of URL Mapper objects for the PageRenderer
     */
    private void initUriIndex(ConfigService configService)
    {
        Config config = configService.getConfig(CONFIG_ELEMENT);
        if (config == null)
        {
            throw new AlfrescoRuntimeException("Cannot find required config element 'UriTemplate'.");
        }
        ConfigElement uriConfig = config.getConfigElement("uri-mappings");
        if (uriConfig == null)
        {
            throw new AlfrescoRuntimeException("Missing required config element 'uri-mappings' under 'UriTemplate'.");
        }
        this.uriTemplateIndex = new UriTemplateMappingIndex(uriConfig);
    }

    /**
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        String uri = req.getRequestURI();
        String qs = req.getQueryString();

        // skip servlet context path and build the path to the resource we are looking for
        uri = uri.substring(req.getContextPath().length());

        // validate and return the URI path - stripping the servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new AlfrescoRuntimeException("Invalid URL: " + uri);
        }

        // build the uri ready for URI Template match
        uri = uri.substring(servletName.length() + 1) + (qs != null ? ("?" + qs) : "");

        if (logger.isDebugEnabled())
            logger.debug("Matching application URI template: " + uri);

        String resource = matchUriTemplate(uri);

        if (logger.isDebugEnabled())
            logger.debug("Resolved uri template to resource: " + resource);

        // rebuild page servlet URL to perform forward too
        req.getRequestDispatcher(resource).forward(req, res);
    }

    /**
     * Match the specified URI against the URI template index
     * 
     * @param uri to match
     * 
     * @return the resource URL to use
     */
    private String matchUriTemplate(String uri)
    {
        String resource = this.uriTemplateIndex.findMatchAndReplace(uri);
        if (resource == null)
        {
            resource = uri;
        }
        return resource;
    }
}
