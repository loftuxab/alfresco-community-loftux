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
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

/**
 * An authentication while will perform an Alfresco ticket
 * handshake.
 * 
 * The credentials for performing the handshake are supplied
 * using a Credentials object.  The acquired ticket is then
 * placed onto the Credentials object.
 * 
 * @author muzquiano
 */
public class AlfrescoAuthenticator implements Authenticator
{

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.connector.Authenticator#authenticate(org.alfresco.connector.Client,
     *      org.alfresco.connector.Credentials)
     */
    public boolean authenticate(Client client, Credentials credentials)
            throws AuthenticationException
    {
        boolean authenticated = false;

        if (client instanceof RemoteClient)
        {
            // set up the remote client
            RemoteClient remoteClient = (RemoteClient) client;
            remoteClient.setTicket(null);
            remoteClient.setUsernamePassword(null, null);

            // call the login web script
            String user = (String) credentials.getProperty(Credentials.CREDENTIAL_ALF_USERNAME);
            String pass = (String) credentials.getProperty(Credentials.CREDENTIAL_ALF_PASSWORD);
            Response response = remoteClient.call("/api/login?u=" + user + "&pw=" + pass);

            // read back the ticket
            if (response.getStatus().getCode() == 200)
            {
                String responseText = response.getResponse();

                // read out the ticket id
                String ticket = null;
                try
                {
                    ticket = DocumentHelper.parseText(responseText).getRootElement().getTextTrim();
                }
                catch (DocumentException de)
                {
                    // the ticket that came back was unparseable or invalid
                    // this will cause the entire handshake to fail
                    throw new AuthenticationException(
                            "Unable to retrieve ticket from Alfresco", de);
                }

                // place the ticket back onto the Credentials object
                credentials.setProperty(Credentials.CREDENTIAL_ALF_TICKET,
                        ticket);

                authenticated = true;
            }
        }

        return authenticated;
    }
}
