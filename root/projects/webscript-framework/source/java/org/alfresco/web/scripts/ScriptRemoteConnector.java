/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorContext;
import org.alfresco.connector.HttpMethod;
import org.alfresco.connector.Response;
import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.util.I18NUtil;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Describes a connector to a remote endpoint.
 * 
 * This is a wrapper around the true connector object and it provides
 * Script-style interfaces for working with buffered response strings
 * and the like.
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class ScriptRemoteConnector
{
    private static final Log logger = LogFactory.getLog(ScriptRemote.class);
    
    final private Connector connector;
    final private EndpointDescriptor descriptor; 
    
    
    /**
     * Constructor
     * 
     * @param connector     The Connector to wrap
     * @param descriptor    The description of the endpoint this connector is managing
     */
    public ScriptRemoteConnector(Connector connector, EndpointDescriptor descriptor)
    {
        this.connector = connector;
        this.descriptor = descriptor;
    }
    
    
    /**
     * Invokes a URI on the endpoint via a GET request.
     * 
     * @param uri the uri
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        return this.connector.call(uri, context);
    }
    
    /**
     * Invokes a GET request URI on the endpoint.
     * 
     * @param uri the uri
     * 
     * @return Response object from the call {@link Response}
     */
    public Response get(String uri)
    {
        return call(uri);
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a POST request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the POST request.
     * 
     * @return Response object from the call {@link Response}
     */
    public Response post(String uri, String body)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.POST);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new AlfrescoRuntimeException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a POST request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the POST request.
     * @param contentType   Content mimetype of the request body
     * 
     * @return Response object from the call {@link Response}
     */
    public Response post(String uri, String body, String contentType)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.POST);
        context.setContentType(contentType);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new AlfrescoRuntimeException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a PUT request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the PUT request.
     * 
     * @return Response object from the call {@link Response}
     */
    public Response put(String uri, String body)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.PUT);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new AlfrescoRuntimeException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a PUT request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the PUT request.
     * @param contentType   Content mimetype of the request
     * 
     * @return Response object from the call {@link Response}
     */
    public Response put(String uri, String body, String contentType)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.PUT);
        context.setContentType(contentType);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new AlfrescoRuntimeException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service as DELETE request.
     * 
     * NOTE: the name of the method is 'del' not 'delete' so as to not
     * interfere with JavaScript Object.delete() method.
     * 
     * @param uri    Uri to call on the endpoint
     * 
     * @return Response object from the call {@link Response}
     */
    public Response del(String uri)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.DELETE);
        return this.connector.call(uri, context);
    }
    
    /**
     * Returns the endpoint string
     * 
     * @return endpoint
     */
    public String getEndpoint()
    {
        return this.connector.getEndpoint();
    }
    
    /**
     * @return the endpoint descriptor object
     */
    public EndpointDescriptor getDescriptor()
    {
        return this.descriptor;
    }
    
    
    /**
     * Helper to build a map of the default headers for script requests - we send over
     * the current users locale so it can be respected by any appropriate REST APIs.
     *  
     * @return map of headers
     */
    private static Map<String, String> buildDefaultHeaders()
    {
        Map<String, String> headers = new HashMap<String, String>(1, 1.0f);
        headers.put("Accept-Language", I18NUtil.getLocale().toString().replace('_', '-'));
        return headers;
    }
}
