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
package org.alfresco.connector;

import java.util.Map;

import org.alfresco.connector.exception.AuthenticationException;
import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;
import org.alfresco.web.scripts.Status;

/**
 * Connector object that can be used for HTTP or HTTPS calls to
 * an endpoint.  The connector supports basic authentication.
 * 
 * @author muzquiano
 */
public class AlfrescoConnector extends HttpConnector
{
	public AlfrescoConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }
	
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractConnector#call(java.lang.String, java.util.Map, java.util.Map)
     */
    public Response call(String uri, Map parameters, Map headers)
    {
    	// if we don't have any credentials, we'll just call the super class
    	// method since that will implement unauthenticated HTTP
    	if (getCredentials() == null)
    	{
    		return super.call(uri, parameters, headers);
    	}
    	
    	// instantiate the remote client if not instantiated
        RemoteClient remoteClient = ((RemoteClient) this.getClient());

        // check to see if we have a ticket
        String alfTicket = (String) getCredentials().getProperty(Credentials.CREDENTIAL_ALF_TICKET);
        
        // if we have a ticket, we assume it is valid
        // it may, however, be possible that the ticket is invalid
        // if it is invalid, we will have to fetch another ticket
        if (alfTicket != null)
        {
        	remoteClient.setTicket(alfTicket);
        	Response response = remoteClient.call(uri);
        	if(response.getStatus().getCode() == 200)
        	{
        		// successful response, so simply return
        		return response;
        	}
        }
        
        // otherwise, we either have an invalid ticket or we have no ticket
        // either way, we want to do a handshake to get a new ticket
        Response response = null;
        boolean authenticated = false;
        try
        {
        	authenticated = authenticate();
        }
    	catch (AuthenticationException ae)
    	{
    		Status status = new Status();
    		status.setCode(401);
    		status.setException(ae);
    		response = new Response(status);
    		authenticated = false;
    	}
    	
    	// did we successfully authenticate?
    	if (authenticated)
    	{
	    	// now we have a valid ticket
	    	// this ticket has been placed back onto the Credentials object
    		// we retrieve it here
	    	alfTicket = (String)getCredentials().getProperty(Credentials.CREDENTIAL_ALF_TICKET);
	    	remoteClient.setTicket(alfTicket);
	    	response = remoteClient.call(uri);
    	}
    	
    	return response;
    }
}
