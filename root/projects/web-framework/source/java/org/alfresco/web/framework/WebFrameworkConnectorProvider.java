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
package org.alfresco.web.framework;

import javax.servlet.http.HttpSession;

import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorProvider;
import org.alfresco.connector.ConnectorService;
import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.User;
import org.alfresco.connector.exception.ConnectorProviderException;
import org.alfresco.connector.exception.ConnectorServiceException;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThreadLocalRequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An implementation of connector provider that provides access to the
 * Web Framework request context to build connectors
 *
 * @author Kevin Roast
 * @author muzquiano
 */
public class WebFrameworkConnectorProvider implements ConnectorProvider
{    
    private static final Log logger = LogFactory.getLog(WebFrameworkConnectorProvider.class);

    private ConnectorService connectorService;
    
    /**
     * Sets the connector service.
     * 
     * @param connectorService
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    /**
     * Implementation of the contract to provide a Connector for our remote store.
     * This allows lazy providing of the Connector object only if the remote store actually needs
     * it. Otherwise acquiring the Connector when rarely used is an expensive overhead as most
     * objects are cached by the persister in which case the remote store isn't actually called.
     */
    public Connector provide(String endpoint) throws ConnectorProviderException
    {
        Connector conn = null;
        RequestContext rc = ThreadLocalRequestContext.getRequestContext();

        if (rc != null)
        {
            try
            {
                // check whether we have a credential vault set up
                CredentialVault vault = rc.getCredentialVault();
                
                if (logger.isDebugEnabled())
                    logger.debug("Found credential vault: " + vault);
    
                // check whether we have a current user
                User user = rc.getUser();
                if (user == null || vault == null)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("No user was found, creating unauthenticated connector");
    
                    // return the non-credential'ed connector to this endpoint
                    conn = connectorService.getConnector(endpoint);                       
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("User '" + user.getId() + "' was found, creating authenticated connector");
    
                    // return the credential'ed connector to this endpoint
                    HttpSession httpSession = rc.getRequest().getSession(true);
                    conn = connectorService.getConnector(endpoint, rc.getUserId(), httpSession);
                }
            }
            catch(ConnectorServiceException cse)
            {
                throw new ConnectorProviderException("Unable to provision connector for endpoint: " + endpoint, cse);
            }
        }
        else
        {
            // if we don't have a request context, we can still provision
            // a non-credential'd connector
            try
            {
                conn = connectorService.getConnector(endpoint);
            }
            catch(ConnectorServiceException cse)
            {
                throw new ConnectorProviderException("Unable to provision non-credential'd connector for endpoint: " + endpoint, cse);
            }
        }

        return conn;
    }
}
