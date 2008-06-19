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

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.config.ConfigService;
import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorContext;
import org.alfresco.connector.ConnectorService;
import org.alfresco.connector.HttpMethod;
import org.alfresco.connector.Response;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.alfresco.web.config.RemoteConfigElement.IdentityType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * EndPoint HTTP Proxy Servlet.
 * 
 * Provides the ability to submit a URL request via a configured end point such as a
 * remote  Alfresco Server. Automatically appends TICKET information for the current
 * user context.
 * 
 * This servlet accepts:
 * 
 * /proxy/<endpointid>[/uri]*[?[<argName>=<argValue>]*]
 * 
 * Where:
 * 
 * - endpointid is the ID of a configured EndPoint model object to make a request against
 * - url is the uri to call on the EndPoint URL e.g. /api/sites
 * - argName is the name of a URL argument to append to the request
 * - argValue is the value of URL argument
 * 
 * E.g.
 * 
 * /proxy/alf1/api/sites?name=mysite&desc=description
 * 
 * The proxy currently supports all valid HTTP methods.
 * 
 * @author kevinr
 */
public class EndPointProxyServlet extends HttpServlet
{
    private static Log logger = LogFactory.getLog(EndPointProxyServlet.class);
    
    private static final long serialVersionUID = -176412355613122789L;

    protected RemoteConfigElement config;
    protected ConnectorService connectorService;

    @Override
    public void init() throws ServletException
    {
        super.init();
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        ConfigService configService = (ConfigService)context.getBean("web.config");
        
        // retrieve the remote configuration
        this.config = (RemoteConfigElement)configService.getConfig("Remote").getConfigElement("remote");
        
        // retrieve the connector service
        this.connectorService = (ConnectorService) context.getBean("connector.service");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        String uri = req.getRequestURI().substring(req.getContextPath().length());
        
        // validate and return the endpoint id from the URI path - stripping the servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new IllegalArgumentException("Proxy URL did not specify endpoint id.");
        }
        String endpointId = t.nextToken();
        
        // rebuild rest of the URL for the proxy request
        StringBuilder buf = new StringBuilder(64);
        if (t.hasMoreTokens())
        {
            do
            {
                buf.append('/');
                buf.append(t.nextToken());
            } while (t.hasMoreTokens());
        }
        else
        {
            // allow for an empty uri to be passed in
            // this could therefore refer to the root of a service i.e. /webapp/axis
            buf.append('/');
        }
        
        try
        {
            // retrieve the endpoint descriptor - do not allow proxy access to unsecure endpoints
            EndpointDescriptor descriptor = this.config.getEndpointDescriptor(endpointId);
            if (descriptor == null || descriptor.getUnsecure())
            {
                // throw an exception if endpoint ID is does not exist or invalid
                throw new AlfrescoRuntimeException("Invalid EndPoint Id: " + endpointId);
            }
            
            // user id from session
            // TODO: this comes from the web-framework UserFactory - should it be moved down to this project?
            Connector connector;
            String userId = (String)req.getSession().getAttribute("USER_ID");
            if (userId != null)
            {
                // build an authenticated connector - as we have a userId
                connector = this.connectorService.getConnector(endpointId, userId, req.getSession());
            }
            else if (descriptor.getIdentity() == IdentityType.NONE ||
                     descriptor.getIdentity() == IdentityType.DECLARED)
            {
                // build an unauthenticated/predeclared authentication connector
                connector = this.connectorService.getConnector(endpointId);
            }
            else
            {
                throw new AlfrescoRuntimeException("No USER_ID found in session and" +
                        " requested 'endpoint' requires authentication.");
            }
            
            // build a connector context, stores information about how we will drive the remote client
            ConnectorContext context = new ConnectorContext();
            context.setContentType(req.getContentType());
            context.setMethod(HttpMethod.valueOf(req.getMethod().toUpperCase()));
            
            // build proxy URL referencing the endpoint
            String q = req.getQueryString();
            String url = buf.toString() + (q != null && q.length() != 0 ? "?" + q : "");
            
            if (logger.isDebugEnabled())
            {
                logger.debug("EndPointProxyServlet preparing to proxy:");
                logger.debug(" - endpointId: " + endpointId);
                logger.debug(" - userId: " + userId);
                logger.debug(" - connector: " + connector);
                logger.debug(" - method: " + context.getMethod());
                logger.debug(" - url: " + url);
            }
            
            // call through using our connector to proxy
            Response response = connector.call(url, context, req, res);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Return code: " + response.getStatus().getCode());
            }
        }
        catch (Throwable err)
        {
            // TODO: trap and handle errors!
            throw new AlfrescoRuntimeException("Error during endpoint proxy processing: " + err.getMessage(), err);
        }
    }
}
