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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Authenticates credentials against a WebHelpdesk service.
 * This is used for ACT at Alfresco.
 * 
 * The means for achieving this is to perform a GET, establish a session
 * and then perform a POST to the form handler.  This logs the user in
 * and we track all cookies.
 * 
 * @author muzquiano
 */
public class WebHelpdeskAuthenticator extends AbstractAuthenticator
{
    protected static Log logger = LogFactory.getLog(WebHelpdeskAuthenticator.class);
    
    public final static String CS_PARAM_FORM_ACTION_HANDLER = "formActionHandler";
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Authenticator#authenticate(java.lang.String, org.alfresco.connector.Credentials, org.alfresco.connector.ConnectorSession)
     */
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException
    {
        ConnectorSession cs = null;
        
        if(credentials != null)
        {
            // extract the form action handler
            String formActionHandler = null;
            if(formActionHandler == null)
            {
                // build a new remote client
                RemoteClient remoteClient = new RemoteClient(endpoint);
        
                // do a simple get to load the page
                Response response = remoteClient.call("/helpdesk/WebObjects/Helpdesk");
                String html = response.getResponse();
                
                // look for the form action handler
                int x = html.indexOf("action=\"");
                if(x > -1)
                {
                    String cdr = html.substring(x+8, html.length());
                    int y = cdr.indexOf("\"");
                    if(y > -1)
                    {
                        formActionHandler = cdr.substring(0, y);
                    }
                }
            }
    
            if(logger.isDebugEnabled())
                logger.debug("formActionHandler: " + formActionHandler);
            
            if(formActionHandler != null)
            {
                // set the form action handler onto the connector session
                connectorSession.setParameter(CS_PARAM_FORM_ACTION_HANDLER, formActionHandler);
                
                // build a new remote client
                //RemoteClient remoteClient = new RemoteClient(endpoint);
    
                // our credentials
                String user = (String) credentials.getProperty(Credentials.CREDENTIAL_USERNAME);
                String pass = (String) credentials.getProperty(Credentials.CREDENTIAL_PASSWORD);
                String url = formActionHandler + "?userName=" + user + "&password=" + pass;
                
                connectorSession.setParameter(CS_PARAM_FORM_ACTION_HANDLER, url);
                
                /*
                // call over to log in and get jsessionid
                Response response = remoteClient.call(url);
    
                if(logger.isDebugEnabled())
                    logger.debug("Heard back: " + response.getStatus().getCode());
                
                // read back properties
                if (response.getStatus().getCode() == 200)
                {
                    // pull back auth cookies
                    this.processResponse(response, connectorSession);
                                                                
                    // signal success
                    cs = connectorSession;                            
                }
                */

                cs = connectorSession;
            }
        }
        
        return cs;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractAuthenticator#isAuthenticated(java.lang.String, org.alfresco.connector.ConnectorSession)
     */
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession)
    {
        String formActionHandler = (String) connectorSession.getParameter(CS_PARAM_FORM_ACTION_HANDLER);
        return (formActionHandler != null);
    }
}
