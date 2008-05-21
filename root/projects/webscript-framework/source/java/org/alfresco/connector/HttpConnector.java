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

import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * Basic Connector implementation that can be used to perform simple HTTP and
 * HTTP communication with a remote endpoint. This connector supports basic
 * authentication.
 * 
 * @author muzquiano
 */
public class HttpConnector extends AbstractConnector
{
    /**
     * Instantiates a new http connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     * @param credentials the credentials
     * @param authId the auth id
     */
    public HttpConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }

    /**
     * Gets the client.
     * 
     * @return the client
     */
    public Client getClient()
    {
        if (client == null)
        {
            client = new RemoteClient(endpoint);
        }

        return client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.connector.AbstractConnector#call(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    public Response call(String uri, Map parameters, Map headers)
    {
        // instantiate the remote client if not instantiated
        RemoteClient remoteClient = ((RemoteClient) this.getClient());

        // apply the credentials to the client
        // with the HttpConnector, we also plug in username/password
        if (getCredentials() != null)
        {
            String user = (String) getCredentials().getProperty(
                    Credentials.CREDENTIAL_ALF_USERNAME);
            String pass = (String) getCredentials().getProperty(
                    Credentials.CREDENTIAL_ALF_PASSWORD);
            remoteClient.setUsernamePassword(user, pass);
        }

        // execute
        // Note that this is a single pass call
        // No Authentication challenges are responded to - the auth method never
        // gets fired
        return remoteClient.call(uri);
    }

    /** The client. */
    protected RemoteClient client = null;

}
