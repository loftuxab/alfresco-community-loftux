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

import java.text.MessageFormat;

import org.alfresco.connector.exception.AuthenticationException;
import org.springframework.extensions.surf.util.URLEncoder;
import org.alfresco.web.scripts.json.JSONUtils;
import org.alfresco.web.scripts.json.JSONWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An implementation of an Alfresco ticket-based Authenticator.
 * 
 * This Authenticator can be plugged into a connector to allo the connector
 * to handshake with an Alfresco Repository. This handshake involves POSTing
 * the username and password to the /api/login WebScript.
 * 
 * A ticket is returned that is then plugged into a connector session.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class AlfrescoAuthenticator extends AbstractAuthenticator
{
    private static Log logger = LogFactory.getLog(AlfrescoAuthenticator.class);
    
    private static final String JSON_lOGIN = "'{'\"username\": \"{0}\", \"password\": \"{1}\"'}'";
    private static final String API_LOGIN = "/api/login";
    private static final String MIMETYPE_APPLICATION_JSON = "application/json";
    
    public final static String CS_PARAM_ALF_TICKET = "alfTicket";
    
    
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
            
            // retrieve the username and password
            String user = (String) credentials.getProperty(Credentials.CREDENTIAL_USERNAME);
            String pass = (String) credentials.getProperty(Credentials.CREDENTIAL_PASSWORD);
            
            if (logger.isDebugEnabled())
                logger.debug("Authenticating user: " + user);
            
            // POST to the login WebScript
            remoteClient.setRequestContentType(MIMETYPE_APPLICATION_JSON);
            String body = MessageFormat.format(JSON_lOGIN, JSONWriter.encodeJSONString(user), JSONWriter.encodeJSONString(pass));
            Response response = remoteClient.call(API_LOGIN, body);
            
            // read back the ticket
            if (response.getStatus().getCode() == 200)
            {
                String ticket;
                try
                {
                    JSONObject json = new JSONObject(response.getResponse());
                    ticket = json.getJSONObject("data").getString("ticket");
                } 
                catch (JSONException jErr)
                {
                    // the ticket that came back could not be parsed
                    // this will cause the entire handshake to fail
                    throw new AuthenticationException(
                            "Unable to retrieve login ticket from Alfresco", jErr);
                }
                
                if (logger.isDebugEnabled())
                    logger.debug("Parsed ticket: " + ticket);
                
                // place the ticket back into the connector session
                if (connectorSession != null)
                {
                    connectorSession.setParameter(CS_PARAM_ALF_TICKET, ticket);
                    
                    // signal that this succeeded
                    cs = connectorSession;
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Authentication failed, received response code: " + response.getStatus().getCode());            
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("No user credentials available - cannot authenticate.");
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