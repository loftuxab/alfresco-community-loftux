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
package org.alfresco.web.site.remote;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.Endpoint;

/**
 * @author muzquiano
 */
public class RemoteFactory
{
    protected static Identity getIdentity(RequestContext context,
            Endpoint endpoint)
    {
        String credentials = endpoint.getSetting("credentials");
        String username = endpoint.getSetting("username");
        String password = endpoint.getSetting("password");

        // build the credentials package
        Identity identity = null;
        if ("specificuser".equalsIgnoreCase(credentials))
        {
            // use the specified user
            identity = new UserPasswordIdentity(username, password);
        }

        if ("currentuser".equalsIgnoreCase(credentials))
        {
            // TODO: Get the current user's login credentials to this server
            //username = getCurrentUsername(context);
            //password = getCurrentPassword(context);
            identity = new UserPasswordIdentity(username, password);
        }

        if ("alfrescoticket".equalsIgnoreCase(credentials))
        {
            // TODO: Get the current user's login credentials to this server
            //username = getCurrentUsername(context);
            //password = getCurrentPassword(context);
            identity = new AlfrescoTicketIdentity(username, password);
        }

        return identity;
    }

    protected static Client getClient(Endpoint endpoint)
    {
        String host = endpoint.getSetting("host");
        String portString = endpoint.getSetting("port");
        String protocol = endpoint.getSetting("protocol");
        String uri = endpoint.getSetting("uri");
        String authentication = endpoint.getSetting("authentication");

        int port = 80;
        try
        {
            int iPort = Integer.parseInt(portString);
            port = iPort;
        }
        catch (NumberFormatException nfe)
        {
        }

        // build the client
        Client client = null;
        if ("http".equalsIgnoreCase(protocol))
            client = new HttpRemoteClient(host, port, uri, protocol);
        if ("https".equalsIgnoreCase(protocol))
            client = new HttpsRemoteClient(host, port, uri, protocol);
        //if("rmi".equalsIgnoreCase(protocol))
        //	client = new RMIClient();

        // support for authentication schemes over http/https
        if ("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol))
            ((HttpRemoteClient) client).setAuthenticationMode(authentication);

        return client;
    }

    public static URLConnector newURLConnector(RequestContext context,
            Endpoint endpoint)
    {
        // get the credentials package
        Identity identity = getIdentity(context, endpoint);

        // get the client
        Client client = getClient(endpoint);

        // build the connector
        URLConnector connector = new URLConnector();
        if (identity != null)
            connector.setIdentity(identity);
        if (client != null)
            connector.setClient(client);

        return connector;
    }

    public static WebscriptConnector newWebscriptConnector(
            RequestContext context, Endpoint endpoint, String webscriptUri)
    {
        // get the credentials and client
        Identity identity = getIdentity(context, endpoint);

        // get the client
        HttpRemoteClient client = (HttpRemoteClient) getClient(endpoint);
        client.setUri(client.getUri() + webscriptUri);

        // build the connector
        WebscriptConnector connector = new WebscriptConnector();
        if (identity != null)
            connector.setIdentity(identity);
        if (client != null)
            connector.setClient(client);

        return connector;
    }

}
