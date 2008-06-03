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

import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * Abstract class for use by developers in building their own custom connectors.
 * This provides basic implementations of most of the helper functions that
 * simply call through to workhorse functions.
 * 
 * The primary workhorse functions are the two call methods - one of which
 * buffers response data onto the response object and the other which streams
 * data from source to destination.
 * 
 * Extending this class makes it easier for developers by removing most of the
 * tedious stuff and letting them concentrate on the interesting functions.
 * 
 * @author muzquiano
 */
public abstract class AbstractConnector implements Connector
{
    private Credentials credentials;
    protected String endpoint;
    protected ConnectorDescriptor descriptor;
    protected ConnectorSession connectorSession;
    
    /**
     * Instantiates a new abstract connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    protected AbstractConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        this.descriptor = descriptor;
        this.endpoint = endpoint;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String)
     */
    public Response call(String uri)
    {
        return call(uri, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext)
     */
    public abstract Response call(String uri, ConnectorContext context);
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, java.io.InputStream, java.io.OutputStream)
     */
    public abstract Response call(String uri, ConnectorContext context, InputStream in, OutputStream out);
        
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse)
     */
    public abstract Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res);
    
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.connector.Connector#setCredentials(org.alfresco.connector.Credentials)
     */
    public void setCredentials(Credentials credentials)
    {
        this.credentials = credentials;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getCredentials()
     */
    public Credentials getCredentials()
    {
        return credentials;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setEndpoint(java.lang.String)
     */
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getEndpoint()
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }    

    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setConnectorSession(org.alfresco.connector.ConnectorSession)
     */
    public void setConnectorSession(ConnectorSession connectorSession)
    {
        this.connectorSession = connectorSession;
        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getConnectorSession()
     */
    public ConnectorSession getConnectorSession()
    {
        return this.connectorSession;
    }    
}
