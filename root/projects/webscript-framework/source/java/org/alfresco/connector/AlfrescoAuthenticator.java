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

import org.alfresco.connector.exception.AuthenticationException;
import org.alfresco.util.URLEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

/**
 * An implementation of an Alfresco ticket-based Authenticator.
 * 
 * This Authenticator can be plugged into a connector to allow
 * the connector to handshake with an Alfresco Repository.  This
 * handshake involves sending username and password to the login
 * web script.
 * 
 * A ticket is returned that is then plugged into
 * a connector session.
 * 
 * @author muzquiano
 */
public class AlfrescoAuthenticator extends AbstractAuthenticator
{
    public final static String CS_PARAM_ALF_TICKET = "alfTicket";
    private static Log logger = LogFactory.getLog(AlfrescoAuthenticator.class);
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractAuthenticator#authenticate(java.lang.String, org.alfresco.connector.Credentials, org.alfresco.connector.ConnectorSession)
     */
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException
    {
        ConnectorSession cs = null;
        
        if (credentials != null)
        {
            // build a new remote client
            RemoteClient remoteClient = new RemoteClient(endpoint);
    
            // call the login web script
            String user = (String) credentials.getProperty(Credentials.CREDENTIAL_USERNAME);
            String pass = (String) credentials.getProperty(Credentials.CREDENTIAL_PASSWORD);
            
            if(logger.isDebugEnabled())
                logger.debug("Authenticating user: " + user);
            
            Response response = remoteClient.call("/api/login?u=" + URLEncoder.encode(user) + "&pw=" + pass);
            
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
                
                if(logger.isDebugEnabled())
                    logger.debug("Parsed ticket: " + ticket);
    
                // place the ticket back into the connector session
                if(connectorSession != null)
                {
                    connectorSession.setParameter(CS_PARAM_ALF_TICKET, ticket);
                    
                    // signal that this succeeded
                    cs = connectorSession;
                }
            }
            else
            {
                if(logger.isDebugEnabled())
                    logger.debug("Authentication failed, received response code: " + response.getStatus().getCode());            
            }
        }

        return cs;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractAuthenticator#isAuthenticated(java.lang.String, org.alfresco.connector.ConnectorSession)
     */
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession)
    {
        return (connectorSession.getParameter(CS_PARAM_ALF_TICKET) != null);
    }
    
}
