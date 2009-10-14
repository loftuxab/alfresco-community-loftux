/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.connector.exception.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A special implementation of an Authenticating Connector.
 * 
 * The AuthenticatingConnector is a wrapper around a Connector object
 * and an Authenticator object.  It appears as a Connector to the
 * outside world but provides additional functionality.
 * 
 * When a call is made, the underlying connector is used to call over
 * to the resource.  The underlying connector retrieves cookie state
 * from the connector session (if available) and attempts to access the
 * remote resource.
 * 
 * If this succeeds, then the AuthenticatingConnector returns this response.
 * 
 * On the other hand, if this fails (i.e. it receives a 401 unauthorized
 * response), the AuthenticatingConnector calls into the underlying
 * Authenticator instance to perform an "authentication handshake".
 * 
 * This handshake retrieves the necessary cookies or tokens and places
 * them into the connector session.  The connector session is persisted
 * to the session (if it was originally bound to the session).
 * 
 * The AuthenticatingConnector then reattempts the connection using the
 * newly retrieved cookies or tokens.  If a 401 is received again, the
 * credentials are assumed to be invalid (or something is incorrect
 * about the handshake model).
 * 
 * @author muzquiano
 */
public class AuthenticatingConnector implements Connector
{
    protected static Log logger = LogFactory.getLog(AuthenticatingConnector.class);
    protected Connector connector = null;
    protected Authenticator authenticator = null;
    
    /**
     * Instantiates a new authenticating connector.
     * 
     * @param connector the connector
     * @param authenticator the authenticator
     */
    public AuthenticatingConnector(Connector connector, Authenticator authenticator)
    {
        this.connector = connector;
        this.authenticator = authenticator;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String)
     */
    public Response call(String uri)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            response = this.connector.call(uri);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext)
     */
    public Response call(String uri, ConnectorContext context)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri, context);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            response = this.connector.call(uri, context);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }

        return response;        
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, java.io.InputStream)
     */
    public Response call(String uri, ConnectorContext context, InputStream in)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri, context, in);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            if (in.markSupported())
            {
                try
                {
                    in.reset();
                }
                catch (IOException ioErr)
                {
                    // if we cannot reset the stream - there's nothing else we can do
                }
            }
            response = this.connector.call(uri, context, in);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, java.io.InputStream, java.io.OutputStream)
     */
    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri, context, in, out);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            if (in.markSupported())
            {
                try
                {
                    in.reset();
                }
                catch (IOException ioErr)
                {
                    // if we cannot reset the stream - there's nothing else we can do
                }
            }
            response = this.connector.call(uri, context, in, out);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri, context, req, res);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            response = this.connector.call(uri, context, req, res);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setCredentials(org.alfresco.connector.Credentials)
     */
    public void setCredentials(Credentials credentials)
    {
        this.connector.setCredentials(credentials);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getCredentials()
     */
    public Credentials getCredentials()
    {
        return this.connector.getCredentials();
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setEndpoint(java.lang.String)
     */
    public void setEndpoint(String endpoint)
    {
        this.connector.setEndpoint(endpoint);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getEndpoint()
     */
    public String getEndpoint()
    {
        return connector.getEndpoint();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractConnector#setConnectorSession(org.alfresco.connector.ConnectorSession)
     */
    public void setConnectorSession(ConnectorSession connectorSession)
    {
        this.connector.setConnectorSession(connectorSession);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractConnector#getConnectorSession()
     */
    public ConnectorSession getConnectorSession()
    {
        return this.connector.getConnectorSession();
    }
    
    /**
     * Returns whether the current session is authenticated already.
     * 
     * @return true, if checks if is authenticated
     */
    protected boolean isAuthenticated()
    {
        return this.authenticator.isAuthenticated(getEndpoint(), getConnectorSession());        
    }
    
    
    /**
     * Performs the authentication handshake.
     * 
     * @return true, if successful
     */
    final public boolean handshake()
    {
        boolean success = false;
        
        if (logger.isDebugEnabled())
            logger.debug("Performing authentication handshake");
        
        if (EndpointManager.allowConnect(getEndpoint()))
        {
            ConnectorSession cs = null;
            try
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Authentication handshake using credentials: " + getCredentials());
                    logger.debug("Authentication handshake using connectorSession: " + getConnectorSession());
                }
                
                cs = this.authenticator.authenticate(getEndpoint(), getCredentials(), getConnectorSession());
            }
            catch (AuthenticationException ae)
            {
                logger.error("An exception occurred while attempting authentication handshake for endpoint: " + getEndpoint(), ae);
            }
            if (cs != null)
            {
                this.setConnectorSession(cs);
                success = true;
            }
        }
        else
        {
            if (logger.isDebugEnabled())
                logger.debug("Skipping authentication handshake, waiting for reconnect on: " + getEndpoint());
        }
        
        return success;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.connector.toString();
    }
}
