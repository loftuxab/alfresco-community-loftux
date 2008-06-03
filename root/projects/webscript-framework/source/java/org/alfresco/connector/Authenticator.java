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

import org.alfresco.connector.exception.AuthenticationException;

/**
 * Interface that defines an Authenticator.  Authenticators are used to
 * retrieve cookies and tokens from a remote service based on credentials
 * which are locally managed and passed to the remote service.
 * 
 * Authenticator objects are used when a "token" must be passed to the endpoint
 * and the current token is either invalid or non-existent. The Connectors must
 * then handshake with the endpoint to acquire a token.
 * 
 * Tokens are not always necessary. An example is HTTP Basic Authentication
 * where user names and passwords are sent on every request. Alternatively, of
 * course, you may wish only to authenticate on the first request and then pass
 * the Authenticate hash on every subsequent request.
 * 
 * In that case, the role of the authenticate() method would be to handshake
 * with the endpoint to acquire this hash.
 * 
 * @author muzquiano
 */
public interface Authenticator
{
    /**
     * Authenticate against the given Endpoint URL with the supplied Credentials
     * 
     * @return The connector session instance
     * 
     * @throws AuthenticationException on error
     */
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException;

    /**
     * Returns whether the current connector session has been authenticated or not
     * 
     * @param endpoint
     * @param connectorSession
     * @return
     */
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession);
}
