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

import java.util.Map;

import org.alfresco.connector.exception.AuthenticationException;
import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;


/**
 * Abstract class for use by developers in building their own custom
 * connectors.  This provides basic implementations of most of the
 * helper functions that simply call through to workhorse functions.
 * 
 * Extending this class makes it easier for developers by removing
 * most of the tedious stuff and letting them concentrate on the
 * interesting functions.
 * 
 * @author muzquiano
 */
public abstract class AbstractConnector implements Connector
{    
    /**
     * Instantiates a new abstract connector.
     * 
     * @param endpoint the endpoint
     * @param descriptor the descriptor
     */
    protected AbstractConnector(ConnectorDescriptor descriptor, String endpoint)
    {
    	this.descriptor = descriptor;
    	this.endpoint = endpoint;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#get(java.lang.String)
     */
    public Response call(String uri)
    {
    	return call(uri, null, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#get(java.lang.String, java.util.Map)
     */
    public Response call(String uri, Map parameters)
    {
    	return call(uri, parameters, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#get(java.lang.String, java.util.Map, java.util.Map)
     */
    public abstract Response call(String uri, Map parameters, Map headers);
               
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getClient()
     */
    public abstract Client getClient();
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setCredentials(org.alfresco.connector.Credentials)
     */
    public void setCredentials(Credentials credentials)
    {
    	this.credentials = credentials;
    }
    
    /**
     * Gets the credentials.
     * 
     * @return the credentials
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
    
    /**
     * Gets the authenticator.
     * 
     * @return the authenticator
     */
    public Authenticator getAuthenticator()
    {
    	return this.authenticator;
    }
    
    /**
     * Sets the authenticator.
     * 
     * @param authenticator the authenticator
     */
    public void setAuthenticator(Authenticator authenticator)
    {
    	this.authenticator = authenticator;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractConnector#authenticate()
     */
    public boolean authenticate()
    	throws AuthenticationException
    {
    	// if the credentials are null, then don't bother authenticating
    	if(credentials == null)
    	{
    		return false;
    	}
    	
    	boolean authenticated = false;
    	if(authenticator != null)
    	{
    		authenticated = authenticator.authenticate(getClient(), credentials);
    	}
    	return authenticated;
    }
    
    
    /** The credentials. */
    private Credentials credentials;
    
    /** The endpoint. */
    protected String endpoint;
        
    /** The descriptor. */
    protected ConnectorDescriptor descriptor;
    
    /** The authenticator. */
    private Authenticator authenticator;
}
