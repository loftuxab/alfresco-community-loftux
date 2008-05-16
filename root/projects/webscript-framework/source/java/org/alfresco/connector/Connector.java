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

/**
 * Describes a connector to a remote endpoint.
 * 
 * A connector is scoped to a given user. When a connector is constructed, it
 * obtains access to a given user's credentials. Thus, all subsequent connector
 * activity is processed in the context of that user.
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
     * Invokes a URI on the endpoint.
     * 
     * @param uri the uri
     * 
     * @return the response
     */
    public Response call(String uri);

    /**
     * Invokes a URI on the endpoint and passes in the provided parameters as
     * GET request parameters.
     * 
     * @param uri the uri
     * @param parameters the parameters
     * 
     * @return the response
     */
    public Response call(String uri, Map parameters);

    /**
     * Invokes a URI on the endpoint and passes in the provided parameters as
     * GET request parameters. The provided headers are plugged into the
     * request.
     * 
     * @param uri the uri
     * @param parameters the parameters
     * @param headers the headers
     * 
     * @return the response
     */
    public Response call(String uri, Map parameters, Map headers);

    /**
     * Method to be called by the connector in the event that the client returns
     * an authentication problem.
     * 
     * Connectors will generally follow one of the following patterns:
     * 
     * 1) They will always pass authentication information (as is the case in
     * the basic authentication schema)
     * 
     * 2) They will wait to be challenged and then respond with the correct
     * authentication information (retrieving the endpoint credential for
     * subsequent use)
     * 
     * 3) They will receive an "unauthorized response" and then have to go
     * through an authentication call to retrieve an authentication token which
     * should then be passed into a reattempt of the original call.
     * 
     * The latter is akin to how Alfresco handles its authentication via Web
     * Scripts.
     * 
     * This method provides a means for implemented the separate authentication
     * call for #3.
     * 
     * @return true, if authenticate
     */
    public boolean authenticate() throws AuthenticationException;

    /**
     * Returns the id of the authentication scheme currently configured for this
     * connector.
     * 
     * @return the authenticator
     */
    public Authenticator getAuthenticator();

    /**
     * Sets the authenticator.
     * 
     * @param authenticator the new authenticator
     */
    public void setAuthenticator(Authenticator authenticator);

    /**
     * Binds Credentials to this connector
     * 
     * @param credentials the new credentials
     */
    public void setCredentials(Credentials credentials);

    /**
     * Returns the cre
     * 
     * @return
     */
    public Credentials getCredentials();

    /**
     * Sets the endpoint.
     * 
     * @param endpoint the new endpoint
     */
    public void setEndpoint(String endpoint);

    /**
     * Gets the client.
     * 
     * @return the client
     */
    public Client getClient();
}
