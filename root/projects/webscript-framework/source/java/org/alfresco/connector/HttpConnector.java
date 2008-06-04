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
package org.alfresco.connector;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic Connector implementation that can be used to perform simple HTTP and
 * HTTP communication with a remote endpoint. This connector supports basic
 * authentication.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class HttpConnector extends AbstractConnector
{
    private static Log logger = LogFactory.getLog(HttpConnector.class);
    
    /**
     * Instantiates a new http connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    public HttpConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }


    public Response call(String uri, ConnectorContext context)
    {
        RemoteClient remoteClient = initRemoteClient(context);
                
        // call client and process response
        Response response = remoteClient.call(uri);
        processResponse(response);
        
        return response;        
    }

    public Response call(String uri, ConnectorContext context, InputStream in)
    {
        RemoteClient remoteClient = initRemoteClient(context);
        
        // call client and process response
        Response response = remoteClient.call(uri, in);
        processResponse(response);
        
        return response;
    }

    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out)
    {
        RemoteClient remoteClient = initRemoteClient(context);
        
        // call client and process response
        Response response = remoteClient.call(uri, in, out);
        processResponse(response);
        
        return response;
    }
    
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res)
    {
        RemoteClient remoteClient = initRemoteClient(context);
                
        // call client and process response
        Response response = remoteClient.call(uri, req, res);
        processResponse(response);
        
        return response;        
    }
       
    /**
     * Stamps headers onto the remote client
     * 
     * @param remoteClient
     * @param context
     */
    private void applyRequestHeaders(RemoteClient remoteClient, ConnectorContext context)
    {
        // get the headers
        Map<String, String> headers = new HashMap<String, String>(8, 1.0f);
        if (context != null)
        {
            headers.putAll(context.getHeaders());
        }

        // copy in cookies that have been stored back as part of the connector session
        if (getConnectorSession() != null)
        {
            String[] keys = getConnectorSession().getCookieNames();
            if (keys.length != 0)
            {
                StringBuilder builder = new StringBuilder(256);
                
                for (int i = 0; i < keys.length; i++)
                {
                    String cookieName = keys[i];
                    String cookieValue = getConnectorSession().getCookie(cookieName);
                    
                    if (builder.length() != 0)
                    {
                        builder.append(";");
                    }
                    builder.append(cookieName);
                    builder.append("=");
                    builder.append(cookieValue);
                }
                
                String cookieString = builder.toString();
                
                if (logger.isDebugEnabled())
                    logger.debug("HttpConnector setting cookie header: " + cookieString);
                
                headers.put("Cookie", cookieString);
            }
        }
        
        // stamp all headers onto the remote client
        if (headers.size() != 0)
        {
            remoteClient.setRequestProperties(headers);
        }
    }
    
    /**
     * Stamps Credentials values onto the remote client
     * 
     * @param remoteClient
     */
    private void applyRequestAuthentication(RemoteClient remoteClient, ConnectorContext context)
    {
        // TODO: is this necessary?
        // support for basic authentication
        if (getCredentials() != null)
        {
            String user = (String) getCredentials().getProperty(Credentials.CREDENTIAL_USERNAME);
            String pass = (String) getCredentials().getProperty(Credentials.CREDENTIAL_PASSWORD);
            remoteClient.setUsernamePassword(user, pass);
        }        
    }    
    
    /**
     * Retrieves headers from response and stores back onto Credentials
     * 
     * @param response
     */
    protected void processResponse(Response response)
    {
        Map<String, String> headers = response.getStatus().getHeaders();
        Iterator<String> it = headers.keySet().iterator();
        while (it.hasNext())
        {
            String headerName = it.next();
            if (headerName.toLowerCase().equals("set-cookie"))
            {
                String headerValue = headers.get(headerName);
                
                int z = headerValue.indexOf("=");
                if (z != -1)
                {
                    String cookieName = headerValue.substring(0,z);
                    String cookieValue = headerValue.substring(z+1, headerValue.length());
                    int y = cookieValue.indexOf(";");
                    if (y != -1)
                    {
                        cookieValue = cookieValue.substring(0,y);
                    }

                    // store cookie back
                    if (logger.isDebugEnabled())
                        logger.debug("Connector found set-cookie: " + cookieName + " = " + cookieValue);
                    
                    if (getConnectorSession() != null)
                    {
                        getConnectorSession().setCookie(cookieName, cookieValue);
                    }
                }
            }
        }
    }
    
    /**
     * Init the RemoteClient object based on the Connector Context.
     * Applies Request headers and authentication as required.
     * 
     * @return RemoteClient
     */
    protected RemoteClient initRemoteClient(ConnectorContext context)
    {
        // create a remote client
        RemoteClient remoteClient = new RemoteClient(getEndpoint());
        
        // configure the client
        if (context != null)
        {
            remoteClient.setRequestContentType(context.getContentType());
            remoteClient.setRequestMethod(context.getMethod());
        }
        
        // stamp headers onto the remote client
        applyRequestHeaders(remoteClient, context);
        
        // TODO: copy parameters
        
        // stamp credentials onto the remote client
        applyRequestAuthentication(remoteClient, context);
        
        return remoteClient;
    }
}
