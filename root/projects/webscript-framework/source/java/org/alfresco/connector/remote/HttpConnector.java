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

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.UsernamePasswordCredentials;

/**
 * @author muzquiano
 */
public class HttpConnector extends AbstractConnector
{
    public HttpConnector(HttpClient client)
    {
        super(client);
    }

    public HttpConnector(String url)
    {
        this(new HttpClient(url));
    }

    protected Response service(String uri, Map parameters, Map headers)
    {
        HttpClient remoteClient = ((HttpClient) this.getClient());
        remoteClient.init(parameters, headers, uri);

        // stamp credentials onto the client
        credentials(remoteClient);

        // execute
        Status status = new Status();
        String result = null;
        try
        {
            result = remoteClient.execute();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            // TODO: How to handle... set code, etc
        }

        return new Response(result, status);
    }

    protected void credentials(Client client)
    {
        HttpClient httpClient = (HttpClient) client;

        // set up authentication
        if (getCredentials() != null)
        {
            // set up authentication (specific to HttpClient)
            if (httpClient.getAuthenticationMode() != HttpClient.AUTHENTICATION_NONE)
            {
                String username = (String) getCredentials().get("USERNAME");
                String password = (String) getCredentials().get("PASSWORD");
                UsernamePasswordCredentials credz = new UsernamePasswordCredentials(
                        username, password);
                httpClient.applyCredentials(credz);
            }
        }
    }
}
