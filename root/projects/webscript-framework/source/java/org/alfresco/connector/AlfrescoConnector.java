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

import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * An implementation of an Alfresco Connector that can be used to conncet
 * to an Alfresco Repository and issue URL invokes against it.
 * 
 * The Alfresco Connector extends the Http Connector and provides the
 * additional functionality of stamping a ticket onto the outgoing request.
 * 
 * The ticket is retrieved from the connector session.
 * 
 * @author muzquiano
 */
public class AlfrescoConnector extends HttpConnector
{
    private static final String PARAM_TICKETNAME_ALF_TICKET = "alf_ticket";

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

    /* (non-Javadoc)
     * @see org.alfresco.connector.HttpConnector#stampCredentials(org.alfresco.connector.RemoteClient, org.alfresco.connector.ConnectorContext)
     */
    @Override
    protected void applyRequestAuthentication(RemoteClient remoteClient, ConnectorContext context)
    {
        // support for Alfresco ticket-based authentication
        if (getCredentials() != null)
        {
            // if this connector is managing session info
            if (getConnectorSession() != null)
            {
                // apply alfresco ticket
                String alfTicket = (String) getConnectorSession().getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);
                if (alfTicket != null)
                {
                    remoteClient.setTicket(alfTicket);
                    remoteClient.setTicketName(PARAM_TICKETNAME_ALF_TICKET);
                }
            }
        }        
    }  
}
