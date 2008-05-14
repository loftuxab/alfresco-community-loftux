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
import org.alfresco.connector.ConnectorFactory;
import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.CredentialVaultFactory;
import org.alfresco.connector.Credentials;
import org.alfresco.connector.RemoteClient;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
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
 * The proxy currently supports HTTP methods of GET and POST.
 * 
 * @author kevinr
 */
public class EndPointProxyServlet extends HttpServlet
{
    private static final long serialVersionUID = -176412355613122789L;

    protected RemoteConfigElement config;
    protected ConnectorFactory connectorFactory;
    protected CredentialVault credentialsVault;


    @Override
    public void init() throws ServletException
    {
        super.init();
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        ConfigService configService = (ConfigService)context.getBean("web.config");
        
        // retrieve the remote configuration
        this.config = (RemoteConfigElement)configService.getConfig("Remote").getConfigElement("remote");
        this.connectorFactory = ConnectorFactory.getInstance(configService);
        try
        {
            this.credentialsVault = CredentialVaultFactory.getInstance(configService).vault();
        }
        catch (RemoteConfigException err)
        {
            throw new AlfrescoRuntimeException("Error retrieving CredentialVault: " + err.getMessage(), err);
        }
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
        if (!t.hasMoreTokens())
        {
            throw new IllegalArgumentException("Proxy URL did not specify destination URL.");
        }
        StringBuilder buf = new StringBuilder(64);
        do
        {
            buf.append('/');
            buf.append(t.nextToken());
        } while (t.hasMoreTokens());
        
        try
        {
            // retrieve the endpoint descriptor - do not allow proxy access to unsecure endpoints
            EndpointDescriptor descriptor = this.config.getEndpointDescriptor(endpointId);
            if (descriptor == null || descriptor.getUnsecure())
            {
                // throw an exception if endpoint ID is does not exist or invalid
                throw new AlfrescoRuntimeException("Invalid EndPoint Id: " + endpointId);
            }
            
            // userid from session
            // TODO: this comes from the web-framework UserFactory - should it be moved down to this project?
            String userId = (String)req.getSession().getAttribute("USER_ID");
            
            // retrieve the connector for this user and endpoint - get user credentials from the supplied vault
            Connector connector = this.connectorFactory.connector(endpointId, userId, this.credentialsVault);
            
            // build proxy URL referencing the endpoint
            String q = req.getQueryString();
            String url = buf.toString() + (q != null && q.length() != 0 ? "?" + q : "");
            
            // execute proxy URL via remote client
            RemoteClient client = ((RemoteClient)connector.getClient());
            
            // check to see if we have a ticket from the credentials on the connector
            Credentials credentials = connector.getCredentials();
            if (credentials != null)
            {
                String alfTicket = (String)credentials.getProperty(Credentials.CREDENTIAL_ALF_TICKET);
                client.setTicket(alfTicket);
            }
            
            // TODO: copy request headers for proxy call
            String method = req.getMethod();
            client.setRequestContentType(req.getContentType());
            client.setRequestMethod(method);
            if (method.equalsIgnoreCase("POST"))
            {
                client.call(url, req.getInputStream(), res.getOutputStream());
            }
            else
            {
                client.call(url, res.getOutputStream());
            }
        }
        catch (Throwable err)
        {
            // TODO: trap and handle errors!
            throw new AlfrescoRuntimeException("Error during endpoint proxy processing: " + err.getMessage(), err);
        }
    }
}
