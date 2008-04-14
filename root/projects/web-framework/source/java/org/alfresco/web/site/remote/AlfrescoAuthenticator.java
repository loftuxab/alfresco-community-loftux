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

import org.alfresco.connector.Authenticator;
import org.alfresco.connector.Credentials;
import org.alfresco.connector.DefaultCredentials;
import org.alfresco.connector.Identity;
import org.alfresco.connector.remote.AbstractClient;
import org.alfresco.connector.remote.Connector;
import org.alfresco.connector.remote.Response;
import org.alfresco.connector.remote.WebClient;
import org.alfresco.connector.remote.WebConnector;
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 * @author muzquiano
 */
public class AlfrescoAuthenticator implements Authenticator
{
    public Credentials authenticate(Connector connector, Identity identity)
    {
        AlfrescoConnector alfConnector = (AlfrescoConnector) connector;
        AbstractClient alfClient = (AbstractClient) alfConnector.getClient();
        String endpointUrl = alfClient.getEndpoint();

        // the endpoint would be
        // http://localhost:8080/alfresco

        // create a new web connector
        WebConnector webConnector = ConnectorFactory.newWebConnector(endpointUrl);

        // plug identity onto the connector/client
        WebClient webClient = (WebClient) webConnector.getClient();
        String username = (String) identity.get("USERNAME");
        String password = (String) identity.get("PASSWORD");
        webClient.setUsernamePassword(username, password);

        // call and get the ticket
        Response r = webConnector.call("/service/ticket");

        // unwrap the ticket?
        String responseXml = r.getResponse();
        try
        {
            Document responseDoc = org.dom4j.DocumentHelper.parseText(responseXml);
            String ticket = (String) responseDoc.getRootElement().getStringValue();

            // make credentials
            DefaultCredentials credentials = new DefaultCredentials();
            credentials.put("ALF_TICKET", ticket);

            return credentials;
        }
        catch (DocumentException de)
        {
            de.printStackTrace();
        }
        return null;
    }

}
