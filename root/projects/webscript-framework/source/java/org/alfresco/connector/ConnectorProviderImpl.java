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

import org.alfresco.connector.exception.ConnectorProviderException;
import org.alfresco.connector.exception.ConnectorServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A very simple implementation of a connector provider that provisions
 * web script connectors.  These are inherently stateless connectors - no
 * reuse of credentials or connector session data is applied to the 
 * provisioned connectors.
 * 
 * The connector provider pattern is utilized by the remote store as well
 * as the script remote object.  Both delegate to connector providers so as
 * to acquire connectors.
 * 
 * @author muzquiano
 */
public class ConnectorProviderImpl implements ConnectorProvider
{    
    private static final Log logger = LogFactory.getLog(ConnectorProviderImpl.class);

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
     * Implementation of the contract to provide a Connector for our
     * the web script framework.
     * 
     * Allows lazy providing of the Connector object only if the remote store actually needs
     * it. Otherwise acquiring the Connector when rarely used is an expensive overhead as most
     * objects are cached by the persister in which case the remote store isn't actually called.
     */
    public Connector provide(String endpoint)
        throws ConnectorProviderException
    {
        Connector conn = null;

        try
        {
            conn = connectorService.getConnector(endpoint);
        }
        catch(ConnectorServiceException cse)
        {
            throw new ConnectorProviderException("Unable to provision connector for endpoint: " + endpoint, cse);
        }
        
        return conn;
    }
}
