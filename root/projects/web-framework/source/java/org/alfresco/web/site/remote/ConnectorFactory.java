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
package org.alfresco.web.site.remote;

import java.util.HashMap;

import org.alfresco.connector.Authenticator;
import org.alfresco.connector.Credentials;
import org.alfresco.connector.CredentialsVault;
import org.alfresco.connector.DefaultIdentity;
import org.alfresco.connector.Identity;
import org.alfresco.connector.IdentityVault;
import org.alfresco.connector.remote.Connector;
import org.alfresco.connector.remote.RemoteClient;
import org.alfresco.connector.remote.WebConnector;
import org.alfresco.tools.ReflectionHelper;
import org.alfresco.web.site.Framework;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.Endpoint;

/**
 * @author muzquiano
 */
public class ConnectorFactory
{
    protected static HashMap<String, Object> cache = null;
    
    public static Connector newInstance(RequestContext context,Endpoint endpoint)
    {
        Connector connector = newInstance(endpoint);

        // Does this endpoint have any authentication set up on it?
        String authenticatorId = endpoint.getAuthenticatorId();
        if (authenticatorId == null || authenticatorId.length() == 0)
        {
            // Nope, so just return the connector
            return connector;
        }

        // Yes, so we have to plug authentication credentials

        // Get the user's credential vault
        CredentialsVault userCredentialVault = context.getUserCredentialVault();
        if (userCredentialVault != null)
        {
            // check whether the user has cached authentication credentials (for this endpoint)
            Credentials credentials = userCredentialVault.getCredentials(endpoint.getId());
            if (credentials != null)
            {
                // they do have cached credentials, so use those
                connector.setCredentials(credentials);
                return connector;
            }
        }

        // Otherwise, we have to authenticate them and get new credentials

        // Determine the user identity to use
        Identity identity = null;
        String identityString = endpoint.getIdentity();
        if ("specific".equalsIgnoreCase(identityString))
        {
            // Use a specific identity
            // Specific credentials are to be used
            String username = endpoint.getUsername();
            String password = endpoint.getPassword();

            // mock up an identity instance for this
            identity = new DefaultIdentity();
            identity.put("USERNAME", username);
            identity.put("PASSWORD", password);
        }
        else if ("current".equalsIgnoreCase(identityString))
        {
            // use this user's identity

            // Get the user's identity vault
            IdentityVault userIdentityVault = context.getUserIdentityVault();
            if (userIdentityVault != null)
            {
                // get the user's identity for this endpoint
                identity = userIdentityVault.getIdentity(endpoint.getId());
            }
        }

        // Now, execute an authenticator to fetch the credentials that we need...
        Credentials credentials = null;
        if (authenticatorId != null)
        {
            String authClassName = context.getConfig().getRemoteAuthenticatorClass(authenticatorId);
            Authenticator auth = (Authenticator) _getAuthenticator(authClassName);
            credentials = auth.authenticate(connector, identity);
        }

        // Did we get credentials?
        if (credentials != null)
        {
            if (userCredentialVault != null)
            {
                // Update the credentials in the user's vault
                userCredentialVault.putCredentials(endpoint.getId(), credentials);
            }

            // Set the credentials onto the connector
            connector.setCredentials(credentials);
        }

        return connector;
    }

    public static Connector newInstance(Endpoint endpoint)
    {
        String endpointUrl = (String) endpoint.getEndpointURL();
        if (endpointUrl == null)
        {
            return null;
        }

        String defaultUri = (String) endpoint.getDefaultURI();

        // build the final URL
        String url = endpointUrl;
        if (defaultUri != null)
        {
            url = url + defaultUri;
        }

        // get the connector id
        String connectorId = (String) endpoint.getConnectorId();
        if (connectorId == null || connectorId.length() == 0)
        {
            connectorId = "http";
        }

        // instantiate this connector
        String className = Framework.getConfig().getRemoteConnectorClass(connectorId);
        Connector connector = _getConnector(className, url);

        return connector;
    }

    public static WebConnector newWebConnector(String endpointUrl)
    {
        RemoteClient webClient = new RemoteClient(endpointUrl);
        return new WebConnector(webClient);
    }

    protected static Connector _getConnector(String className, String url)
    {
        if (cache == null)
        {
            cache = new HashMap();
        }

        String cacheKey = className + "_" + url;

        Connector connector = (Connector) cache.get(cacheKey);
        if (connector == null)
        {
            Class[] argTypes = new Class[] { url.getClass() };
            String[] args = new String[] { url };
            connector = (Connector) ReflectionHelper.newObject(className,
                    argTypes, args);

            cache.put(cacheKey, connector);
        }

        return connector;
    }

    protected static Authenticator _getAuthenticator(String className)
    {
        if (cache == null)
        {
            cache = new HashMap<String, Object>();
        }

        String cacheKey = className;

        Authenticator auth = (Authenticator) cache.get(className);
        if (auth == null)
        {
            auth = (Authenticator) ReflectionHelper.newObject(className);

            cache.put(cacheKey, auth);
        }

        return auth;
    }
}
