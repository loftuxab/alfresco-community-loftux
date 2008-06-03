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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Describes a connector to a remote endpoint for a given user.
 * 
 * All connectors are scoped to a particular user and a particular
 * endpoint.  As such, their endpoints and user bindings cannot
 * be swapped once they are created.  Rather, you should create
 * new connectors for new users and new endpoints.
 * 
 * A connector is scoped to a given user and a given endpoint.
 * 
 * All calls using a connector will be stamped with a user's
 * connector credentials.  These connector credentials usually consist
 * of things like cookies, tokens, additional request parameters and
 * other HTTP request state.
 * 
 * The caller does not have to pass this data manually.  It is managed
 * for the developer by the underlying ConnectorService during the
 * factory construction of Connector objects.
 * 
 * If a connector is constructed without user information, then it is scoped to
 * a null user. No credential information is passed through and the connections
 * are anonymous.
 * 
 * @author muzquiano
 */
public interface Connector
{
    /**
     * Invokes a URI on a remote service.
     * 
     * The response data is buffered into a data element on the returned
     * object of type Response.
     * 
     * @param uri the uri
     * 
     * @return the response
     */
    public Response call(String uri);

    /**
     * Invokes a URI on a remote service.
     * If the context is null, then it will not be utilized.
     * 
     * The response data is buffered into a data element on the returned
     * object of type Response.
     * 
     * @param uri the uri
     * @param context the context of the invoke
     * 
     * @return the response
     */
    public Response call(String uri, ConnectorContext context);

    /**
     * Invokes a URI on a remote service.  Data is streamed back from the
     * response into the provided output stream.  Headers and response state
     * is maintained on the Response object.
     * 
     * If the context is null, then it will not be utilized.
     * 
     * The response data is not buffered
     * 
     * @param uri the uri
     * @param context the context of the invoke
     * @param in the input stream
     * @param out the output stream
     * 
     * @return the response
     */    
    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out);

    /**
     * Invokes a URI on a remote service and streams back results to the
     * provided response object.  This method makes sure that the full
     * response is propagated into the servlet response, including headers,
     * exception states and more.
     * 
     * If the context is null, then it will not be utilized.
     * 
     * The response data is not buffered.
     * 
     * @param uri
     * @param context
     * @param req
     * @param res
     * @return
     */
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res);

    /**
     * Binds Credentials to this connector.
     * 
     * @param credentials the new credentials
     */
    public void setCredentials(Credentials credentials);

    /**
     * Returns the credents for this connector.
     * 
     * @return the credentials
     */
    public Credentials getCredentials();

    /**
     * Sets the endpoint.
     * 
     * @param endpoint the new endpoint
     */
    public void setEndpoint(String endpoint);

    /**
     * Returns the endpoint to which this connector connects.
     * 
     * @return endpoint the endpoint
     */
    public String getEndpoint();
    
    /**
     * Sets the connector session
     * 
     * @param connectorSession
     */
    public void setConnectorSession(ConnectorSession connectorSession);
    
    /**
     * Returns the connector session
     * 
     * @return
     */
    public ConnectorSession getConnectorSession();    
}
