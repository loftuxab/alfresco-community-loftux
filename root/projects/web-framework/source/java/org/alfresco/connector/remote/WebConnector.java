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
package org.alfresco.connector.remote;

import java.util.Map;

import org.alfresco.connector.Credentials;

/**
 * @author muzquiano
 */
public class WebConnector extends AbstractConnector
{
    public WebConnector(WebClient client)
    {
        super(client);
    }

    public WebConnector(String url)
    {
        this(new WebClient(url));
    }

    //
    // Internal Method for doing the actual work
    //

    protected Response service(String uri, Map parameters, Map headers)
    {
        WebClient remoteClient = ((WebClient) this.getClient());

        // stamp credentials onto the client
        credentials(remoteClient);

        // do the call
        return remoteClient.call(uri);
    }

    protected void credentials(Client client)
    {
        WebClient webClient = (WebClient) client;

        // set up authentication
        if (getCredentials() != null)
        {
            // user/pass?
            String user = (String) getCredentials().get("USERNAME");
            String pass = (String) getCredentials().get("PASSWORD");

            if (user != null && pass != null)
            {
                webClient.setUsernamePassword(user, pass);
            }
        }
    }

    protected WebClient client;
    protected Credentials credentials;

}
