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
import org.alfresco.web.scripts.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Connector object that can be used for HTTP or HTTPS calls to an endpoint. The
 * connector supports basic authentication.
 * 
 * @author muzquiano
 */
public class AlfrescoConnector extends HttpConnector
{
    protected static Log logger = LogFactory.getLog(AlfrescoConnector.class);

    /**
     * Instantiates a new alfresco connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    public AlfrescoConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.connector.AbstractConnector#call(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    public Response call(String uri, Map parameters, Map headers)
    {
        if (logger.isDebugEnabled())
            logger.debug("Start [uri = " + uri + "]");

        // if we don't have any credentials, we'll just call the super class
        // method since that will implement unauthenticated HTTP
        if (getCredentials() == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("No credentials, performing unauthenticated call");

            return super.call(uri, parameters, headers);
        }

        // instantiate the remote client if not instantiated
        RemoteClient remoteClient = ((RemoteClient)this.getClient());

        // check to see if we have a ticket
        String alfTicket = (String)getCredentials().getProperty(Credentials.CREDENTIAL_ALF_TICKET);

        if (logger.isDebugEnabled())
            logger.debug("Pass 1: alfTicket = " + alfTicket);

        // if we have a ticket, we assume it is valid
        // it may, however, be possible that the ticket is invalid
        // if it is invalid, we will have to fetch another ticket
        if (alfTicket != null)
        {
            remoteClient.setTicket(alfTicket);

            if (logger.isDebugEnabled())
                logger.debug("Pass 1: Alf Ticket not null, passing into remote call");

            Response response = remoteClient.call(uri);
            if (response.getStatus().getCode() == 200)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Pass 1: Remote call succeeded");

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
            if (logger.isDebugEnabled())
                logger.debug("Pass 2: Call authenticate on Alfresco authenticator");

            authenticated = authenticate();

            if (logger.isDebugEnabled())
                logger.debug("Pass 2: authenticated: " + authenticated);
        }
        catch (AuthenticationException ae)
        {
            if (logger.isDebugEnabled())
                logger.debug("AuthenticationException during authenticate call");

            Status status = new Status();
            status.setCode(401);
            status.setException(ae);
            response = new Response(status);
            authenticated = false;
        }

        if (logger.isDebugEnabled())
            logger.debug("Pass 2: authenticated = " + authenticated);

        // did we successfully authenticate?
        if (authenticated)
        {
            // now we have a valid ticket
            // this ticket has been placed back onto the Credentials object
            // we retrieve it here
            alfTicket = (String) getCredentials().getProperty(
                    Credentials.CREDENTIAL_ALF_TICKET);
            remoteClient.setTicket(alfTicket);

            if (logger.isDebugEnabled())
                logger.debug("Pass 3: Calling remote client with alfTicket = " + alfTicket);

            response = remoteClient.call(uri);
        }

        if (logger.isDebugEnabled())
            logger.debug("Response: " + response);

        return response;
    }
}
